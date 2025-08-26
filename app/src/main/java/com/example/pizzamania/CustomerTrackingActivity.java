package com.example.pizzamania;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class CustomerTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double SHOP_LAT = 6.9008;
    private static final double SHOP_LNG = 79.8539;

    private GoogleMap map;
    private TextView txtStatus, txtEta;
    private DBHelper db;
    private long orderId;

    private LatLng custLatLng;
    private Marker driverMarker;
    private Polyline path;
    private final Handler handler = new Handler();

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_customer_tracking);

        txtStatus = findViewById(R.id.txtStatus);
        txtEta = findViewById(R.id.txtEta);
        db = new DBHelper(this);

        orderId = getIntent().getLongExtra("orderId", -1);

        SupportMapFragment mf = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mf != null) mf.getMapAsync(this);
    }

    @Override public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        loadOnce();     // draw initial
        startPolling(); // poll DB every 5s to simulate "messages"
    }

    private void loadOnce() {
        Cursor c = db.getOrderById(orderId);
        if (!c.moveToFirst()) return;

        String status = c.getString(c.getColumnIndexOrThrow("status"));
        txtStatus.setText("Status: " + status);

        double custLat = c.getDouble(c.getColumnIndexOrThrow("custLat"));
        double custLng = c.getDouble(c.getColumnIndexOrThrow("custLng"));
        double dLat = c.getDouble(c.getColumnIndexOrThrow("driverLat"));
        double dLng = c.getDouble(c.getColumnIndexOrThrow("driverLng"));

        LatLng shop = new LatLng(SHOP_LAT, SHOP_LNG);
        custLatLng = new LatLng(custLat, custLng);

        map.clear();
        map.addMarker(new MarkerOptions().position(shop).title("Pizza Mania - Colombo 4"));
        map.addMarker(new MarkerOptions().position(custLatLng).title("Customer"));

        // straight polyline shop -> customer (simple)
        if (path != null) path.remove();
        path = map.addPolyline(new PolylineOptions().add(shop, custLatLng));

        // driver marker (if driver started)
        if (dLat != 0 && dLng != 0) {
            LatLng d = new LatLng(dLat, dLng);
            driverMarker = map.addMarker(new MarkerOptions().position(d).title("Driver"));
            updateEta(d, custLatLng);
        } else {
            updateEta(shop, custLatLng); // before pickup, estimate shop -> customer
        }

        LatLngBounds bounds = new LatLngBounds.Builder().include(shop).include(custLatLng).build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void startPolling() {
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                Cursor c = db.getOrderById(orderId);
                if (c.moveToFirst()) {
                    String status = c.getString(c.getColumnIndexOrThrow("status"));
                    txtStatus.setText("Status: " + status);

                    double dLat = c.getDouble(c.getColumnIndexOrThrow("driverLat"));
                    double dLng = c.getDouble(c.getColumnIndexOrThrow("driverLng"));
                    if (dLat != 0 && dLng != 0 && map != null) {
                        LatLng pos = new LatLng(dLat, dLng);
                        if (driverMarker == null) {
                            driverMarker = map.addMarker(new MarkerOptions().position(pos).title("Driver"));
                        } else {
                            driverMarker.setPosition(pos);
                        }
                        updateEta(pos, custLatLng);
                    }
                }
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    // quick ETA estimate: straight-line distance and avg speed 25 km/h
    private void updateEta(LatLng from, LatLng to) {
        float[] res = new float[1];
        android.location.Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, res);
        float meters = res[0];
        double km = meters / 1000.0;
        double speedKmh = 25.0;
        double hours = km / speedKmh;
        int minutes = (int)Math.round(hours * 60);
        txtEta.setText("Estimated time: ~" + minutes + " min (" + String.format("%.1f", km) + " km)");
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
    
}
