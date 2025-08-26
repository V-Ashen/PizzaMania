package com.example.pizzamania;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
    ArrayList<CartItem> data;
    Runnable onChange;

    public CartAdapter(ArrayList<CartItem> data, Runnable onChange) {
        this.data = data; this.onChange = onChange;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_cart, p, false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        CartItem ci = data.get(pos);
        h.name.setText(ci.pizza.name);
        h.price.setText(ci.pizza.priceText);
        h.qty.setText(String.valueOf(ci.qty));
        h.line.setText("Line: " + ci.lineTotal() + " LKR");
        h.img.setImageResource(ci.pizza.imageResId);

        h.plus.setOnClickListener(v -> { CartManager.getInstance().inc(h.getAdapterPosition()); notifyDataSetChanged(); onChange.run(); });
        h.minus.setOnClickListener(v -> { CartManager.getInstance().dec(h.getAdapterPosition()); notifyDataSetChanged(); onChange.run(); });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img; TextView name, price, qty, line; Button plus, minus;
        VH(View v){ super(v);
            img=v.findViewById(R.id.imgPizza);
            name=v.findViewById(R.id.txtName);
            price=v.findViewById(R.id.txtPrice);
            qty=v.findViewById(R.id.txtQty);
            line=v.findViewById(R.id.txtLineTotal);
            plus=v.findViewById(R.id.btnPlus);
            minus=v.findViewById(R.id.btnMinus);
        }
    }
}
