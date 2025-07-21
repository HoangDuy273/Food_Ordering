package com.example.food_ordering.model;

public class UserProfileResponse {
    private boolean success;
    private String message;
    private UserProfile user;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public UserProfile getUser() { return user; }
    public void setUser(UserProfile user) { this.user = user; }
}
