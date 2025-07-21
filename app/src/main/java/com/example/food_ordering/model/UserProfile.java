package com.example.food_ordering.model;

public class UserProfile {
    private String _id;
    private String name;
    private String email;
    private String createdAt;

    // Getter, Setter
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
