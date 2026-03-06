package com.example.quanlynhahang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.data.CartManager;

import java.util.ArrayList;
import java.util.List;

public class CartDishAdapter extends RecyclerView.Adapter<CartDishAdapter.CartDishViewHolder> {

    public interface OnQuantityActionListener {
        void onIncrease(CartManager.CartItem item);

        void onDecrease(CartManager.CartItem item);
    }

    private final List<CartManager.CartItem> items = new ArrayList<>();
    private final OnQuantityActionListener onQuantityActionListener;

    public CartDishAdapter(List<CartManager.CartItem> items,
                           OnQuantityActionListener onQuantityActionListener) {
        this.items.addAll(items);
        this.onQuantityActionListener = onQuantityActionListener;
    }

    public void updateData(List<CartManager.CartItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_dish, parent, false);
        return new CartDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartDishViewHolder holder, int position) {
        CartManager.CartItem item = items.get(position);

        holder.ivDishImage.setImageResource(item.getDish().getImageResId());
        holder.tvDishName.setText(item.getDish().getName());
        holder.tvDishQuantity.setText(
                holder.itemView.getContext().getString(R.string.order_quantity_format, item.getQuantity())
        );
        holder.tvDishPrice.setText(
                holder.itemView.getContext().getString(R.string.order_price_format, item.getDish().getPrice())
        );

        holder.layoutActions.setVisibility(View.VISIBLE);
        holder.btnIncrease.setOnClickListener(v -> onQuantityActionListener.onIncrease(item));
        holder.btnDecrease.setOnClickListener(v -> onQuantityActionListener.onDecrease(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartDishViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDishImage;
        private final TextView tvDishName;
        private final TextView tvDishQuantity;
        private final TextView tvDishPrice;
        private final LinearLayout layoutActions;
        private final ImageButton btnIncrease;
        private final ImageButton btnDecrease;

        CartDishViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDishImage = itemView.findViewById(R.id.ivOrderDishImage);
            tvDishName = itemView.findViewById(R.id.tvOrderDishName);
            tvDishQuantity = itemView.findViewById(R.id.tvOrderDishQuantity);
            tvDishPrice = itemView.findViewById(R.id.tvOrderDishPrice);
            layoutActions = itemView.findViewById(R.id.layoutOrderDishActions);
            btnIncrease = itemView.findViewById(R.id.btnOrderDishIncrease);
            btnDecrease = itemView.findViewById(R.id.btnOrderDishDecrease);
        }
    }
}
