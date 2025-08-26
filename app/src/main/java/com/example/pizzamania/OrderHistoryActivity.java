package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrderHistoryActivity extends AppCompatActivity {

    TextView txtHistory;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        txtHistory = findViewById(R.id.txtOrderHistory);
        db = new DBHelper(this);

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sp.getString("username", null);

        if (username == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        StringBuilder sb = new StringBuilder();
        Cursor c = db.getOrdersByUser(username);

        if (c.getCount() == 0) {
            txtHistory.setText("No past orders found.");
            return;
        }

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String items = c.getString(c.getColumnIndexOrThrow("items"));
            int cost = c.getInt(c.getColumnIndexOrThrow("totalCost"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            sb.append("Order #").append(id)
                    .append("\nItems: ").append(items)
                    .append("\nCost: ").append(cost).append(" LKR")
                    .append("\nStatus: ").append(status)
                    .append("\n-----------------\n");
        }

        txtHistory.setText(sb.toString());
    }
}
