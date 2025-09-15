package com.example.pizzar11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pizzar11.AdminDashbord;
import com.example.pizzar11.R;

public class AdminLogin extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginBtn;

    // Hardcoded admin credentials
    private final String ADMIN_EMAIL = "admin@gmail.com";
    private final String ADMIN_PASSWORD = "admin1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.buttonLogin);

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                // ✅ Go to Admin Dashboard
                Intent intent = new Intent(AdminLogin.this, AdminDashbord.class);
                startActivity(intent);
                finish();
            } else {
                // ❌ Show error message
                Toast.makeText(AdminLogin.this,
                        "Please contact the Pizza Mania admin",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
