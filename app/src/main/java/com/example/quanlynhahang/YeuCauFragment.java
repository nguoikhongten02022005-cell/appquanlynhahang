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
import com.example.quanlynhahang.helper.DichVuKhachHangHelper;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class YeuCauFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";

    private final List<YeuCauPhucVu> serviceRequests = new ArrayList<>();

    private TextView tvServiceRequestEmptyState;
    private TextView tvServiceRequestUnavailableState;
    private TextView tvServiceRequestCaption;
    private TextView tvPendingServiceRequestTitle;
    private TextView tvPendingServiceRequestSubtitle;
    private View cardPendingServiceRequest;
    private RecyclerView rvServiceRequests;
    private MaterialButton btnRequestCallStaff;
    private MaterialButton btnRequestMoreWater;
    private MaterialButton btnRequestPayment;

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
        tvServiceRequestUnavailableState = view.findViewById(R.id.tvServiceRequestUnavailableState);
        tvServiceRequestCaption = view.findViewById(R.id.tvServiceRequestCaption);
        tvPendingServiceRequestTitle = view.findViewById(R.id.tvPendingServiceRequestTitle);
        tvPendingServiceRequestSubtitle = view.findViewById(R.id.tvPendingServiceRequestSubtitle);
        cardPendingServiceRequest = view.findViewById(R.id.cardPendingServiceRequest);
        rvServiceRequests = view.findViewById(R.id.rvServiceRequests);
        btnRequestCallStaff = view.findViewById(R.id.btnRequestCallStaff);
        btnRequestMoreWater = view.findViewById(R.id.btnRequestMoreWater);
        btnRequestPayment = view.findViewById(R.id.btnRequestPayment);
    }

    private void thietLapDanhSachYeuCau(View view) {
        rvServiceRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        serviceRequestAdapter = new YeuCauPhucVuAdapter(serviceRequests, this::xacNhanHuyYeuCau);
        rvServiceRequests.setAdapter(serviceRequestAdapter);
    }

    private void thietLapHanhDong(View view) {
        if (cardPendingServiceRequest != null) {
            cardPendingServiceRequest.setOnClickListener(v -> rvServiceRequests.smoothScrollToPosition(0));
        }
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
            datTrangThaiDangGui(false, null);
            hienThiPhanHoiNgan(R.string.service_request_login_required);
            return;
        }

        if (!coTheDungHoTro(idNguoiDungHienTai)) {
            datTrangThaiDangGui(false, null);
            capNhatTrangThaiRong();
            hienThiPhanHoiNgan(R.string.service_request_unavailable);
            return;
        }

        datTrangThaiDangGui(true, loaiYeuCau);

        if (databaseHelper.coYeuCauDangXuLyGanDay(idNguoiDungHienTai, loaiYeuCau, "")) {
            datTrangThaiDangGui(false, null);
            hienThiPhanHoiNgan(R.string.service_request_duplicate_blocked);
            return;
        }

        String thoiGianGui = layChuoiThoiGianHienTai();
        String soBanHienTai = timBanHienTai(idNguoiDungHienTai);
        long idYeuCau = databaseHelper.themYeuCauPhucVu(
                idNguoiDungHienTai,
                loaiYeuCau,
                noiDungYeuCau,
                soBanHienTai,
                0,
                thoiGianGui,
                YeuCauPhucVu.TrangThai.DANG_XU_LY
        );
        if (idYeuCau <= 0) {
            datTrangThaiDangGui(false, null);
            hienThiPhanHoiNgan(R.string.service_request_submit_failed);
            return;
        }

        taiDanhSachYeuCau();
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        capNhatTrangThaiRong();

        datTrangThaiDangGui(false, null);
        hienThiPhanHoiNgan(getString(R.string.service_request_submit_success, noiDungYeuCau));
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
        if (tvServiceRequestEmptyState == null || tvServiceRequestUnavailableState == null || rvServiceRequests == null) {
            return;
        }

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        boolean daDangNhap = sessionManager.daDangNhap() && idNguoiDung > 0;
        boolean coNgucCanhHoTro = daDangNhap && coTheDungHoTro(idNguoiDung);

        if (tvServiceRequestCaption != null) {
            String soBanHienTai = daDangNhap ? timBanHienTai(idNguoiDung) : null;
            if (!coNgucCanhHoTro) {
                tvServiceRequestCaption.setText(getString(R.string.service_request_unavailable));
            } else if (!TextUtils.isEmpty(soBanHienTai)) {
                tvServiceRequestCaption.setText(getString(R.string.service_request_caption_with_table, soBanHienTai));
            } else {
                tvServiceRequestCaption.setText(getString(R.string.service_request_section_caption));
            }
        }

        YeuCauPhucVu yeuCauDangCho = DichVuKhachHangHelper.timYeuCauHoTroDangXuLy(serviceRequests);
        if (cardPendingServiceRequest != null) {
            cardPendingServiceRequest.setVisibility(coNgucCanhHoTro && yeuCauDangCho != null ? View.VISIBLE : View.GONE);
        }
        if (tvPendingServiceRequestTitle != null && yeuCauDangCho != null) {
            tvPendingServiceRequestTitle.setText(getString(R.string.activity_hub_summary_support_waiting));
        }
        if (tvPendingServiceRequestSubtitle != null && yeuCauDangCho != null) {
            tvPendingServiceRequestSubtitle.setText(yeuCauDangCho.layNoiDung());
        }

        tvServiceRequestUnavailableState.setVisibility(!coNgucCanhHoTro ? View.VISIBLE : View.GONE);
        tvServiceRequestEmptyState.setVisibility(coNgucCanhHoTro && serviceRequests.isEmpty() ? View.VISIBLE : View.GONE);
        rvServiceRequests.setVisibility(coNgucCanhHoTro && !serviceRequests.isEmpty() ? View.VISIBLE : View.GONE);

        btnRequestCallStaff.setEnabled(coNgucCanhHoTro);
        btnRequestMoreWater.setEnabled(coNgucCanhHoTro);
        btnRequestPayment.setEnabled(coNgucCanhHoTro);
        float alpha = coNgucCanhHoTro ? 1f : 0.5f;
        btnRequestCallStaff.setAlpha(alpha);
        btnRequestMoreWater.setAlpha(alpha);
        btnRequestPayment.setAlpha(alpha);
    }

    private boolean coTheDungHoTro(long idNguoiDung) {
        return !TextUtils.isEmpty(timBanHienTai(idNguoiDung));
    }

    @Nullable
    private String timBanHienTai(long idNguoiDung) {
        DonHang donTaiQuanDangHoatDong = DichVuKhachHangHelper.timDonHangTaiQuanDangHoatDong(
                databaseHelper.layDonHangTheoNguoiDung(idNguoiDung)
        );
        return DichVuKhachHangHelper.timBanHienTai(
                sessionManager.layBanHienTai(),
                donTaiQuanDangHoatDong,
                null
        );
    }

    private void datTrangThaiDangGui(boolean dangGui, @Nullable YeuCauPhucVu.LoaiYeuCau loaiYeuCau) {
        capNhatNutYeuCau(btnRequestCallStaff, dangGui, loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.GOI_NHAN_VIEN, R.string.service_request_quick_call_staff);
        capNhatNutYeuCau(btnRequestMoreWater, dangGui, loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.THEM_NUOC, R.string.service_request_quick_more_water);
        capNhatNutYeuCau(btnRequestPayment, dangGui, loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN, R.string.service_request_quick_payment);
    }

    private void capNhatNutYeuCau(MaterialButton button, boolean dangGui, boolean laNutDangGui, int textRes) {
        if (button == null) {
            return;
        }
        button.setEnabled(!dangGui);
        button.setText(dangGui && laNutDangGui ? getString(R.string.order_submitting) : getString(textRes));
    }

    private void xacNhanHuyYeuCau(YeuCauPhucVu yeuCauPhucVu, int viTri) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.service_request_cancel_confirm_title)
                .setMessage(R.string.service_request_cancel_confirm_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.service_request_cancel_action, (dialog, which) -> thucHienHuyYeuCau(yeuCauPhucVu, viTri))
                .show();
    }

    private void thucHienHuyYeuCau(YeuCauPhucVu yeuCauPhucVu, int viTri) {
        if (yeuCauPhucVu == null || !yeuCauPhucVu.coTheHuy()) {
            return;
        }
        boolean daCapNhat = databaseHelper.capNhatTrangThaiYeuCauPhucVu(yeuCauPhucVu.layId(), YeuCauPhucVu.TrangThai.DA_HUY);
        if (!daCapNhat) {
            hienThiPhanHoiNgan(R.string.service_request_submit_failed);
            return;
        }
        yeuCauPhucVu.danhDauDaHuy();
        taiDanhSachYeuCau();
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        capNhatTrangThaiRong();
        hienThiPhanHoiNgan(R.string.service_request_cancel_success);
    }

    private String layChuoiThoiGianHienTai() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    private void hienThiPhanHoiNgan(int messageRes) {
        hienThiPhanHoiNgan(getString(messageRes));
    }

    private void hienThiPhanHoiNgan(String message) {
        View root = getView();
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
