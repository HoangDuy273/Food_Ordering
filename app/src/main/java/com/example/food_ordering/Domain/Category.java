package com.example.food_ordering.Domain;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private String icon;
    private int backgroundColor;

    public Category() {
    }

    public Category(int id, String name, String icon, int backgroundColor) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.backgroundColor = backgroundColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return name;
    }
}