package com.example.food_ordering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_ordering.Activity.MainActivity;
import com.example.food_ordering.databinding.ActivityOrderTrackingBinding;

public class OrderTrackingActivity extends AppCompatActivity {
    private ActivityOrderTrackingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hiển thị thông tin đơn hàng (lấy từ Intent)
        String orderId = getIntent().getStringExtra("orderId");
        binding.textViewOrderId.setText("Order ID: " + (orderId != null ? orderId : "N/A"));
        binding.textViewStatus.setText("Status: Đang vận chuyển");

        // Xử lý nút quay về Home
        Button backToHomeButton = findViewById(R.id.buttonBackToHome);
        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrderTrackingActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng activity hiện tại
        });
    }
}