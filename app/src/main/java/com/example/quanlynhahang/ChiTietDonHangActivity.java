package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MonTrongDonAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.helper.HanhDongNghiepVuHelper;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;


public class ChiTietDonHangActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private DonHang donHang;

    private TextView tvHeaderTitle;
    private AppCompatTextView tvStatusBadge;
    private TextView tvProgressLabel;
    private TextView tvProgressSteps;
    private TextView tvTableInfo;
    private TextView tvPaymentInfo;
    private TextView tvNoteInfo;
    private TextView tvTotal;
    private MaterialButton btnCancelOrder;
    private MaterialButton btnRequestPayment;
    private MonTrongDonAdapter monTrongDonAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        databaseHelper.chuanBiCoSoDuLieu();

        khoiTaoView();
        if (!taiDonHang()) {
            finish();
            return;
        }
        ganDuLieu();
        thietLapNutHuyDon();
        thietLapNutYeuCauThanhToan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!taiDonHang()) {
            finish();
            return;
        }
        ganDuLieu();
        thietLapNutHuyDon();
        thietLapNutYeuCauThanhToan();
    }

    private void khoiTaoView() {
        ImageButton btnBack = findViewById(R.id.btnOrderDetailBack);
        tvHeaderTitle = findViewById(R.id.tvOrderDetailTitle);
        tvStatusBadge = findViewById(R.id.tvOrderDetailStatus);
        tvProgressLabel = findViewById(R.id.tvOrderDetailProgressLabel);
        tvProgressSteps = findViewById(R.id.tvOrderDetailProgressSteps);
        tvTableInfo = findViewById(R.id.tvOrderDetailTable);
        tvPaymentInfo = findViewById(R.id.tvOrderDetailPayment);
        tvNoteInfo = findViewById(R.id.tvOrderDetailNote);
        tvTotal = findViewById(R.id.tvOrderDetailTotal);
        btnCancelOrder = findViewById(R.id.btnOrderDetailCancel);
        btnRequestPayment = findViewById(R.id.btnOrderDetailRequestPayment);

        RecyclerView rvItems = findViewById(R.id.rvOrderDetailItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        monTrongDonAdapter = new MonTrongDonAdapter(java.util.Collections.emptyList());
        rvItems.setAdapter(monTrongDonAdapter);

        btnBack.setOnClickListener(v -> finish());
    }

    private boolean taiDonHang() {
        String maDon = getIntent().getStringExtra("maDon");
        long userId = sessionManager.layIdNguoiDungHienTai();
        donHang = databaseHelper.layDonHangTheoMa(userId, maDon);
        if (donHang == null) {
            Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void ganDuLieu() {
        tvHeaderTitle.setText(donHang.layMaDon());
        int statusRes = TrangThaiHienThiHelper.layTextTrangThaiDon(donHang);
        tvStatusBadge.setText(statusRes);
        ViewCompat.setBackgroundTintList(
                tvStatusBadge,
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, TrangThaiHienThiHelper.layMauTrangThaiDon(donHang.layTrangThai())))
        );

        boolean daHuy = donHang.layTrangThai() == DonHang.TrangThai.DA_HUY;
        tvProgressLabel.setVisibility(daHuy ? android.view.View.GONE : android.view.View.VISIBLE);
        tvProgressSteps.setVisibility(daHuy ? android.view.View.GONE : android.view.View.VISIBLE);
        if (!daHuy) {
            tvProgressLabel.setText(getString(
                    donHang.laAnTaiQuan() ? R.string.order_status_label_dine_in : R.string.order_status_label_takeaway,
                    getString(statusRes)
            ));
            tvProgressSteps.setText(donHang.laAnTaiQuan() ? R.string.order_timeline_dine_in : R.string.order_timeline_takeaway);
        }

        tvTableInfo.setText(donHang.coBanAn()
                ? getString(R.string.order_table_format, donHang.laySoBan())
                : getString(R.string.order_table_not_required));
        tvPaymentInfo.setText(layTextThanhToan(donHang));
        tvNoteInfo.setText(donHang.coGhiChu()
                ? getString(R.string.order_note_format, donHang.layGhiChu())
                : getString(R.string.order_note_empty));
        tvTotal.setText(dinhDangGia(donHang.layTongTien()));
        monTrongDonAdapter.capNhatDuLieu(donHang.layDanhSachMon());
    }

    private void thietLapNutHuyDon() {
        boolean hienNut = HanhDongNghiepVuHelper.khachCoTheHuyDon(donHang);
        btnCancelOrder.setVisibility(hienNut ? android.view.View.VISIBLE : android.view.View.GONE);
        btnCancelOrder.setEnabled(hienNut);
        if (!hienNut) {
            btnCancelOrder.setOnClickListener(null);
            return;
        }
        btnCancelOrder.setOnClickListener(v -> new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.order_cancel_confirm_title)
                .setMessage(R.string.order_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.order_cancel, (dialog, which) -> {
                    boolean daHuy = databaseHelper.huyDonHang(donHang.layId());
                    if (!daHuy) {
                        Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, getString(R.string.order_cancel_success, donHang.layMaDon()), Toast.LENGTH_SHORT).show();
                    if (!taiDonHang()) {
                        finish();
                        return;
                    }
                    ganDuLieu();
                    thietLapNutHuyDon();
                    thietLapNutYeuCauThanhToan();
                })
                .show());
    }

    private void thietLapNutYeuCauThanhToan() {
        boolean hienNut = donHang.laAnTaiQuan()
                && donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN
                && donHang.layTrangThai() != DonHang.TrangThai.DA_HUY
                && !databaseHelper.coYeuCauThanhToanDangXuLy(sessionManager.layIdNguoiDungHienTai(), donHang.layId())
                && !databaseHelper.coYeuCauThanhToanDangHoatDongTheoBan(sessionManager.layIdNguoiDungHienTai(), donHang.laySoBan());

        btnRequestPayment.setVisibility(hienNut ? android.view.View.VISIBLE : android.view.View.GONE);
        btnRequestPayment.setEnabled(hienNut);
        if (!hienNut) {
            btnRequestPayment.setOnClickListener(null);
            return;
        }

        btnRequestPayment.setOnClickListener(v -> {
            if (databaseHelper.coYeuCauThanhToanDangHoatDongTheoBan(sessionManager.layIdNguoiDungHienTai(), donHang.laySoBan())) {
                Toast.makeText(this, R.string.service_request_payment_duplicate_by_table, Toast.LENGTH_SHORT).show();
                btnRequestPayment.setVisibility(android.view.View.GONE);
                return;
            }
            long idYeuCau = databaseHelper.themYeuCauPhucVu(
                    sessionManager.layIdNguoiDungHienTai(),
                    YeuCauPhucVu.LoaiYeuCau.THANH_TOAN,
                    getString(R.string.service_request_payment_for_order_format, donHang.layMaDon()),
                    donHang.laySoBan(),
                    donHang.layId(),
                    DateTimeUtils.layThoiGianHienTai(),
                    YeuCauPhucVu.TrangThai.DANG_CHO
            );
            if (idYeuCau <= 0) {
                Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            btnRequestPayment.setEnabled(false);
            btnRequestPayment.setVisibility(android.view.View.GONE);
            Toast.makeText(this, R.string.order_payment_request_sent, Toast.LENGTH_SHORT).show();
        });
    }

    private String layTextThanhToan(DonHang donHang) {
        if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_THANH_TOAN) {
            return getString(R.string.order_payment_status_paid);
        }
        if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_GOI_THANH_TOAN) {
            return getString(R.string.order_payment_status_requested);
        }
        if (!donHang.laAnTaiQuan()) {
            if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.TIEN_MAT_KHI_NHAN
                    || donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.TAI_QUAY) {
                return getString(R.string.order_payment_status_pay_on_pickup);
            }
            if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUYEN_KHOAN_NGAN_HANG) {
                return getString(R.string.order_payment_status_bank_transfer);
            }
            if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.VI_DIEN_TU) {
                return getString(R.string.order_payment_status_ewallet);
            }
        }
        return getString(R.string.order_payment_status_unpaid);
    }

    private String dinhDangGia(String chuoiGiaGoc) {
        long soTien = TextUtils.isEmpty(chuoiGiaGoc) ? 0L : com.example.quanlynhahang.helper.MoneyUtils.tachGiaTienTuChuoi(chuoiGiaGoc);
        return soTien <= 0L ? getString(R.string.admin_price_zero) : com.example.quanlynhahang.helper.MoneyUtils.dinhDangTienViet(soTien);
    }
}
