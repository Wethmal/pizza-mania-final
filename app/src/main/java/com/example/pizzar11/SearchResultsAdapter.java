package com.example.pizzar11;

import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder> {

    interface OnItemClick {
        void onClick(LatLng latLng);
    }

    private List<Address> results;
    private OnItemClick listener;

    public SearchResultsAdapter(List<Address> results, OnItemClick listener) {
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Address address = results.get(position);
        holder.text.setText(address.getAddressLine(0));
        holder.itemView.setOnClickListener(v -> {
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            listener.onClick(latLng);
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
        }
    }
}
