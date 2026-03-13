package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.User;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MAX_BADGE_COUNT = 99;
    private static final String TAG_HOME = "home";
    private static final String TAG_MENU = "menu";
    private static final String TAG_ACTIVITY_HUB = "activity_hub";
    private static final String TAG_ACCOUNT = "account";
    private static final String KEY_PENDING_ACTIVITY_TAB = "pending_activity_tab";
    private static final String KEY_HAS_PENDING_MENU_NAVIGATION = "has_pending_menu_navigation";

    private TextView tvCartBadge;
    private TextView tvGreeting;
    private BottomNavigationView bottomNavigationView;

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private int pendingActivityHubTab = ActivityHubFragment.TAB_ORDERS;
    private String pendingMenuCategory;
    private boolean pendingMenuSearchFocus;
    private String pendingMenuQuery;
    private boolean hasPendingMenuNavigation;

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
            pendingMenuQuery = savedInstanceState.getString(MenuFragment.ARG_TU_KHOA_TIM_KIEM);
            hasPendingMenuNavigation = savedInstanceState.getBoolean(KEY_HAS_PENDING_MENU_NAVIGATION, false);
            pendingActivityHubTab = savedInstanceState.getInt(KEY_PENDING_ACTIVITY_TAB, ActivityHubFragment.TAB_ORDERS);
        }

        setupBottomNavigation();
        setupHeaderActions();
        refreshHeaderState();

        if (savedInstanceState == null) {
            showHome();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CartManager.getInstance().addListener(cartListener);
        refreshHeaderState();
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
        outState.putString(MenuFragment.ARG_TU_KHOA_TIM_KIEM, pendingMenuQuery);
        outState.putBoolean(KEY_HAS_PENDING_MENU_NAVIGATION, hasPendingMenuNavigation);
        outState.putInt(KEY_PENDING_ACTIVITY_TAB, pendingActivityHubTab);
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
            nutTimKiem.setOnClickListener(v -> navigateToMenu(null, true, null));
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
        if (hasPendingMenuNavigation) {
            fragment.applyHomeNavigationState(pendingMenuCategory, pendingMenuSearchFocus, pendingMenuQuery);
        }
        showFragment(fragment, TAG_MENU);
        hasPendingMenuNavigation = false;
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
        return MenuFragment.newInstance(pendingMenuCategory, pendingMenuSearchFocus, pendingMenuQuery);
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
        navigateToMenu(null, false, null);
    }

    public void navigateToMenu(@Nullable String tenDanhMuc, boolean moTimKiem) {
        navigateToMenu(tenDanhMuc, moTimKiem, null);
    }

    public void navigateToMenu(@Nullable String tenDanhMuc, boolean moTimKiem, @Nullable String tuKhoaTimKiem) {
        pendingMenuCategory = TextUtils.isEmpty(tenDanhMuc) ? null : tenDanhMuc;
        pendingMenuSearchFocus = moTimKiem;
        pendingMenuQuery = TextUtils.isEmpty(tuKhoaTimKiem) ? null : tuKhoaTimKiem;
        hasPendingMenuNavigation = true;
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

    public void refreshHeaderState() {
        updateGreeting();
        updateCartBadge();
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
        int totalQuantity = CartManager.getInstance().getTotalQuantity();
        String badgeText = dinhDangSoLuongBadge(totalQuantity);

        if (tvCartBadge != null) {
            if (badgeText == null) {
                tvCartBadge.setVisibility(View.GONE);
            } else {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(badgeText);
            }
        }

        if (bottomNavigationView == null) {
            return;
        }

        if (badgeText == null) {
            bottomNavigationView.removeBadge(R.id.nav_cart);
            return;
        }

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
        badgeDrawable.setVisible(true);
        badgeDrawable.setMaxCharacterCount(3);
        badgeDrawable.setNumber(Math.min(totalQuantity, MAX_BADGE_COUNT));
    }

    @Nullable
    private String dinhDangSoLuongBadge(int totalQuantity) {
        if (totalQuantity <= 0) {
            return null;
        }
        if (totalQuantity > MAX_BADGE_COUNT) {
            return getString(R.string.cart_badge_overflow);
        }
        return String.valueOf(totalQuantity);
    }
}
