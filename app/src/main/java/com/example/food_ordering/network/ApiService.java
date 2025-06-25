package com.example.food_ordering.network;

import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.Domain.Location;
import com.example.food_ordering.Domain.Price;
import com.example.food_ordering.Domain.Time;
import com.example.food_ordering.model.LoginRequest;
import com.example.food_ordering.model.LoginResponse;
import com.example.food_ordering.model.RegisterRequest;
import com.example.food_ordering.model.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Authentication endpoints
    @POST("/api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/auth/logout")
    Call<Void> logout(@Header("Authorization") String token);

    // Food endpoints
    @GET("/api/foods/best")
    Call<List<Foods>> getBestFoods();

    @GET("/api/foods")
    Call<List<Foods>> getAllFoods();

    @GET("/api/foods/{id}")
    Call<Foods> getFoodById(@Path("id") String foodId);

    @GET("/api/foods/search")
    Call<List<Foods>> searchFoods(@Query("query") String searchQuery);

    // Location endpoints
    @GET("/api/locations")
    Call<List<Location>> getLocations();

    // Time slots endpoints
    @GET("/api/times")
    Call<List<Time>> getTimes();

    // Price range endpoints
    @GET("/api/prices")
    Call<List<Price>> getPrices();
}