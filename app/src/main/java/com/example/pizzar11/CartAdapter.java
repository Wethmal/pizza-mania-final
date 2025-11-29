package com.example.pizzar11;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartList;

    private CartDatabaseHelper dbHelper;

    private Runnable onCartUpdated;

    public CartAdapter(Context context, List<CartItem> cartList,Runnable onCartUpdated){
        this.context = context;
        this.cartList = cartList;
        this.dbHelper = new CartDatabaseHelper(context);

        this.onCartUpdated = onCartUpdated;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("Rs " + item.getPrice());
        holder.tvQuantity.setText("x" + item.getQuantity());


        // Load image into the ImageView
        Glide.with(context)
                .load(item.getImageUrl()) // URL from CartItem
                .placeholder(R.drawable.logo) // optional placeholder
                .into(holder.ivItemImage);


        CartDatabaseHelper db = new CartDatabaseHelper(context);

        //  Decrease quantity
        holder.btnDecrease.setOnClickListener(v -> {
            int qty = item.getQuantity();
            if (qty > 1) {
                item.setQuantity(qty - 1);
                dbHelper.updateQuantity(item.getName(), item.getQuantity());
                notifyItemChanged(position);
                if (onCartUpdated != null) onCartUpdated.run();
            }
        });

//  Increase quantity
        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            dbHelper.updateQuantity(item.getName(), item.getQuantity());
            notifyItemChanged(position);
            if (onCartUpdated != null) onCartUpdated.run();
        });

//  Remove item
        holder.btnRemove.setOnClickListener(v -> {
            dbHelper.removeItem(item.getName());
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
            if (onCartUpdated != null) onCartUpdated.run();
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView ivItemImage,btnDecrease, btnIncrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvItemQty);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            btnDecrease = itemView.findViewById(R.id.btn_decrease_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase_quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove_item);
        }
    }
}
