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
import com.example.quanlynhahang.adapter.ThucDonAdapter;
import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.FragmentThucDonBinding;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThucDonFragment extends Fragment {

    public static final String ARG_TEN_DANH_MUC = "ten_danh_muc";
    public static final String ARG_MO_TIM_KIEM = "mo_tim_kiem";
    public static final String ARG_TU_KHOA_TIM_KIEM = "tu_khoa_tim_kiem";

    private final List<MonAnDeXuat> tatCaMonAn = new ArrayList<>();
    private final List<String> tatCaMoTa = new ArrayList<>();
    private final List<String> tatCaTenAnh = new ArrayList<>();
    private final List<MonAnDeXuat> danhSachMonDaLoc = new ArrayList<>();
    private final List<String> danhSachMoTaDaLoc = new ArrayList<>();
    private final List<String> danhSachTenAnhDaLoc = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private ThucDonAdapter boDieuHopThucDon;
    private FragmentThucDonBinding binding;

    private String tenDanhMucDangChon;
    private boolean moTimKiemKhiMoMan;
    private String tuKhoaTimKiemBanDau;
    private boolean dangCapNhatTimKiemNoiBo;

    public static ThucDonFragment newInstance(@Nullable String tenDanhMuc, boolean moTimKiem) {
        return newInstance(tenDanhMuc, moTimKiem, null);
    }

    public static ThucDonFragment newInstance(@Nullable String tenDanhMuc,
                                           boolean moTimKiem,
                                           @Nullable String tuKhoaTimKiem) {
        ThucDonFragment fragment = new ThucDonFragment();
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
        binding = FragmentThucDonBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        docTrangThaiDieuHuong(savedInstanceState);
        thietLapRecyclerView();
        thietLapTimKiem();
        taiDuLieuMonAn();
        return binding.getRoot();
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
        if (moTimKiemKhiMoMan && binding != null) {
            moBanPhimTimKiem();
            moTimKiemKhiMoMan = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void apDungTrangThaiDieuHuongTuTrangChu(@Nullable String tenDanhMuc, boolean moTimKiem) {
        apDungTrangThaiDieuHuongTuTrangChu(tenDanhMuc, moTimKiem, null);
    }

    public void apDungTrangThaiDieuHuongTuTrangChu(@Nullable String tenDanhMuc,
                                                   boolean moTimKiem,
                                                   @Nullable String tuKhoaTimKiem) {
        tenDanhMucDangChon = TextUtils.isEmpty(tenDanhMuc) ? null : tenDanhMuc;
        moTimKiemKhiMoMan = moTimKiem;
        tuKhoaTimKiemBanDau = tuKhoaTimKiem == null ? "" : tuKhoaTimKiem.trim();
        if (isAdded()) {
            apDungTuKhoaTimKiemNeuCan();
            taiDuLieuMonAn();
            if (moTimKiemKhiMoMan && binding != null) {
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
    }

    private void thietLapRecyclerView() {
        binding.rvMenu.setLayoutManager(new LinearLayoutManager(requireContext()));

        boDieuHopThucDon = new ThucDonAdapter(
                danhSachMonDaLoc,
                danhSachMoTaDaLoc,
                danhSachTenAnhDaLoc,
                dish -> {
                    if (dish == null || !dish.laConPhucVu()) {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.menu_unavailable_blocked),
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }
                    layGioKhachHang().themVaoGio(dish);
                    Toast.makeText(
                            requireContext(),
                            getString(R.string.menu_added_to_cart, dish.layTenMon()),
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        binding.rvMenu.setAdapter(boDieuHopThucDon);
    }

    private void thietLapTimKiem() {
        apDungTuKhoaTimKiemNeuCan();
        binding.etMenuSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dangCapNhatTimKiemNoiBo) {
                    return;
                }
                apDungBoLocHienTai();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void taiDuLieuMonAn() {
        tatCaMonAn.clear();
        tatCaMoTa.clear();
        tatCaTenAnh.clear();

        List<DatabaseHelper.DishRecord> dishRecords = databaseHelper.layTatCaMonAn();
        for (DatabaseHelper.DishRecord record : dishRecords) {
            MonAnDeXuat dishItem = record.layMonAn();
            if (!TextUtils.isEmpty(tenDanhMucDangChon)
                    && !TextUtils.equals(tenDanhMucDangChon, dishItem.layTenDanhMuc())) {
                continue;
            }
            tatCaMonAn.add(dishItem);
            tatCaMoTa.add(record.layMoTa());
            tatCaTenAnh.add(record.layTenAnhTaiNguyen());
        }

        apDungBoLocHienTai();
    }

    public void apDungBoLocHienTai() {
        String tuKhoa = layTuKhoaHienTai().toLowerCase(Locale.ROOT);

        danhSachMonDaLoc.clear();
        danhSachMoTaDaLoc.clear();
        danhSachTenAnhDaLoc.clear();

        for (int i = 0; i < tatCaMonAn.size(); i++) {
            MonAnDeXuat monAn = tatCaMonAn.get(i);
            String moTa = tatCaMoTa.get(i);

            String tenMonLower = giaTriLowerAnToan(monAn == null ? null : monAn.layTenMon());
            String moTaLower = giaTriLowerAnToan(moTa);
            String danhMucLower = giaTriLowerAnToan(monAn == null ? null : monAn.layTenDanhMuc());

            if (TextUtils.isEmpty(tuKhoa)
                    || tenMonLower.contains(tuKhoa)
                    || moTaLower.contains(tuKhoa)
                    || danhMucLower.contains(tuKhoa)) {
                danhSachMonDaLoc.add(monAn);
                danhSachMoTaDaLoc.add(moTa == null ? "" : moTa);
                danhSachTenAnhDaLoc.add(i < tatCaTenAnh.size() ? tatCaTenAnh.get(i) : "");
            }
        }

        boDieuHopThucDon.capNhatDuLieu(danhSachMonDaLoc, danhSachMoTaDaLoc, danhSachTenAnhDaLoc);
        capNhatHintBoLoc();
        capNhatEmptyState();
    }

    private void capNhatHintBoLoc() {
        if (binding == null) {
            return;
        }

        String tuKhoa = layTuKhoaHienTai();
        boolean coDanhMuc = !TextUtils.isEmpty(tenDanhMucDangChon);
        boolean coTuKhoa = !TextUtils.isEmpty(tuKhoa);

        if (!coDanhMuc && !coTuKhoa) {
            binding.tvMenuFilterHint.setVisibility(View.GONE);
            return;
        }

        binding.tvMenuFilterHint.setVisibility(View.VISIBLE);
        if (coDanhMuc && coTuKhoa) {
            binding.tvMenuFilterHint.setText(getString(R.string.menu_filter_hint_with_query_format, tenDanhMucDangChon, tuKhoa));
            return;
        }
        if (coDanhMuc) {
            binding.tvMenuFilterHint.setText(getString(R.string.menu_filter_hint_format, tenDanhMucDangChon));
            return;
        }
        binding.tvMenuFilterHint.setText(getString(R.string.menu_filter_query_hint_format, tuKhoa));
    }

    private void capNhatEmptyState() {
        if (binding == null) {
            return;
        }

        boolean coKetQua = !danhSachMonDaLoc.isEmpty();
        binding.layoutMenuEmptyState.setVisibility(coKetQua ? View.GONE : View.VISIBLE);

        boolean coBoLoc = !TextUtils.isEmpty(tenDanhMucDangChon) || !TextUtils.isEmpty(layTuKhoaHienTai());
        binding.tvMenuEmptyMessage.setText(coBoLoc
                ? R.string.menu_empty_with_filters
                : R.string.menu_empty_default);
    }

    private String giaTriLowerAnToan(@Nullable String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private void apDungTuKhoaTimKiemNeuCan() {
        if (binding == null || tuKhoaTimKiemBanDau == null) {
            return;
        }

        String tuKhoaMucTieu = tuKhoaTimKiemBanDau;
        String tuKhoaHienTai = layTuKhoaHienTai();
        if (TextUtils.equals(tuKhoaMucTieu, tuKhoaHienTai)) {
            tuKhoaTimKiemBanDau = null;
            return;
        }

        dangCapNhatTimKiemNoiBo = true;
        binding.etMenuSearch.setText(tuKhoaMucTieu);
        binding.etMenuSearch.setSelection(binding.etMenuSearch.length());
        dangCapNhatTimKiemNoiBo = false;
        tuKhoaTimKiemBanDau = null;
    }

    private String layTuKhoaHienTai() {
        if (binding == null || binding.etMenuSearch.getText() == null) {
            return "";
        }
        return binding.etMenuSearch.getText().toString().trim();
    }

    private QuanLyGioHang layGioKhachHang() {
        return QuanLyGioHang.layInstance(sessionManager.layKhoaPhienKhachHang());
    }

    private void moBanPhimTimKiem() {
        if (binding == null) {
            return;
        }
        binding.etMenuSearch.requestFocus();
        binding.etMenuSearch.post(() -> {
            if (binding == null) {
                return;
            }
            binding.etMenuSearch.setSelection(binding.etMenuSearch.getText() == null ? 0 : binding.etMenuSearch.getText().length());
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(binding.etMenuSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
