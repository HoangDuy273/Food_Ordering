package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.food_ordering.R;

public class AddCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        EditText edtCardHolder = findViewById(R.id.edtCardHolder);
        EditText edtCardNumber = findViewById(R.id.edtCardNumber);
        EditText edtExpireDate = findViewById(R.id.edtExpireDate);
        Button btnAddMakePayment = findViewById(R.id.btnAddMakePayment);

        btnAddMakePayment.setOnClickListener(v -> {
            String cardHolder = edtCardHolder.getText().toString().trim();
            String cardNumber = edtCardNumber.getText().toString().trim();
            String expireDate = edtExpireDate.getText().toString().trim();
            String cvc = "";
            EditText edtCVC = null;
            try { edtCVC = findViewById(R.id.edtCVC); cvc = edtCVC.getText().toString().trim(); } catch (Exception ignored) {}
            boolean valid = true;
            if (cardHolder.isEmpty()) {
                edtCardHolder.setError("Required"); valid = false;
            }
            if (cardNumber.length() != 16 || !cardNumber.matches("\\d{16}")) {
                edtCardNumber.setError("Card number must be 16 digits"); valid = false;
            }
            if (!expireDate.matches("(0[1-9]|1[0-2])/\\d{4}")) {
                edtExpireDate.setError("Format mm/yyyy"); valid = false;
            }
            if (edtCVC != null && (cvc.length() != 3 || !cvc.matches("\\d{3}"))) {
                edtCVC.setError("CVC must be 3 digits"); valid = false;
            }
            if (!valid) return;
            Intent result = new Intent();
            result.putExtra("card_holder", cardHolder);
            result.putExtra("card_number", cardNumber);
            result.putExtra("expire_date", expireDate);
            setResult(RESULT_OK, result);
            finish();
        });
    }
} 