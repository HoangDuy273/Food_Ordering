package com.example.food_ordering.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_ordering.R;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;

public class BasicActivity extends AppCompatActivity {
    protected ApiService apiService;
    protected SharedPreferences sharedPreferences;
    public String TAG = "foodapp";
    public static final String PREF_NAME = "FoodOrderingPrefs";
    public static final String KEY_TOKEN = "auth_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize API service
        apiService = RetrofitClient.getClient().create(ApiService.class);


        // Initialize SharedPreferences for storing user data
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Set status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
    }

    // Helper methods for authentication
    protected void saveAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    protected String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    protected void saveUserInfo(String userId, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    protected String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    protected String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    protected boolean isUserLoggedIn() {
        return getAuthToken() != null && !getAuthToken().isEmpty();
    }

    protected void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}