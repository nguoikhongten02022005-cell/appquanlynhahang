package com.example.quanlynhahang.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.RecommendedDishItem;

import java.util.List;

public class RecommendedDishAdapter extends RecyclerView.Adapter<RecommendedDishAdapter.RecommendedDishViewHolder> {

    private final List<RecommendedDishItem> items;

    public RecommendedDishAdapter(List<RecommendedDishItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecommendedDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommended_dish, parent, false);
        return new RecommendedDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedDishViewHolder holder, int position) {
        RecommendedDishItem item = items.get(position);

        holder.ivDishImage.setImageResource(item.getImageResId());
        holder.tvDishName.setText(item.getName());
        holder.tvDishPrice.setText(item.getPrice());

        int statusTextRes = item.isAvailable()
                ? R.string.dish_status_available
                : R.string.dish_status_unavailable;
        int statusColorRes = item.isAvailable()
                ? R.color.status_available
                : R.color.status_unavailable;

        holder.tvDishStatus.setText(statusTextRes);
        holder.tvDishStatus.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), statusColorRes)
        );

        int addButtonColorRes = item.isAvailable()
                ? R.color.brand_green
                : R.color.bottom_nav_unselected;

        holder.btnAddDish.setEnabled(item.isAvailable());
        holder.btnAddDish.setBackgroundTintList(
                ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), addButtonColorRes)
                )
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecommendedDishViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDishImage;
        private final TextView tvDishName;
        private final TextView tvDishPrice;
        private final TextView tvDishStatus;
        private final ImageButton btnAddDish;

        RecommendedDishViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDishImage = itemView.findViewById(R.id.ivDishImage);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvDishPrice = itemView.findViewById(R.id.tvDishPrice);
            tvDishStatus = itemView.findViewById(R.id.tvDishStatus);
            btnAddDish = itemView.findViewById(R.id.btnAddDish);
        }
    }
}
