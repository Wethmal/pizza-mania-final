package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ResOrder extends AppCompatActivity implements OrdersAdapter.OnOrderClickListener {

    private RecyclerView recyclerViewOrders;
    private FirebaseFirestore db;
    private List<Order> orderList;
    private OrdersAdapter adapter;
    private TextView orderCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_order);

        ImageView ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> finish());

        // Initialize views
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        orderCountText = findViewById(R.id.orderCountText);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();

        // Setup RecyclerView
        setupRecyclerView();

        // Load orders
        loadOrders();
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter(orderList, this);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        db.collection("orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    orderList.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Order order = new Order();
                            order.setId(doc.getId());

                            // Set customer name with emoji
                            String customerName = doc.getString("customerName");
                            order.setCustomerName("👤 " + customerName);

                            String status = doc.getString("status");
                            switch (status) {
                                case "created":
                                    order.setStatus("📝 " + status);
                                    break;
                                case "order confirmed":
                                    order.setStatus("✅ " + status);
                                    break;
                                case "order preparing":
                                    order.setStatus("🍳 " + status);
                                    break;
                                case "order hand out to delivery":
                                    order.setStatus("📦🚚 " + status);
                                    break;
                                case "order Delivered":
                                    order.setStatus("🏠📬 " + status);
                                    break;
                                default:
                                    order.setStatus("ℹ️ " + status);
                                    break;
                            }

                            orderList.add(order);
                        }
                    }

                    // Update UI
                    adapter.notifyDataSetChanged();
                    updateOrderCount();
                });
    }

    private void updateOrderCount() {
        orderCountText.setText("Total Orders: " + orderList.size());
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(ResOrder.this, OrderDetailsActivity.class);
        intent.putExtra("orderId", order.getId());
        startActivity(intent);
    }
}