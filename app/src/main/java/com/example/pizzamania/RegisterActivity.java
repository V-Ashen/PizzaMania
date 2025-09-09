package com.example.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password;
    Button btnRegister;
    DBHelper db;

    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.editRegUsername);
        password = findViewById(R.id.editRegPassword);
        btnRegister = findViewById(R.id.btnDoRegister);
        login= findViewById(R.id.btnLogin);

        db = new DBHelper(this);

        btnRegister.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.insertUser(user, pass)) {
                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                finish(); // go back to login
            } else {
                Toast.makeText(this, "User Already Exists", Toast.LENGTH_SHORT).show();
            }
        });
        login.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
    }
}
