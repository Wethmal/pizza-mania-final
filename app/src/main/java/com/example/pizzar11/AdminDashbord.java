package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashbord extends AppCompatActivity {

    CardView cardAddFoods, cardViewOrders, cardCustomerMessages;
    TextView tvusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashbord); // your XML filename

        // Link cards
        cardAddFoods = findViewById(R.id.cardAddFoods);
        cardViewOrders = findViewById(R.id.cardViewOrders);
        cardCustomerMessages = findViewById(R.id.cardCustomerMessages);
        tvusers = findViewById(R.id.tvusers);

        // Go to Add Foods Page
        cardAddFoods.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashbord.this, AddFood.class);
            startActivity(intent);
        });

        // Go to View Orders Page
        cardViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashbord.this, ResOrder.class);
            startActivity(intent);
        });

        // Go to Customer Messages Page
        cardCustomerMessages.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashbord.this, MessagesActivity.class);
            startActivity(intent);
        });

        // Go to Customer Messages Page
        tvusers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashbord.this, userwatch.class);
            startActivity(intent);
        });
    }
}
