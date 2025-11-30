package com.example.pizzar11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // you can use any layout

        testFirebaseConnection();
    }

    private void testFirebaseConnection() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create sample data
        Map<String, Object> testData = new HashMap<>();
        testData.put("status", "connected");
        testData.put("timestamp", System.currentTimeMillis());

        // Try writing to Firestore
        db.collection("firebase_test")
                .add(testData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FIREBASE_TEST", "Firebase Connected! Document ID: " + documentReference.getId());
                    Toast.makeText(FirebaseTestActivity.this, "Firebase Connected ✔", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_TEST", "Firebase Connection FAILED → " + e.getMessage());
                    Toast.makeText(FirebaseTestActivity.this, "Firebase NOT Connected ❌", Toast.LENGTH_LONG).show();
                });
    }
}
