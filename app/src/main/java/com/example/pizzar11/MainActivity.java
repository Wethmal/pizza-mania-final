package com.example.pizzar11;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerFood;
    FoodAdapter adapter;
    List<Food> foodList;
    FirebaseFirestore db;

    FusedLocationProviderClient fusedLocationClient;
    TextView textViewBranch;

    // Branch locations
    double colomboLat = 6.9271;
    double colomboLng = 79.8612;
    double galleLat = 6.0535;
    double galleLng = 80.2210;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);

        textViewBranch = findViewById(R.id.textViewBranch);

        recyclerFood = findViewById(R.id.recyclerFoods);

        recyclerFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        foodList = new ArrayList<>();
        adapter = new FoodAdapter(this, foodList);
        recyclerFood.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

      //this is for to the hanlde the nav bar
        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already on Home
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_location) {
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });


        getUserLocation();




    }



    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if(location != null){
                        String nearestBranch = getNearestBranch(location);

                        textViewBranch.setText(nearestBranch.substring(0,1).toUpperCase() + nearestBranch.substring(1));
                        loadFoods(nearestBranch);
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private String getNearestBranch(Location userLocation){
        Location colombo = new Location("");
        colombo.setLatitude(colomboLat);
        colombo.setLongitude(colomboLng);

        Location galle = new Location("");
        galle.setLatitude(galleLat);
        galle.setLongitude(galleLng);

        float distColombo = userLocation.distanceTo(colombo);
        float distGalle = userLocation.distanceTo(galle);

        return (distColombo < distGalle) ? "colombo" : "galle";
    }

    private void loadFoods(String branch){
        db.collection("branches").document(branch).collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        foodList.clear();
                        for(DocumentSnapshot doc : task.getResult()){
                            Food food = doc.toObject(Food.class);
                            foodList.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load foods", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getUserLocation();
        }
    }
}
