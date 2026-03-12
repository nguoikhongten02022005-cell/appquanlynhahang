package com.example.quanlynhahang.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.CategoryItem;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem item, int position);
    }

    private static final int KHONG_CO_DANH_MUC_DANG_CHON = -1;

    private final List<CategoryItem> items;
    private final OnCategoryClickListener onCategoryClickListener;
    private int selectedPosition;

    public CategoryAdapter(List<CategoryItem> items, OnCategoryClickListener onCategoryClickListener) {
        this(items, onCategoryClickListener, KHONG_CO_DANH_MUC_DANG_CHON);
    }

    public CategoryAdapter(List<CategoryItem> items,
                           OnCategoryClickListener onCategoryClickListener,
                           int selectedPosition) {
        this.items = items;
        this.onCategoryClickListener = onCategoryClickListener;
        this.selectedPosition = isViTriHopLe(selectedPosition) ? selectedPosition : KHONG_CO_DANH_MUC_DANG_CHON;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        boolean isSelected = position == selectedPosition;

        holder.ivCategoryIcon.setImageResource(item.getIconResId());
        holder.tvCategoryName.setText(item.getTenHienThi());

        holder.itemView.setSelected(isSelected);
        holder.iconContainer.setSelected(isSelected);

        int iconColor = ContextCompat.getColor(
                holder.itemView.getContext(),
                isSelected ? R.color.category_selected_icon : R.color.category_unselected_icon
        );
        int textColor = ContextCompat.getColor(
                holder.itemView.getContext(),
                isSelected ? R.color.on_surface : R.color.on_surface_variant
        );

        holder.ivCategoryIcon.setImageTintList(ColorStateList.valueOf(iconColor));
        holder.tvCategoryName.setTextColor(textColor);

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            setSelectedPosition(adapterPosition);
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(items.get(adapterPosition), adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedPosition(int newPosition) {
        int viTriMoi = isViTriHopLe(newPosition) ? newPosition : KHONG_CO_DANH_MUC_DANG_CHON;
        if (viTriMoi == selectedPosition) {
            return;
        }

        int previousPosition = selectedPosition;
        selectedPosition = viTriMoi;

        if (isViTriHopLe(previousPosition)) {
            notifyItemChanged(previousPosition);
        }
        if (isViTriHopLe(selectedPosition)) {
            notifyItemChanged(selectedPosition);
        }
    }

    private boolean isViTriHopLe(int position) {
        return position >= 0 && position < items.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout iconContainer;
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.categoryIconContainer);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
