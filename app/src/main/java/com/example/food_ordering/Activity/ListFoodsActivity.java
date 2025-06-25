package com.example.food_ordering.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.food_ordering.Adapter.FoodListAdapter;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.databinding.ActivityListFoodsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFoodsActivity extends BasicActivity {
    private ActivityListFoodsBinding binding;
    private FoodListAdapter adapterListFoods;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("searchText");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        binding.titleTxt.setText(categoryName != null ? categoryName : "Foods");
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initList() {
        binding.progressBar.setVisibility(View.VISIBLE);

        if (isSearch && searchText != null && !searchText.trim().isEmpty()) {
            searchFoodsByName(searchText.trim());
        } else {
            getAllFoods();
        }
    }

    private void getAllFoods() {
        Call<List<Foods>> call = apiService.getAllFoods();
        call.enqueue(new Callback<List<Foods>>() {
            @Override
            public void onResponse(Call<List<Foods>> call, Response<List<Foods>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Foods> allFoods = response.body();
                    List<Foods> filteredFoods = new ArrayList<>();

                    for (Foods food : allFoods) {
                        if (!isSearch || categoryId == 0 || String.valueOf(categoryId).equals(food.getCategory())) {
                            filteredFoods.add(food);
                        }
                    }

                    setupRecyclerView(filteredFoods);
                } else {
                    handleError("Failed to load foods: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Foods>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                handleError("Network error: " + t.getMessage());
                Log.e("ListFoodsActivity", "Error loading foods", t);
            }
        });
    }

    private void searchFoodsByName(String searchQuery) {
        Call<List<Foods>> call = apiService.getAllFoods();
        call.enqueue(new Callback<List<Foods>>() {
            @Override
            public void onResponse(Call<List<Foods>> call, Response<List<Foods>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Foods> allFoods = response.body();
                    List<Foods> searchResults = new ArrayList<>();

                    String query = searchQuery.toLowerCase();
                    for (Foods food : allFoods) {
                        if ((food.getTitle() != null && food.getTitle().toLowerCase().contains(query)) ||
                                (food.getDescription() != null && food.getDescription().toLowerCase().contains(query))) {
                            searchResults.add(food);
                        }
                    }

                    setupRecyclerView(searchResults);

                    if (searchResults.isEmpty()) {
                        Toast.makeText(ListFoodsActivity.this, "No foods found for: " + searchQuery, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleError("Failed to search foods: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Foods>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                handleError("Search failed: " + t.getMessage());
                Log.e("ListFoodsActivity", "Error searching foods", t);
            }
        });
    }

    private void setupRecyclerView(List<Foods> foodsList) {
        if (foodsList != null && !foodsList.isEmpty()) {
            adapterListFoods = new FoodListAdapter((ArrayList<Foods>) foodsList);
            binding.foodListView.setLayoutManager(new GridLayoutManager(this, 2));
            binding.foodListView.setAdapter(adapterListFoods);
        } else {
            Toast.makeText(this, "No foods available", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e("ListFoodsActivity", message);
        setupRecyclerView(new ArrayList<>());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
