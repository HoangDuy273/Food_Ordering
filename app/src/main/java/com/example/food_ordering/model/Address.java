package com.example.food_ordering.model;

import com.google.gson.annotations.SerializedName;

public class Address implements java.io.Serializable {
    @SerializedName("_id")
    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private boolean isDefault;

    // Getters
    public String getId() { return id; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public boolean isDefault() { return isDefault; }

    // Setters
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
} 