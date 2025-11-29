package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, phoneEditText, passwordEditText;
    Button registerButton, signinButton;
    FirebaseAuth auth;
    FirebaseFirestore db;
    UserDatabaseHelper localDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        signinButton = findViewById(R.id.signinButton);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        localDb = new UserDatabaseHelper(this);

        // Register button click
        registerButton.setOnClickListener(v -> registerUser());

        // Signin button click
        signinButton.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1️ Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // Get UID
                        String uid = auth.getCurrentUser().getUid();

                        //  Prepare user data map
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("phone", phone);
                        user.put("role", "customer");
                        user.put("createdAt", FieldValue.serverTimestamp());
                        // profileImage optional, will add later
                        user.put("profileImage", null);

                        // Save to Firestore "user_data" collection
                        db.collection("user_data").document(uid).set(user)
                                .addOnSuccessListener(unused -> {
                                    // Save also to SQLite
                                    localDb.insertUser(uid, name, email, phone, "customer", null);

                                    Toast.makeText(SignupActivity.this, "Registered Successfully deer yuise !", Toast.LENGTH_SHORT).show();
                                    // Go to MainActivity or ProfileActivity
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();


                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(SignupActivity.this, "Firestore Error: "+e.getMessage(), Toast.LENGTH_SHORT).show());


                    } else {
                        Toast.makeText(SignupActivity.this, "Auth Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
