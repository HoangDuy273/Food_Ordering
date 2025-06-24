package com.example.food_ordering;

import android.os.Bundle;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import adapter.CategoryAdapter;
import adapter.FoodAdapter;
import model.Category;
import model.Food;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFoods;
    private GridView gridViewCategories;
    private FoodAdapter foodAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupFoodRecyclerView();
        setupCategoryGridView();
    }

    private void initViews() {
        recyclerViewFoods = findViewById(R.id.recyclerViewFoods);
        gridViewCategories = findViewById(R.id.gridViewCategories);
    }

    private void setupFoodRecyclerView() {
        List<Food> foodList = new ArrayList<>();
        foodList.add(new Food("Pizza Paradise", "$11.99", "17 min", 4.5f, R.drawable.pizza_image));
        foodList.add(new Food("Classic Beef Burger", "$8.99", "15 min", 4.5f, R.drawable.burger_image));

        foodAdapter = new FoodAdapter(this, foodList);
        recyclerViewFoods.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFoods.setAdapter(foodAdapter);
    }

    private void setupCategoryGridView() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Pizza", "🍕", getResources().getColor(R.color.purple_category)));
        categoryList.add(new Category("Burger", "🍔", getResources().getColor(R.color.blue_category)));
        categoryList.add(new Category("Chicken", "🍗", getResources().getColor(R.color.orange_category)));
        categoryList.add(new Category("Sushi", "🍣", getResources().getColor(R.color.green_category)));
        categoryList.add(new Category("Meat", "🥩", getResources().getColor(R.color.pink_category)));
        categoryList.add(new Category("Hotdog", "🌭", getResources().getColor(R.color.green_category)));
        categoryList.add(new Category("Drink", "🥤", getResources().getColor(R.color.purple_category)));
        categoryList.add(new Category("More", "➕", getResources().getColor(R.color.blue_category)));

        categoryAdapter = new CategoryAdapter(this, categoryList);
        gridViewCategories.setAdapter(categoryAdapter);
    }
}