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
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RecommendedDishAdapter extends RecyclerView.Adapter<RecommendedDishAdapter.RecommendedDishViewHolder> {

    public interface OnDishActionListener {
        void onDishClick(RecommendedDishItem item);

        void onAddDishClick(RecommendedDishItem item);
    }

    private final List<RecommendedDishItem> items;
    private final OnDishActionListener onDishActionListener;

    public RecommendedDishAdapter(List<RecommendedDishItem> items,
                                  OnDishActionListener onDishActionListener) {
        this.items = items;
        this.onDishActionListener = onDishActionListener;
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
        boolean isAvailable = item.isAvailable();

        holder.ivDishImage.setImageResource(item.getImageResId());
        holder.tvDishName.setText(item.getName());
        holder.tvDishPrice.setText(item.getPrice());
        holder.tvDishStatus.setText(isAvailable ? R.string.dish_status_available : R.string.dish_status_unavailable);
        holder.tvDishStatus.setBackgroundResource(
                isAvailable ? R.drawable.bg_status_available : R.drawable.bg_status_unavailable
        );
        holder.tvDishStatus.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(),
                isAvailable ? R.color.status_available_text : R.color.status_unavailable_text
        ));

        holder.btnAddDish.setEnabled(isAvailable);
        holder.btnAddDish.setAlpha(isAvailable ? 1f : 0.85f);
        holder.btnAddDish.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                holder.itemView.getContext(),
                isAvailable ? R.color.add_button_icon_enabled : R.color.add_button_icon_disabled
        )));

        View.OnClickListener dishClickListener = v -> {
            if (onDishActionListener != null) {
                onDishActionListener.onDishClick(item);
            }
        };
        holder.cardRecommendedDish.setOnClickListener(dishClickListener);
        holder.itemView.setOnClickListener(dishClickListener);

        holder.btnAddDish.setOnClickListener(v -> {
            if (isAvailable && onDishActionListener != null) {
                onDishActionListener.onAddDishClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecommendedDishViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardRecommendedDish;
        private final ImageView ivDishImage;
        private final TextView tvDishName;
        private final TextView tvDishPrice;
        private final TextView tvDishStatus;
        private final ImageButton btnAddDish;

        RecommendedDishViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRecommendedDish = itemView.findViewById(R.id.cardRecommendedDish);
            ivDishImage = itemView.findViewById(R.id.ivDishImage);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvDishPrice = itemView.findViewById(R.id.tvDishPrice);
            tvDishStatus = itemView.findViewById(R.id.tvDishStatus);
            btnAddDish = itemView.findViewById(R.id.btnAddDish);
        }
    }
}
