package com.example.pizzamania;

public class CartItem {
    public Pizza pizza;
    public int qty;

    public CartItem(Pizza pizza, int qty) {
        this.pizza = pizza;
        this.qty = qty;
    }

    public int lineTotal() {
        return pizza.priceValue * qty;
    }
}
