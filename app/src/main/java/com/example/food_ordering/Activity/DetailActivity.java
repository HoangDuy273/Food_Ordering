package com.example.food_ordering.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.R;
import com.example.food_ordering.databinding.ActivityDetailBinding;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.util.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private ActivityDetailBinding binding;
    private Foods object;
    private int quantity = 0;
    private boolean isLiked = false;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo SharedPrefManager
        sharedPrefManager = new SharedPrefManager(this);

        // Bật nút Back trên ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Lấy dữ liệu từ Intent
        object = (Foods) getIntent().getSerializableExtra("object");

        if (object != null && object.getId() != null) {
            // Hiển thị thông tin
            binding.titleTxt.setText(object.getTitle());
            binding.priceTxt.setText("$" + String.format("%.2f", object.getPrice().getValue()));
            binding.descriptionTxt.setText(object.getDescription());
            binding.rateTxt.setText(String.valueOf(object.getStar()));
            binding.timeTxt.setText(object.getTime().getValue());
            Glide.with(this).load(object.getImagePath()).into(binding.pic);
            binding.ratingBar.setRating((float) object.getStar());

            // Log Id của Foods để kiểm tra
            Log.d(TAG, "Food Id: " + object.getId());

            // Khởi tạo số lượng và giá tổng
            updateQuantityAndTotal();
        } else {
            binding.titleTxt.setText("Không có dữ liệu");
            Log.e(TAG, "Foods object is null or Id is null");
            Toast.makeText(this, "Không thể tải thông tin món ăn", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Xử lý nút Back
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // Xử lý nút tăng/giảm số lượng
        binding.minusBtn.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity--;
                updateQuantityAndTotal();
            }
        });

        binding.plusBtn.setOnClickListener(v -> {
            quantity++;
            updateQuantityAndTotal();
        });

        // Xử lý nút "thích"
        binding.favBtn.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                binding.favBtn.setImageResource(R.drawable.favorite_red);
                Toast.makeText(this, "Đã thích!", Toast.LENGTH_SHORT).show();
            } else {
                binding.favBtn.setImageResource(R.drawable.favorite_white);
                Toast.makeText(this, "Bỏ thích!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút "Add to cart"
        binding.addBtn.setOnClickListener(v -> {
            if (quantity > 0) {
                addToCart();
            } else {
                Toast.makeText(this, "Vui lòng chọn số lượng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật số lượng và giá tổng
    private void updateQuantityAndTotal() {
        binding.numTxt.setText(String.valueOf(quantity));
        double price = object != null ? object.getPrice().getValue() : 0.0;
        binding.totalTxt.setText("$" + String.format("%.2f", quantity * price));
    }

    // Gửi món hàng lên server
    private void addToCart() {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log foodId trước khi gửi
        String foodId = object.getId();
        Log.d(TAG, "Sending foodId: " + foodId);

        ApiService.CartItemRequest cartItemRequest = new ApiService.CartItemRequest(foodId, quantity);

        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiService.CartItemResponse> call = apiService.addToCart("Bearer " + token, cartItemRequest);
        call.enqueue(new Callback<ApiService.CartItemResponse>() {
            @Override
            public void onResponse(Call<ApiService.CartItemResponse> call, Response<ApiService.CartItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DetailActivity.this, "Đã thêm " + quantity + " món vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Add to cart failed: " + errorBody);
                        Toast.makeText(DetailActivity.this, "Thêm vào giỏ hàng thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        Toast.makeText(DetailActivity.this, "Thêm vào giỏ hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.CartItemResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(DetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}