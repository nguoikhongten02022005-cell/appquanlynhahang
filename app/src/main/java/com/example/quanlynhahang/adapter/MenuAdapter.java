package com.example.quanlynhahang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.RecommendedDishItem;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    public interface OnAddDishClickListener {
        void onAddDishClick(RecommendedDishItem dish);
    }

    private final List<RecommendedDishItem> items = new ArrayList<>();
    private final List<String> descriptions = new ArrayList<>();
    private final OnAddDishClickListener onAddDishClickListener;

    public MenuAdapter(List<RecommendedDishItem> items,
                       List<String> descriptions,
                       OnAddDishClickListener onAddDishClickListener) {
        this.items.addAll(items);
        this.descriptions.addAll(descriptions);
        this.onAddDishClickListener = onAddDishClickListener;
    }

    public void updateData(List<RecommendedDishItem> newItems, List<String> newDescriptions) {
        items.clear();
        descriptions.clear();
        items.addAll(newItems);
        descriptions.addAll(newDescriptions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_full, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        RecommendedDishItem item = items.get(position);

        holder.ivMenuDishImage.setImageResource(item.getImageResId());
        holder.tvMenuDishName.setText(item.getName());
        holder.tvMenuDishDescription.setText(descriptions.get(position));
        holder.tvMenuDishPrice.setText(item.getPrice());

        holder.btnMenuAddDish.setEnabled(item.isAvailable());
        holder.btnMenuAddDish.setAlpha(item.isAvailable() ? 1f : 0.5f);
        holder.btnMenuAddDish.setOnClickListener(v -> {
            if (item.isAvailable()) {
                onAddDishClickListener.onAddDishClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMenuDishImage;
        private final TextView tvMenuDishName;
        private final TextView tvMenuDishDescription;
        private final TextView tvMenuDishPrice;
        private final ImageButton btnMenuAddDish;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuDishImage = itemView.findViewById(R.id.ivMenuDishImage);
            tvMenuDishName = itemView.findViewById(R.id.tvMenuDishName);
            tvMenuDishDescription = itemView.findViewById(R.id.tvMenuDishDescription);
            tvMenuDishPrice = itemView.findViewById(R.id.tvMenuDishPrice);
            btnMenuAddDish = itemView.findViewById(R.id.btnMenuAddDish);
        }
    }
}
