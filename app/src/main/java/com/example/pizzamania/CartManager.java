package com.example.pizzamania;

import java.util.ArrayList;

public class CartManager {
    private static CartManager instance;
    private final ArrayList<CartItem> items = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void add(Pizza p) {
        for (CartItem ci : items) {
            if (ci.pizza.name.equals(p.name)) { ci.qty++; return; }
        }
        items.add(new CartItem(p, 1));
    }

    public void inc(int pos) { items.get(pos).qty++; }
    public void dec(int pos) {
        CartItem ci = items.get(pos);
        ci.qty--;
        if (ci.qty <= 0) items.remove(pos);
    }

    public ArrayList<CartItem> getItems() { return items; }

    public int getTotal() {
        int t=0;
        for (CartItem ci : items) t += ci.lineTotal();
        return t;
    }

    public String itemsSummaryText() {
        StringBuilder sb = new StringBuilder();
        for (CartItem ci : items) {
            sb.append(ci.pizza.name).append(" x").append(ci.qty).append(", ");
        }
        return sb.toString();
    }

    public void clear(){ items.clear(); }
}
