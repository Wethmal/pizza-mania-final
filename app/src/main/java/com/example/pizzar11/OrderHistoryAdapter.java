package com.example.pizzar11;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderHistoryAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Order Id - " + order.getId());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.tvDate.setText(sdf.format(order.getDate()));

        // Convert items list to comma-separated string
        StringBuilder itemsText = new StringBuilder();
        for (Map<String, Object> item : order.getItems()) {
            if (item.containsKey("name") && item.containsKey("quantity")) {
                itemsText.append(item.get("name")).append(" x")
                        .append(item.get("quantity")).append(", ");
            }
        }
        String itemsStr = itemsText.length() > 0 ? itemsText.substring(0, itemsText.length() - 2) : "";
        holder.tvItems.setText("Items - " + itemsStr);

        holder.tvStatus.setText("Order status - " + order.getStatus());

        //  Add click listener for "Live Tracking" button
        holder.tvLiveTracking.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TrackOrderActivity.class);
            intent.putExtra("orderId", order.getId()); // pass order id
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDate, tvItems, tvStatus, tvLiveTracking;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvItems = itemView.findViewById(R.id.tvItems);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvLiveTracking = itemView.findViewById(R.id.track);
        }
    }
}
