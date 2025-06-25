package com.example.food_ordering.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.food_ordering.Adapter.BestFoodsAdapter;
import com.example.food_ordering.Adapter.CategoryAdapter;
import com.example.food_ordering.Domain.Category;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.R;
import com.example.food_ordering.databinding.ActivityMainBinding;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getApiService();

        initBestFoods();
        initCategories();
    }

    private void initBestFoods() {
        apiService.getBestFoods().enqueue(new Callback<List<Foods>>() {
            @Override
            public void onResponse(Call<List<Foods>> call, Response<List<Foods>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Foods> list = response.body();
                    Log.d("API", "Danh sách foods: " + (list != null ? list.size() : "null"));
                    if (list != null && !list.isEmpty()) {
                        binding.recyclerViewFoods.setLayoutManager(new LinearLayoutManager(
                                MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        BestFoodsAdapter adapter = new BestFoodsAdapter(new ArrayList<>(list));
                        binding.recyclerViewFoods.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Không có món ăn nào để hiển thị", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "Lỗi API: " + response.code() + " - " + response.message());
                    Log.e("API", "Error body: " + (response.errorBody() != null ? response.errorBody().toString() : "No error body"));
                    Toast.makeText(MainActivity.this, "Lấy dữ liệu món ăn thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Foods>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Pizza", "🍕", android.graphics.Color.parseColor("#FFE6EEFD")));
        categories.add(new Category(2, "Burger", "🍔", android.graphics.Color.parseColor("#FFF3E0FD")));
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        binding.gridViewCategories.setAdapter(adapter);
    }
}