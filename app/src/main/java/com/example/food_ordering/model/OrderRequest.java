package com.example.food_ordering.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequest {
    @SerializedName("items")
    private List<OrderItemRequest> items;
    @SerializedName("deliveryAddress")
    private String deliveryAddress;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("notes")
    private String notes;
    @SerializedName("paymentMethod")
    private String paymentMethod;

    public OrderRequest() {}

    public OrderRequest(List<OrderItemRequest> items, String deliveryAddress, String phoneNumber) {
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    // Inner class OrderItemRequest
    public static class OrderItemRequest {
        @SerializedName("id")
        private String id;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("price")
        private double price; // Thêm giá để backend không cần truy xuất food.Price

        public OrderItemRequest() {}

        public OrderItemRequest(String id, int quantity, double price) {
            this.id = id;
            this.quantity = quantity;
            this.price = price;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
}