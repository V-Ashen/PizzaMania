package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    TextView btnLogin , btnDoRegister;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        db = new DBHelper(this);
        btnDoRegister = findViewById(R.id.btnDoRegister);

        btnLogin.setOnClickListener(v -> {
            // inside your login onClick
            String user = editUsername.getText().toString().trim();
            String pass = editPassword.getText().toString().trim();

// staff Colombo
            if (user.equalsIgnoreCase("staff") && pass.equals("staff123")) {
                Intent i = new Intent(this, StaffDashboardActivity.class);
                i.putExtra("branchId", 1);
                startActivity(i);
                finish();
            }
// staff Galle
            else if (user.equalsIgnoreCase("staffgalle") && pass.equals("staff123")) {
                Intent i = new Intent(this, StaffDashboardActivity.class);
                i.putExtra("branchId", 2);
                startActivity(i);
                finish();
            }
// driver Colombo
            else if (user.equalsIgnoreCase("driver") && pass.equals("driver123")) {
                Intent i = new Intent(this, DriverDashboardActivity.class);
                i.putExtra("branchId", 1);
                startActivity(i);
                finish();
            }
// driver Galle
            else if (user.equalsIgnoreCase("drivergalle") && pass.equals("driver123")) {
                Intent i = new Intent(this, DriverDashboardActivity.class);
                i.putExtra("branchId", 2);
                startActivity(i);
                finish();
            }
// else customer (use DB)
            else if (db.checkUser(user, pass)) {
                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                sp.edit().putString("username", user).putString("role", "customer").apply();
                startActivity(new Intent(this, MenuActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }

        });

        btnDoRegister.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)) );

    }
}
