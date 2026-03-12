package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.CategoryAdapter;
import com.example.quanlynhahang.adapter.RecommendedDishAdapter;
import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.CategoryItem;
import com.example.quanlynhahang.model.RecommendedDishItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int SO_MON_DE_XUAT = 4;
    private static final int KHONG_CO_DANH_MUC_DANG_CHON = -1;

    private final List<CategoryItem> categories = new ArrayList<>();
    private final List<RecommendedDishItem> recommendedDishes = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());

        setupCategoryData();
        setupRecommendedData();
        setupHeroActions(view);
        setupCategoryList(view);
        setupRecommendedGrid(view);
    }

    private void setupHeroActions(View view) {
        MaterialButton btnHeroCta = view.findViewById(R.id.btnHeroCta);
        View actionOrder = view.findViewById(R.id.actionQuickOrder);
        View actionBook = view.findViewById(R.id.actionQuickBook);

        btnHeroCta.setOnClickListener(v -> {
            resetCategorySelection();
            navigateToMenu(null, true, null);
        });
        actionOrder.setOnClickListener(v -> {
            resetCategorySelection();
            navigateToMenu(null, true, null);
        });
        actionBook.setOnClickListener(v -> navigateToRequests());
    }

    private void setupCategoryList(View view) {
        RecyclerView rvCategory = view.findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categories, (item, position) -> {
            if (categoryAdapter != null) {
                categoryAdapter.setSelectedPosition(position);
            }
            navigateToMenu(item.getTenDanhMuc(), false, null);
        }, KHONG_CO_DANH_MUC_DANG_CHON);
        rvCategory.setAdapter(categoryAdapter);
    }

    private void setupRecommendedGrid(View view) {
        RecyclerView rvRecommended = view.findViewById(R.id.rvRecommended);
        rvRecommended.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvRecommended.setNestedScrollingEnabled(false);
        rvRecommended.setAdapter(new RecommendedDishAdapter(recommendedDishes, new RecommendedDishAdapter.OnDishActionListener() {
            @Override
            public void onDishClick(RecommendedDishItem item) {
                resetCategorySelection();
                navigateToMenu(item.getTenDanhMuc(), false, item.getTenMon());
            }

            @Override
            public void onAddDishClick(RecommendedDishItem item) {
                CartManager.getInstance().addToCart(item);
                Toast.makeText(
                        requireContext(),
                        getString(R.string.menu_added_to_cart, item.getTenMon()),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }));
    }

    private void setupCategoryData() {
        categories.clear();
        categories.add(new CategoryItem(
                R.drawable.ic_restaurant_24,
                getString(R.string.category_main_course),
                getString(R.string.category_main_course)
        ));
        categories.add(new CategoryItem(
                R.drawable.ic_receipt_24,
                getString(R.string.category_hotpot),
                getString(R.string.category_hotpot)
        ));
        categories.add(new CategoryItem(
                R.drawable.ic_local_drink_24,
                getString(R.string.category_drink),
                getString(R.string.category_drink)
        ));
        categories.add(new CategoryItem(
                R.drawable.ic_calendar_24,
                getString(R.string.category_dessert),
                getString(R.string.category_dessert)
        ));
        categories.add(new CategoryItem(
                R.drawable.ic_menu_24,
                getString(R.string.category_combo),
                getString(R.string.category_combo)
        ));
    }

    private void setupRecommendedData() {
        recommendedDishes.clear();
        recommendedDishes.addAll(databaseHelper.getMonDeXuatTrangChu(SO_MON_DE_XUAT));
    }

    private void resetCategorySelection() {
        if (categoryAdapter != null) {
            categoryAdapter.setSelectedPosition(KHONG_CO_DANH_MUC_DANG_CHON);
        }
    }

    private void navigateToMenu(@Nullable String tenDanhMuc,
                                boolean sanSangTimKiem,
                                @Nullable String tuKhoaTimKiem) {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).navigateToMenu(tenDanhMuc, sanSangTimKiem, tuKhoaTimKiem);
        }
    }

    private void navigateToRequests() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).openActivityHub(ActivityHubFragment.TAB_REQUESTS);
        }
    }
}
