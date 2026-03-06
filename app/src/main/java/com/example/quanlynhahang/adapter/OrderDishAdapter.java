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
import com.example.quanlynhahang.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDishAdapter extends RecyclerView.Adapter<OrderDishAdapter.OrderDishViewHolder> {

    private final List<Order.OrderDish> items = new ArrayList<>();

    public OrderDishAdapter(List<Order.OrderDish> items) {
        this.items.addAll(items);
    }

    public void updateData(List<Order.OrderDish> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_dish, parent, false);
        return new OrderDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDishViewHolder holder, int position) {
        Order.OrderDish item = items.get(position);

        holder.ivOrderDishImage.setImageResource(item.getDishItem().getImageResId());
        holder.tvOrderDishName.setText(item.getDishItem().getName());
        holder.tvOrderDishQuantity.setText(
                holder.itemView.getContext().getString(
                        R.string.order_quantity_format,
                        item.getQuantity()
                )
        );
        holder.tvOrderDishPrice.setText(
                holder.itemView.getContext().getString(
                        R.string.order_price_format,
                        item.getDishItem().getPrice()
                )
        );

        holder.layoutOrderDishActions.setVisibility(View.GONE);
        holder.btnOrderDishIncrease.setOnClickListener(null);
        holder.btnOrderDishDecrease.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderDishViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivOrderDishImage;
        private final TextView tvOrderDishName;
        private final TextView tvOrderDishQuantity;
        private final TextView tvOrderDishPrice;
        private final LinearLayout layoutOrderDishActions;
        private final ImageButton btnOrderDishIncrease;
        private final ImageButton btnOrderDishDecrease;

        OrderDishViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrderDishImage = itemView.findViewById(R.id.ivOrderDishImage);
            tvOrderDishName = itemView.findViewById(R.id.tvOrderDishName);
            tvOrderDishQuantity = itemView.findViewById(R.id.tvOrderDishQuantity);
            tvOrderDishPrice = itemView.findViewById(R.id.tvOrderDishPrice);
            layoutOrderDishActions = itemView.findViewById(R.id.layoutOrderDishActions);
            btnOrderDishIncrease = itemView.findViewById(R.id.btnOrderDishIncrease);
            btnOrderDishDecrease = itemView.findViewById(R.id.btnOrderDishDecrease);
        }
    }
}
