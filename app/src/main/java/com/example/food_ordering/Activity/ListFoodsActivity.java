package com.example.food_ordering.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.food_ordering.Adapter.FoodListAdapter;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.Domain.FoodsResponse;
import com.example.food_ordering.databinding.ActivityListFoodsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFoodsActivity extends BasicActivity {
    private static final String TAG = "ListFoodsActivity";
    private ActivityListFoodsBinding binding;
    private FoodListAdapter adapterListFoods;
    private String categoryId;
    private String categoryName;
    private String searchQuery;
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
        categoryId = getIntent().getStringExtra("CategoryId");
        categoryName = getIntent().getStringExtra("CategoryName");
        searchQuery = getIntent().getStringExtra("searchQuery");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        binding.titleTxt.setText(categoryName != null ? categoryName : "Kết quả tìm kiếm");
        binding.backBtn.setOnClickListener(v -> finish());
        Log.d(TAG, "Received Intent - CategoryId: " + categoryId + ", CategoryName: " + categoryName + ", isSearch: " + isSearch + ", searchQuery: " + searchQuery);
    }

    private void initList() {
        binding.progressBar.setVisibility(View.VISIBLE);

        if (isSearch && searchQuery != null && !searchQuery.trim().isEmpty()) {
            // Chỉ gọi đúng apiService.searchFoods(query)
            searchFoodsByName(searchQuery.trim());
        } else {
            getAllFoods();
        }
    }

    private void getAllFoods() {
        Log.d(TAG, "Fetching all foods with categoryId: " + categoryId);
        Call<FoodsResponse> call = apiService.getAllFoodsObject();
        call.enqueue(new Callback<FoodsResponse>() {
            @Override
            public void onResponse(Call<FoodsResponse> call, Response<FoodsResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getFoods() != null) {
                    List<Foods> allFoods = response.body().getFoods();
                    Log.d(TAG, "Received " + allFoods.size() + " foods from API");
                    List<Foods> filteredFoods = new ArrayList<>();
                    for (Foods food : allFoods) {
                        // Nếu categoryId là null, "0", số, hoặc không phải ObjectId 24 ký tự, hiển thị tất cả
                        if (categoryId == null || categoryId.equals("0") || !categoryId.matches("^[0-9a-fA-F]{24}$")) {
                            filteredFoods.add(food);
                        } else if (categoryId.equals(food.getCategoryId())) {
                            filteredFoods.add(food);
                        }
                    }
                    Log.d(TAG, "Filtered foods count: " + filteredFoods.size() + " for CategoryId " + categoryId + ", Displaying category: " + categoryName);
                    setupRecyclerView(filteredFoods);
                } else {
                    String errorMessage = "Không thể tải danh sách món ăn: " + response.code() + " - " + response.message();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                        errorMessage += "\nChi tiết: " + errorBody;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    handleError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<FoodsResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                handleError("Lỗi kết nối mạng: " + t.getMessage());
                Log.e(TAG, "Error loading foods", t);
            }
        });
    }

    private void searchFoodsByName(String searchQuery) {
        Log.d(TAG, "Searching foods with query: " + searchQuery);
        Call<List<Foods>> call = apiService.searchFoods(searchQuery);
        call.enqueue(new Callback<List<Foods>>() {
            @Override
            public void onResponse(Call<List<Foods>> call, Response<List<Foods>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<Foods> searchResults = response.body();
                    Log.d(TAG, "Raw API response: " + response.body());
                    Log.d(TAG, "Received " + (searchResults != null ? searchResults.size() : 0) + " foods for search query: " + searchQuery);
                    List<Foods> filteredResults = new ArrayList<>();
                    List<Foods> exactMatch = new ArrayList<>();
                    List<Foods> relatedMatches = new ArrayList<>();

                    if (searchResults != null && !searchResults.isEmpty()) {
                        String query = searchQuery.toLowerCase();
                        for (Foods food : searchResults) {
                            // Loại bỏ điều kiện categoryId để hiển thị tất cả kết quả tìm kiếm
                            if (food.getTitle() != null && food.getTitle().toLowerCase().equals(query)) {
                                exactMatch.add(food);
                                Log.d(TAG, "Exact match: " + food.getTitle() + " (CategoryId: " + food.getCategoryId() + ")");
                            } else if ((food.getTitle() != null && food.getTitle().toLowerCase().contains(query)) ||
                                    (food.getDescription() != null && food.getDescription().toLowerCase().contains(query))) {
                                relatedMatches.add(food);
                                Log.d(TAG, "Related match: " + food.getTitle() + " (CategoryId: " + food.getCategoryId() + ")");
                            }
                        }
                        if (!exactMatch.isEmpty()) {
                            filteredResults.addAll(exactMatch);
                            Log.d(TAG, "Displaying " + exactMatch.size() + " exact matches for query: " + searchQuery);
                        } else {
                            filteredResults.addAll(relatedMatches);
                            Log.d(TAG, "No exact match, displaying " + relatedMatches.size() + " related matches for query: " + searchQuery);
                        }
                    }
                    setupRecyclerView(filteredResults);
                    if (filteredResults.isEmpty()) {
                        Toast.makeText(ListFoodsActivity.this, "Không tìm thấy món ăn nào cho: " + searchQuery, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = response.code() == 404 ?
                            "Không tìm thấy món ăn nào cho: " + searchQuery :
                            "Lỗi tìm kiếm: " + response.code() + " - " + response.message();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                        errorMessage += "\nChi tiết: " + errorBody;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    handleError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Foods>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                handleError("Lỗi kết nối mạng khi tìm kiếm: " + t.getMessage());
                Log.e(TAG, "Error searching foods", t);
            }
        });
    }

    private void setupRecyclerView(List<Foods> foodsList) {
        Log.d(TAG, "Setting up RecyclerView with " + (foodsList != null ? foodsList.size() : 0) + " items for CategoryId " + categoryId + ", Displaying category: " + categoryName);
        if (foodsList != null && !foodsList.isEmpty()) {
            adapterListFoods = new FoodListAdapter((ArrayList<Foods>) foodsList);
            binding.foodListView.setLayoutManager(new GridLayoutManager(this, 2));
            binding.foodListView.setAdapter(adapterListFoods);
        } else {
            Toast.makeText(this, "Không có món ăn nào để hiển thị", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
        setupRecyclerView(new ArrayList<>());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}