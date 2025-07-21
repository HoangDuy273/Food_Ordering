package com.example.food_ordering.network;

import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.model.CartResponse;
import com.example.food_ordering.model.LoginRequest;
import com.example.food_ordering.model.LoginResponse;
import com.example.food_ordering.model.OrderRequest;
import com.example.food_ordering.model.OrderResponse;
import com.example.food_ordering.model.RegisterRequest;
import com.example.food_ordering.model.RegisterResponse;
import com.example.food_ordering.model.UpdateProfileRequest;
import com.example.food_ordering.model.UserProfile;
import com.example.food_ordering.model.UserProfileResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.example.food_ordering.model.ChangePasswordRequest;
import com.example.food_ordering.model.ChangePasswordResponse;
import com.example.food_ordering.model.ForgotPasswordRequest;
import com.example.food_ordering.model.ForgotPasswordResponse;
import com.example.food_ordering.model.ResetPasswordRequest;
import com.example.food_ordering.model.ResetPasswordResponse;
import com.example.food_ordering.model.AddressResponse;
import com.example.food_ordering.model.Address;

import java.util.Map;

public interface ApiService {
    // My Profile
    @GET("/api/users/profile")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);
    
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

    // Cart endpoints
    @GET("/api/cart")
    Call<CartResponse> getCart(@Header("Authorization") String token);

    @POST("/api/cart")
    Call<CartItemResponse> addToCart(@Header("Authorization") String token, @Body CartItemRequest request);

    @PUT("/api/cart/{id}")
    Call<CartItemResponse> updateCartItem(@Header("Authorization") String token, @Path("id") String cartId, @Body CartItemRequest request);

    @DELETE("/api/cart/{id}")
    Call<Void> removeFromCart(@Header("Authorization") String token, @Path("id") String cartId);

    // Order endpoints
    @POST("/api/orders")
    Call<OrderResponse> placeOrder(@Header("Authorization") String token, @Body OrderRequest request);

    @GET("/api/orders")
    Call<List<OrderResponse>> getOrders(@Header("Authorization") String token);

    @PUT("/api/users/profile")
    Call<UserProfileResponse> updateUserProfile(
        @Header("Authorization") String token,
        @Body UpdateProfileRequest request
    );

    @PUT("/api/users/change-password")
    Call<ChangePasswordResponse> changePassword(
        @Header("Authorization") String token,
        @Body ChangePasswordRequest request
    );

    @POST("/api/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("/api/auth/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

    @GET("/api/users/addresses")
    Call<AddressResponse> getAddresses(@Header("Authorization") String token);

    @PUT("/api/users/address/{addressId}")
    Call<AddressResponse> updateAddress(@Header("Authorization") String token, @Path("addressId") String addressId, @Body Address address);

    @DELETE("/api/users/address/{addressId}")
    Call<AddressResponse> deleteAddress(@Header("Authorization") String token, @Path("addressId") String addressId);

    @PUT("/api/users/address/default/{addressId}")
    Call<AddressResponse> setDefaultAddress(@Header("Authorization") String token, @Path("addressId") String addressId);

    @POST("/api/users/address")
    Call<AddressResponse> addAddress(@Header("Authorization") String token, @Body Map<String, Object> address);

    // Lớp CartItemRequest để gửi dữ liệu lên backend
    class CartItemRequest {
        private String foodId;
        private int quantity;

        public CartItemRequest(String foodId, int quantity) {
            this.foodId = foodId;
            this.quantity = quantity;
        }

        public String getFoodId() {
            return foodId;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    // Lớp CartItemResponse để nhận dữ liệu từ backend
    class CartItemResponse {
        @com.google.gson.annotations.SerializedName("_id")
        private String id;
        private Food food;
        private int quantity;
        private String user;
        private String createdAt;
        private String updatedAt;

        public String getId() {
            return id;
        }

        public Food getFood() {
            return food;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getUser() {
            return user;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public static class Food {
            @com.google.gson.annotations.SerializedName("_id")
            private String id;
            @com.google.gson.annotations.SerializedName("Title")
            private String title;
            @com.google.gson.annotations.SerializedName("Price")
            private Price price;
            @com.google.gson.annotations.SerializedName("ImagePath")
            private String image;
            @com.google.gson.annotations.SerializedName("BestFood")
            private boolean bestFood;
            @com.google.gson.annotations.SerializedName("CategoryId")
            private int categoryId;
            @com.google.gson.annotations.SerializedName("Description")
            private String description;
            @com.google.gson.annotations.SerializedName("Star")
            private double star;
            @com.google.gson.annotations.SerializedName("Time")
            private Time time;
            @com.google.gson.annotations.SerializedName("Location")
            private Location location;

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public Price getPrice() {
                return price;
            }

            public String getImage() {
                return image;
            }

            public boolean isBestFood() {
                return bestFood;
            }

            public int getCategoryId() {
                return categoryId;
            }

            public String getDescription() {
                return description;
            }

            public double getStar() {
                return star;
            }

            public Time getTime() {
                return time;
            }

            public Location getLocation() {
                return location;
            }

            public static class Price {
                @com.google.gson.annotations.SerializedName("Value")
                private double value;
                @com.google.gson.annotations.SerializedName("Range")
                private String range;

                public double getValue() {
                    return value;
                }

                public String getRange() {
                    return range;
                }
            }

            public static class Time {
                @com.google.gson.annotations.SerializedName("Id")
                private int id;
                @com.google.gson.annotations.SerializedName("Value")
                private String value;
                @com.google.gson.annotations.SerializedName("TimeValue")
                private int timeValue;

                public int getId() {
                    return id;
                }

                public String getValue() {
                    return value;
                }

                public int getTimeValue() {
                    return timeValue;
                }
            }

            public static class Location {
                @com.google.gson.annotations.SerializedName("Id")
                private int id;
                @com.google.gson.annotations.SerializedName("loc")
                private String loc;

                public int getId() {
                    return id;
                }

                public String getLoc() {
                    return loc;
                }
            }
        }
    }
}