package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TrackOrderActivity extends AppCompatActivity {

    TextView txtTrack;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        txtTrack = findViewById(R.id.txtTrackStatus);
        db = new DBHelper(this);

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sp.getString("username", null);

        if (username == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Cursor c = db.getLatestOrder(username);
        if (c != null && c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            txtTrack.setText("Order #" + id + " Status: " + status);
        } else {
            txtTrack.setText("No active orders.");
        }
    }
}
