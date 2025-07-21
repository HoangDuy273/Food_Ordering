package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.food_ordering.R;

public class OrderTrackingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);
        
        // Lấy Order ID từ Intent
        String orderId = getIntent().getStringExtra("orderId");
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        if (orderId != null && !orderId.isEmpty()) {
            tvOrderId.setText(orderId);
        } else {
            tvOrderId.setText("N/A");
        }
        
        // Xử lý nút Back to Home
        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}