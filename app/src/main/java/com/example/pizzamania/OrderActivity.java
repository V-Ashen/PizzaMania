package com.example.pizzamania;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {

    TextView pizzaName;
    Button placeOrder;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        pizzaName = findViewById(R.id.textPizzaName);
        placeOrder = findViewById(R.id.btnPlaceOrder);

        db = new DBHelper(this);

        String selectedPizza = getIntent().getStringExtra("pizzaName");
        pizzaName.setText("Order: " + selectedPizza);

        placeOrder.setOnClickListener(v -> {
            if (db.insertOrder(selectedPizza)) {
                Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to Place Order", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
