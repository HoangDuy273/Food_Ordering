package com.example.food_ordering.model;
import java.util.List;

public class CouponsResponse {
    public boolean success;
    public List<Coupon> coupons;
    public List<Coupon> getCoupons() { return coupons; }
} 