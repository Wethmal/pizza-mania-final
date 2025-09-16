package com.example.pizzar11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PickLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    LatLng selectedLocation;

    ImageView btnBack, btnSearch, btnMyLocation, btnZoomIn, btnZoomOut;
    EditText etSearch;
    CardView loadingOverlay, locationInfoCard, searchResultsCard;
    TextView tvSelectedAddress, tvCoordinates;
    RecyclerView searchResultsRecycler;
    Button btnCancel, btnDone;

    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();

        // Setup Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        // Done Button
        btnDone.setOnClickListener(v -> {
            if (selectedLocation != null) {
                Intent result = new Intent();
                result.putExtra("lat", selectedLocation.latitude);
                result.putExtra("lng", selectedLocation.longitude);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel Button
        btnCancel.setOnClickListener(v -> finish());

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // My Location Button
        btnMyLocation.setOnClickListener(v -> getMyLocation());

        // Zoom Buttons
        btnZoomIn.setOnClickListener(v -> {
            if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomIn());
        });

        btnZoomOut.setOnClickListener(v -> {
            if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomOut());
        });

        // Search Button
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) searchLocation(query);
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        etSearch = findViewById(R.id.et_search_location);

        btnMyLocation = findViewById(R.id.btn_my_location);
        btnZoomIn = findViewById(R.id.btn_zoom_in);
        btnZoomOut = findViewById(R.id.btn_zoom_out);

        loadingOverlay = findViewById(R.id.loading_overlay);
        locationInfoCard = findViewById(R.id.location_info_card);
        searchResultsCard = findViewById(R.id.search_results_card);

        tvSelectedAddress = findViewById(R.id.tv_selected_address);
        tvCoordinates = findViewById(R.id.tv_coordinates);

        searchResultsRecycler = findViewById(R.id.search_results_recycler);
        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));

        btnCancel = findViewById(R.id.btn_cancel);
        btnDone = findViewById(R.id.btn_done);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            updateLocationInfo(latLng);
        });
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                selectedLocation = latLng;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                updateLocationInfo(latLng);
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLocationInfo(LatLng latLng) {
        tvCoordinates.setText("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);

        new Thread(() -> {
            Geocoder geocoder = new Geocoder(PickLocationActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                runOnUiThread(() -> {
                    if (addresses != null && !addresses.isEmpty()) {
                        tvSelectedAddress.setText(addresses.get(0).getAddressLine(0));
                    } else {
                        tvSelectedAddress.setText("Address not found");
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> tvSelectedAddress.setText("Error fetching address"));
                e.printStackTrace();
            }
        }).start();
    }

    private void searchLocation(String query) {
        loadingOverlay.setVisibility(View.VISIBLE);
        searchResultsCard.setVisibility(View.GONE);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocationName(query, 5);
                runOnUiThread(() -> {
                    loadingOverlay.setVisibility(View.GONE);

                    if (addresses != null && !addresses.isEmpty()) {
                        searchResultsCard.setVisibility(View.VISIBLE);

                        SearchResultsAdapter adapter = new SearchResultsAdapter(addresses, latLng -> {
                            selectedLocation = latLng;
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            updateLocationInfo(latLng);
                            searchResultsCard.setVisibility(View.GONE);
                        });

                        searchResultsRecycler.setAdapter(adapter);

                    } else {
                        Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(this, "Error searching location", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        }).start();
    }
}
