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

import com.example.quanlynhahang.adapter.DanhMucMonAdapter;
import com.example.quanlynhahang.adapter.MonAnDeXuatAdapter;
import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.DanhMucMon;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.List;

public class TrangChuFragment extends Fragment {

    private static final int SO_MON_DE_XUAT = 4;
    private static final int KHONG_CO_DANH_MUC_DANG_CHON = -1;

    private final List<DanhMucMon> danhSachDanhMuc = new ArrayList<>();
    private final List<MonAnDeXuat> danhSachMonDeXuat = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private DanhMucMonAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trang_chu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        thietLapDuLieuDanhMuc();
        thietLapDuLieuMonDeXuat();
        thietLapHanhDongHero(view);
        thietLapDanhSachDanhMuc(view);
        thietLapLuoiMonDeXuat(view);
    }

    private void thietLapHanhDongHero(View view) {
        View hanhDongNhanhDonHang = view.findViewById(R.id.hanhDongNhanhDonHang);
        View hanhDongNhanhDatBan = view.findViewById(R.id.actionQuickBook);

        hanhDongNhanhDonHang.setOnClickListener(v -> {
            datLaiDanhMucDangChon();
            dieuHuongDenMenu(null, true, null);
        });
        hanhDongNhanhDatBan.setOnClickListener(v -> dieuHuongDenYeuCau());
    }

    private void thietLapDanhSachDanhMuc(View view) {
        RecyclerView rvCategory = view.findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new DanhMucMonAdapter(danhSachDanhMuc, (item, position) -> {
            if (categoryAdapter != null) {
                categoryAdapter.capNhatViTriDangChon(position);
            }
            dieuHuongDenMenu(item.layTenDanhMuc(), false, null);
        }, KHONG_CO_DANH_MUC_DANG_CHON);
        rvCategory.setAdapter(categoryAdapter);
    }

    private void thietLapLuoiMonDeXuat(View view) {
        RecyclerView rvRecommended = view.findViewById(R.id.rvRecommended);
        rvRecommended.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvRecommended.setNestedScrollingEnabled(false);
        rvRecommended.setAdapter(new MonAnDeXuatAdapter(danhSachMonDeXuat, new MonAnDeXuatAdapter.HanhDongMonListener() {
            @Override
            public void khiChonMon(MonAnDeXuat item) {
                datLaiDanhMucDangChon();
                dieuHuongDenMenu(item.layTenDanhMuc(), false, item.layTenMon());
            }

            @Override
            public void khiThemMon(MonAnDeXuat item) {
                layGioKhachHang().themVaoGio(item);
                Toast.makeText(
                        requireContext(),
                        getString(R.string.menu_added_to_cart, item.layTenMon()),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }));
    }

    private void thietLapDuLieuDanhMuc() {
        danhSachDanhMuc.clear();
        for (String tenDanhMuc : databaseHelper.layDanhMucMonAn()) {
            themDanhMucMon(tenDanhMuc);
        }
    }

    private void themDanhMucMon(String tenDanhMuc) {
        DanhMucMon danhMucMon = new DanhMucMon(layBieuTuongDanhMuc(tenDanhMuc), tenDanhMuc, tenDanhMuc);
        danhSachDanhMuc.add(danhMucMon);
    }

    private int layBieuTuongDanhMuc(@Nullable String tenDanhMuc) {
        if (getString(R.string.category_hotpot).equals(tenDanhMuc)) {
            return R.drawable.ic_receipt_24;
        }
        if (getString(R.string.category_drink).equals(tenDanhMuc)) {
            return R.drawable.ic_local_drink_24;
        }
        if (getString(R.string.category_salad).equals(tenDanhMuc)) {
            return R.drawable.ic_calendar_24;
        }
        return R.drawable.ic_restaurant_24;
    }

    private void thietLapDuLieuMonDeXuat() {
        danhSachMonDeXuat.clear();
        danhSachMonDeXuat.addAll(databaseHelper.layMonDeXuatTrangChu(SO_MON_DE_XUAT));
    }

    private void datLaiDanhMucDangChon() {
        if (categoryAdapter != null) {
            categoryAdapter.capNhatViTriDangChon(KHONG_CO_DANH_MUC_DANG_CHON);
        }
    }

    private void dieuHuongDenMenu(@Nullable String tenDanhMuc,
                                  boolean sanSangTimKiem,
                                  @Nullable String tuKhoaTimKiem) {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).dieuHuongDenMenu(tenDanhMuc, sanSangTimKiem, tuKhoaTimKiem);
        }
    }

    private QuanLyGioHang layGioKhachHang() {
        return QuanLyGioHang.layInstance(sessionManager.layKhoaPhienKhachHang());
    }

    private void dieuHuongDenYeuCau() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).moTrungTamHoatDong(TrungTamHoatDongFragment.TAB_RESERVATIONS);
        }
    }
}
