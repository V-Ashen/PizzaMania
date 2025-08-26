package com.example.pizzamania;

public class Pizza {
    public String name;
    public String priceText;   // "1200 LKR"
    public int priceValue;     // 1200
    public int imageResId;

    public Pizza(String name, String priceText, int priceValue, int imageResId) {
        this.name = name;
        this.priceText = priceText;
        this.priceValue = priceValue;
        this.imageResId = imageResId;
    }
}
