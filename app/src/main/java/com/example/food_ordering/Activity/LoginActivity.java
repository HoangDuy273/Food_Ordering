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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                                SharedPrefManager sharedPrefManager = new SharedPrefManager(LoginActivity.this);
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
    }
}