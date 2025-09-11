package com.example.pizzamania;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StaffMenuAdapter extends BaseAdapter {

    public static class MenuItemRow {
        public int id;
        public String name;
        public int price;
        public int imageResId;
        public boolean available;

        public MenuItemRow(int id, String name, int price, int imageResId, boolean available) {
            this.id = id; this.name = name; this.price = price; this.imageResId = imageResId; this.available = available;
        }
    }

    private Context ctx;
    private ArrayList<MenuItemRow> list;
    private DBHelper db;

    public StaffMenuAdapter(Context ctx, ArrayList<MenuItemRow> list) {
        this.ctx = ctx;
        this.list = list;
        this.db = new DBHelper(ctx);
    }

    public void update(ArrayList<MenuItemRow> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override public int getCount() { return list.size(); }
    @Override public Object getItem(int position) { return list.get(position); }
    @Override public long getItemId(int position) { return list.get(position).id; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuItemRow row = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_staff_menu, parent, false);
        }
        TextView txtName = convertView.findViewById(R.id.txtMenuName);
        TextView txtPrice = convertView.findViewById(R.id.txtMenuPrice);
        ImageView img = convertView.findViewById(R.id.imgMenu);
        Switch sw = convertView.findViewById(R.id.switchAvailable);

        txtName.setText(row.name);
        txtPrice.setText(row.price + " LKR");
        img.setImageResource(row.imageResId);
        sw.setChecked(row.available);

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.setMenuAvailability(row.id, isChecked);
            row.available = isChecked;
            Toast.makeText(ctx, row.name + (isChecked ? " set Available" : " set Unavailable"), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
