package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Collections;
import java.util.List;

public class OrderFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private RecyclerView rvOrders;
    private TextView tvOrderEmpty;
    private OrderAdapter orderAdapter;

    private boolean hasPromptedLogin;
    private boolean embedded;

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                hasPromptedLogin = false;
                refreshOrdersUi(false);
            }
    );

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
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvOrderEmpty = view.findViewById(R.id.tvCartEmpty);

        View titleView = view.findViewById(R.id.tvOrderTitle);
        if (embedded && titleView != null) {
            titleView.setVisibility(View.GONE);
        }

        View layoutCartFooter = view.findViewById(R.id.layoutCartFooter);
        if (layoutCartFooter != null) {
            layoutCartFooter.setVisibility(View.GONE);
        }

        setupRecyclerView();
        refreshOrdersUi(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshOrdersUi(false);
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new OrderAdapter(new ArrayList<>(), this::cancelOrder);
        rvOrders.setAdapter(orderAdapter);
    }

    private void refreshOrdersUi(boolean autoLaunchLogin) {
        if (!isAdded()) {
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            showLoginRequiredState();
            if (!embedded && autoLaunchLogin && !hasPromptedLogin) {
                hasPromptedLogin = true;
                launchLogin();
            }
            return;
        }

        long userId = sessionManager.getCurrentUserId();
        if (userId <= 0) {
            sessionManager.clearSession();
            showLoginRequiredState();
            if (!embedded && autoLaunchLogin && !hasPromptedLogin) {
                hasPromptedLogin = true;
                launchLogin();
            }
            return;
        }

        hasPromptedLogin = false;

        List<Order> orders = databaseHelper.getOrdersByUserId((int) userId);
        orderAdapter.updateData(orders);

        if (orders.isEmpty()) {
            showEmptyState(getString(R.string.order_empty_message));
            return;
        }

        showListState();
    }

    private void cancelOrder(Order order, int position) {
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
    }

    private void showLoginRequiredState() {
        orderAdapter.updateData(Collections.emptyList());
        showEmptyState(getString(R.string.order_login_required));
    }

    private void showEmptyState(String message) {
        tvOrderEmpty.setText(message);
        tvOrderEmpty.setVisibility(View.VISIBLE);
        rvOrders.setVisibility(View.GONE);
    }

    private void showListState() {
        tvOrderEmpty.setVisibility(View.GONE);
        rvOrders.setVisibility(View.VISIBLE);
    }

    private void launchLogin() {
        if (!isAdded()) {
            return;
        }

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_RETURN_TO_CALLER, true);
        loginLauncher.launch(intent);
    }
}
