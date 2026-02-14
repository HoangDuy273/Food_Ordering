package com.example.food_ordering.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_ordering.databinding.ActivityIntroBinding;

public class IntroActivity extends BasicActivity {
    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        getWindow().setStatusBarColor(Color.parseColor("#FFE4B5"));
    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
        });

        binding.signupBtn.setOnClickListener(v ->
                startActivity(new Intent(IntroActivity.this, SignupActivity.class))
        );
    }
}
