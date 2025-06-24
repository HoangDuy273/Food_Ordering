package model;

public class Food {
    private String name;
    private String price;
    private String time;
    private float rating;
    private int imageResource;

    public Food(String name, String price, String time, float rating, int imageResource) {
        this.name = name;
        this.price = price;
        this.time = time;
        this.rating = rating;
        this.imageResource = imageResource;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getImageResource() { return imageResource; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }
}