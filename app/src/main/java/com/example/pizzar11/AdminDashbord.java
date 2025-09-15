package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashbord extends AppCompatActivity {

    LinearLayout cardAddFoods, cardViewOrders, cardCustomerMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashbord); // your XML filename

        // Link cards
        cardAddFoods = findViewById(R.id.cardAddFoods);
        cardViewOrders = findViewById(R.id.cardViewOrders);
        cardCustomerMessages = findViewById(R.id.cardCustomerMessages);

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
    }
}
