package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MenuAdapter;
import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.RecommendedDishItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuFragment extends Fragment {

    public static final String ARG_TEN_DANH_MUC = "ten_danh_muc";
    public static final String ARG_MO_TIM_KIEM = "mo_tim_kiem";

    private final List<RecommendedDishItem> allDishes = new ArrayList<>();
    private final List<String> allDescriptions = new ArrayList<>();
    private final List<RecommendedDishItem> filteredDishes = new ArrayList<>();
    private final List<String> filteredDescriptions = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private MenuAdapter menuAdapter;
    private EditText etMenuSearch;
    private TextView tvMenuFilterHint;

    private String tenDanhMucDangChon;
    private boolean moTimKiemKhiMoMan;

    public static MenuFragment newInstance(@Nullable String tenDanhMuc, boolean moTimKiem) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEN_DANH_MUC, tenDanhMuc);
        args.putBoolean(ARG_MO_TIM_KIEM, moTimKiem);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        docTrangThaiDieuHuong(savedInstanceState);
        setupRecyclerView(view);
        setupSearch(view);
        taiDuLieuMonAn();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TEN_DANH_MUC, tenDanhMucDangChon);
        outState.putBoolean(ARG_MO_TIM_KIEM, moTimKiemKhiMoMan);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (moTimKiemKhiMoMan && etMenuSearch != null) {
            etMenuSearch.requestFocus();
            etMenuSearch.post(() -> etMenuSearch.setSelection(etMenuSearch.getText() == null ? 0 : etMenuSearch.getText().length()));
            moTimKiemKhiMoMan = false;
        }
    }

    public void applyHomeNavigationState(@Nullable String tenDanhMuc, boolean moTimKiem) {
        tenDanhMucDangChon = TextUtils.isEmpty(tenDanhMuc) ? null : tenDanhMuc;
        moTimKiemKhiMoMan = moTimKiem;
        if (isAdded()) {
            taiDuLieuMonAn();
            if (moTimKiemKhiMoMan && etMenuSearch != null) {
                etMenuSearch.requestFocus();
            }
        }
    }

    private void docTrangThaiDieuHuong(@Nullable Bundle savedInstanceState) {
        Bundle source = savedInstanceState != null ? savedInstanceState : getArguments();
        if (source == null) {
            tenDanhMucDangChon = null;
            moTimKiemKhiMoMan = false;
            return;
        }
        tenDanhMucDangChon = source.getString(ARG_TEN_DANH_MUC);
        moTimKiemKhiMoMan = source.getBoolean(ARG_MO_TIM_KIEM, false);
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvMenu = view.findViewById(R.id.rvMenu);
        rvMenu.setLayoutManager(new LinearLayoutManager(requireContext()));

        menuAdapter = new MenuAdapter(
                filteredDishes,
                filteredDescriptions,
                dish -> {
                    CartManager.getInstance().addToCart(dish);
                    Toast.makeText(
                            requireContext(),
                            getString(R.string.menu_added_to_cart, dish.getTenMon()),
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        rvMenu.setAdapter(menuAdapter);
        tvMenuFilterHint = view.findViewById(R.id.tvMenuFilterHint);
    }

    private void setupSearch(View view) {
        etMenuSearch = view.findViewById(R.id.etMenuSearch);
        etMenuSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyCurrentFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void taiDuLieuMonAn() {
        allDishes.clear();
        allDescriptions.clear();

        List<DatabaseHelper.DishRecord> dishRecords = databaseHelper.getAllDishes();
        for (DatabaseHelper.DishRecord record : dishRecords) {
            RecommendedDishItem dishItem = record.getDishItem();
            if (!TextUtils.isEmpty(tenDanhMucDangChon)
                    && !TextUtils.equals(tenDanhMucDangChon, dishItem.getTenDanhMuc())) {
                continue;
            }
            allDishes.add(dishItem);
            allDescriptions.add(record.getDescription());
        }

        applyCurrentFilter();
    }

    private void applyCurrentFilter() {
        String tuKhoa = etMenuSearch == null || etMenuSearch.getText() == null
                ? ""
                : etMenuSearch.getText().toString().trim().toLowerCase(Locale.ROOT);

        filteredDishes.clear();
        filteredDescriptions.clear();

        for (int i = 0; i < allDishes.size(); i++) {
            RecommendedDishItem dish = allDishes.get(i);
            String description = allDescriptions.get(i);

            String tenMonLower = dish.getTenMon().toLowerCase(Locale.ROOT);
            String descriptionLower = description.toLowerCase(Locale.ROOT);
            String danhMucLower = dish.getTenDanhMuc().toLowerCase(Locale.ROOT);

            if (TextUtils.isEmpty(tuKhoa)
                    || tenMonLower.contains(tuKhoa)
                    || descriptionLower.contains(tuKhoa)
                    || danhMucLower.contains(tuKhoa)) {
                filteredDishes.add(dish);
                filteredDescriptions.add(description);
            }
        }

        menuAdapter.updateData(filteredDishes, filteredDescriptions);
        capNhatHintBoLoc();
    }

    private void capNhatHintBoLoc() {
        if (tvMenuFilterHint == null) {
            return;
        }

        if (TextUtils.isEmpty(tenDanhMucDangChon)) {
            tvMenuFilterHint.setVisibility(View.GONE);
            return;
        }

        tvMenuFilterHint.setVisibility(View.VISIBLE);
        tvMenuFilterHint.setText(getString(R.string.menu_filter_hint_format, tenDanhMucDangChon));
    }
}
