package com.example.pizzar11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    TextView tvAddressLine1, tvAddressLine2, tvAddressLine3, tvAddressLine4, tvTotalPayment;
    Button btnPlaceOrder;
    ImageView btnEditAddress, btnBack;
    RadioButton rbMastercard, rbPaypal;

    double totalPayment = 0;
    String customerName = "";
    String uid;

    CartDatabaseHelper dbHelper;
    List<CartItem> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Views
        tvAddressLine1 = findViewById(R.id.tv_address_line1);
        tvAddressLine2 = findViewById(R.id.tv_address_line2);
        tvAddressLine3 = findViewById(R.id.tv_address_line3);
        tvAddressLine4 = findViewById(R.id.tv_address_line4);
        tvTotalPayment = findViewById(R.id.tv_total_payment);

        btnEditAddress = findViewById(R.id.btn_edit_address);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnBack = findViewById(R.id.btn_back);

        rbMastercard = findViewById(R.id.rb_mastercard);
        rbPaypal = findViewById(R.id.rb_paypal);

        btnBack.setOnClickListener(v -> finish());

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load customer name
        FirebaseFirestore.getInstance().collection("user_data").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        customerName = doc.getString("name");
                    }
                });

        // Load cart
        dbHelper = new CartDatabaseHelper(this);
        cartList = dbHelper.getAllCartItems();
        calculateTotal();

        // Open map to pick location
        btnEditAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, PickLocationActivity.class);
            startActivityForResult(intent, 1001);
        });

        // Payment method handling
        rbMastercard.setOnClickListener(v -> rbPaypal.setChecked(false));
        rbPaypal.setOnClickListener(v -> rbMastercard.setChecked(false));

        // Place order
        btnPlaceOrder.setOnClickListener(v -> {
            if(rbMastercard.isChecked()){
                Toast.makeText(this, "Payment gateway coming soon!", Toast.LENGTH_SHORT).show();
            } else if(rbPaypal.isChecked()){
                placeOrderCOD();
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotal(){
        double total = 0;
        for(CartItem item: cartList){
            total += item.getPrice() * item.getQuantity();
        }
        totalPayment = total;
        tvTotalPayment.setText("IDR " + totalPayment);
    }

    private void placeOrderCOD(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> order = new HashMap<>();
        order.put("customerName", customerName);
        order.put("address", tvAddressLine1.getText().toString() + ", " +
                tvAddressLine2.getText().toString() + ", " +
                tvAddressLine3.getText().toString() + ", " +
                tvAddressLine4.getText().toString());
        order.put("items", cartList);
        order.put("date", new Date());
        order.put("paymentMethod", "Cash On Delivery");

        db.collection("orders").add(order).addOnSuccessListener(doc -> {
            Toast.makeText(this, "Your order placed successfully! 🍕", Toast.LENGTH_LONG).show();
            dbHelper.clearCart();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1001 && resultCode == RESULT_OK){
            double lat = data.getDoubleExtra("lat",0);
            double lng = data.getDoubleExtra("lng",0);

            // Reverse geocode
            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if(addresses != null && addresses.size() > 0){
                    Address address = addresses.get(0);

                    tvAddressLine1.setText(address.getFeatureName());
                    tvAddressLine2.setText(address.getThoroughfare());
                    tvAddressLine3.setText(address.getLocality());
                    tvAddressLine4.setText(address.getAdminArea() + ", " + address.getCountryName());
                } else {
                    tvAddressLine1.setText("Lat: "+lat);
                    tvAddressLine2.setText("Lng: "+lng);
                    tvAddressLine3.setText("");
                    tvAddressLine4.setText("");
                }
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Failed to get address!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
