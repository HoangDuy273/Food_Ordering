package com.example.food_ordering.model;

import com.example.food_ordering.network.ApiService;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<ApiService.CartItemResponse> data;

    public CartResponse() {}

    public CartResponse(boolean success, List<ApiService.CartItemResponse> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ApiService.CartItemResponse> getData() {
        return data;
    }

    public void setData(List<ApiService.CartItemResponse> data) {
        this.data = data;
    }
}