package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.food_ordering.R;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {
    EditText[] otpFields;
    Button btnVerify;
    TextView tvResend, tvEmail;
    CountDownTimer resendTimer;
    int resendSeconds = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        otpFields = new EditText[]{
                findViewById(R.id.etCode1),
                findViewById(R.id.etCode2),
                findViewById(R.id.etCode3),
                findViewById(R.id.etCode4),
                findViewById(R.id.etCode5),
                findViewById(R.id.etCode6)
        };
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);
        tvEmail = findViewById(R.id.tvEmail);
        ImageButton btnBack = findViewById(R.id.btnBack);

        String email = getIntent().getStringExtra("email");
        if (email != null) {
            tvEmail.setText("We have sent a code to your email\n" + email);
        }

        btnBack.setOnClickListener(v -> finish());

        // Disable VERIFY khi chưa đủ 6 số
        TextWatcher enableVerifyWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnVerify.setEnabled(isOtpFilled());
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        for (EditText et : otpFields) {
            et.addTextChangedListener(enableVerifyWatcher);
        }
        btnVerify.setEnabled(false);

        // Tự động chuyển focus khi nhập số
        for (int i = 0; i < otpFields.length; i++) {
            final int idx = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && idx < otpFields.length - 1) {
                        otpFields[idx + 1].requestFocus();
                    }
                    if (s.length() == 0 && idx > 0) {
                        otpFields[idx - 1].requestFocus();
                    }
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // Gộp mã và xử lý VERIFY
        btnVerify.setOnClickListener(v -> {
            StringBuilder code = new StringBuilder();
            for (EditText et : otpFields) {
                String digit = et.getText().toString().trim();
                if (digit.isEmpty()) {
                    et.setError("Nhập đủ 6 số");
                    et.requestFocus();
                    return;
                }
                code.append(digit);
            }
            String otp = code.toString();
            if (email == null) {
                Toast.makeText(this, "Không tìm thấy email!", Toast.LENGTH_SHORT).show();
                return;
            }
            btnVerify.setEnabled(false);
            btnVerify.setText("Đang xác thực...");
            ApiService apiService = RetrofitClient.getApiService();
            apiService.verifyEmail(email, otp).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    btnVerify.setEnabled(true);
                    btnVerify.setText("VERIFY");
                    if (response.isSuccessful()) {
                        Toast.makeText(VerificationActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerificationActivity.this, "Mã xác thực không đúng hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    btnVerify.setEnabled(true);
                    btnVerify.setText("VERIFY");
                    Toast.makeText(VerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Xử lý resend
        startResendTimer();
        tvResend.setOnClickListener(v -> {
            if (resendSeconds == 0) {
                if (email == null) {
                    Toast.makeText(this, "Không tìm thấy email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ApiService apiService = RetrofitClient.getApiService();
                tvResend.setEnabled(false);
                apiService.resendOtp(email).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(VerificationActivity.this, "Đã gửi lại mã xác thực!", Toast.LENGTH_SHORT).show();
                            startResendTimer();
                        } else {
                            Toast.makeText(VerificationActivity.this, "Không gửi lại được mã!", Toast.LENGTH_SHORT).show();
                            tvResend.setEnabled(true);
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(VerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        tvResend.setEnabled(true);
                    }
                });
            }
        });
    }

    private boolean isOtpFilled() {
        for (EditText et : otpFields) {
            if (et.getText().toString().trim().isEmpty()) return false;
        }
        return true;
    }

    private void startResendTimer() {
        resendSeconds = 50;
        tvResend.setEnabled(false);
        resendTimer = new CountDownTimer(50000, 1000) {
            public void onTick(long millisUntilFinished) {
                resendSeconds = (int) (millisUntilFinished / 1000);
                tvResend.setText("Resend in " + resendSeconds + " sec");
            }
            public void onFinish() {
                tvResend.setText("Resend");
                tvResend.setEnabled(true);
            }
        }.start();
    }
} 