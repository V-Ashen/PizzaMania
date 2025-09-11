package com.example.pizzamania;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.*;

public class CartActivity extends AppCompatActivity {

    RecyclerView rv;
    TextView total;
    EditText editAddress;
    Button confirm, btnUseLocation;
    DBHelper db;
    FusedLocationProviderClient fused;
    private static final int REQ_LOCATION = 3002;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cart);

        rv = findViewById(R.id.cartRecycler);
        total = findViewById(R.id.textTotalCost);
        editAddress = findViewById(R.id.editAddress);
        confirm = findViewById(R.id.btnConfirmOrder);
        btnUseLocation = findViewById(R.id.btnUseLocation); // optional button to auto-fill address

        db = new DBHelper(this);
        fused = LocationServices.getFusedLocationProviderClient(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter(CartManager.getInstance().getItems(), this::updateTotal);
        rv.setAdapter(adapter);
        updateTotal();

        btnUseLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
                return;
            }
            fused.getLastLocation().addOnSuccessListener(loc -> {
                if (loc != null) {
                    editAddress.setText("Lat:"+loc.getLatitude()+",Lng:"+loc.getLongitude());
                } else {
                    Toast.makeText(this, "Unable to get location now", Toast.LENGTH_SHORT).show();
                }
            });
        });

        confirm.setOnClickListener(v -> {
            // ensure we have an address (or location)
            String addr = editAddress.getText().toString().trim();
            if (addr.isEmpty()) {
                Toast.makeText(this, "Please enter address or use location", Toast.LENGTH_SHORT).show();
                return;
            }

            // get customer location (best effort)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
                return;
            }

            fused.getLastLocation().addOnSuccessListener(loc -> {
                double lat = loc != null ? loc.getLatitude() : 0.0;
                double lng = loc != null ? loc.getLongitude() : 0.0;

                // get assigned branch (from session)
                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                int branchId = sp.getInt("branchId", 1);

                // prepare order details
                String items = CartManager.getInstance().itemsSummaryText();
                int cost = CartManager.getInstance().getTotal();
                String username = sp.getString("username", "Guest");

                long orderId = db.insertOrder(username, items, cost, addr, lat, lng, branchId);
                Toast.makeText(this, "Order #"+orderId+" placed (Pending) at branch "+branchId, Toast.LENGTH_LONG).show();

                CartManager.getInstance().clear();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void updateTotal() {
        total.setText("Total: " + CartManager.getInstance().getTotal() + " LKR");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // nothing, user can retry action (use location)
            } else {
                Toast.makeText(this, "Location permission required to auto-fill address", Toast.LENGTH_SHORT).show();
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
