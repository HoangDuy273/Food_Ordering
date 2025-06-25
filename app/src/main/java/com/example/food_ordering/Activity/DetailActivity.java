package com.example.food_ordering.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.R;
import com.example.food_ordering.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private Foods object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy dữ liệu từ Intent
        object = (Foods) getIntent().getSerializableExtra("object");

        if (object != null) {
            // Hiển thị thông tin
            binding.titleTxt.setText(object.getTitle());
            binding.priceTxt.setText("$" + object.getPrice().getValue());
            binding.descriptionTxt.setText(object.getDescription());
            binding.rateTxt.setText(String.valueOf(object.getStar()));
            binding.timeTxt.setText(object.getTime().getTimeValue() + " min"); // Sửa ở đây
            Glide.with(this).load(object.getImagePath()).into(binding.pic);

            // Có thể thêm các trường khác như Location, Category, v.v.
        } else {
            // Xử lý khi object null
            binding.titleTxt.setText("Không có dữ liệu");
        }
    }
}