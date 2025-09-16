package com.example.pizzar11;

public class CartItem {
    String name;
    double price;
    int quantity;
    String imageUrl;

    public CartItem(String name, double price, int quantity, String imageUrl){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
    public String getImageUrl() { return imageUrl; }
    public String getName(){ return name; }
    public double getPrice(){ return price; }
    public int getQuantity(){ return quantity; }

    public void setQuantity(int quantity){ this.quantity = quantity; }
}
