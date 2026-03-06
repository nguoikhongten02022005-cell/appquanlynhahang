package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.CartDishAdapter;
import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.Order;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderFragment extends Fragment {

    private static final AtomicInteger ORDER_SEQUENCE = new AtomicInteger(10000);

    private final CartManager cartManager = CartManager.getInstance();

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private CartDishAdapter cartDishAdapter;

    private TextView tvCartEmpty;
    private TextView tvCartTotal;
    private Button btnCheckout;

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (!isAdded()) {
                    return;
                }
                refreshCartUi();
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
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        tvCartEmpty = view.findViewById(R.id.tvCartEmpty);
        tvCartTotal = view.findViewById(R.id.tvCartTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        setupRecyclerView(view);
        btnCheckout.setOnClickListener(v -> checkout());

        refreshCartUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCartUi();
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        cartDishAdapter = new CartDishAdapter(cartManager.getItems(), new CartDishAdapter.OnQuantityActionListener() {
            @Override
            public void onIncrease(CartManager.CartItem item) {
                cartManager.increaseQuantity(cartManager.getDishKey(item));
                refreshCartUi();
            }

            @Override
            public void onDecrease(CartManager.CartItem item) {
                cartManager.decreaseQuantity(cartManager.getDishKey(item));
                refreshCartUi();
            }
        });

        rvOrders.setAdapter(cartDishAdapter);
    }

    private void refreshCartUi() {
        List<CartManager.CartItem> items = cartManager.getItems();
        cartDishAdapter.updateData(items);

        boolean isEmpty = items.isEmpty();
        tvCartEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        btnCheckout.setEnabled(!isEmpty);
        btnCheckout.setAlpha(isEmpty ? 0.5f : 1f);

        tvCartTotal.setText(getString(R.string.cart_total_label, formatPrice(calculateCartTotal(items))));
    }

    private void checkout() {
        List<CartManager.CartItem> items = cartManager.getItems();
        if (items.isEmpty()) {
            refreshCartUi();
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(), getString(R.string.cart_checkout_requires_login), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_RETURN_TO_CALLER, true);
            loginLauncher.launch(intent);
            return;
        }

        long userId = sessionManager.getCurrentUserId();
        if (userId <= 0) {
            sessionManager.clearSession();
            Toast.makeText(requireContext(), getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        List<Order.OrderDish> orderDishes = new ArrayList<>();
        for (CartManager.CartItem item : items) {
            orderDishes.add(new Order.OrderDish(item.getDish(), item.getQuantity()));
        }

        String orderCode = buildOrderCode();
        String orderTime = buildOrderTime();
        String totalPrice = formatPrice(calculateCartTotal(items));

        long newOrderId = databaseHelper.insertOrder(
                (int) userId,
                orderCode,
                orderTime,
                totalPrice,
                Order.Status.PENDING_CONFIRMATION,
                orderDishes
        );

        if (newOrderId <= 0) {
            Toast.makeText(requireContext(), getString(R.string.db_operation_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        cartManager.clearCart();
        refreshCartUi();

        Toast.makeText(requireContext(), getString(R.string.cart_checkout_success), Toast.LENGTH_SHORT).show();
    }

    private int calculateCartTotal(List<CartManager.CartItem> items) {
        int total = 0;
        for (CartManager.CartItem item : items) {
            total += parsePrice(item.getDish().getPrice()) * item.getQuantity();
        }
        return total;
    }

    private int parsePrice(String rawPrice) {
        if (rawPrice == null) {
            return 0;
        }

        String digitsOnly = rawPrice.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(digitsOnly);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String formatPrice(int amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount) + " đ";
    }

    private String buildOrderCode() {
        int next = ORDER_SEQUENCE.incrementAndGet();
        return getString(R.string.cart_order_code_prefix) + next;
    }

    private String buildOrderTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}
