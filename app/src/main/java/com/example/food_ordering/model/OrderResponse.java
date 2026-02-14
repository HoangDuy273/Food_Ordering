package com.example.food_ordering.model;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private OrderData data;
    @SerializedName("message")
    private String message;

    public OrderResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public OrderData getData() {
        return data;
    }

    public void setData(OrderData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class OrderData {
        @SerializedName("orderId")
        private String orderId;
        @SerializedName("status")
        private String status;
        @SerializedName("totalAmount")
        private double totalAmount;
        @SerializedName("estimatedDeliveryTime")
        private String estimatedDeliveryTime;

        public OrderData() {}

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

        public String getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
        public void setEstimatedDeliveryTime(String estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    }
}