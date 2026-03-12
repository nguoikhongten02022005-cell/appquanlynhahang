package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TAG_HOME = "home";
    private static final String TAG_MENU = "menu";
    private static final String TAG_ACTIVITY_HUB = "activity_hub";
    private static final String TAG_ACCOUNT = "account";

    private TextView tvCartBadge;
    private TextView tvGreeting;
    private BottomNavigationView bottomNavigationView;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private int pendingActivityHubTab = ActivityHubFragment.TAB_ORDERS;
    private String pendingMenuCategory;
    private boolean pendingMenuSearchFocus;

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

        Log.i(TAG, "Bắt đầu khởi tạo SessionManager và DatabaseHelper.");
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        Log.i(TAG, "Bắt đầu mở cơ sở dữ liệu và chạy migration phiên đăng nhập cũ.");
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        Log.i(TAG, "Hoàn tất chuẩn bị cơ sở dữ liệu và migration phiên đăng nhập.");

        tvCartBadge = findViewById(R.id.tvCartBadge);
        tvGreeting = findViewById(R.id.tvGreeting);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (savedInstanceState != null) {
            pendingMenuCategory = savedInstanceState.getString(MenuFragment.ARG_TEN_DANH_MUC);
            pendingMenuSearchFocus = savedInstanceState.getBoolean(MenuFragment.ARG_MO_TIM_KIEM, false);
            pendingActivityHubTab = savedInstanceState.getInt("pending_activity_tab", ActivityHubFragment.TAB_ORDERS);
        }

        setupBottomNavigation();
        setupHeaderActions();
        updateGreeting();
        updateCartBadge();

        if (savedInstanceState == null) {
            showHome();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MenuFragment.ARG_TEN_DANH_MUC, pendingMenuCategory);
        outState.putBoolean(MenuFragment.ARG_MO_TIM_KIEM, pendingMenuSearchFocus);
        outState.putInt("pending_activity_tab", pendingActivityHubTab);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == bottomNavigationView.getSelectedItemId()) {
                if (itemId == R.id.nav_menu) {
                    showMenu();
                } else if (itemId == R.id.nav_orders) {
                    showActivityHub();
                } else if (itemId == R.id.nav_account) {
                    showAccount();
                }
                return itemId != R.id.nav_cart;
            }

            if (itemId == R.id.nav_home) {
                showHome();
                return true;
            }
            if (itemId == R.id.nav_menu) {
                showMenu();
                return true;
            }
            if (itemId == R.id.nav_orders) {
                showActivityHub();
                return true;
            }
            if (itemId == R.id.nav_cart) {
                openCart();
                return false;
            }
            if (itemId == R.id.nav_account) {
                showAccount();
                return true;
            }
            return false;
        });
    }

    private void setupHeaderActions() {
        findViewById(R.id.layoutCartIcon).setOnClickListener(v -> openCart());
        View nutTimKiem = findViewById(R.id.layoutSearchAction);
        if (nutTimKiem != null) {
            nutTimKiem.setOnClickListener(v -> navigateToMenu(null, true));
        }

        View avatar = findViewById(R.id.layoutAvatarAction);
        if (avatar != null) {
            avatar.setOnClickListener(v -> {
                if (bottomNavigationView.getSelectedItemId() == R.id.nav_account) {
                    showAccount();
                    return;
                }
                bottomNavigationView.setSelectedItemId(R.id.nav_account);
            });
        }
    }

    private void showHome() {
        showFragment(findOrCreateHomeFragment(), TAG_HOME);
    }

    private void showMenu() {
        MenuFragment fragment = findOrCreateMenuFragment();
        fragment.applyHomeNavigationState(pendingMenuCategory, pendingMenuSearchFocus);
        showFragment(fragment, TAG_MENU);
    }

    private void showActivityHub() {
        ActivityHubFragment fragment = findActivityHubFragment();
        if (fragment == null) {
            fragment = ActivityHubFragment.newInstance(pendingActivityHubTab);
        }
        showFragment(fragment, TAG_ACTIVITY_HUB);
        fragment.selectTab(pendingActivityHubTab);
        pendingActivityHubTab = ActivityHubFragment.TAB_ORDERS;
    }

    private void showAccount() {
        AccountFragment fragment = findAccountFragment();
        if (fragment == null) {
            fragment = new AccountFragment();
        }
        showFragment(fragment, TAG_ACCOUNT);
        getSupportFragmentManager().executePendingTransactions();
        fragment.onAccountTabSelected();
    }

    private void showFragment(Fragment fragment, String tag) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (currentFragment == fragment || (currentFragment != null && tag.equals(currentFragment.getTag()))) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment, tag)
                .commit();
    }

    private HomeFragment findOrCreateHomeFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
        return fragment instanceof HomeFragment ? (HomeFragment) fragment : new HomeFragment();
    }

    private MenuFragment findOrCreateMenuFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MENU);
        if (fragment instanceof MenuFragment) {
            return (MenuFragment) fragment;
        }
        return MenuFragment.newInstance(pendingMenuCategory, pendingMenuSearchFocus);
    }

    private ActivityHubFragment findActivityHubFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ACTIVITY_HUB);
        return fragment instanceof ActivityHubFragment ? (ActivityHubFragment) fragment : null;
    }

    private AccountFragment findAccountFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ACCOUNT);
        return fragment instanceof AccountFragment ? (AccountFragment) fragment : null;
    }

    public void navigateToMenu() {
        navigateToMenu(null, false);
    }

    public void navigateToMenu(String tenDanhMuc, boolean moTimKiem) {
        pendingMenuCategory = TextUtils.isEmpty(tenDanhMuc) ? null : tenDanhMuc;
        pendingMenuSearchFocus = moTimKiem;
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_menu) {
            showMenu();
            return;
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);
    }

    public void openActivityHub(int tab) {
        pendingActivityHubTab = tab;
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_orders) {
            showActivityHub();
            return;
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);
    }

    private void openCart() {
        startActivity(new Intent(this, CartActivity.class));
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
                            && !TextUtils.equals(name, getString(R.string.db_test_user_name))) {
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
        if (totalQuantity <= 0) {
            tvCartBadge.setVisibility(View.GONE);
            return;
        }

        tvCartBadge.setVisibility(View.VISIBLE);
        tvCartBadge.setText(totalQuantity > 99 ? getString(R.string.cart_badge_overflow) : String.valueOf(totalQuantity));
    }
}
