package com.example.pizzar11;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        initViews();
        setupRecyclerView();
        loadOrderHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        ivBack = findViewById(R.id.ivBack);

        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();

        ivBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new OrderHistoryAdapter(orderList, this::onOrderClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadOrderHistory() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);

        // Replace "userId" with actual user ID from your authentication
        String userId = "current_user_id"; // You should get this from your auth system

        db.collection("orders")
                .whereEqualTo("customerName", userId) // You might want to change this to a proper user field
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = new Order();
                        order.setOrderId(document.getId());
                        order.setItems(document.getString("items"));
                        order.setStatus(document.getString("status"));
                        order.setTimestamp(document.getLong("timestamp"));
                        order.setTotalAmount(document.getDouble("totalAmount"));

                        orderList.add(order);
                    }

                    progressBar.setVisibility(View.GONE);

                    if (orderList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderHistory", "Error loading orders", e);
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Failed to load orders");
                    Toast.makeText(this, "Error loading order history", Toast.LENGTH_SHORT).show();
                });
    }

    private void onOrderClick(Order order) {
        // Navigate to live tracking activity
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("orderId", order.getOrderId());
        startActivity(intent);
    }
}