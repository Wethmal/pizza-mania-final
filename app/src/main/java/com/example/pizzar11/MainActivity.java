package com.example.pizzar11;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerFood;
    FoodAdapter adapter;
    List<Food> foodList;
    List<Food> filteredList; // For search filtering
    FirebaseFirestore db;
    FirebaseAuth auth;

    EditText editTextSearch;
    TextView tvName;
    TextView textViewBranch;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    Location lastKnownLocation;
    BranchResult lastNearestBranch; // store last branch

    // Branch coordinates
    double colomboLat = 6.9271, colomboLng = 79.8612;
    double galleLat = 6.0535, galleLng = 80.2210;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewBranch = findViewById(R.id.textViewBranch);
        recyclerFood = findViewById(R.id.recyclerFoods);
        tvName = findViewById(R.id.tv_name);
        editTextSearch = findViewById(R.id.editTextSearch);

        // RecyclerView setup
        recyclerFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        foodList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new FoodAdapter(this, filteredList);
        recyclerFood.setAdapter(adapter);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_cart) startActivity(new Intent(this, CartActivity.class));
            else if (id == R.id.nav_location) startActivity(new Intent(this, OrderHistoryActivity.class));
            else if (id == R.id.nav_profile) startActivity(new Intent(this, ProfileActivity.class));
            return true;
        });

        loadUserData();
        startLocationUpdates();
        setupSearch();

        // Click listener for branch to open Google Maps
        textViewBranch.setOnClickListener(v -> {
            if (lastNearestBranch == null) return;

            double lat = lastNearestBranch.name.equals("colombo") ? colomboLat : galleLat;
            double lng = lastNearestBranch.name.equals("colombo") ? colomboLng : galleLng;

            String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + lastNearestBranch.name + " Branch)";
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if(intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
            else Toast.makeText(this, "Google Maps not found", Toast.LENGTH_SHORT).show();
        });



        // inside onCreate()
        ViewPager2 bannerViewPager = findViewById(R.id.bannerViewPager);

// List of banners
        List<Integer> bannerList = new ArrayList<>();
        bannerList.add(R.drawable.banner5);
        bannerList.add(R.drawable.banner);
        bannerList.add(R.drawable.banner2);
        bannerList.add(R.drawable.banner3);

        BannerAdapter bannerAdapter = new BannerAdapter(this, bannerList);
        bannerViewPager.setAdapter(bannerAdapter);

// Auto scroll
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int currentItem = 0;
            @Override
            public void run() {
                if(currentItem == bannerList.size())
                    currentItem = 0;
                bannerViewPager.setCurrentItem(currentItem++, true);
                handler.postDelayed(this, 3000); // 3s delay
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void setupSearch(){
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterFood(s.toString().toLowerCase());
            }
        });
    }

    private void filterFood(String query){
        filteredList.clear();
        for(Food f : foodList){
            if(f.getName().toLowerCase().contains(query)) filteredList.add(f);
        }
        adapter.notifyDataSetChanged();
    }

    private void loadUserData(){
        db.collection("user_data").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists())
                        tvName.setText(documentSnapshot.getString("name"));
                });
    }

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult == null) return;

                Location location = locationResult.getLastLocation();
                if(location != null){
                    lastKnownLocation = location;
                    lastNearestBranch = getNearestBranch(location);

                    String branchName = lastNearestBranch.name.substring(0,1).toUpperCase() + lastNearestBranch.name.substring(1);
                    String distance = String.format("%.1f km", lastNearestBranch.distance / 1000);

                    // Add location pin emoji
                    textViewBranch.setText("📍 " + branchName + " • " + distance);

                    loadFoods(lastNearestBranch.name);
                }
            }
        }, getMainLooper());
    }

    private BranchResult getNearestBranch(Location userLocation){
        Location colombo = new Location(""); colombo.setLatitude(colomboLat); colombo.setLongitude(colomboLng);
        Location galle = new Location(""); galle.setLatitude(galleLat); galle.setLongitude(galleLng);

        float distColombo = userLocation.distanceTo(colombo);
        float distGalle = userLocation.distanceTo(galle);

        return distColombo < distGalle ? new BranchResult("colombo", distColombo)
                : new BranchResult("galle", distGalle);
    }

    private void loadFoods(String branch){
        db.collection("branches").document(branch).collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        foodList.clear();
                        for(DocumentSnapshot doc : task.getResult()) foodList.add(doc.toObject(Food.class));

                        // Initially filtered list = all foods
                        filteredList.clear();
                        filteredList.addAll(foodList);
                        adapter.notifyDataSetChanged();

                        // Apply search if user typed something before load
                        String query = editTextSearch.getText().toString().toLowerCase();
                        if(!query.isEmpty()) filterFood(query);
                    } else Toast.makeText(this, "Failed to load foods", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }
    }

    private static class BranchResult{
        String name;
        float distance;
        BranchResult(String name, float distance){ this.name = name; this.distance = distance; }
    }
}
