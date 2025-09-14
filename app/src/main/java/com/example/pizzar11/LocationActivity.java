package com.example.pizzar11;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker deliveryMarker;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String deliveryBoyId = "delivery123"; // 🔹 Example, assign dynamically later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 🔹 Listen to delivery boy location updates in Firestore
        DocumentReference ref = db.collection("delivery_locations").document(deliveryBoyId);
        ref.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                double lat = snapshot.getDouble("lat");
                double lng = snapshot.getDouble("lng");

                LatLng position = new LatLng(lat, lng);

                if (deliveryMarker == null) {
                    deliveryMarker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title("Delivery Boy"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                } else {
                    deliveryMarker.setPosition(position);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
                }
            }
        });
    }
}
