package com.example.food_ordering.Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.food_ordering.R;
import com.example.food_ordering.model.UpdateProfileRequest;
import com.example.food_ordering.model.UserProfileResponse;
import com.example.food_ordering.model.UserProfile;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.network.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.google.gson.Gson;

import com.example.food_ordering.model.ChangePasswordRequest;
import com.example.food_ordering.model.ChangePasswordResponse;
import android.content.Intent;
import com.example.food_ordering.Activity.AddressActivity;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameTextView, emailTextView, createdAtTextView;
    private Button editProfileButton, changePasswordButton, manageAddressButton;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        createdAtTextView = findViewById(R.id.createdAtTextView);
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        manageAddressButton = findViewById(R.id.manageAddressButton);

        apiService = RetrofitClient.getApiService();
        sharedPrefManager = new SharedPrefManager(this);

        String token = sharedPrefManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    UserProfile user = response.body().getUser();
                    nameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());
                    createdAtTextView.setText("Tham gia từ: " + formatDate(user.getCreatedAt()));
                } else {
                    Toast.makeText(ProfileActivity.this, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        manageAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddressActivity.class);
            startActivity(intent);
        });
    }

    private void showEditProfileDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.diaglog_edit_profile, null);

        EditText nameEditText = dialogView.findViewById(R.id.editName);
        EditText emailEditText = dialogView.findViewById(R.id.editEmail);

        // Set current values
        nameEditText.setText(nameTextView.getText().toString());
        emailEditText.setText(emailTextView.getText().toString());

        new AlertDialog.Builder(this)
            .setTitle("Chỉnh sửa hồ sơ")
            .setView(dialogView)
            .setPositiveButton("Lưu", (dialog, which) -> {
                String newName = nameEditText.getText().toString().trim();
                String newEmail = emailEditText.getText().toString().trim();
                updateProfile(newName, newEmail);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void updateProfile(String name, String email) {
        String token = sharedPrefManager.getToken();
        ApiService apiService = RetrofitClient.getApiService();

        UpdateProfileRequest request = new UpdateProfileRequest(name, email);

        apiService.updateUserProfile("Bearer " + token, request)
            .enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        UserProfile user = response.body().getUser();
                        nameTextView.setText(user.getName());
                        emailTextView.setText(user.getEmail());
                        Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = "Cập nhật thất bại";
                        if (response.body() != null) msg = response.body().getMessage();
                        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_change_pasword, null);

        final EditText oldPasswordEditText = dialogView.findViewById(R.id.editOldPassword);
        final EditText newPasswordEditText = dialogView.findViewById(R.id.editNewPassword);
        final EditText confirmPasswordEditText = dialogView.findViewById(R.id.editConfirmPassword);

        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Đổi mật khẩu")
            .setView(dialogView)
            .setPositiveButton("Lưu", null) // Tạm thời để null, sẽ override sau
            .setNegativeButton("Hủy", (d, which) -> d.dismiss())
            .create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Kiểm tra ngay trên client trước khi gọi API
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return; // Không đóng dialog
                }
                if (newPassword.length() < 6) {
                    Toast.makeText(ProfileActivity.this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return; // Không đóng dialog
                }
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ProfileActivity.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                    return; // Không đóng dialog
                }

                // Gọi API và truyền dialog vào để xử lý đóng/mở
                changePassword(oldPassword, newPassword, confirmPassword, dialog);
            });
        });

        dialog.show();
    }

    private void changePassword(String oldPassword, String newPassword, String confirmPassword, final AlertDialog dialog) {
        String token = sharedPrefManager.getToken();
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, confirmPassword);

        apiService.changePassword("Bearer " + token, request).enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thành công
                    Toast.makeText(ProfileActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if (response.body().isSuccess()) {
                        dialog.dismiss();
                    }
                } else {
                    // Thất bại
                    String errorMessage = "Có lỗi xảy ra, vui lòng thử lại.";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            Gson gson = new Gson();
                            ChangePasswordResponse errorResponse = gson.fromJson(errorJson, ChangePasswordResponse.class);

                            if (errorResponse != null && errorResponse.getMessage() != null && !errorResponse.getMessage().isEmpty()) {
                                errorMessage = errorResponse.getMessage();
                            }
                        } catch (Exception e) {
                            Log.e("ChangePasswordError", "Failed to parse error response", e);
                        }
                    }
                    Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Hàm định dạng lại ngày cho đẹp
    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }
}