package com.example.pizzar11;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddFood extends AppCompatActivity {

    EditText etFoodName, etImageUrl, etPrice;
    Button btnAddFood;
    Spinner spinnerBranch;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // UI elements
        spinnerBranch = findViewById(R.id.spinnerBranch);
        etFoodName = findViewById(R.id.etFoodName);
        etImageUrl = findViewById(R.id.etImageUrl);
        etPrice = findViewById(R.id.etPrice);
        btnAddFood = findViewById(R.id.btnAddFood);

        // Firestore init
        db = FirebaseFirestore.getInstance();

        btnAddFood.setOnClickListener(v -> {
            String branch = spinnerBranch.getSelectedItem().toString();
            addFoodToFirestore(branch);
        });
    }

    private void addFoodToFirestore(String branch) {
        String name = etFoodName.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || imageUrl.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Food data
        Map<String, Object> food = new HashMap<>();
        food.put("name", name);
        food.put("image", imageUrl);
        food.put("price", price);

        // Save to Firestore
        db.collection("branches")
                .document(branch)
                .collection("foods")
                .add(food)
                .addOnSuccessListener(docRef ->
                        Toast.makeText(this, "Food added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
