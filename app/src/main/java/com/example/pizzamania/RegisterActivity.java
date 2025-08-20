package com.example.pizzamania;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password;
    Button registerBtn;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.editUsername);
        password = findViewById(R.id.editPassword);
        registerBtn = findViewById(R.id.btnRegister);

        db = new DBHelper(this);

        registerBtn.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (db.insertUser(user, pass)) {
                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "User Already Exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
