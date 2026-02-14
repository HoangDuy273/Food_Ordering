package com.example.food_ordering.Domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Foods implements Serializable {
    @SerializedName("_id")
    private String Id;
    @SerializedName("BestFood")
    private boolean BestFood;
    @SerializedName("CategoryId")
    private String CategoryId;
    @SerializedName("Description")
    private String Description;
    @SerializedName("ImagePath")
    private String ImagePath;
    @SerializedName("Location")
    private Location Location;
    @SerializedName("Price")
    private Price Price;
    @SerializedName("Star")
    private double Star;
    @SerializedName("Time")
    private Time Time;
    @SerializedName("Title")
    private String Title;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;

    public Foods() {}

    public Foods(String id, boolean bestFood, String categoryId, String description, String imagePath,
                 Location location, Price price, double star, Time time, String title,
                 String createdAt, String updatedAt) {
        this.Id = id;
        this.BestFood = bestFood;
        this.CategoryId = categoryId;
        this.Description = description;
        this.ImagePath = imagePath;
        this.Location = location;
        this.Price = price;
        this.Star = star;
        this.Time = time;
        this.Title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return Id; }
    public void setId(String id) { this.Id = id; }
    public boolean isBestFood() { return BestFood; }
    public void setBestFood(boolean bestFood) { this.BestFood = bestFood; }
    public String getCategoryId() { return CategoryId; }
    public void setCategoryId(String categoryId) { this.CategoryId = categoryId; }
    public String getDescription() { return Description; }
    public void setDescription(String description) { this.Description = description; }
    public String getImagePath() { return ImagePath; }
    public void setImagePath(String imagePath) { this.ImagePath = imagePath; }
    public Location getLocation() { return Location; }
    public void setLocation(Location location) { this.Location = location; }
    public Price getPrice() { return Price; }
    public void setPrice(Price price) { this.Price = price; }
    public double getStar() { return Star; }
    public void setStar(double star) { this.Star = star; }
    public Time getTime() { return Time; }
    public void setTime(Time time) { this.Time = time; }
    public String getTitle() { return Title; }
    public void setTitle(String title) { this.Title = title; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Alias
    public String getName() { return Title; }
    public String getImageUrl() { return ImagePath; }
    public String getCategory() { return CategoryId; }
    public void setPic(String imageUrl) { this.ImagePath = imageUrl; }

    // Nested classes
    public static class Location implements Serializable {
        @SerializedName("loc")
        private String loc;

        public String getLoc() { return loc; }
        public void setLoc(String loc) { this.loc = loc; }
    }

    public static class Price implements Serializable {
        @SerializedName("Value")
        private double Value;
        @SerializedName("Range")
        private String Range;

        public double getValue() { return Value; }
        public void setValue(double value) { this.Value = value; }
        public String getRange() { return Range; }
        public void setRange(String range) { this.Range = range; }
    }

    public static class Time implements Serializable {
        @SerializedName("Value")
        private String Value;

        public String getValue() { return Value; }
        public void setValue(String value) { this.Value = value; }
    }
}