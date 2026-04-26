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

import com.example.quanlynhahang.adapter.YeuCauPhucVuNhanVienAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.ArrayList;
import java.util.List;

public class YeuCauNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private YeuCauPhucVuNhanVienAdapter yeuCauAdapter;
    private final List<YeuCauPhucVu> danhSachTatCaYeuCau = new ArrayList<>();
    private TextView tvEmptyState;
    private TextView tvSoKhongCapBach;
    private TextView tvSoDangCho;
    private TextView tvSoDangXuLy;
    private TextView tvSoDaXong;
    private TextView chipTatCa;
    private TextView chipDangCho;
    private TextView chipDangXuLy;
    private TextView chipDaXong;
    private String boLocTrangThai = "tat_ca";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yeu_cau_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        tvEmptyState = view.findViewById(R.id.tvYeuCauNoiBoEmptyState);
        tvSoKhongCapBach = view.findViewById(R.id.tvAdminRequestSummaryUrgent);
        tvSoDangCho = view.findViewById(R.id.tvAdminRequestSummaryWaiting);
        tvSoDangXuLy = view.findViewById(R.id.tvAdminRequestSummaryProcessing);
        tvSoDaXong = view.findViewById(R.id.tvAdminRequestSummaryDone);
        chipTatCa = view.findViewById(R.id.chipAdminRequestFilterAll);
        chipDangCho = view.findViewById(R.id.chipAdminRequestFilterWaiting);
        chipDangXuLy = view.findViewById(R.id.chipAdminRequestFilterProcessing);
        chipDaXong = view.findViewById(R.id.chipAdminRequestFilterDone);

        RecyclerView rvYeuCau = view.findViewById(R.id.rvYeuCauNoiBo);
        rvYeuCau.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvYeuCau.setNestedScrollingEnabled(false);

        yeuCauAdapter = new YeuCauPhucVuNhanVienAdapter(new YeuCauPhucVuNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiNhanXuLy(YeuCauPhucVu yeuCau) {
                capNhatTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DANG_XU_LY);
            }

            @Override
            public void khiDanhDauDaXong(YeuCauPhucVu yeuCau) {
                capNhatTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DA_XU_LY);
            }

            @Override
            public void khiHuy(YeuCauPhucVu yeuCau) {
                xacNhanHuyYeuCau(yeuCau);
            }
        });
        rvYeuCau.setAdapter(yeuCauAdapter);

        caiDatBoLoc();
        taiDanhSachYeuCau();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachYeuCau();
    }

    private void caiDatBoLoc() {
        chipTatCa.setOnClickListener(v -> doiBoLoc("tat_ca"));
        chipDangCho.setOnClickListener(v -> doiBoLoc("dang_cho"));
        chipDangXuLy.setOnClickListener(v -> doiBoLoc("dang_xu_ly"));
        chipDaXong.setOnClickListener(v -> doiBoLoc("da_xong"));
        capNhatTrangThaiChip();
    }

    private void doiBoLoc(String boLocMoi) {
        boLocTrangThai = boLocMoi;
        capNhatTrangThaiChip();
        apDungBoLocYeuCau();
    }

    private void capNhatTrangThaiChip() {
        capNhatChip(chipTatCa, "tat_ca".equals(boLocTrangThai));
        capNhatChip(chipDangCho, "dang_cho".equals(boLocTrangThai));
        capNhatChip(chipDangXuLy, "dang_xu_ly".equals(boLocTrangThai));
        capNhatChip(chipDaXong, "da_xong".equals(boLocTrangThai));
    }

    private void capNhatChip(TextView chip, boolean duocChon) {
        chip.setBackgroundResource(duocChon ? R.drawable.bg_button_orange : R.drawable.bg_search_rounded);
        chip.setTextColor(ContextCompat.getColor(requireContext(), duocChon ? android.R.color.white : R.color.on_surface_variant));
    }

    private void taiDanhSachYeuCau() {
        danhSachTatCaYeuCau.clear();
        danhSachTatCaYeuCau.addAll(databaseHelper.layTatCaYeuCauPhucVu());
        capNhatTongQuanYeuCau(danhSachTatCaYeuCau);
        apDungBoLocYeuCau();
    }

    private void capNhatTongQuanYeuCau(List<YeuCauPhucVu> danhSachYeuCau) {
        int soThanhToan = 0;
        int soDangCho = 0;
        int soDangXuLy = 0;
        int soDaXong = 0;
        for (YeuCauPhucVu yeuCau : danhSachYeuCau) {
            if (yeuCau.layLoaiYeuCau() == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN) {
                soThanhToan++;
            }
            if (yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_CHO) {
                soDangCho++;
            } else if (yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_XU_LY) {
                soDangXuLy++;
            } else if (yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DA_XU_LY) {
                soDaXong++;
            }
        }
        tvSoKhongCapBach.setText(String.valueOf(soThanhToan));
        tvSoDangCho.setText(String.valueOf(soDangCho));
        tvSoDangXuLy.setText(String.valueOf(soDangXuLy));
        tvSoDaXong.setText(String.valueOf(soDaXong));
    }

    private void apDungBoLocYeuCau() {
        List<YeuCauPhucVu> ketQua = new ArrayList<>();
        for (YeuCauPhucVu yeuCau : danhSachTatCaYeuCau) {
            if (!khopBoLoc(yeuCau)) {
                continue;
            }
            ketQua.add(yeuCau);
        }
        yeuCauAdapter.capNhatDanhSach(ketQua);
        tvEmptyState.setVisibility(ketQua.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean khopBoLoc(YeuCauPhucVu yeuCau) {
        if ("dang_cho".equals(boLocTrangThai)) {
            return yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_CHO;
        }
        if ("dang_xu_ly".equals(boLocTrangThai)) {
            return yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_XU_LY;
        }
        if ("da_xong".equals(boLocTrangThai)) {
            return yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DA_XU_LY;
        }
        return true;
    }

    private void capNhatTrangThaiYeuCau(YeuCauPhucVu yeuCau, YeuCauPhucVu.TrangThai trangThai) {
        boolean daCapNhat = databaseHelper.capNhatTrangThaiYeuCauPhucVu(yeuCau.layId(), trangThai);
        Toast.makeText(
                requireContext(),
                daCapNhat ? R.string.employee_service_request_status_update_success : R.string.employee_status_update_failed,
                Toast.LENGTH_SHORT
        ).show();
        if (daCapNhat) {
            taiDanhSachYeuCau();
        }
    }

    private void xacNhanHuyYeuCau(YeuCauPhucVu yeuCau) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.employee_service_request_cancel_confirm_title)
                .setMessage(R.string.employee_service_request_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) ->
                        capNhatTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DA_HUY))
                .show();
    }
}
