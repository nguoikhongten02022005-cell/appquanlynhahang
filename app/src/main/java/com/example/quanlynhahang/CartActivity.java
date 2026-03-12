package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.CartDishAdapter;
import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.Order;
import com.example.quanlynhahang.model.RecommendedDishItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private TextView tvCartTotal;
    private TextView tvCartEmpty;
    private Button btnCheckout;
    private Button btnClearCart;
    private Button btnContinueShopping;

    private CartDishAdapter cartAdapter;
    private CartManager cartManager;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private boolean pendingCheckoutAfterLogin;

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (pendingCheckoutAfterLogin && sessionManager.isLoggedIn()) {
                    pendingCheckoutAfterLogin = false;
                    handleCheckout();
                    return;
                }
                pendingCheckoutAfterLogin = false;
                updateCartDisplay();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        initViews();
        setupRecyclerView();
        setupCheckoutButton();
        setupActionButtons();
        updateCartDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartDisplay();
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        tvCartEmpty = findViewById(R.id.tvCartEmpty);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void setupRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartDishAdapter(
                cartManager.getItems(),
                new CartDishAdapter.OnQuantityActionListener() {
                    @Override
                    public void onIncrease(CartManager.CartItem item) {
                        String key = cartManager.getDishKey(item);
                        cartManager.increaseQuantity(key);
                        updateCartDisplay();
                    }

                    @Override
                    public void onDecrease(CartManager.CartItem item) {
                        String key = cartManager.getDishKey(item);
                        cartManager.decreaseQuantity(key);
                        updateCartDisplay();
                    }

                    @Override
                    public void onRemove(CartManager.CartItem item) {
                        removeSingleItem(item);
                    }
                }
        );

        rvCartItems.setAdapter(cartAdapter);
    }

    private void setupCheckoutButton() {
        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void setupActionButtons() {
        btnClearCart.setOnClickListener(v -> clearAllCartItems());
        btnContinueShopping.setOnClickListener(v -> finish());
    }

    private void updateCartDisplay() {
        List<CartManager.CartItem> items = cartManager.getItems();
        cartAdapter.updateData(items);

        long totalPrice = calculateTotalPrice(items);
        tvCartTotal.setText(getString(R.string.cart_total_label, formatPrice(totalPrice)));

        boolean isEmpty = items.isEmpty();
        rvCartItems.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvCartEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        btnContinueShopping.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        btnClearCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        btnCheckout.setEnabled(!isEmpty);
        btnCheckout.setAlpha(isEmpty ? 0.5f : 1f);
    }

    private long calculateTotalPrice(List<CartManager.CartItem> items) {
        long total = 0;
        for (CartManager.CartItem item : items) {
            long price = parsePriceFromString(item.getDish().getPrice());
            total += price * item.getQuantity();
        }
        return total;
    }

    private long parsePriceFromString(@Nullable String priceString) {
        if (priceString == null || priceString.isEmpty()) {
            return 0;
        }

        String cleaned = priceString.replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatPrice(long price) {
        return String.format(Locale.getDefault(), "%,d đ", price);
    }

    private void removeSingleItem(@NonNull CartManager.CartItem item) {
        String dishName = item.getDish().getName();
        String key = cartManager.getDishKey(item);
        cartManager.removeItem(key);
        updateCartDisplay();
        Toast.makeText(this, getString(R.string.cart_item_removed, dishName), Toast.LENGTH_SHORT).show();
    }

    private void clearAllCartItems() {
        if (cartManager.isEmpty()) {
            updateCartDisplay();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage(R.string.cart_clear)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    cartManager.clearCart();
                    updateCartDisplay();
                    Toast.makeText(this, R.string.cart_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void handleCheckout() {
        List<CartManager.CartItem> items = cartManager.getItems();
        if (items.isEmpty()) {
            Toast.makeText(this, R.string.cart_empty_message, Toast.LENGTH_SHORT).show();
            updateCartDisplay();
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            pendingCheckoutAfterLogin = true;
            Toast.makeText(this, R.string.cart_checkout_requires_login, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_RETURN_TO_CALLER, true);
            loginLauncher.launch(intent);
            return;
        }

        long userId = sessionManager.getCurrentUserId();
        if (userId <= 0) {
            pendingCheckoutAfterLogin = true;
            sessionManager.clearSession();
            Toast.makeText(this, R.string.session_invalid, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_RETURN_TO_CALLER, true);
            loginLauncher.launch(intent);
            return;
        }

        String orderCode = generateOrderCode();
        String orderTime = getCurrentDateTime();
        long totalPrice = calculateTotalPrice(items);
        String totalPriceString = formatPrice(totalPrice);

        List<Order.OrderDish> orderDishes = new ArrayList<>();
        for (CartManager.CartItem item : items) {
            RecommendedDishItem dish = item.getDish();
            orderDishes.add(new Order.OrderDish(dish, item.getQuantity()));
        }

        long orderId = databaseHelper.insertOrder(
                (int) userId,
                orderCode,
                orderTime,
                totalPriceString,
                Order.Status.PENDING_CONFIRMATION,
                orderDishes
        );

        if (orderId > 0) {
            pendingCheckoutAfterLogin = false;
            Toast.makeText(this, R.string.cart_checkout_success, Toast.LENGTH_SHORT).show();
            cartManager.clearCart();
            updateCartDisplay();
            finish();
        } else {
            Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private String generateOrderCode() {
        long timestamp = System.currentTimeMillis();
        return getString(R.string.cart_order_code_prefix) + timestamp % 100000;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}
