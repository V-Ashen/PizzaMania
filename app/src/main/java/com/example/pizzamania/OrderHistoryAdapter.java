package com.example.pizzamania;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<String> orders;
    private Context context;

    public OrderHistoryAdapter(List<String> orders, Context ctx) {
        this.orders = orders;
        this.context = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        TextView txtOrder = new TextView(context);
        txtOrder.setId(View.generateViewId());
        txtOrder.setTextSize(16f);
        txtOrder.setTextColor(context.getResources().getColor(android.R.color.black));

        Button btnReview = new Button(context);
        btnReview.setId(View.generateViewId());
        btnReview.setText("Add Review");

        layout.addView(txtOrder);
        layout.addView(btnReview);

        return new ViewHolder(layout, txtOrder, btnReview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String raw = orders.get(position);

        // Handle case: "No past orders"
        if (raw.startsWith("No past orders")) {
            holder.txtOrder.setText(raw);
            holder.btnReview.setVisibility(View.GONE);
            return;
        }

        String[] parts = raw.split("\\|");
        String details = parts[0];
        String orderId = parts[1];

        holder.txtOrder.setText(details);

        holder.btnReview.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ReviewActivity.class);
            i.putExtra("orderId", orderId);
            v.getContext().startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrder;
        Button btnReview;

        ViewHolder(View itemView, TextView txtOrder, Button btnReview) {
            super(itemView);
            this.txtOrder = txtOrder;
            this.btnReview = btnReview;
        }
    }
}
