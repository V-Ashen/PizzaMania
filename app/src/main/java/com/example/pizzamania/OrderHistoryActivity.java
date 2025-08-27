package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerOrders;
    DBHelper db;
    List<String> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        db = new DBHelper(this);

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sp.getString("username", null);

        if (username == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        orderList = new ArrayList<>();
        Cursor c = db.getOrdersByUser(username);

        if (c.getCount() == 0) {
            orderList.add("No past orders found.");
        } else {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String items = c.getString(c.getColumnIndexOrThrow("items"));
                int cost = c.getInt(c.getColumnIndexOrThrow("totalCost"));
                String status = c.getString(c.getColumnIndexOrThrow("status"));
                String orderDetails = "Order #" + id +
                        "\nItems: " + items +
                        "\nCost: " + cost + " LKR" +
                        "\nStatus: " + status;
                orderList.add(orderDetails + "|" + id); // store orderId too
            }
        }

        OrderHistoryAdapter adapter = new OrderHistoryAdapter(orderList, this);
        recyclerOrders.setAdapter(adapter);
    }
}
