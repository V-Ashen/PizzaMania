package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView txtName;
    Button btnDelete;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        txtName = findViewById(R.id.txtProfileName);
        btnDelete = findViewById(R.id.btnDeleteAccount);
        db = new DBHelper(this);

        // Existing code...
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go back when the back button in the toolbar is clicked
        onBackPressed();
        return true;
    }

}
