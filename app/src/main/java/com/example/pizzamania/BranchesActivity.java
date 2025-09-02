package com.example.pizzamania;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BranchesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Branch 1: Colombo
        LatLng colombo = new LatLng(6.9271, 79.8612);
        mMap.addMarker(new MarkerOptions().position(colombo).title("Colombo Branch"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, 10));

        // Branch 2: Galle
        LatLng galle = new LatLng(6.0535, 80.2210);
        mMap.addMarker(new MarkerOptions().position(galle).title("Galle Branch"));
    }
}
