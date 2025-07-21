package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_ordering.Activity.MainActivity;
import com.example.food_ordering.databinding.ActivityOrderTrackingBinding;
import com.example.food_ordering.model.OrderResponse;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.network.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import android.view.View;
import com.example.food_ordering.R;

public class OrderTrackingActivity extends AppCompatActivity {
    private ActivityOrderTrackingBinding binding;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getApiService();
        sharedPrefManager = new SharedPrefManager(this);

        // Hiển thị thông tin đơn hàng (lấy từ Intent)
        String orderId = getIntent().getStringExtra("orderId");
        binding.tvOrderId.setText("Order ID: " + (orderId != null ? orderId : "N/A"));
        fetchOrderStatus(orderId);

        // Xử lý nút quay về Home
        binding.btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderTrackingActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng activity hiện tại
        });

        // Gán id cho các chấm tiến độ (dùng View Binding)
        View dotStep1 = binding.dotStep1;
        View dotStep2 = binding.dotStep2;
        View dotStep3 = binding.dotStep3;
        View dotStatus = binding.dotStatus;
    }

    private void fetchOrderStatus(String orderId) {
        if (orderId == null) {
            binding.tvStatus.setText("Trạng thái: Không xác định");
            binding.tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
            return;
        }
        String token = sharedPrefManager.getToken();
        apiService.getOrders("Bearer " + token).enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (OrderResponse order : response.body()) {
                        if (order.getData() != null && orderId.equals(order.getData().getOrderId())) {
                            String status = order.getData().getStatus();
                            String statusText;
                            int color;
                            if ("completed".equalsIgnoreCase(status)) {
                                statusText = "Đã giao hàng";
                                color = getResources().getColor(android.R.color.holo_green_dark);
                            } else if ("pending".equalsIgnoreCase(status)) {
                                statusText = "Đã đặt hàng";
                                color = getResources().getColor(android.R.color.holo_green_dark);
                            } else if ("shipping".equalsIgnoreCase(status)) {
                                statusText = "Đang vận chuyển";
                                color = getResources().getColor(android.R.color.holo_orange_dark);
                            } else if ("cancelled".equalsIgnoreCase(status)) {
                                statusText = "Đã hủy";
                                color = getResources().getColor(android.R.color.holo_red_dark);
                            } else {
                                statusText = "Không xác định";
                                color = getResources().getColor(android.R.color.darker_gray);
                            }
                            binding.tvStatus.setText("Trạng thái: " + statusText);
                            binding.tvStatus.setTextColor(color);

                            // Đổi màu chấm trạng thái dotStatus
                            if (binding.dotStatus != null) {
                                if ("completed".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
                                    binding.dotStatus.setBackgroundResource(R.drawable.circle_green);
                                } else if ("shipping".equalsIgnoreCase(status)) {
                                    binding.dotStatus.setBackgroundResource(R.drawable.circle_orange);
                                } else if ("cancelled".equalsIgnoreCase(status)) {
                                    binding.dotStatus.setBackgroundResource(R.drawable.circle_gray);
                                } else {
                                    binding.dotStatus.setBackgroundResource(R.drawable.circle_gray);
                                }
                            }
                            return;
                        }
                    }
                    binding.tvStatus.setText("Trạng thái: Không xác định");
                    binding.tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    binding.tvStatus.setText("Trạng thái: Lỗi khi lấy dữ liệu");
                    binding.tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                binding.tvStatus.setText("Trạng thái: Lỗi kết nối");
                binding.tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                Log.e("OrderTracking", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(OrderTrackingActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}