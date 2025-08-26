package com.example.pizzamania;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationHelper {
    public static final int REQ = 2025;

    public static boolean ensureLocationPermission(Activity a) {
        boolean fine = ContextCompat.checkSelfPermission(a, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(a, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!fine || !coarse) {
            ActivityCompat.requestPermissions(a, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQ);
            return false;
        }
        return true;
    }
}
