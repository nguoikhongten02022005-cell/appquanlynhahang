package com.example.quanlynhahang;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    public static final String ARG_TU_KHOA_TIM_KIEM = "tu_khoa_tim_kiem";

    private final List<RecommendedDishItem> allDishes = new ArrayList<>();
    private final List<String> allDescriptions = new ArrayList<>();
    private final List<RecommendedDishItem> filteredDishes = new ArrayList<>();
    private final List<String> filteredDescriptions = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private MenuAdapter menuAdapter;
    private EditText etMenuSearch;
    private TextView tvMenuFilterHint;
    private View layoutMenuEmptyState;
    private TextView tvMenuEmptyMessage;

    private String tenDanhMucDangChon;
    private boolean moTimKiemKhiMoMan;
    private String tuKhoaTimKiemBanDau;
    private String tuKhoaTimKiemDaApDung;
    private boolean dangCapNhatTimKiemNoiBo;

    public static MenuFragment newInstance(@Nullable String tenDanhMuc, boolean moTimKiem) {
        return newInstance(tenDanhMuc, moTimKiem, null);
    }

    public static MenuFragment newInstance(@Nullable String tenDanhMuc,
                                           boolean moTimKiem,
                                           @Nullable String tuKhoaTimKiem) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEN_DANH_MUC, tenDanhMuc);
        args.putBoolean(ARG_MO_TIM_KIEM, moTimKiem);
        args.putString(ARG_TU_KHOA_TIM_KIEM, tuKhoaTimKiem);
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
        outState.putString(ARG_TU_KHOA_TIM_KIEM, layTuKhoaHienTai());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (moTimKiemKhiMoMan && etMenuSearch != null) {
            moBanPhimTimKiem();
            moTimKiemKhiMoMan = false;
        }
    }

    public void applyHomeNavigationState(@Nullable String tenDanhMuc, boolean moTimKiem) {
        applyHomeNavigationState(tenDanhMuc, moTimKiem, null);
    }

    public void applyHomeNavigationState(@Nullable String tenDanhMuc,
                                         boolean moTimKiem,
                                         @Nullable String tuKhoaTimKiem) {
        tenDanhMucDangChon = TextUtils.isEmpty(tenDanhMuc) ? null : tenDanhMuc;
        moTimKiemKhiMoMan = moTimKiem;
        tuKhoaTimKiemBanDau = tuKhoaTimKiem == null ? "" : tuKhoaTimKiem.trim();
        tuKhoaTimKiemDaApDung = null;
        if (isAdded()) {
            apDungTuKhoaTimKiemNeuCan();
            taiDuLieuMonAn();
            if (moTimKiemKhiMoMan && etMenuSearch != null) {
                moBanPhimTimKiem();
                moTimKiemKhiMoMan = false;
            }
        }
    }

    private void docTrangThaiDieuHuong(@Nullable Bundle savedInstanceState) {
        Bundle source = savedInstanceState != null ? savedInstanceState : getArguments();
        if (source == null) {
            tenDanhMucDangChon = null;
            moTimKiemKhiMoMan = false;
            tuKhoaTimKiemBanDau = null;
            return;
        }
        tenDanhMucDangChon = source.getString(ARG_TEN_DANH_MUC);
        moTimKiemKhiMoMan = source.getBoolean(ARG_MO_TIM_KIEM, false);
        tuKhoaTimKiemBanDau = source.getString(ARG_TU_KHOA_TIM_KIEM);
        tuKhoaTimKiemDaApDung = null;
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
        layoutMenuEmptyState = view.findViewById(R.id.layoutMenuEmptyState);
        tvMenuEmptyMessage = view.findViewById(R.id.tvMenuEmptyMessage);
    }

    private void setupSearch(View view) {
        etMenuSearch = view.findViewById(R.id.etMenuSearch);
        apDungTuKhoaTimKiemNeuCan();
        etMenuSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dangCapNhatTimKiemNoiBo) {
                    return;
                }
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

    public void applyCurrentFilter() {
        String tuKhoa = layTuKhoaHienTai().toLowerCase(Locale.ROOT);

        filteredDishes.clear();
        filteredDescriptions.clear();

        for (int i = 0; i < allDishes.size(); i++) {
            RecommendedDishItem dish = allDishes.get(i);
            String description = allDescriptions.get(i);

            String tenMonLower = giaTriLowerAnToan(dish == null ? null : dish.getTenMon());
            String descriptionLower = giaTriLowerAnToan(description);
            String danhMucLower = giaTriLowerAnToan(dish == null ? null : dish.getTenDanhMuc());

            if (TextUtils.isEmpty(tuKhoa)
                    || tenMonLower.contains(tuKhoa)
                    || descriptionLower.contains(tuKhoa)
                    || danhMucLower.contains(tuKhoa)) {
                filteredDishes.add(dish);
                filteredDescriptions.add(description == null ? "" : description);
            }
        }

        menuAdapter.updateData(filteredDishes, filteredDescriptions);
        capNhatHintBoLoc();
        capNhatEmptyState();
    }

    private void capNhatHintBoLoc() {
        if (tvMenuFilterHint == null) {
            return;
        }

        String tuKhoa = layTuKhoaHienTai();
        boolean coDanhMuc = !TextUtils.isEmpty(tenDanhMucDangChon);
        boolean coTuKhoa = !TextUtils.isEmpty(tuKhoa);

        if (!coDanhMuc && !coTuKhoa) {
            tvMenuFilterHint.setVisibility(View.GONE);
            return;
        }

        tvMenuFilterHint.setVisibility(View.VISIBLE);
        if (coDanhMuc && coTuKhoa) {
            tvMenuFilterHint.setText(getString(R.string.menu_filter_hint_with_query_format, tenDanhMucDangChon, tuKhoa));
            return;
        }
        if (coDanhMuc) {
            tvMenuFilterHint.setText(getString(R.string.menu_filter_hint_format, tenDanhMucDangChon));
            return;
        }
        tvMenuFilterHint.setText(getString(R.string.menu_filter_query_hint_format, tuKhoa));
    }

    private void capNhatEmptyState() {
        if (layoutMenuEmptyState == null || tvMenuEmptyMessage == null) {
            return;
        }

        boolean coKetQua = !filteredDishes.isEmpty();
        layoutMenuEmptyState.setVisibility(coKetQua ? View.GONE : View.VISIBLE);

        boolean coBoLoc = !TextUtils.isEmpty(tenDanhMucDangChon) || !TextUtils.isEmpty(layTuKhoaHienTai());
        tvMenuEmptyMessage.setText(coBoLoc
                ? R.string.menu_empty_with_filters
                : R.string.menu_empty_default);
    }

    private String giaTriLowerAnToan(@Nullable String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private void apDungTuKhoaTimKiemNeuCan() {
        if (etMenuSearch == null) {
            return;
        }

        String tuKhoaMucTieu = tuKhoaTimKiemBanDau;
        if (tuKhoaMucTieu == null) {
            if (tuKhoaTimKiemDaApDung == null) {
                return;
            }
            tuKhoaMucTieu = "";
        }

        String tuKhoaHienTai = layTuKhoaHienTai();
        if (TextUtils.equals(tuKhoaMucTieu, tuKhoaHienTai)) {
            tuKhoaTimKiemDaApDung = tuKhoaMucTieu;
            tuKhoaTimKiemBanDau = null;
            return;
        }

        dangCapNhatTimKiemNoiBo = true;
        etMenuSearch.setText(tuKhoaMucTieu);
        etMenuSearch.setSelection(etMenuSearch.length());
        dangCapNhatTimKiemNoiBo = false;
        tuKhoaTimKiemDaApDung = tuKhoaMucTieu;
        tuKhoaTimKiemBanDau = null;
    }

    private String layTuKhoaHienTai() {
        if (etMenuSearch == null || etMenuSearch.getText() == null) {
            return "";
        }
        return etMenuSearch.getText().toString().trim();
    }

    private void moBanPhimTimKiem() {
        etMenuSearch.requestFocus();
        etMenuSearch.post(() -> {
            etMenuSearch.setSelection(etMenuSearch.getText() == null ? 0 : etMenuSearch.getText().length());
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(etMenuSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
