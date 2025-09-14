package com.example.pizzar11;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ResOrder extends AppCompatActivity {

    private ListView listViewOrders;
    private FirebaseFirestore db;
    private List<Order> orderList;
    private ArrayAdapter<String> adapter;
    private List<String> orderDisplayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_order);

        listViewOrders = findViewById(R.id.listViewOrders);
        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();
        orderDisplayList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderDisplayList);
        listViewOrders.setAdapter(adapter);

        listViewOrders.setOnItemClickListener((parent, view, position, id) -> {
            Order order = orderList.get(position);
            Intent intent = new Intent(ResOrder.this, OrderDetailsActivity.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });

        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    orderList.clear();
                    orderDisplayList.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Order order = new Order();
                            order.setId(doc.getId());
                            order.setCustomerName(doc.getString("customerName"));
                            order.setStatus(doc.getString("status"));
                            // Skip date for now to avoid errors

                            orderList.add(order);

                            String displayText = order.getCustomerName() + "\n" +
                                    "Status: " + order.getStatus();
                            orderDisplayList.add(displayText);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}