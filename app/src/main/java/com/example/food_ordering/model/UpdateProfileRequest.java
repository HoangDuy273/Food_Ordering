package com.example.food_ordering.model;

public class UpdateProfileRequest {
    private String name;
    private String email;

    public UpdateProfileRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // Getter, Setter nếu cần
}
