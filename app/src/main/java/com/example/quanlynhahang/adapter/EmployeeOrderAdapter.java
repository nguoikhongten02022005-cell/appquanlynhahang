package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.Order;

import java.util.ArrayList;
import java.util.List;

public class EmployeeOrderAdapter extends RecyclerView.Adapter<EmployeeOrderAdapter.EmployeeOrderViewHolder> {

    public interface ActionListener {
        void onConfirm(Order order);

        void onComplete(Order order);

        void onCancel(Order order);
    }

    private final List<Order> orders = new ArrayList<>();
    private final ActionListener actionListener;

    public EmployeeOrderAdapter(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<Order> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee_order, parent, false);
        return new EmployeeOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeOrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class EmployeeOrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderCode;
        private final TextView tvOrderTime;
        private final TextView tvOrderTotal;
        private final TextView tvOrderStatus;
        private final TextView tvToggleDetails;
        private final LinearLayout layoutOrderDetails;
        private final TextView tvEmptyDishes;
        private final TextView btnConfirm;
        private final TextView btnComplete;
        private final TextView btnCancel;
        private final OrderDishAdapter orderDishAdapter;

        EmployeeOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvEmployeeOrderCode);
            tvOrderTime = itemView.findViewById(R.id.tvEmployeeOrderTime);
            tvOrderTotal = itemView.findViewById(R.id.tvEmployeeOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvEmployeeOrderStatus);
            tvToggleDetails = itemView.findViewById(R.id.tvEmployeeOrderToggleDetails);
            layoutOrderDetails = itemView.findViewById(R.id.layoutEmployeeOrderDetails);
            tvEmptyDishes = itemView.findViewById(R.id.tvEmployeeOrderEmptyDishes);
            btnConfirm = itemView.findViewById(R.id.btnEmployeeOrderConfirm);
            btnComplete = itemView.findViewById(R.id.btnEmployeeOrderComplete);
            btnCancel = itemView.findViewById(R.id.btnEmployeeOrderCancel);

            RecyclerView rvOrderDishes = itemView.findViewById(R.id.rvEmployeeOrderDishes);
            rvOrderDishes.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvOrderDishes.setNestedScrollingEnabled(false);
            orderDishAdapter = new OrderDishAdapter(new ArrayList<>());
            rvOrderDishes.setAdapter(orderDishAdapter);
        }

        void bind(Order order) {
            Context context = itemView.getContext();
            tvOrderCode.setText(order.getCode());
            tvOrderTime.setText(order.getTime());
            tvOrderTotal.setText(order.getTotalPrice());
            tvOrderStatus.setText(getStatusText(order.getStatus()));
            ViewCompat.setBackgroundTintList(tvOrderStatus, ColorStateList.valueOf(ContextCompat.getColor(context, getStatusColor(order.getStatus()))));

            orderDishAdapter.updateData(order.getDishes());
            boolean hasDishes = !order.getDishes().isEmpty();
            tvEmptyDishes.setVisibility(hasDishes ? View.GONE : View.VISIBLE);
            layoutOrderDetails.setVisibility(order.isExpanded() ? View.VISIBLE : View.GONE);
            tvToggleDetails.setText(order.isExpanded() ? R.string.employee_order_toggle_hide : R.string.employee_order_toggle_view);
            tvToggleDetails.setOnClickListener(v -> toggleDetails(order));

            bindAction(btnConfirm, order.getStatus() == Order.Status.PENDING_CONFIRMATION, v -> actionListener.onConfirm(order));
            bindAction(btnComplete, order.getStatus() == Order.Status.CONFIRMED, v -> actionListener.onComplete(order));
            bindAction(btnCancel, order.getStatus() == Order.Status.PENDING_CONFIRMATION || order.getStatus() == Order.Status.CONFIRMED, v -> actionListener.onCancel(order));
        }

        private void toggleDetails(Order order) {
            int adapterPosition = getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            order.setExpanded(!order.isExpanded());
            notifyItemChanged(adapterPosition);
        }

        private void bindAction(TextView view, boolean visible, View.OnClickListener onClickListener) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            view.setOnClickListener(visible ? onClickListener : null);
        }
    }

    private int getStatusText(Order.Status status) {
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

    private int getStatusColor(Order.Status status) {
        if (status == Order.Status.PENDING_CONFIRMATION) {
            return R.color.warning;
        }
        if (status == Order.Status.CONFIRMED) {
            return R.color.success;
        }
        if (status == Order.Status.COMPLETED) {
            return R.color.primary;
        }
        return R.color.error;
    }
}
