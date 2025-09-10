package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Your existing variables
    RecyclerView recyclerView; // For the main pizza list
    ArrayList<Pizza> pizzaList;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    // Variable for the new featured slider
    RecyclerView featuredRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Drawer setup (Your existing code - no changes)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Your XML now handles the title, so this Java part is optional
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pizza Mania");
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // --- NEW: Setup for the Featured Pizzas Slider ---
        setupFeaturedSlider();

        // --- Your existing code for the "All Pizzas" list ---
        recyclerView = findViewById(R.id.recyclerView);
        // Note: Your XML uses a GridLayoutManager. You can set it here if needed,
        // but the XML definition usually works fine on its own.
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Keeping your original code
        pizzaList = new ArrayList<>();
        pizzaList.add(new Pizza("Cheese Pizza", "1300 LKR", 1300, R.drawable.cheese));
        pizzaList.add(new Pizza("Chicken Pizza", "1500 LKR", 1500, R.drawable.chicken));
        pizzaList.add(new Pizza("Veg Pizza", "1000 LKR", 1000, R.drawable.veg));
        pizzaList.add(new Pizza("Sausage Pizza", "1200 LKR", 1200, R.drawable.sausage));
        pizzaList.add(new Pizza("Pepsi", "500 LKR", 500, R.drawable.pepsi));

        PizzaAdapter adapter = new PizzaAdapter(pizzaList, pizza -> {
            CartManager.getInstance().add(pizza);
            Toast.makeText(this, pizza.name + " added to cart", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        // Your existing code for the cart button
        findViewById(R.id.btnViewCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
    }

    /**
     * NEW METHOD
     * This sets up the horizontal RecyclerView to act as a paged image slider.
     */
    private void setupFeaturedSlider() {
        featuredRecyclerView = findViewById(R.id.featuredRecyclerView);
        // The horizontal layout manager is already defined in your activity_menu.xml,
        // so we don't need to set it again here.

        // Create a list of your banner images from the drawable folder
        List<Integer> bannerList = Arrays.asList(
                R.drawable.banner1, // Make sure banner1.png, banner2.png, etc.
                R.drawable.banner2, // are in your res/drawable folder
                R.drawable.banner3
        );

        // Set the new adapter for the slider
        FeaturedAdapter featuredAdapter = new FeaturedAdapter(bannerList);
        featuredRecyclerView.setAdapter(featuredAdapter);

        // This helper class makes the RecyclerView snap to items, creating the slider effect
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(featuredRecyclerView);
    }


    // Your existing code for handling navigation drawer clicks (no changes)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_track) {
            startActivity(new Intent(this, TrackOrderActivity.class));
        } else if (id == R.id.nav_contact) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:+94783536671"));
            smsIntent.putExtra("sms_body", "Hello PizzaMania Colombo branch, I need help with my order.");
            startActivity(smsIntent);
        } else if (id == R.id.nav_branches) {
            startActivity(new Intent(this, BranchesActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
            sp.edit().clear().apply();

            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}