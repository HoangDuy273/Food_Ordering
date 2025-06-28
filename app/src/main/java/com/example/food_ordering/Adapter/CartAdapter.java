package com.example.food_ordering.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.food_ordering.CartActivity;
import com.example.food_ordering.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartActivity.CartItem> cartItems;
    private CartActivity.OnQuantityChangedListener onQuantityChangedListener;
    private CartActivity.OnRemoveItemListener onRemoveItemListener;
    private Context context;

    public CartAdapter(List<CartActivity.CartItem> cartItems, CartActivity.OnQuantityChangedListener listener, CartActivity.OnRemoveItemListener removeListener) {
        this.cartItems = cartItems;
        this.onQuantityChangedListener = listener;
        this.onRemoveItemListener = removeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartActivity.CartItem item = cartItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(String.format("%d x $%.2f", item.getQuantity(), item.getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        holder.totalPrice.setText(String.format("$%.2f", item.getPrice() * item.getQuantity()));

        // Load image with Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.placeholder_food);
        }

        holder.minusBtn.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                if (onQuantityChangedListener != null) {
                    onQuantityChangedListener.onQuantityChanged();
                }
            } else {
                if (onRemoveItemListener != null) {
                    onRemoveItemListener.onRemoveItem(item, position);
                }
            }
        });

        holder.plusBtn.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (onQuantityChangedListener != null) {
                onQuantityChangedListener.onQuantityChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemPrice;
        TextView quantityText;
        TextView totalPrice;
        ImageButton minusBtn;
        ImageButton plusBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            plusBtn = itemView.findViewById(R.id.plusBtn);
        }
    }
}