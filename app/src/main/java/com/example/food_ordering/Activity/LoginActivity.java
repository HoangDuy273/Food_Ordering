package com.example.food_ordering.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_ordering.databinding.ActivityLoginBinding;
import com.example.food_ordering.model.LoginRequest;
import com.example.food_ordering.model.LoginResponse;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.network.SharedPrefManager;
import com.example.food_ordering.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.food_ordering.model.ForgotPasswordRequest;
import com.example.food_ordering.model.ForgotPasswordResponse;
import com.example.food_ordering.model.ResetPasswordRequest;
import com.example.food_ordering.model.ResetPasswordResponse;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private boolean toastShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#FFE4B5"));

        setVariable();
    }

    private void showToastOnce(String message) {
        if (!toastShown) {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            toastShown = true;
            binding.getRoot().postDelayed(() -> toastShown = false, 3000);
        }
    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(v -> {
            String email = binding.userEdt.getText().toString().trim();
            String password = binding.passEdt.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                LoginRequest request = new LoginRequest(email, password);

                RetrofitClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String token = response.body().getToken();
                            LoginResponse.User user = response.body().getUser();
                            if (user != null) {
                                String name = user.getName();
                                String email = user.getEmail();

                                SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
                                sharedPrefManager.saveToken(token);
                                sharedPrefManager.saveUserInfo(name, email); // Lưu tên và email

                                showToastOnce("Login success!");

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                showToastOnce("Login failed: User data is null");
                            }
                        } else {
                            showToastOnce("Login failed: " + (response.body() != null ? response.body().getMessage() : response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        showToastOnce("Network error: " + t.getMessage());
                    }
                });

            } else {
                showToastOnce("Please fill in both fields");
            }
        });

        binding.textView3.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });

        binding.forgotPasswordTv.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void showForgotPasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
        final EditText emailEditText = dialogView.findViewById(R.id.editEmail);

        new AlertDialog.Builder(this)
            .setTitle("Quên mật khẩu")
            .setView(dialogView)
            .setPositiveButton("Gửi", (dialog, which) -> {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendForgotPasswordRequest(email);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void sendForgotPasswordRequest(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        RetrofitClient.getApiService().forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    // Nếu thành công, hiển thị dialog để reset password
                    showResetPasswordDialog(email);
                } else {
                    String errorMessage = "Yêu cầu thất bại. Vui lòng thử lại.";
                    // Cố gắng lấy lỗi cụ thể từ server nếu có
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResetPasswordDialog(String email) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_reset_password, null);
        final EditText otpEditText = dialogView.findViewById(R.id.editOtp);
        final EditText newPasswordEditText = dialogView.findViewById(R.id.editNewPassword);

        new AlertDialog.Builder(this)
            .setTitle("Đặt lại mật khẩu")
            .setView(dialogView)
            .setPositiveButton("Xác nhận", (dialog, which) -> {
                String otp = otpEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                if (otp.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập OTP và mật khẩu mới", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendResetPasswordRequest(email, otp, newPassword);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void sendResetPasswordRequest(String email, String otp, String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);
        RetrofitClient.getApiService().resetPassword(request).enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    String errorMessage = "Đặt lại mật khẩu thất bại.";
                    // Cần logic phân tích errorBody tương tự như các chức năng khác
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}