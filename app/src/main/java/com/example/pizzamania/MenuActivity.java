package com.example.pizzamania;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQ_LOCATION = 2001;
    private static final long SLIDER_DELAY = 3000; // 3 seconds

    // Your existing variables
    RecyclerView recyclerView;
    RecyclerView featuredRecyclerView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    DBHelper db;
    FusedLocationProviderClient fused;
    int assignedBranchId = 1;

    // --- NEW: Variables for the auto-slider ---
    private Handler sliderHandler;
    private Runnable sliderRunnable;
    // ------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = new DBHelper(this);
        fused = LocationServices.getFusedLocationProviderClient(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Pizza Mania");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup for the "Featured Pizzas" slider
        setupFeaturedSlider();

        // Branch detection and menu loading logic (unchanged)
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        assignedBranchId = sp.getInt("branchId", -1);
        if (assignedBranchId != -1 && assignedBranchId != 0) {
            loadMenuForBranch(assignedBranchId);
        } else {
            detectNearestBranchAndLoadMenu();
        }

        findViewById(R.id.btnViewCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
    }

    private void setupFeaturedSlider() {
        featuredRecyclerView = findViewById(R.id.featuredRecyclerView);
        List<Integer> bannerList = Arrays.asList(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3);
        FeaturedAdapter featuredAdapter = new FeaturedAdapter(bannerList);
        featuredRecyclerView.setAdapter(featuredAdapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(featuredRecyclerView);

        // --- NEW: Initialize the handler and runnable for auto-sliding ---
        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (featuredRecyclerView.getAdapter() == null || featuredRecyclerView.getAdapter().getItemCount() == 0) {
                    return; // Don't do anything if the list is empty
                }
                // Find the current item's position
                int currentPosition = ((LinearLayoutManager) Objects.requireNonNull(featuredRecyclerView.getLayoutManager()))
                        .findFirstVisibleItemPosition();

                if (currentPosition == RecyclerView.NO_POSITION) {
                    currentPosition = 0;
                }

                // Calculate the next position, looping back to the start if at the end
                int nextPosition = (currentPosition + 1) % featuredRecyclerView.getAdapter().getItemCount();
                featuredRecyclerView.smoothScrollToPosition(nextPosition);

                // Schedule the next slide
                sliderHandler.postDelayed(this, SLIDER_DELAY);
            }
        };
        // -----------------------------------------------------------------
    }


    // --- NEW: Add lifecycle methods to control the slider ---

    @Override
    protected void onResume() {
        super.onResume();
        // Start auto-sliding when the activity is visible
        sliderHandler.postDelayed(sliderRunnable, SLIDER_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop auto-sliding when the activity is not visible to save resources
        sliderHandler.removeCallbacks(sliderRunnable);
    }
    // -------------------------------------------------------


    // --- All your other methods (detectNearestBranch, loadMenuForBranch, etc.) remain unchanged ---

    private void detectNearestBranchAndLoadMenu() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
            return;
        }
        fused.getLastLocation().addOnSuccessListener(loc -> {
            int branchId;
            if (loc != null) {
                branchId = db.getNearestBranchId(loc.getLatitude(), loc.getLongitude());
                assignedBranchId = branchId;
                getSharedPreferences("UserSession", MODE_PRIVATE).edit().putInt("branchId", branchId).apply();
            } else {
                branchId = 1;
                assignedBranchId = 1;
                getSharedPreferences("UserSession", MODE_PRIVATE).edit().putInt("branchId", 1).apply();
            }
            loadMenuForBranch(branchId);
            Toast.makeText(this, "Assigned to branch: " + (branchId == 1 ? "Colombo" : "Galle"), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            assignedBranchId = 1;
            getSharedPreferences("UserSession", MODE_PRIVATE).edit().putInt("branchId", 1).apply();
            loadMenuForBranch(1);
            Toast.makeText(this, "Failed to get location. Showing Colombo menu.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadMenuForBranch(int branchId) {
        ArrayList<Pizza> pizzas = db.getAvailableMenuForBranch(branchId);
        PizzaAdapter adapter = new PizzaAdapter(pizzas, pizza -> {
            CartManager.getInstance().add(pizza);
            Toast.makeText(this, pizza.name + " added to cart", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) startActivity(new Intent(this, ProfileActivity.class));
        else if (id == R.id.nav_history) startActivity(new Intent(this, OrderHistoryActivity.class));
        else if (id == R.id.nav_cart) startActivity(new Intent(this, CartActivity.class));
        else if (id == R.id.nav_track) startActivity(new Intent(this, TrackOrderActivity.class));
        else if (id == R.id.nav_contact) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:+94783536671"));
            smsIntent.putExtra("sms_body", "Hello PizzaMania Colombo branch, I need help with my order.");
            startActivity(smsIntent);
        } else if (id == R.id.nav_branches) startActivity(new Intent(this, BranchesActivity.class));
        else if (id == R.id.nav_logout) {
            getSharedPreferences("UserSession", MODE_PRIVATE).edit().clear().apply();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectNearestBranchAndLoadMenu();
            } else {
                getSharedPreferences("UserSession", MODE_PRIVATE).edit().putInt("branchId", 1).apply();
                loadMenuForBranch(1);
                Toast.makeText(this, "Location denied. Showing Colombo menu.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}