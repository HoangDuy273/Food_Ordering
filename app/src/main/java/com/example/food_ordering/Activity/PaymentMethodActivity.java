package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.food_ordering.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import com.example.food_ordering.model.OrderRequest;
import com.example.food_ordering.model.OrderResponse;
import com.example.food_ordering.model.CartResponse;
import com.example.food_ordering.model.CartItem;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.network.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class PaymentMethodActivity extends AppCompatActivity {
    private LinearLayout layoutCash, layoutMastercard;
    private CardView cardInfoBox;
    private ImageView cardIcon;
    private TextView cardNumber, cardHolder;
    private ImageView cashCheck, mastercardCheck, btnRemoveCard;
    private Button btnPayConfirm;
    private String selectedMethod = "Cash";
    private String cardInfo = null;
    private CardView cardEmptyBox;
    private RecyclerView recyclerViewCards;
    private CardAdapter cardAdapter;
    private ArrayList<CardModel> cardList = new ArrayList<>();
    private int selectedCardIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        layoutCash = findViewById(R.id.paymentMethodScroll).findViewWithTag("cashLayout");
        layoutMastercard = findViewById(R.id.paymentMethodScroll).findViewWithTag("mastercardLayout");
        cardInfoBox = findViewById(R.id.cardInfoBox);
        cardIcon = findViewById(R.id.cardIcon);
        cardNumber = findViewById(R.id.cardNumber);
        cardHolder = findViewById(R.id.cardHolder);
        cashCheck = findViewById(R.id.cashCheck);
        mastercardCheck = findViewById(R.id.mastercardCheck);
        btnRemoveCard = findViewById(R.id.btnRemoveCard);
        btnPayConfirm = findViewById(R.id.btnPayConfirm);
        cardEmptyBox = findViewById(R.id.cardEmptyBox);
        recyclerViewCards = findViewById(R.id.recyclerViewCards);
        recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(cardList, new CardAdapter.OnCardActionListener() {
            @Override
            public void onCardSelected(int position) {
                selectedCardIndex = position;
                cardAdapter.setSelectedIndex(position);
                selectMethod("Mastercard");
            }
            @Override
            public void onCardRemoved(int position) {
                cardList.remove(position);
                cardAdapter.notifyDataSetChanged();
                updateCardListUI();
                if (!cardList.isEmpty()) {
                    if (selectedCardIndex == position) selectedCardIndex = 0;
                    cardAdapter.setSelectedIndex(selectedCardIndex);
                } else {
                    selectedCardIndex = -1;
                }
            }
        });
        recyclerViewCards.setAdapter(cardAdapter);
        updateCardListUI();

        // 1. Đảm bảo nút + ADD NEW luôn hiển thị (đã ở layout)
        // 2. Luôn hiển thị tổng tiền và nút PAY & CONFIRM (đã ở layout)
        // 3. Khi add card mới, không thay đổi card cũ nếu đã có
        // 4. Validate khi nhập card (AddCardActivity)
        // 5. Thêm hiệu ứng click (ripple) khi chọn card box và phương thức (đã ở layout)

        // 1. Hiển thị tổng tiền
        double total = getIntent().getDoubleExtra("total_amount", 0);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTotalAmount.setText(String.format("$%.2f", total));

        // 2. Xử lý chọn phương thức thanh toán
        layoutCash.setOnClickListener(v -> selectMethod("Cash"));
        layoutMastercard.setOnClickListener(v -> selectMethod("Mastercard"));
        // Mặc định: chưa có thẻ
        cardEmptyBox.setVisibility(View.VISIBLE);
        cardInfoBox.setVisibility(View.GONE);
        selectMethod("Cash"); // Mặc định chọn Cash

        // 3. Thêm thẻ mới
        Button btnAddNew = findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentMethodActivity.this, AddCardActivity.class);
            startActivityForResult(intent, 1001);
        });

        btnRemoveCard.setOnClickListener(v -> {
            cardInfo = null;
            cardInfoBox.setVisibility(View.GONE);
            cardEmptyBox.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Đã xóa thẻ Mastercard", Toast.LENGTH_SHORT).show();
            selectMethod("Cash");
        });

        btnPayConfirm.setOnClickListener(v -> {
            if (selectedMethod.equals("Cash")) {
                placeOrder("Cash on Delivery");
            } else if (selectedMethod.equals("Mastercard")) {
                if (cardList.isEmpty() || selectedCardIndex < 0) {
                    Toast.makeText(this, "Vui lòng thêm và chọn thẻ Mastercard trước khi thanh toán!", Toast.LENGTH_SHORT).show();
                } else {
                    placeOrder("Mastercard");
                }
            }
        });
    }

    private void selectMethod(String method) {
        selectedMethod = method;
        if (method.equals("Cash")) {
            layoutCash.setBackgroundResource(R.drawable.payment_method_selector_bg_selected);
            layoutMastercard.setBackgroundResource(R.drawable.payment_method_selector_bg);
            cashCheck.setVisibility(View.VISIBLE);
            mastercardCheck.setVisibility(View.GONE);
            recyclerViewCards.setVisibility(View.GONE);
            cardEmptyBox.setVisibility(View.GONE);
        } else {
            layoutCash.setBackgroundResource(R.drawable.payment_method_selector_bg);
            layoutMastercard.setBackgroundResource(R.drawable.payment_method_selector_bg_selected);
            cashCheck.setVisibility(View.GONE);
            mastercardCheck.setVisibility(View.VISIBLE);
            updateCardListUI();
        }
    }

    // Sửa lại onActivityResult để không thay đổi card cũ nếu đã có
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String cardHolderStr = data.getStringExtra("card_holder");
            String cardNumberStr = data.getStringExtra("card_number");
            String expireDate = data.getStringExtra("expire_date");
            CardModel card = new CardModel(cardHolderStr, cardNumberStr, expireDate);
            cardList.add(card);
            cardAdapter.notifyDataSetChanged();
            updateCardListUI();
            selectedCardIndex = cardList.size() - 1;
            cardAdapter.setSelectedIndex(selectedCardIndex);
            selectMethod("Mastercard");
        }
    }

    private void updateCardListUI() {
        if (selectedMethod.equals("Mastercard")) {
            if (cardList.isEmpty()) {
                recyclerViewCards.setVisibility(View.GONE);
                cardEmptyBox.setVisibility(View.VISIBLE);
            } else {
                recyclerViewCards.setVisibility(View.VISIBLE);
                cardEmptyBox.setVisibility(View.GONE);
            }
        } else {
            recyclerViewCards.setVisibility(View.GONE);
            cardEmptyBox.setVisibility(View.GONE);
        }
    }

    // Thêm hiệu ứng click cho card box (chọn Mastercard khi bấm vào cardInfoBox hoặc cardEmptyBox)
    @Override
    protected void onResume() {
        super.onResume();
        cardInfoBox.setOnClickListener(v -> selectMethod("Mastercard"));
        cardEmptyBox.setOnClickListener(v -> selectMethod("Mastercard"));
    }

    private void placeOrder(String paymentMethod) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        ApiService apiService = RetrofitClient.getApiService();
        String token = sharedPrefManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Lấy giỏ hàng từ backend
        apiService.getCart("Bearer " + token).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderRequest.OrderItemRequest> orderItems = new ArrayList<>();
                    List<String> cartItemIds = new ArrayList<>();
                    for (com.example.food_ordering.network.ApiService.CartItemResponse item : response.body().getData()) {
                        com.example.food_ordering.network.ApiService.CartItemResponse.Food food = item.getFood();
                        orderItems.add(new OrderRequest.OrderItemRequest(
                            food.getId(),
                            item.getQuantity(),
                            food.getPrice().getValue()
                        ));
                        cartItemIds.add(item.getId());
                    }
                    // Thông tin giao hàng mẫu
                    String deliveryAddress = "123 Main St, LA California";
                    String phoneNumber = "1234567890";
                    String notes = "";
                    OrderRequest orderRequest = new OrderRequest(orderItems, deliveryAddress, phoneNumber);
                    orderRequest.setNotes(notes);
                    orderRequest.setPaymentMethod(paymentMethod);
                    apiService.placeOrder("Bearer " + token, orderRequest).enqueue(new Callback<OrderResponse>() {
                        @Override
                        public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(PaymentMethodActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                clearCartAndGoToSuccess(token, cartItemIds, response.body().getData().getOrderId());
                            } else {
                                Toast.makeText(PaymentMethodActivity.this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<OrderResponse> call, Throwable t) {
                            Toast.makeText(PaymentMethodActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PaymentMethodActivity.this, "Không lấy được giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(PaymentMethodActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCartAndGoToSuccess(String token, List<String> cartItemIds, String orderId) {
        ApiService apiService = RetrofitClient.getApiService();
        if (cartItemIds.isEmpty()) {
            goToOrderSuccess(orderId);
            return;
        }
        final int[] pending = {cartItemIds.size()};
        for (String id : cartItemIds) {
            apiService.removeFromCart("Bearer " + token, id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    pending[0]--;
                    if (pending[0] == 0) goToOrderSuccess(orderId);
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    pending[0]--;
                    if (pending[0] == 0) goToOrderSuccess(orderId);
                }
            });
        }
    }

    private void goToOrderSuccess(String orderId) {
        Intent intent = new Intent(PaymentMethodActivity.this, OrderSuccessActivity.class);
        intent.putExtra("orderId", orderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    public static class CardModel {
        String holder, number, expire;
        CardModel(String h, String n, String e) { holder = h; number = n; expire = e; }
    }
} 