package com.example.pizzar11;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeliveryMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double customerLat, customerLng;
    private String orderId;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_map);

        // Get intent data safely
        orderId = getIntent().getStringExtra("orderId");
        customerLat = getIntent().getDoubleExtra("lat", 0);
        customerLng = getIntent().getDoubleExtra("lng", 0);

        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Error: Order ID missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnStartNav = findViewById(R.id.btnStartNav);
        Button btnDelivered = findViewById(R.id.btnDelivered);

        // Navigation intent
        btnStartNav.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + customerLat + "," + customerLng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        // Mark as delivered
        btnDelivered.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("orders")
                    .document(orderId)
                    .update("status", "delivered")
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "Order delivered ✅", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update order", Toast.LENGTH_SHORT).show()
                    );
        });

        startUpdatingLocation();
    }

    private void startUpdatingLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        // Last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                updateDeliveryBoyLocation(location.getLatitude(), location.getLongitude());
            }
        });

        // Realtime updates
        com.google.android.gms.location.LocationRequest request =
                com.google.android.gms.location.LocationRequest.create()
                        .setInterval(5000)
                        .setFastestInterval(2000)
                        .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(request,
                location -> {
                    if (location != null) {
                        updateDeliveryBoyLocation(location.getLatitude(), location.getLongitude());
                    }
                },
                getMainLooper());
    }

    private void updateDeliveryBoyLocation(double lat, double lng) {
        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put("lat", lat);
        locationMap.put("lng", lng);

        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .update("deliveryBoyLocation", locationMap)
                .addOnFailureListener(e ->
                        Log.e("DeliveryMapActivity", "Failed to update location", e)
                );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng customerLocation = new LatLng(customerLat, customerLng);
        mMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 15));
    }
}
