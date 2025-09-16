package com.example.pizzar11;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrdersAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView customerNameText;
        private TextView orderIdText;
        private TextView statusText;
        private View statusColorBar;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            statusText = itemView.findViewById(R.id.statusText);
            statusColorBar = itemView.findViewById(R.id.statusColorBar);
        }

        public void bind(Order order, OnOrderClickListener listener) {
            // Set customer name (remove emoji prefix if exists)
            String customerName = order.getCustomerName();
            if (customerName.startsWith("👤 ")) {
                customerName = customerName.substring(2);
            }
            customerNameText.setText(customerName);

            // Set order ID (first 8 characters)
            orderIdText.setText("#" + order.getId().substring(0, Math.min(8, order.getId().length())));

            // Set status
            statusText.setText(order.getStatus());

            // Set status color
            setStatusColor(order.getStatus());

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
        }

        private void setStatusColor(String status) {
            int color;
            int backgroundColor;
            int textColor;

            if (status.contains("created")) {
                color = Color.parseColor("#FFC107"); // Yellow
                backgroundColor = Color.parseColor("#FFF8E1");
                textColor = Color.parseColor("#F57F17");
            } else if (status.contains("confirmed")) {
                color = Color.parseColor("#4CAF50"); // Green
                backgroundColor = Color.parseColor("#E8F5E8");
                textColor = Color.parseColor("#2E7D32");
            } else if (status.contains("preparing")) {
                color = Color.parseColor("#FF6B35"); // Orange
                backgroundColor = Color.parseColor("#FFF3E0");
                textColor = Color.parseColor("#E65100");
            } else if (status.contains("delivery")) {
                color = Color.parseColor("#2196F3"); // Blue
                backgroundColor = Color.parseColor("#E3F2FD");
                textColor = Color.parseColor("#1565C0");
            } else if (status.contains("Delivered")) {
                color = Color.parseColor("#9C27B0"); // Purple
                backgroundColor = Color.parseColor("#F3E5F5");
                textColor = Color.parseColor("#7B1FA2");
            } else {
                color = Color.parseColor("#757575"); // Gray
                backgroundColor = Color.parseColor("#F5F5F5");
                textColor = Color.parseColor("#424242");
            }

            statusColorBar.setBackgroundColor(color);
            statusText.setBackgroundColor(backgroundColor);
            statusText.setTextColor(textColor);
        }
    }
}