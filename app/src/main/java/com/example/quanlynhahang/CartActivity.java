package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button btnCheckout;

    private CartDishAdapter cartAdapter;
    private CartManager cartManager;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        updateCartDisplay();
        setupCheckoutButton();
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
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
                }
        );

        rvCartItems.setAdapter(cartAdapter);
    }

    private void updateCartDisplay() {
        List<CartManager.CartItem> items = cartManager.getItems();
        cartAdapter.updateData(items);

        long totalPrice = calculateTotalPrice(items);
        tvCartTotal.setText(getString(R.string.cart_total_label, formatPrice(totalPrice)));
    }

    private long calculateTotalPrice(List<CartManager.CartItem> items) {
        long total = 0;
        for (CartManager.CartItem item : items) {
            long price = parsePriceFromString(item.getDish().getPrice());
            total += price * item.getQuantity();
        }
        return total;
    }

    private long parsePriceFromString(String priceString) {
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

    private void setupCheckoutButton() {
        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void handleCheckout() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, R.string.cart_checkout_requires_login, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        List<CartManager.CartItem> items = cartManager.getItems();
        if (items.isEmpty()) {
            Toast.makeText(this, R.string.cart_empty_message, Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = sessionManager.getCurrentUserId();
        String orderCode = generateOrderCode();
        String orderTime = getCurrentDateTime();
        long totalPrice = calculateTotalPrice(items);
        String totalPriceString = formatPrice(totalPrice);

        List<Order.OrderDish> orderDishes = new ArrayList<>();
        for (CartManager.CartItem item : items) {
            orderDishes.add(new Order.OrderDish(item.getDish(), item.getQuantity()));
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
            Toast.makeText(this, R.string.cart_checkout_success, Toast.LENGTH_SHORT).show();
            cartManager.clearCart();
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
