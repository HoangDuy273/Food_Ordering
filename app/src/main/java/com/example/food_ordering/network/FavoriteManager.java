package com.example.food_ordering.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.food_ordering.Domain.Foods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static final String PREF_NAME = "FavoritePrefs";
    private static final String KEY_FAVORITES = "favorites";
    private SharedPreferences prefs;
    private Gson gson;

    public FavoriteManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Thêm món ăn vào danh sách yêu thích
    public void addToFavorite(Foods food) {
        List<Foods> favorites = getFavorites();

        // Kiểm tra xem món ăn đã có trong danh sách chưa
        boolean exists = false;
        for (Foods f : favorites) {
            if (f.getId().equals(food.getId())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            favorites.add(food);
            saveFavorites(favorites);
        }
    }

    // Xóa món ăn khỏi danh sách yêu thích
    public void removeFromFavorite(String foodId) {
        List<Foods> favorites = getFavorites();
        favorites.removeIf(food -> food.getId().equals(foodId));
        saveFavorites(favorites);
    }

    // Kiểm tra món ăn có trong danh sách yêu thích không
    public boolean isFavorite(String foodId) {
        List<Foods> favorites = getFavorites();
        for (Foods food : favorites) {
            if (food.getId().equals(foodId)) {
                return true;
            }
        }
        return false;
    }

    // Lấy danh sách yêu thích
    public List<Foods> getFavorites() {
        String json = prefs.getString(KEY_FAVORITES, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Foods>>(){}.getType();
        List<Foods> favorites = gson.fromJson(json, type);
        return favorites != null ? favorites : new ArrayList<>();
    }

    // Lưu danh sách yêu thích
    private void saveFavorites(List<Foods> favorites) {
        String json = gson.toJson(favorites);
        prefs.edit().putString(KEY_FAVORITES, json).apply();
    }

    // Xóa tất cả yêu thích
    public void clearFavorites() {
        prefs.edit().remove(KEY_FAVORITES).apply();
    }
}