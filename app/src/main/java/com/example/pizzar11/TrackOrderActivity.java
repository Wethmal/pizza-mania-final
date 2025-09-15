package com.example.pizzar11;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class TrackOrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String orderId;
    private Marker deliveryMarker;
    private Marker customerMarker;
    private Polyline routeLine;

    private FusedLocationProviderClient fusedLocationClient;
    private LatLng customerLatLng;
    private LatLng restaurantLatLng = new LatLng(6.9271, 79.8612); // example restaurant

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        orderId = getIntent().getStringExtra("orderId");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCustomer);
        mapFragment.getMapAsync(this);

        TextView tvStatus = findViewById(R.id.tvStatus);

        // Listen for order updates
        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .addSnapshotListener((doc, e) -> {
                    if (doc != null && doc.exists()) {
                        String status = doc.getString("status");
                        tvStatus.setText("Order Status: " + status);

                        Map<String, Object> loc = (Map<String, Object>) doc.get("customerLocation");
                        if (loc != null) {
                            double lat = (double) loc.get("lat");
                            double lng = (double) loc.get("lng");
                            customerLatLng = new LatLng(lat, lng);

                            if (customerMarker == null) {
                                customerMarker = mMap.addMarker(new MarkerOptions()
                                        .position(customerLatLng)
                                        .title("Customer"));
                            }
                        }
                    }
                });

        startTrackingDeliveryBoy();
    }

    private void startTrackingDeliveryBoy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        LocationRequest request = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location location = result.getLastLocation();
                if (location != null && mMap != null) {
                    LatLng deliveryLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (deliveryMarker == null) {
                        deliveryMarker = mMap.addMarker(new MarkerOptions()
                                .position(deliveryLatLng)
                                .title("Delivery Boy"));
                    } else {
                        deliveryMarker.setPosition(deliveryLatLng);
                    }

                    // Draw line from restaurant -> delivery boy -> customer
                    if (customerLatLng != null) {
                        if (routeLine != null) routeLine.remove();
                        routeLine = mMap.addPolyline(new PolylineOptions()
                                .add(restaurantLatLng, deliveryLatLng, customerLatLng)
                                .width(8)
                                .color(0xFFFF5722)); // orange
                    }

                    // Move camera to delivery boy
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deliveryLatLng, 13));
                }
            }
        }, getMainLooper());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Show restaurant marker
        mMap.addMarker(new MarkerOptions()
                .position(restaurantLatLng)
                .title("Restaurant"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackingDeliveryBoy();
        } else {
            Toast.makeText(this, "Location permission required!", Toast.LENGTH_SHORT).show();
        }
    }
}
