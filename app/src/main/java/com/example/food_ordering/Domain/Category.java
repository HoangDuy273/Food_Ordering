package com.example.food_ordering.Domain;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private String icon;
    private int drawableRes; // Thêm field cho drawable resource
    private int backgroundColor;

    public Category() {
    }

    // Constructor cũ (cho emoji)
    public Category(int id, String name, String icon, int backgroundColor) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.drawableRes = 0;
        this.backgroundColor = backgroundColor;
    }

    // Constructor mới (cho drawable resource)
    public Category(int id, String name, String icon, int drawableRes, int backgroundColor) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.drawableRes = drawableRes;
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

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    // Helper methods
    public boolean hasDrawable() {
        return drawableRes != 0;
    }

    public boolean hasIcon() {
        return icon != null && !icon.trim().isEmpty();
    }

    @Override
    public String toString() {
        return name;
    }
}