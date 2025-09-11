package com.example.pizzamania;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;

public class DriverDashboardActivity extends AppCompatActivity {

    DBHelper db;
    ListView lv;
    ArrayAdapter<String> adapter;
    ArrayList<Long> ids = new ArrayList<>();
    long currentOrderId = -1;

    FusedLocationProviderClient fused;
    LocationRequest req;
    LocationCallback callback;

    // 👇 Added field: which branch this driver belongs to
    int branchId = 1; // default Colombo, you can pass dynamically at login

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_driver_dashboard);
        db = new DBHelper(this);
        lv = findViewById(R.id.listApproved);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(getResources().getColor(android.R.color.black));
                return view;
            }
        };

        lv.setAdapter(adapter);

        lv.setOnItemClickListener((p, v, pos, id) -> {
            currentOrderId = ids.get(pos);
            db.assignDriver(currentOrderId, "Driver1"); // you can replace with logged-in driver name
            Toast.makeText(this, "Selected order #" + currentOrderId, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnPicked).setOnClickListener(v -> {
            if (currentOrderId == -1) {
                Toast.makeText(this, "Select an order first", Toast.LENGTH_SHORT).show();
                return;
            }
            db.updateStatus(currentOrderId, "OnTheWay");

            double lat = db.getCustomerLat(currentOrderId);
            double lng = db.getCustomerLng(currentOrderId);

            openGoogleMaps(lat, lng);

            Toast.makeText(this, "Marked Picked Up. Customer will be notified.", Toast.LENGTH_SHORT).show();
        });

        fused = LocationServices.getFusedLocationProviderClient(this);

        // ✅ Load orders for this driver’s branch
        load();
    }

    private void load() {
        adapter.clear();
        ids.clear();

        // ✅ FIXED: pass branchId
        Cursor c = db.getOrdersByStatus("Approved", branchId);
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow("id"));
            String items = c.getString(c.getColumnIndexOrThrow("items"));
            String address = c.getString(c.getColumnIndexOrThrow("address"));
            double lat = c.getDouble(c.getColumnIndexOrThrow("custLat"));
            double lng = c.getDouble(c.getColumnIndexOrThrow("custLng"));

            adapter.add("#" + id + " | " + items +
                    "\nAddr: " + (address == null ? "" : address) +
                    "\n(" + lat + ", " + lng + ")\n(tap to select)");
            ids.add(id);
        }
        c.close();
        adapter.notifyDataSetChanged();
    }

    private void startDriverLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3001);
            return;
        }
        req = LocationRequest.create();
        req.setInterval(5000);
        req.setFastestInterval(3000);
        req.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                if (currentOrderId == -1) return;
                if (result.getLastLocation() != null) {
                    db.updateDriverLocation(currentOrderId,
                            result.getLastLocation().getLatitude(),
                            result.getLastLocation().getLongitude());
                }
            }
        };
        fused.requestLocationUpdates(req, callback, Looper.getMainLooper());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fused != null && callback != null) fused.removeLocationUpdates(callback);
    }

    private void openGoogleMaps(double customerLat, double customerLng) {
        // ✅ Instead of hardcoding, pick branch by branchId
        double branchLat, branchLng;

        if (branchId == 1) { // Colombo
            branchLat = 6.9271;
            branchLng = 79.8612;
        } else { // Galle
            branchLat = 6.0535;
            branchLng = 80.2210;
        }

        String uri = "https://www.google.com/maps/dir/?api=1" +
                "&origin=" + branchLat + "," + branchLng +
                "&destination=" + customerLat + "," + customerLng +
                "&travelmode=driving";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps not installed!", Toast.LENGTH_SHORT).show();
        }
    }
}