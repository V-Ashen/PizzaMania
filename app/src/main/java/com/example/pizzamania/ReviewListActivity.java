package com.example.pizzamania;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewListActivity extends AppCompatActivity {

    RecyclerView rv;
    FirebaseFirestore firestore;
    ReviewAdapter adapter;
    List<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        rv = findViewById(R.id.reviewRecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList);
        rv.setAdapter(adapter);

        loadReviews();
    }

    private void loadReviews() {
        firestore.collection("reviews")
                .get()
                .addOnSuccessListener(result -> {
                    reviewList.clear();
                    for (QueryDocumentSnapshot doc : result) {
                        try {
                            Review r = doc.toObject(Review.class);
                            reviewList.add(r);
                        } catch (Exception e) {
                            Log.e("ReviewList", "Error parsing review: ", e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load reviews: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
