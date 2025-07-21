package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.food_ordering.R;
import com.example.food_ordering.Activity.OrderTrackingActivity;

public class OrderSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Lấy Order ID từ Intent
        String orderId = getIntent().getStringExtra("orderId");
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        if (orderId != null) {
            tvOrderId.setText("Order ID: " + orderId);
        }

        Button btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnTrackOrder.setOnClickListener(v -> {
            // Chuyển sang màn hình tracking đơn hàng
            Intent intent = new Intent(this, OrderTrackingActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
            finish();
        });

        // Thêm nút quay về Home
        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}