package com.example.pizzamania;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
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

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_driver_dashboard);
        db = new DBHelper(this);
        lv = findViewById(R.id.listApproved);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((p, v, pos, id) -> {
            currentOrderId = ids.get(pos);
            db.assignDriver(currentOrderId, "Driver1");
            Toast.makeText(this, "Selected order #"+currentOrderId, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnPicked).setOnClickListener(v -> {
            if (currentOrderId == -1) { Toast.makeText(this,"Select an order first",Toast.LENGTH_SHORT).show(); return; }
            db.updateStatus(currentOrderId, "OnTheWay");
            startDriverLocationUpdates();
            Toast.makeText(this, "Marked Picked Up. Customer will be notified.", Toast.LENGTH_SHORT).show();
        });

        fused = LocationServices.getFusedLocationProviderClient(this);
        load();
    }

    private void load() {
        adapter.clear(); ids.clear();
        Cursor c = db.getOrdersByStatus("Approved");
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow("id"));
            String items = c.getString(c.getColumnIndexOrThrow("items"));
            adapter.add("#"+id+" | "+items+" (tap to select)");
            ids.add(id);
        }
        adapter.notifyDataSetChanged();
    }

    private void startDriverLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3001);
            return;
        }
        req = LocationRequest.create();
        req.setInterval(5000); req.setFastestInterval(3000); req.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        callback = new LocationCallback() {
            @Override public void onLocationResult(@NonNull LocationResult result) {
                if (currentOrderId == -1) return;
                if (result.getLastLocation() != null) {
                    db.updateDriverLocation(currentOrderId, result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                }
            }
        };
        fused.requestLocationUpdates(req, callback, Looper.getMainLooper());
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (fused!=null && callback!=null) fused.removeLocationUpdates(callback);
    }
}
