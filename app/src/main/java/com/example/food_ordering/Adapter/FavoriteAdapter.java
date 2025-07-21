package com.example.food_ordering.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.food_ordering.R;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.Activity.DetailActivity; // Đảm bảo import đúng

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private final Context context;
    private final List<Foods> favoriteList;
    private final OnFavoriteRemovedListener listener;

    // Interface cho sự kiện xóa
    public interface OnFavoriteRemovedListener {
        void onFavoriteRemoved(int position);
    }

    // Constructor
    public FavoriteAdapter(Context context, List<Foods> favoriteList, OnFavoriteRemovedListener listener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Foods food = favoriteList.get(position);

        // Set food name (Title)
        holder.textFoodName.setText(food.getTitle());

        // Set price (from Price object)
        if (food.getPrice() != null) {
            holder.textPrice.setText("$" + String.format("%.2f", food.getPrice().getValue()));
        }

        // Set time (from Time object)
        if (food.getTime() != null) {
            holder.textTime.setText(food.getTime().getValue());
        }

        // Set rating (Star)
        holder.textRating.setText(String.valueOf(food.getStar()));

        // Load image using Glide
        if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
            Glide.with(context)
                    .load(food.getImagePath())
                    .placeholder(R.drawable.placeholder_food) // Đảm bảo bạn có placeholder_food.xml
                    .error(R.drawable.placeholder_food)
                    .into(holder.imageFood);
        } else {
            holder.imageFood.setImageResource(R.drawable.placeholder_food);
        }

        // Click vào toàn bộ item để mở trang chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", food); // Key để truyền Foods object
            context.startActivity(intent);
        });

        // Click vào nút xóa khỏi favorites
        holder.buttonRemoveFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteRemoved(holder.getAdapterPosition()); // Sử dụng getAdapterPosition()
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView textFoodName, textPrice, textTime, textRating;
        ImageView imageFood, buttonRemoveFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            textFoodName = itemView.findViewById(R.id.textFoodName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textTime = itemView.findViewById(R.id.textTime);
            textRating = itemView.findViewById(R.id.textRating);
            imageFood = itemView.findViewById(R.id.imageFood);
            buttonRemoveFavorite = itemView.findViewById(R.id.buttonRemoveFavorite);
        }
    }
}