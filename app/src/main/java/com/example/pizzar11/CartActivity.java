package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerCart;
    CartAdapter adapter;
    CartDatabaseHelper db;
    List<CartItem> cartList;
    TextView tvCartTotal, tvTax, tvDelivery, tvSubtotal;

    double TAX_RATE = 0.18; // 18%
    double DELIVERY = 150;  // fixed delivery cost

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recyclerCart);
        tvCartTotal = findViewById(R.id.tv_cart_total);
        tvTax = findViewById(R.id.tv_tax);
        tvDelivery = findViewById(R.id.tv_delivery);
        tvSubtotal = findViewById(R.id.tv_subtotal);

        db = new CartDatabaseHelper(this);
        cartList = db.getAllCartItems();

        adapter = new CartAdapter(this, cartList);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(adapter);

        calculateTotals();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_location) {
                startActivity(new Intent(this, LocationActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    // Call this after any change in cart
    private void calculateTotals() {
        double total = 0;
        for(CartItem item : cartList){
            total += item.getPrice() * item.getQuantity();
        }

        double tax = total * TAX_RATE;
        double subtotal = total + tax + DELIVERY;

        tvCartTotal.setText("Rs " + total);
        tvTax.setText("Rs " + tax);
        tvDelivery.setText("Rs " + DELIVERY);
        tvSubtotal.setText("Rs " + subtotal);
    }


}
