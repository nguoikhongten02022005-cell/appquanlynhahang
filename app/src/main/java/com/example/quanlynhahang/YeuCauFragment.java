package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.YeuCauPhucVuAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class YeuCauFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";

    private final List<YeuCauPhucVu> serviceRequests = new ArrayList<>();

    private TextView tvServiceRequestEmptyState;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private YeuCauPhucVuAdapter serviceRequestAdapter;
    private boolean embedded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yeu_cau, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        khoiTaoView(view);
        if (embedded) {
            View titleView = view.findViewById(R.id.tvRequestsTitle);
            if (titleView != null) {
                titleView.setVisibility(View.GONE);
            }
        }
        thietLapDanhSachYeuCau(view);
        taiDanhSachYeuCau();
        thietLapHanhDong(view);
        capNhatTrangThaiRong();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachYeuCau();
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        capNhatTrangThaiRong();
    }

    private void khoiTaoView(View view) {
        tvServiceRequestEmptyState = view.findViewById(R.id.tvServiceRequestEmptyState);
    }

    private void thietLapDanhSachYeuCau(View view) {
        RecyclerView rvServiceRequests = view.findViewById(R.id.rvServiceRequests);
        rvServiceRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        serviceRequestAdapter = new YeuCauPhucVuAdapter(serviceRequests);
        rvServiceRequests.setAdapter(serviceRequestAdapter);
    }

    private void thietLapHanhDong(View view) {
        MaterialButton btnRequestCallStaff = view.findViewById(R.id.btnRequestCallStaff);
        MaterialButton btnRequestMoreWater = view.findViewById(R.id.btnRequestMoreWater);
        MaterialButton btnRequestPayment = view.findViewById(R.id.btnRequestPayment);

        btnRequestCallStaff.setOnClickListener(v -> guiYeuCauPhucVuNhanh(
                YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN,
                getString(R.string.service_request_quick_call_staff)
        ));
        btnRequestMoreWater.setOnClickListener(v -> guiYeuCauPhucVuNhanh(
                YeuCauPhucVu.LoaiYeuCau.THEM_NUOC,
                getString(R.string.service_request_quick_more_water)
        ));
        btnRequestPayment.setOnClickListener(v -> guiYeuCauPhucVuNhanh(
                YeuCauPhucVu.LoaiYeuCau.THANH_TOAN,
                getString(R.string.service_request_quick_payment)
        ));
    }

    private void guiYeuCauPhucVuNhanh(YeuCauPhucVu.LoaiYeuCau loaiYeuCau, String noiDungYeuCau) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (!sessionManager.daDangNhap() || idNguoiDungHienTai <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.service_request_login_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (databaseHelper.coYeuCauDangXuLyGanDay(idNguoiDungHienTai, loaiYeuCau, "")) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.service_request_duplicate_blocked),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String thoiGianGui = layChuoiThoiGianHienTai();
        long idYeuCau = databaseHelper.themYeuCauPhucVu(
                idNguoiDungHienTai,
                loaiYeuCau,
                noiDungYeuCau,
                null,
                0,
                thoiGianGui,
                YeuCauPhucVu.TrangThai.DANG_XU_LY
        );
        if (idYeuCau <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        taiDanhSachYeuCau();
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        capNhatTrangThaiRong();

        Toast.makeText(
                requireContext(),
                getString(R.string.service_request_submit_success, noiDungYeuCau),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void taiDanhSachYeuCau() {
        serviceRequests.clear();

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDung <= 0 || !sessionManager.daDangNhap()) {
            return;
        }

        serviceRequests.addAll(databaseHelper.layYeuCauTheoNguoiDung(idNguoiDung));
    }

    private void capNhatTrangThaiRong() {
        if (tvServiceRequestEmptyState != null) {
            tvServiceRequestEmptyState.setVisibility(serviceRequests.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private String layChuoiThoiGianHienTai() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }
}
