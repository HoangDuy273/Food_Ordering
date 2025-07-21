package com.example.food_ordering.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_ordering.R;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.Adapter.FavoriteAdapter;
import com.example.food_ordering.network.FavoriteManager;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity
        implements FavoriteAdapter.OnFavoriteRemovedListener { // Chỉ implement OnFavoriteRemovedListener

    private RecyclerView recyclerViewFavorites;
    private LinearLayout layoutEmptyState;
    private ImageView buttonBackFavorite;
    private FavoriteAdapter favoriteAdapter;
    private List<Foods> favoriteList;
    private FavoriteManager favoriteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initViews();
        loadFavorites();
        setupRecyclerView();
    }

    private void initViews() {
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        buttonBackFavorite = findViewById(R.id.buttonBackFavorite);

        favoriteManager = new FavoriteManager(this);

        buttonBackFavorite.setOnClickListener(v -> finish());
    }

    private void loadFavorites() {
        favoriteList = favoriteManager.getFavorites();
    }

    private void setupRecyclerView() {
        if (favoriteList.isEmpty()) {
            showEmptyState();
        } else {
            showFavoriteList();
        }
    }

    private void showEmptyState() {
        recyclerViewFavorites.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void showFavoriteList() {
        recyclerViewFavorites.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);

        if (favoriteList == null) {
            favoriteList = new ArrayList<>();
        }

        // Truyền 'this' cho OnFavoriteRemovedListener
        favoriteAdapter = new FavoriteAdapter(this, favoriteList, this);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFavorites.setAdapter(favoriteAdapter);
    }

    @Override
    public void onFavoriteRemoved(int position) {
        if (position >= 0 && position < favoriteList.size()) {
            Foods removedFood = favoriteList.get(position);
            String foodName = removedFood.getTitle();

            favoriteManager.removeFromFavorite(removedFood.getId());

            favoriteList.remove(position);
            favoriteAdapter.notifyItemRemoved(position);

            Toast.makeText(this, foodName + " đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();

            if (favoriteList.isEmpty()) {
                showEmptyState();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
        setupRecyclerView();
    }
}