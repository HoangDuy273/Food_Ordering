package com.example.food_ordering.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_ordering.R;
import com.example.food_ordering.model.OrderResponse;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderResponse> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderResponse order);
    }

    public OrderAdapter(List<OrderResponse> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse order = orders.get(position);
        holder.orderId.setText("Order ID: " + order.getData().getOrderId());
        holder.orderStatus.setText("Status: " + order.getData().getStatus());
        holder.orderTotal.setText("Total: $" + String.format("%.2f", order.getData().getTotalAmount()));
        holder.orderDeliveryTime.setText("Estimated Delivery: " + order.getData().getEstimatedDeliveryTime());

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus, orderTotal, orderDeliveryTime;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderDeliveryTime = itemView.findViewById(R.id.orderDeliveryTime);
        }
    }
} 