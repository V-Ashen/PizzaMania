package com.example.pizzamania;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StaffDashboardActivity extends AppCompatActivity {

    DBHelper db;

    // Order List variables
    ListView listPending;
    ArrayAdapter<String> ordersAdapter;
    ArrayList<Long> orderIds = new ArrayList<>();

    // Menu List variables
    ListView listMenu;
    StaffMenuAdapter menuAdapter;
    ArrayList<StaffMenuAdapter.MenuItemRow> menuItems = new ArrayList<>();

    // This staff member's assigned branch ID
    int branchId = 1; // Default to Colombo

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        // Assuming your new layout file is named activity_staff_dashboard.xml
        setContentView(R.layout.activity_staff_dashboard);

        // --- Get the branchId passed from LoginActivity ---
        branchId = getIntent().getIntExtra("branchId", 1);
        setTitle("Staff Dashboard - Branch " + branchId); // Set a helpful title

        db = new DBHelper(this);

        // --- Setup for Pending Orders List ---
        listPending = findViewById(R.id.listPending);
        ordersAdapter = new ArrayAdapter<>(this, R.layout.item_pending_order, R.id.txtOrderRow, new ArrayList<>());
        listPending.setAdapter(ordersAdapter);

        listPending.setOnItemClickListener((parent, view, position, id) -> {
            long orderId = orderIds.get(position);
            db.updateStatus(orderId, "Approved");
            Toast.makeText(this, "Approved order #" + orderId, Toast.LENGTH_SHORT).show();
            loadOrders(); // Reload only the orders list
        });


        // --- Setup for Menu Management List ---
        listMenu = findViewById(R.id.listMenu);
        menuAdapter = new StaffMenuAdapter(this, menuItems);
        listMenu.setAdapter(menuAdapter);


        // --- Setup for Buttons ---
        Button btnViewReviews = findViewById(R.id.btnViewReviews);
        btnViewReviews.setOnClickListener(v -> {
            startActivity(new Intent(this, ReviewListActivity.class));
        });

        // --- Load initial data for both lists ---
        loadOrders();
        loadMenuItems();
    }

    private void loadOrders() {
        ordersAdapter.clear();
        orderIds.clear();

        // Load only orders for this staff’s branch
        Cursor c = db.getOrdersByStatusAndBranch("Pending", branchId);
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow("id"));
            String items = c.getString(c.getColumnIndexOrThrow("items"));
            int total = c.getInt(c.getColumnIndexOrThrow("totalCost"));

            ordersAdapter.add("#" + id + " | " + items + " | " + total + " LKR (tap to Approve)");
            orderIds.add(id);
        }
        c.close();
        ordersAdapter.notifyDataSetChanged();
    }

    private void loadMenuItems() {
        menuItems.clear();

        // Load the full menu for this branch to manage availability
        Cursor c = db.getMenuCursorForBranch(branchId);
        while(c.moveToNext()){
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String name = c.getString(c.getColumnIndexOrThrow("name"));
            int price = c.getInt(c.getColumnIndexOrThrow("price"));
            int imageResId = c.getInt(c.getColumnIndexOrThrow("imageResId"));
            boolean available = c.getInt(c.getColumnIndexOrThrow("available")) == 1;

            menuItems.add(new StaffMenuAdapter.MenuItemRow(id, name, price, imageResId, available));
        }
        c.close();
        menuAdapter.notifyDataSetChanged();
    }
}