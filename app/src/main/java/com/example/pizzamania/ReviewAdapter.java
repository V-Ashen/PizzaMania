package com.example.pizzamania;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewVH> {

    List<Review> list;

    public ReviewAdapter(List<Review> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewVH h, int i) {
        Review r = list.get(i);
        h.name.setText(r.customerName);
        h.text.setText(r.reviewText);

        if (r.photoBase64 != null && !r.photoBase64.isEmpty()) {
            // Decode Base64 → Bitmap
            byte[] decodedBytes = Base64.decode(r.photoBase64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            h.photo.setImageBitmap(bmp);
        } else if (r.photoUrl != null && !r.photoUrl.isEmpty()) {
            // If you ever use Firebase Storage URLs
            Glide.with(h.itemView.getContext()).load(r.photoUrl).into(h.photo);
        } else {
            h.photo.setImageResource(android.R.color.darker_gray); // placeholder
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ReviewVH extends RecyclerView.ViewHolder {
        TextView name, text;
        ImageView photo;

        ReviewVH(View v) {
            super(v);
            name = v.findViewById(R.id.txtCustomerName);
            text = v.findViewById(R.id.txtReview);
            photo = v.findViewById(R.id.imgReview);
        }
    }
}
