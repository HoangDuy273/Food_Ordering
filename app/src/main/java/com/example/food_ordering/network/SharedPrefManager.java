package com.example.food_ordering.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "food_app_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // Thêm method để lưu thông tin user
    public void saveUserInfo(String name, String email) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    // Thêm method để lấy tên user
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "User");
    }

    // Thêm method để lấy email user
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public void clearToken() {
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.apply();
    }

    // Kiểm tra user đã login chưa
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}