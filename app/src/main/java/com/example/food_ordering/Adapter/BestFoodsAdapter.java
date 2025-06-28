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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.food_ordering.Activity.DetailActivity;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.R;

import java.util.ArrayList;

public class BestFoodsAdapter extends RecyclerView.Adapter<BestFoodsAdapter.ViewHolder> {
    private ArrayList<Foods> items;
    private Context context;
    private OnFoodClickListener listener; // Thêm listener

    // Interface cho sự kiện click
    public interface OnFoodClickListener {
        void onFoodClick(Foods food);
    }

    // Constructor với list và listener
    public BestFoodsAdapter(ArrayList<Foods> items, OnFoodClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_best_deal, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foods food = items.get(position);

        holder.titleTxt.setText(food.getTitle());

        // Hiển thị giá
        if (food.getPrice() != null) {
            holder.priceTxt.setText("$" + String.format("%.2f", food.getPrice().getValue()));
        } else {
            holder.priceTxt.setText("N/A");
        }

        // Hiển thị thời gian
        if (food.getTime() != null) {
            holder.timeTxt.setText(food.getTime().getValue());
        } else {
            holder.timeTxt.setText("N/A");
        }

        // Hiển thị sao
        holder.starTxt.setText(String.valueOf(food.getStar()));

        // Load ảnh
        Glide.with(context)
                .load(food.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodClick(food);
            } else {
                // Fallback: Mở DetailActivity trực tiếp nếu không có listener
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("object", food);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, starTxt, timeTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            starTxt = itemView.findViewById(R.id.starTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}