package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_ordering.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        setListeners();
    }

    private void setListeners() {
        binding.loginBtn.setOnClickListener(v ->
                startActivity(new Intent(IntroActivity.this, LoginActivity.class))
        );
        binding.signupBtn.setOnClickListener(v ->
                startActivity(new Intent(IntroActivity.this, SignupActivity.class))
        );
    }
}
