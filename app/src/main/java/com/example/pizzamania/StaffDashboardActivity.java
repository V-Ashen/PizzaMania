package com.example.pizzamania;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class StaffDashboardActivity extends AppCompatActivity {

    DBHelper db;
    ArrayAdapter<String> adapter;
    ArrayList<Long> ids = new ArrayList<>();

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_staff_dashboard);
        db = new DBHelper(this);
        ListView lv = findViewById(R.id.listPending);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            long orderId = ids.get(position);
            db.updateStatus(orderId, "Approved");
            Toast.makeText(this, "Approved order #" + orderId, Toast.LENGTH_SHORT).show();
            load();
        });

        load();
    }

    private void load() {
        adapter.clear(); ids.clear();
        Cursor c = db.getOrdersByStatus("Pending");
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow("id"));
            String items = c.getString(c.getColumnIndexOrThrow("items"));
            int total = c.getInt(c.getColumnIndexOrThrow("totalCost"));
            adapter.add("#"+id+" | "+items+" | "+total+" LKR (tap to Approve)");
            ids.add(id);
        }
        adapter.notifyDataSetChanged();
    }
}
