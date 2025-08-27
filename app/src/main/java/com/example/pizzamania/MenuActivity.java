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
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ArrayList<Pizza> pizzaList;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Drawer setup
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Menu list
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        findViewById(R.id.btnViewCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
    }

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
            // Example: open SMS for Colombo branch
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:+94783536671"));
            smsIntent.putExtra("sms_body", "Hello PizzaMania Colombo branch, I need help with my order.");
            startActivity(smsIntent);
        }
        else if (id == R.id.nav_logout) {
            // Clear session
            SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
            sp.edit().clear().apply();

            // Go back to LoginActivity
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
