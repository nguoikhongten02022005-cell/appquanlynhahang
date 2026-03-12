package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.CategoryAdapter;
import com.example.quanlynhahang.adapter.RecommendedDishAdapter;
import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.CategoryItem;
import com.example.quanlynhahang.model.RecommendedDishItem;
import com.example.quanlynhahang.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvCartBadge;
    private TextView tvGreeting;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private final CartManager.CartListener cartListener = this::updateCartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        tvCartBadge = findViewById(R.id.tvCartBadge);
        tvGreeting = findViewById(R.id.tvGreeting);

        setupBottomNavigation();
        setupHeaderActions();
        updateGreeting();
        showHome();
        updateCartBadge();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CartManager.getInstance().addListener(cartListener);
        updateGreeting();
        updateCartBadge();
    }

    @Override
    protected void onStop() {
        CartManager.getInstance().removeListener(cartListener);
        super.onStop();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                showHome();
                return true;
            }

            if (item.getItemId() == R.id.nav_menu) {
                showMenu();
                return true;
            }

            if (item.getItemId() == R.id.nav_orders) {
                showRequests();
                return true;
            }

            if (item.getItemId() == R.id.nav_cart) {
                openCart();
                return true;
            }

            if (item.getItemId() == R.id.nav_account) {
                showAccount();
                return true;
            }

            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void setupHeaderActions() {
        View layoutCartIcon = findViewById(R.id.layoutCartIcon);
        layoutCartIcon.setOnClickListener(v -> openCart());
    }

    private void openCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    private void updateGreeting() {
        if (tvGreeting == null) {
            return;
        }

        String displayName = getString(R.string.account_guest_name);

        if (sessionManager != null && databaseHelper != null && sessionManager.isLoggedIn()) {
            long currentUserId = sessionManager.getCurrentUserId();
            if (currentUserId > 0) {
                User currentUser = databaseHelper.getUserById(currentUserId);
                if (currentUser != null) {
                    String name = currentUser.getName();
                    String email = currentUser.getEmail();
                    String defaultName = getString(R.string.account_default_name);

                    if (!TextUtils.isEmpty(name)
                            && !TextUtils.equals(name, defaultName)
                            && !TextUtils.equals(name, "Khách hàng Test")) {
                        displayName = name;
                    } else if (!TextUtils.isEmpty(email)) {
                        displayName = email;
                    }
                }
            }
        }

        tvGreeting.setText(getString(R.string.home_greeting_format, displayName));
    }

    private void updateCartBadge() {
        if (tvCartBadge == null) {
            return;
        }

        int totalQuantity = CartManager.getInstance().getTotalQuantity();
        tvCartBadge.setText(totalQuantity > 99 ? "99+" : String.valueOf(totalQuantity));
    }

    private void showHome() {
        View mainScrollView = findViewById(R.id.mainScrollView);
        View fragmentContainer = findViewById(R.id.fragmentContainer);

        mainScrollView.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);

        setupCategoryList();
        setupRecommendedDishGrid();
    }

    private void showMenu() {
        View mainScrollView = findViewById(R.id.mainScrollView);
        View fragmentContainer = findViewById(R.id.fragmentContainer);

        mainScrollView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        if (!(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof MenuFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new MenuFragment())
                    .commit();
        }
    }

    private void showOrders() {
        View mainScrollView = findViewById(R.id.mainScrollView);
        View fragmentContainer = findViewById(R.id.fragmentContainer);

        mainScrollView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        if (!(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof OrderFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new OrderFragment())
                    .commit();
        }
    }

    private void showRequests() {
        View mainScrollView = findViewById(R.id.mainScrollView);
        View fragmentContainer = findViewById(R.id.fragmentContainer);

        mainScrollView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        if (!(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof RequestsFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new RequestsFragment())
                    .commit();
        }
    }

    private void showAccount() {
        View mainScrollView = findViewById(R.id.mainScrollView);
        View fragmentContainer = findViewById(R.id.fragmentContainer);

        mainScrollView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        if (!(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof AccountFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new AccountFragment())
                    .commitNow();
        }

        AccountFragment accountFragment =
                (AccountFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (accountFragment != null) {
            accountFragment.onAccountTabSelected();
        }
    }

    private void setupCategoryList() {
        RecyclerView rvCategory = findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        rvCategory.setAdapter(new CategoryAdapter(getMockCategories()));
    }

    private void setupRecommendedDishGrid() {
        RecyclerView rvRecommended = findViewById(R.id.rvRecommended);
        rvRecommended.setLayoutManager(new GridLayoutManager(this, 2));
        rvRecommended.setNestedScrollingEnabled(false);
        rvRecommended.setAdapter(new RecommendedDishAdapter(getMockRecommendedDishes()));
    }

    private List<CategoryItem> getMockCategories() {
        List<CategoryItem> categories = new ArrayList<>();
        categories.add(new CategoryItem(R.drawable.ic_restaurant_24, getString(R.string.category_main_course)));
        categories.add(new CategoryItem(R.drawable.ic_restaurant_24, getString(R.string.category_hotpot)));
        categories.add(new CategoryItem(R.drawable.ic_local_drink_24, getString(R.string.category_drink)));
        categories.add(new CategoryItem(R.drawable.ic_restaurant_24, getString(R.string.category_dessert)));
        categories.add(new CategoryItem(R.drawable.ic_menu_24, getString(R.string.category_combo)));
        return categories;
    }

    private List<RecommendedDishItem> getMockRecommendedDishes() {
        List<RecommendedDishItem> dishes = new ArrayList<>();
        dishes.add(new RecommendedDishItem(
                R.drawable.ic_restaurant_24,
                getString(R.string.dish_bo_luc_lac),
                getString(R.string.price_145k),
                true
        ));
        dishes.add(new RecommendedDishItem(
                R.drawable.ic_restaurant_24,
                getString(R.string.dish_salad_ca_hoi),
                getString(R.string.price_129k),
                true
        ));
        dishes.add(new RecommendedDishItem(
                R.drawable.ic_restaurant_24,
                getString(R.string.dish_lau_thai),
                getString(R.string.price_259k),
                false
        ));
        dishes.add(new RecommendedDishItem(
                R.drawable.ic_local_drink_24,
                getString(R.string.dish_tra_dao),
                getString(R.string.price_45k),
                true
        ));
        return dishes;
    }
}
