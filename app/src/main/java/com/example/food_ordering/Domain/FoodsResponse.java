package com.example.food_ordering.Domain;

import java.util.List;

public class FoodsResponse {
    public boolean success;
    public List<Foods> foods;
    // ... có thể có các trường khác như pagination
    public List<Foods> getFoods() { return foods; }
} 