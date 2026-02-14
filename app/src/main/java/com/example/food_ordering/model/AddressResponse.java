package com.example.food_ordering.model;

import java.util.List;

public class AddressResponse {
    private boolean success;
    private String message;
    private List<Address> addresses;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Address> getAddresses() { return addresses; }
} 