package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders = new ArrayList<>();

    public OrderAdapter(List<Order> orders) {
        this.orders.addAll(orders);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_summary, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderCode;
        private final TextView tvOrderTime;
        private final TextView tvOrderTotal;
        private final TextView tvOrderStatus;
        private final Button btnOrderDetail;
        private final Button btnOrderCancel;
        private final LinearLayout layoutOrderDetails;
        private final OrderDishAdapter orderDishAdapter;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnOrderDetail = itemView.findViewById(R.id.btnOrderDetail);
            btnOrderCancel = itemView.findViewById(R.id.btnOrderCancel);
            layoutOrderDetails = itemView.findViewById(R.id.layoutOrderDetails);

            RecyclerView rvOrderDishes = itemView.findViewById(R.id.rvOrderDishes);
            rvOrderDishes.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvOrderDishes.setNestedScrollingEnabled(false);

            orderDishAdapter = new OrderDishAdapter(Collections.emptyList());
            rvOrderDishes.setAdapter(orderDishAdapter);
        }

        void bind(Order order) {
            Context context = itemView.getContext();

            tvOrderCode.setText(order.getCode());
            tvOrderTime.setText(order.getTime());
            tvOrderTotal.setText(order.getTotalPrice());
            tvOrderStatus.setText(getStatusTextRes(order.getStatus()));

            int statusColor = ContextCompat.getColor(context, getStatusColorRes(order.getStatus()));
            ViewCompat.setBackgroundTintList(tvOrderStatus, ColorStateList.valueOf(statusColor));

            orderDishAdapter.updateData(order.getDishes());

            layoutOrderDetails.setVisibility(order.isExpanded() ? View.VISIBLE : View.GONE);
            btnOrderDetail.setText(order.isExpanded() ? R.string.order_hide_details : R.string.order_view_details);
            btnOrderDetail.setOnClickListener(v -> toggleDetails(order));

            btnOrderCancel.setVisibility(order.canCancel() ? View.VISIBLE : View.GONE);
            btnOrderCancel.setOnClickListener(v -> cancelOrder(order));
        }

        private void toggleDetails(Order order) {
            int adapterPosition = getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            order.setExpanded(!order.isExpanded());
            notifyItemChanged(adapterPosition);
        }

        private void cancelOrder(Order order) {
            int adapterPosition = getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION || !order.canCancel()) {
                return;
            }

            order.cancel();
            Toast.makeText(
                    itemView.getContext(),
                    itemView.getContext().getString(R.string.order_cancel_success, order.getCode()),
                    Toast.LENGTH_SHORT
            ).show();
            notifyItemChanged(adapterPosition);
        }
    }

    private int getStatusTextRes(Order.Status status) {
        if (status == Order.Status.PENDING_CONFIRMATION) {
            return R.string.order_status_pending;
        }
        if (status == Order.Status.CONFIRMED) {
            return R.string.order_status_confirmed;
        }
        if (status == Order.Status.COMPLETED) {
            return R.string.order_status_completed;
        }
        return R.string.order_status_canceled;
    }

    private int getStatusColorRes(Order.Status status) {
        if (status == Order.Status.PENDING_CONFIRMATION) {
            return R.color.brand_orange;
        }
        if (status == Order.Status.CONFIRMED) {
            return R.color.brand_green;
        }
        if (status == Order.Status.COMPLETED) {
            return R.color.hero_top;
        }
        return R.color.brand_red;
    }
}
