package com.example.pizzar11;

public class Food {
//    private String id;
    private String name;
    private long price;
    private String image;

    public Food() {}  // Needed for Firebase

    public Food(String id,String name, long price, String image) {
//        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public long getPrice() { return price; }
    public String getImage() { return image; }

    public void setName(String name) { this.name = name; }
    public void setPrice(long price) { this.price = price; }
    public void setImage(String image) { this.image = image; }
}
