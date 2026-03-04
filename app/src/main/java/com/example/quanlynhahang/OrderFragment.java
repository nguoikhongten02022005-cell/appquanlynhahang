package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.OrderAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private final List<Order> orders = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private OrderAdapter orderAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        loadOrders();
        setupRecyclerView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
        if (orderAdapter != null) {
            orderAdapter.updateData(orders);
        }
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new OrderAdapter(orders, (order, position) -> {
            boolean canceled = databaseHelper.cancelOrder(order.getId());
            if (!canceled) {
                Toast.makeText(requireContext(), getString(R.string.db_operation_failed), Toast.LENGTH_SHORT).show();
                return;
            }

            order.cancel();
            orderAdapter.notifyItemChanged(position);
            Toast.makeText(
                    requireContext(),
                    getString(R.string.order_cancel_success, order.getCode()),
                    Toast.LENGTH_SHORT
            ).show();
        });
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        orders.clear();

        long userId = sessionManager.getCurrentUserId();
        if (userId <= 0 || !sessionManager.isLoggedIn()) {
            return;
        }

        orders.addAll(databaseHelper.getOrdersByUserId((int) userId));
    }
}
