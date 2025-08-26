package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button btnLogin , btnDoRegister;
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
            String user = editUsername.getText().toString();
            String pass = editPassword.getText().toString();

            if ((user.equals("staff") && pass.equals("staff123"))) {
                // staff login
                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                sp.edit().putString("role", "staff").apply();
                startActivity(new Intent(this, StaffDashboardActivity.class));
                finish();
            }
            else if ((user.equals("driver") && pass.equals("driver123"))) {
                // driver login
                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                sp.edit().putString("role", "driver").apply();
                startActivity(new Intent(this, DriverDashboardActivity.class));
                finish();
            }
            else if (db.checkUser(user, pass)) {
                // customer login (from DB)
                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                sp.edit()
                        .putString("username", user)
                        .putString("role", "customer")
                        .apply();

                startActivity(new Intent(this, MenuActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }


            findViewById(R.id.btnDoRegister).setOnClickListener(view ->
                    startActivity(new Intent(this, RegisterActivity.class)));

        });

    }
}
