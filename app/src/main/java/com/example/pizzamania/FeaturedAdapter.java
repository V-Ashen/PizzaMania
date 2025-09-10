package com.example.pizzamania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {

    private final List<Integer> bannerImages;

    public FeaturedAdapter(List<Integer> bannerImages) {
        this.bannerImages = bannerImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bannerImage.setImageResource(bannerImages.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerImages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        ViewHolder(View view) {
            super(view);
            bannerImage = view.findViewById(R.id.bannerImageView);
        }
    }
}
