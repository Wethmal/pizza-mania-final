package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String orderId;
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //  Get orderId safely
        orderId = getIntent().getStringExtra("orderId");
        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, "Order not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        tvStatus = findViewById(R.id.tvStatus);

        startLiveTracking();
    }

    private void startLiveTracking() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (orderId == null) return;

                db.collection("orders").document(orderId)
                        .get()
                        .addOnSuccessListener(document -> {
                            if (document.exists()) {
                                String status = document.getString("status");
                                if (status != null) {
                                    tvStatus.setText("Status: " + status);
                                } else {
                                    tvStatus.setText("Status: Updating...");
                                }
                            } else {
                                Log.w("LocationActivity", "Order not found in Firestore!");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("LocationActivity", "Error loading order", e);
                        });

                handler.postDelayed(this, 2000); // refresh every 2s
            }
        };

        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }



}
