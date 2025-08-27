package com.example.pizzamania;

public class Review {
    public String customerName;
    public String reviewText;
    public String photoUrl;      // for old Firebase Storage (keep it in case)
    public String photoBase64;   // new Base64 storage
    public String orderId;
    public long createdAt;

    public Review() {
        // Required empty constructor for Firestore
    }
}
