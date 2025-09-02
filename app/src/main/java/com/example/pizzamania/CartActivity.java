package com.example.pizzamania;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {

    RecyclerView rv;
    TextView total;
    EditText editAddress;
    Button confirm, btnUseLocation;

    DBHelper db;
    FusedLocationProviderClient fused;

    double capturedLat = 0.0;
    double capturedLng = 0.0;

    // permission launcher
    private final ActivityResultLauncher<String> fineLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    fetchCurrentLocationAndFillAddress();
                } else {
                    Toast.makeText(this, "Location permission is required to autofill address", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cart);

        rv = findViewById(R.id.cartRecycler);
        total = findViewById(R.id.textTotalCost);
        editAddress = findViewById(R.id.editAddress);
        confirm = findViewById(R.id.btnConfirmOrder);
        btnUseLocation = findViewById(R.id.btnUseLocation);

        db = new DBHelper(this);
        fused = LocationServices.getFusedLocationProviderClient(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter(CartManager.getInstance().getItems(), this::updateTotal);
        rv.setAdapter(adapter);
        updateTotal();

        btnUseLocation.setOnClickListener(v -> ensureLocationThenFill());

        // If permission already granted, optionally fetch immediately
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocationAndFillAddress();
        }

        confirm.setOnClickListener(v -> {
            String addr = editAddress.getText().toString().trim();
            if (addr.isEmpty()) {
                Toast.makeText(this, "Please enter or use your location to fill the address", Toast.LENGTH_SHORT).show();
                return;
            }

            String items = CartManager.getInstance().itemsSummaryText();
            int cost = CartManager.getInstance().getTotal();

            long orderId = db.insertOrder(
                    getCustomerName(),
                    items,
                    cost,
                    addr,
                    capturedLat,
                    capturedLng
            );

            Toast.makeText(this, "Order #"+orderId+" placed (Pending).", Toast.LENGTH_SHORT).show();
            CartManager.getInstance().clear();
            finish();
        });
    }

    private void updateTotal() {
        total.setText("Total: " + CartManager.getInstance().getTotal() + " LKR");
    }

    private String getCustomerName() {
        return getSharedPreferences("UserSession", MODE_PRIVATE)
                .getString("username", "Customer1");
    }

    private void ensureLocationThenFill() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocationAndFillAddress();
        } else {
            fineLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchCurrentLocationAndFillAddress() {
        try {
            fused.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            capturedLat = location.getLatitude();
                            capturedLng = location.getLongitude();
                            reverseGeocodeAndFill(capturedLat, capturedLng);
                        } else {
                            Toast.makeText(CartActivity.this, "Could not get location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (SecurityException se) {
            Toast.makeText(this, "No permission for location", Toast.LENGTH_SHORT).show();
        }
    }

    private void reverseGeocodeAndFill(double lat, double lng) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String addressText = null;
            try {
                Geocoder geocoder = new Geocoder(CartActivity.this, Locale.getDefault());
                List<Address> res = geocoder.getFromLocation(lat, lng, 1);
                if (res != null && !res.isEmpty()) {
                    Address a = res.get(0);
                    addressText = a.getAddressLine(0);
                }
            } catch (IOException e) {
                // ignore
            }

            final String finalAddr = (addressText != null) ? addressText : "";
            runOnUiThread(() -> {
                if (!finalAddr.isEmpty()) {
                    editAddress.setText(finalAddr);
                    Toast.makeText(CartActivity.this, "Address filled from GPS", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Could not get address, please type it", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
