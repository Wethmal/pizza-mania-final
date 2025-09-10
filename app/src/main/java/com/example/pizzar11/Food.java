package com.example.pizzar11;

public class Food {
    private String name;
    private long price;
    private String image;

    public Food() {}  // Needed for Firebase

    public Food(String name, long price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getName() { return name; }
    public long getPrice() { return price; }
    public String getImage() { return image; }

    public void setName(String name) { this.name = name; }
    public void setPrice(long price) { this.price = price; }
    public void setImage(String image) { this.image = image; }
}
