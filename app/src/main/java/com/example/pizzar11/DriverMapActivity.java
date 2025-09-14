package com.example.pizzar11;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.location.*;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String orderId;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        orderId = getIntent().getStringExtra("orderId");
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startUpdatingLocation();
    }

    private void startUpdatingLocation() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                LatLng driverLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(driverLatLng).title("You"));

                                // Update Firestore driverLocation
                                db.collection("orders").document(orderId)
                                        .update("driverLocation", new GeoPoint(driverLatLng.latitude, driverLatLng.longitude));
                            }
                        });
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnable);
    }
}
