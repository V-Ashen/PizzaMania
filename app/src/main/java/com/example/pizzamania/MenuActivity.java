package com.example.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Pizza> pizzaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pizzaList = new ArrayList<>();
        pizzaList.add(new Pizza("Cheese Pizza", "1200 LKR", R.drawable.cheese));
        pizzaList.add(new Pizza("Chicken Pizza", "1500 LKR", R.drawable.chicken));
        pizzaList.add(new Pizza("Veg Pizza", "1000 LKR", R.drawable.veg));

        PizzaAdapter adapter = new PizzaAdapter(pizzaList, pizza -> {
            Intent intent = new Intent(MenuActivity.this, OrderActivity.class);
            intent.putExtra("pizzaName", pizza.name);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}
