package com.example.quanlynhahang;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.DonHangNhanVienAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.List;

public class DonHangNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private DonHangNhanVienAdapter donHangAdapter;
    private final List<DonHang> danhSachTatCaDon = new ArrayList<>();
    private TextView tvEmptyState;
    private TextView tvTongDon;
    private TextView tvDonChoDuyet;
    private TextView tvDonDangPhucVu;
    private TextView tvDoanhThu;
    private TextView chipTatCa;
    private TextView chipChoDuyet;
    private TextView chipDangPhucVu;
    private TextView chipHoanThanh;
    private String boLocTrangThai = "tat_ca";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_don_hang_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        tvEmptyState = view.findViewById(R.id.tvDonHangNoiBoEmptyState);
        tvTongDon = view.findViewById(R.id.tvAdminOrderSummaryTotal);
        tvDonChoDuyet = view.findViewById(R.id.tvAdminOrderSummaryPending);
        tvDonDangPhucVu = view.findViewById(R.id.tvAdminOrderSummaryServing);
        tvDoanhThu = view.findViewById(R.id.tvAdminOrderSummaryRevenue);
        chipTatCa = view.findViewById(R.id.chipAdminOrderFilterAll);
        chipChoDuyet = view.findViewById(R.id.chipAdminOrderFilterPending);
        chipDangPhucVu = view.findViewById(R.id.chipAdminOrderFilterServing);
        chipHoanThanh = view.findViewById(R.id.chipAdminOrderFilterCompleted);

        RecyclerView rvDonHang = view.findViewById(R.id.rvDonHangNoiBo);
        rvDonHang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDonHang.setNestedScrollingEnabled(false);

        donHangAdapter = new DonHangNhanVienAdapter(new DonHangNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiXacNhan(DonHang order) {
                capNhatTrangThaiDonHang(order, DonHang.TrangThai.DANG_CHUAN_BI);
            }

            @Override
            public void khiHoanTat(DonHang order) {
                if (order.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI) {
                    capNhatTrangThaiDonHang(order, DonHang.TrangThai.SAN_SANG_PHUC_VU);
                    return;
                }
                capNhatTrangThaiDonHang(order, DonHang.TrangThai.HOAN_THANH);
            }

            @Override
            public void khiHuy(DonHang order) {
                xacNhanHuyDonHang(order);
            }
        });
        rvDonHang.setAdapter(donHangAdapter);

        caiDatBoLoc();
        taiDanhSachDonHang();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachDonHang();
    }

    private void caiDatBoLoc() {
        chipTatCa.setOnClickListener(v -> doiBoLoc("tat_ca"));
        chipChoDuyet.setOnClickListener(v -> doiBoLoc("cho_duyet"));
        chipDangPhucVu.setOnClickListener(v -> doiBoLoc("dang_phuc_vu"));
        chipHoanThanh.setOnClickListener(v -> doiBoLoc("hoan_thanh"));
        capNhatTrangThaiChip();
    }

    private void doiBoLoc(String boLocMoi) {
        boLocTrangThai = boLocMoi;
        capNhatTrangThaiChip();
        apDungBoLocDonHang();
    }

    private void capNhatTrangThaiChip() {
        capNhatChip(chipTatCa, "tat_ca".equals(boLocTrangThai));
        capNhatChip(chipChoDuyet, "cho_duyet".equals(boLocTrangThai));
        capNhatChip(chipDangPhucVu, "dang_phuc_vu".equals(boLocTrangThai));
        capNhatChip(chipHoanThanh, "hoan_thanh".equals(boLocTrangThai));
    }

    private void capNhatChip(TextView chip, boolean duocChon) {
        chip.setBackgroundResource(duocChon ? R.drawable.bg_order_filter_selected : R.drawable.bg_order_filter_unselected);
        chip.setTextColor(ContextCompat.getColor(requireContext(), duocChon ? android.R.color.white : R.color.on_surface_variant));
    }

    private void taiDanhSachDonHang() {
        danhSachTatCaDon.clear();
        danhSachTatCaDon.addAll(databaseHelper.layTatCaDonHang());
        capNhatTongQuanDonHang(danhSachTatCaDon);
        apDungBoLocDonHang();
    }

    private void capNhatTongQuanDonHang(List<DonHang> danhSachDon) {
        int choDuyet = 0;
        int dangPhucVu = 0;
        long tongDoanhThu = 0L;
        for (DonHang donHang : danhSachDon) {
            if (donHang.layTrangThai() == DonHang.TrangThai.CHO_XAC_NHAN) {
                choDuyet++;
            }
            if (donHang.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI
                    || donHang.layTrangThai() == DonHang.TrangThai.SAN_SANG_PHUC_VU) {
                dangPhucVu++;
            }
            if (donHang.layTrangThai() != DonHang.TrangThai.DA_HUY) {
                tongDoanhThu += MoneyUtils.tachGiaTienTuChuoi(donHang.layTongTien());
            }
        }
        tvTongDon.setText(String.valueOf(danhSachDon.size()));
        tvDonChoDuyet.setText(String.valueOf(choDuyet));
        tvDonDangPhucVu.setText(String.valueOf(dangPhucVu));
        tvDoanhThu.setText(MoneyUtils.dinhDangTienViet(tongDoanhThu));
    }

    private void apDungBoLocDonHang() {
        List<DonHang> ketQua = new ArrayList<>();
        for (DonHang donHang : danhSachTatCaDon) {
            if (!khopBoLoc(donHang)) {
                continue;
            }
            ketQua.add(donHang);
        }
        donHangAdapter.capNhatDanhSach(ketQua);
        tvEmptyState.setVisibility(ketQua.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean khopBoLoc(DonHang donHang) {
        if ("cho_duyet".equals(boLocTrangThai)) {
            return donHang.layTrangThai() == DonHang.TrangThai.CHO_XAC_NHAN;
        }
        if ("dang_phuc_vu".equals(boLocTrangThai)) {
            return donHang.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI
                    || donHang.layTrangThai() == DonHang.TrangThai.SAN_SANG_PHUC_VU;
        }
        if ("hoan_thanh".equals(boLocTrangThai)) {
            return donHang.layTrangThai() == DonHang.TrangThai.HOAN_THANH;
        }
        return true;
    }

    private void capNhatTrangThaiDonHang(DonHang donHang, DonHang.TrangThai trangThai) {
        boolean daCapNhat = databaseHelper.capNhatTrangThaiDonHang(donHang.layId(), trangThai);
        Toast.makeText(
                requireContext(),
                daCapNhat ? R.string.employee_order_status_update_success : R.string.employee_status_update_failed,
                Toast.LENGTH_SHORT
        ).show();
        if (daCapNhat) {
            taiDanhSachDonHang();
        }
    }

    private void xacNhanHuyDonHang(DonHang donHang) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.employee_order_cancel_confirm_title)
                .setMessage(R.string.employee_order_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) ->
                        capNhatTrangThaiDonHang(donHang, DonHang.TrangThai.DA_HUY))
                .show();
    }
}
