package com.example.pizzamania;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CartActivity extends AppCompatActivity {

    RecyclerView rv;
    TextView total;
    EditText editAddress;
    Button confirm;
    DBHelper db;
    FusedLocationProviderClient fused;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cart);

        // Bind UI
        rv = findViewById(R.id.cartRecycler);
        total = findViewById(R.id.textTotalCost);
        editAddress = findViewById(R.id.editAddress);
        confirm = findViewById(R.id.btnConfirmOrder);

        // DB + Location
        db = new DBHelper(this);
        fused = LocationServices.getFusedLocationProviderClient(this);

        // RecyclerView setup
        rv.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter(CartManager.getInstance().getItems(), this::updateTotal);
        rv.setAdapter(adapter);
        updateTotal();

        // Confirm order button
        confirm.setOnClickListener(v -> {
            if (!LocationHelper.ensureLocationPermission(this)) {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                return;
            }

            fused.getLastLocation().addOnSuccessListener(location -> {
                double lat = 0.0;
                double lng = 0.0;

                // If location is available, use it
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                // Collect order details
                String items = CartManager.getInstance().itemsSummaryText();
                int cost = CartManager.getInstance().getTotal();
                String addr = editAddress.getText().toString();

                // Save into DB
                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                String loggedUser = sp.getString("username", "Customer1"); // fallback
                long orderId = db.insertOrder(loggedUser, items, cost, addr, lat, lng);


                Toast.makeText(this, "Order #" + orderId + " placed (Pending)", Toast.LENGTH_SHORT).show();

                // Clear cart
                CartManager.getInstance().clear();
                finish();
            });
        });
    }

    private void updateTotal() {
        total.setText("Total: " + CartManager.getInstance().getTotal() + " LKR");
    }
}
