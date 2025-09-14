package com.example.pizzar11;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvAddress, tvItems;
    private Spinner spinnerStatus;
    private Button btnUpdate;
    private FirebaseFirestore db;
    private String orderId;

    private String[] statusOptions = {
            "created",
            "order confirmed",
            "order preparing",
            "order hand out to delivery"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvAddress = findViewById(R.id.tvAddress);
        tvItems = findViewById(R.id.tvItems);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdate = findViewById(R.id.btnUpdate);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("orderId");

        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statusOptions);
        spinnerStatus.setAdapter(adapter);

        loadOrderDetails();

        btnUpdate.setOnClickListener(v -> updateStatus());
    }

    private void loadOrderDetails() {
        db.collection("orders").document(orderId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvCustomerName.setText("Customer: " + document.getString("customerName"));
                        tvAddress.setText("Address: " + document.getString("address"));

                        // Display items
                        List<Map<String, Object>> items = (List<Map<String, Object>>) document.get("items");
                        StringBuilder itemsText = new StringBuilder("Items:\n");
                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                String itemName = (String) item.get("name");
                                if (itemName == null) itemName = "Unknown item";
                                itemsText.append("- ").append(itemName).append("\n");
                            }
                        }
                        tvItems.setText(itemsText.toString());

                        // Set current status
                        String currentStatus = document.getString("status");
                        if (currentStatus != null) {
                            for (int i = 0; i < statusOptions.length; i++) {
                                if (statusOptions[i].equals(currentStatus)) {
                                    spinnerStatus.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading order", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateStatus() {
        String newStatus = statusOptions[spinnerStatus.getSelectedItemPosition()];

        db.collection("orders").document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Status updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating status", Toast.LENGTH_SHORT).show();
                });
    }
}