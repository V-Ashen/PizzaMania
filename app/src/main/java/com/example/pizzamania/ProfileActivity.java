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

        txtName = findViewById(R.id.txtProfileName);
        btnDelete = findViewById(R.id.btnDeleteAccount);
        db = new DBHelper(this);

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sp.getString("username", null);

        if (username == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        txtName.setText("Welcome, " + username);

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete this account?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (db.deleteUser(username)) {
                            // clear session
                            sp.edit().clear().apply();
                            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
