package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        adapter = new CartAdapter(this, cartList, this::calculateTotals);

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
                startActivity(new Intent(this, OrderHistoryActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        Button checkoutButton = findViewById(R.id.btn_process_checkout);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

    }

    // Call this after any change in cart
    private void calculateTotals() {
        cartList.clear();
        cartList.addAll(db.getAllCartItems());
        adapter.notifyDataSetChanged();

        double cartTotal = 0;
        for (CartItem item : cartList) {
            cartTotal += item.getPrice() * item.getQuantity();
        }

        double tax = cartTotal * 0.18;
        double delivery = 150; // LKR fixed
        double subtotal = cartTotal + tax + delivery;

        tvCartTotal.setText(String.format("Rs %.2f", cartTotal));
        tvTax.setText(String.format("Rs %.2f", tax));
        tvDelivery.setText(String.format("Rs %.2f", delivery));
        tvSubtotal.setText(String.format("Rs %.2f", subtotal));
    }


}
