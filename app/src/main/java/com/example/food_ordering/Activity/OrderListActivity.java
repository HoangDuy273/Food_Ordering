package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.food_ordering.OrderTrackingActivity;
import com.example.food_ordering.R;
import com.example.food_ordering.databinding.ActivityOrderListBinding;
import com.example.food_ordering.model.OrderResponse;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.Adapter.OrderAdapter;
import com.example.food_ordering.network.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends AppCompatActivity {
    private static final String TAG = "OrderListActivity";
    private ActivityOrderListBinding binding;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi inflate binding: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();
        sharedPrefManager = new SharedPrefManager(this);
        initOrderList();
    }

    private void initOrderList() {
        if (!sharedPrefManager.isLoggedIn()) {
            Log.e(TAG, "Không tìm thấy token xác thực");
            Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OrderListActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String token = sharedPrefManager.getToken();
        apiService.getOrders("Bearer " + token).enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponse> orders = response.body();
                    Log.d(TAG, "Danh sách đơn hàng: " + (orders != null ? orders.size() : "null"));
                    if (orders != null && !orders.isEmpty()) {
                        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(
                                OrderListActivity.this, LinearLayoutManager.VERTICAL, false));
                        OrderAdapter adapter = new OrderAdapter(new ArrayList<>(orders), order -> {
                            Intent intent = new Intent(OrderListActivity.this, OrderTrackingActivity.class);
                            intent.putExtra("orderId", order.getData().getOrderId());
                            startActivity(intent);
                        });
                        binding.recyclerViewOrders.setAdapter(adapter);
                    } else {
                        Log.w(TAG, "Danh sách đơn hàng rỗng");
                        Toast.makeText(OrderListActivity.this, "Không có đơn hàng nào để hiển thị", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Lỗi API: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Toast.makeText(OrderListActivity.this, "Lấy dữ liệu đơn hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}