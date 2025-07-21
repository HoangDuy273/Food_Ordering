package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.food_ordering.Activity.IntroActivity;
import com.example.food_ordering.Activity.MainActivity;
import com.example.food_ordering.Adapter.CartAdapter;
import com.example.food_ordering.databinding.ActivityCartBinding;
import com.example.food_ordering.model.CartResponse;
import com.example.food_ordering.model.OrderRequest;
import com.example.food_ordering.model.OrderResponse;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.network.SharedPrefManager;
import com.example.food_ordering.model.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private Address selectedAddress;

    // Define interfaces for CartAdapter
    @FunctionalInterface
    public interface OnQuantityChangedListener {
        void onQuantityChanged();
    }

    @FunctionalInterface
    public interface OnRemoveItemListener {
        void onRemoveItem(CartItem item, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getApiService();
        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        cartItems = new ArrayList<>();

        // Lấy địa chỉ mặc định khi vào giỏ hàng
        fetchDefaultAddress();

        // Khi ấn vào địa chỉ giao hàng hoặc nút thay đổi
        binding.editTextDeliveryAddress.setFocusable(false);
        binding.editTextDeliveryAddress.setOnClickListener(v -> openAddressSelector());

        // Initialize RecyclerView
        initCartRecyclerView();

        // Fetch cart items from server
        fetchCartItems();

        // Handle Back button
        binding.backBtn.setOnClickListener(v -> finish());

        // Handle Apply Coupon button
        binding.buttonApplyCoupon.setOnClickListener(v -> {
            String couponCode = binding.editTextText.getText().toString().trim();
            if (!couponCode.isEmpty()) {
                Toast.makeText(this, "Đã áp dụng mã: " + couponCode, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Place Order button
        binding.btnPlaceOrder.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            placeOrder();
        });
    }

    private void fetchCartItems() {
        String token = sharedPrefManager.getToken();
        Log.d("TOKEN_DEBUG", "Token: " + token);
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CartActivity.this, IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d(TAG, "Fetching cart items with token: " + token);
        Call<CartResponse> call = apiService.getCart("Bearer " + token);
        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    cartItems.clear();
                    for (ApiService.CartItemResponse item : response.body().getData()) {
                        ApiService.CartItemResponse.Food food = item.getFood();
                        cartItems.add(new CartItem(
                                item.getId(),
                                food.getTitle(),
                                food.getPrice().getValue(),
                                item.getQuantity(),
                                food.getImage()
                        ));
                    }
                    Log.d(TAG, "Cart items received: " + cartItems.size());
                    cartAdapter.notifyDataSetChanged();
                    updateOrderSummary();
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(CartActivity.this, "Lấy giỏ hàng thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CartActivity.this, "Lấy giỏ hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "API Failure: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCartRecyclerView() {
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, this::updateOrderSummary, this::removeCartItem);
        binding.recyclerViewCart.setAdapter(cartAdapter);
    }

    private void updateOrderSummary() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        double delivery = 10.00;
        double tax = 1.00;
        double total = subtotal + delivery + tax;

        binding.tvSubtotal.setText(String.format("$%.2f", subtotal));
        binding.tvDelivery.setText(String.format("$%.2f", delivery));
        binding.tvTax.setText(String.format("$%.2f", tax));
        binding.tvTotal.setText(String.format("$%.2f", total));
    }

    private void updateCartItemOnServer(CartItem item) {
        String token = sharedPrefManager.getToken();
        if (token == null) return;

        ApiService apiService = RetrofitClient.getApiService();
        ApiService.CartItemRequest request = new ApiService.CartItemRequest(item.getId(), item.getQuantity());
        Call<ApiService.CartItemResponse> call = apiService.updateCartItem("Bearer " + token, item.getId(), request);
        call.enqueue(new Callback<ApiService.CartItemResponse>() {
            @Override
            public void onResponse(Call<ApiService.CartItemResponse> call, Response<ApiService.CartItemResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to update cart item: " + response.code());
                    Toast.makeText(CartActivity.this, "Cập nhật giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.CartItemResponse> call, Throwable t) {
                Log.e(TAG, "Error updating cart item: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCartItem(CartItem item, int position) {
        String token = sharedPrefManager.getToken();
        if (token == null) return;

        Call<Void> call = apiService.removeFromCart("Bearer " + token, item.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cartItems.remove(position);
                    cartAdapter.notifyItemRemoved(position);
                    updateOrderSummary();
                    Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to remove cart item: " + response.code());
                    Toast.makeText(CartActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error removing cart item: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart(Runnable onComplete) {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            Log.e(TAG, "No token found for clearing cart");
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CartActivity.this, IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (cartItems.isEmpty()) {
            Log.d(TAG, "Cart is already empty");
            onComplete.run();
            return;
        }

        AtomicInteger pendingRequests = new AtomicInteger(cartItems.size());
        List<CartItem> itemsToRemove = new ArrayList<>(cartItems);

        for (CartItem item : itemsToRemove) {
            Call<Void> call = apiService.removeFromCart("Bearer " + token, item.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Successfully removed cart item: " + item.getId());
                    } else {
                        Log.e(TAG, "Failed to remove cart item: " + item.getId() + ", code: " + response.code());
                    }
                    if (pendingRequests.decrementAndGet() == 0) {
                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateOrderSummary();
                        Log.d(TAG, "All cart items cleared");
                        onComplete.run();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Error removing cart item: " + item.getId() + ", error: " + t.getMessage());
                    if (pendingRequests.decrementAndGet() == 0) {
                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateOrderSummary();
                        Log.d(TAG, "All cart items cleared (with some failures)");
                        onComplete.run();
                    }
                }
            });
        }
    }

    private void placeOrder() {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CartActivity.this, IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Giả định lấy thông tin giao hàng từ người dùng
        String deliveryAddress = binding.editTextDeliveryAddress.getText().toString().trim();
        if (deliveryAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }
        String phoneNumber = "1234567890"; // Giá trị mặc định
        String notes = binding.editTextText.getText().toString().trim(); // Sử dụng trường coupon làm notes tạm thời
        String paymentMethod = "Cash on Delivery"; // Giá trị mặc định

        // Chuẩn bị dữ liệu để gửi lên server
        List<OrderRequest.OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            orderItems.add(new OrderRequest.OrderItemRequest(item.getId(), item.getQuantity(), item.getPrice()));
        }

        OrderRequest orderRequest = new OrderRequest(orderItems, deliveryAddress, phoneNumber);
        orderRequest.setNotes(notes);
        orderRequest.setPaymentMethod(paymentMethod);

        Call<OrderResponse> call = apiService.placeOrder("Bearer " + token, orderRequest);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    String orderId = response.body().getData().getOrderId();

                    // Xóa giỏ hàng trước khi điều hướng
                    clearCart(() -> {
                        // Chuyển hướng về MainActivity
                        Intent intent = new Intent(CartActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);

                        // Chuyển hướng tới OrderTrackingActivity
                        Intent trackingIntent = new Intent(CartActivity.this, com.example.food_ordering.OrderTrackingActivity.class);
                        trackingIntent.putExtra("orderId", orderId);
                        startActivity(trackingIntent);
                        finish(); // Đóng CartActivity
                    });
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(CartActivity.this, "Đặt hàng thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CartActivity.this, "Đặt hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(TAG, "API Failure: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDefaultAddress() {
        String token = sharedPrefManager.getToken();
        if (token == null) return;
        apiService.getAddresses("Bearer " + token).enqueue(new retrofit2.Callback<com.example.food_ordering.model.AddressResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.food_ordering.model.AddressResponse> call, retrofit2.Response<com.example.food_ordering.model.AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<com.example.food_ordering.model.Address> addresses = response.body().getAddresses();
                    for (com.example.food_ordering.model.Address addr : addresses) {
                        if (addr.isDefault()) {
                            selectedAddress = addr;
                            binding.editTextDeliveryAddress.setText(formatAddress(addr));
                            break;
                        }
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.food_ordering.model.AddressResponse> call, Throwable t) {}
        });
    }

    private String formatAddress(com.example.food_ordering.model.Address addr) {
        return addr.getStreet() + ", " + addr.getCity() + ", " + addr.getState() + ", " + addr.getCountry();
    }

    private void openAddressSelector() {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra("selectMode", true);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            com.example.food_ordering.model.Address addr = (com.example.food_ordering.model.Address) data.getSerializableExtra("selectedAddress");
            if (addr != null) {
                selectedAddress = addr;
                binding.editTextDeliveryAddress.setText(formatAddress(addr));
            }
        }
    }

    public static class CartItem implements android.os.Parcelable {
        private String id;
        private String name;
        private double price;
        private int quantity;
        private String imageUrl;

        public CartItem(String id, String name, double price, int quantity, String imageUrl) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.imageUrl = imageUrl;
        }

        protected CartItem(android.os.Parcel in) {
            id = in.readString();
            name = in.readString();
            price = in.readDouble();
            quantity = in.readInt();
            imageUrl = in.readString();
        }

        public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
            @Override
            public CartItem createFromParcel(android.os.Parcel in) {
                return new CartItem(in);
            }

            @Override
            public CartItem[] newArray(int size) {
                return new CartItem[size];
            }
        };

        @Override
        public void writeToParcel(android.os.Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeDouble(price);
            dest.writeInt(quantity);
            dest.writeString(imageUrl);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}