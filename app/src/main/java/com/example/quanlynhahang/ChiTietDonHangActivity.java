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
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
        int statusRes = layTextTrangThai(donHang);
        tvStatusBadge.setText(statusRes);
        ViewCompat.setBackgroundTintList(
                tvStatusBadge,
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, layMauTrangThai(donHang.layTrangThai())))
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

    private void thietLapNutYeuCauThanhToan() {
        boolean hienNut = donHang.laAnTaiQuan()
                && donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN
                && donHang.layTrangThai() != DonHang.TrangThai.DA_HUY
                && !databaseHelper.coYeuCauThanhToanDangXuLy(sessionManager.layIdNguoiDungHienTai(), donHang.layId())
                && !databaseHelper.coYeuCauThanhToanDangHoatDongTheoBan(sessionManager.layIdNguoiDungHienTai(), donHang.laySoBan());

        btnRequestPayment.setVisibility(hienNut ? android.view.View.VISIBLE : android.view.View.GONE);
        btnRequestPayment.setEnabled(hienNut);
        if (!hienNut) {
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
                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new java.util.Date()),
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
        if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_THANH_TOAN_MO_PHONG) {
            return getString(R.string.order_payment_status_paid_mock);
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

    private int layTextTrangThai(DonHang donHang) {
        DonHang.TrangThai trangThai = donHang.layTrangThai();
        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {
            return R.string.order_status_pending;
        }
        if (trangThai == DonHang.TrangThai.DANG_CHUAN_BI) {
            return R.string.order_status_making;
        }
        if (trangThai == DonHang.TrangThai.SAN_SANG_PHUC_VU) {
            return donHang.laAnTaiQuan() ? R.string.order_status_ready : R.string.order_status_ready_takeaway;
        }
        if (trangThai == DonHang.TrangThai.HOAN_THANH) {
            return R.string.order_status_completed;
        }
        return R.string.order_status_canceled;
    }

    private int layMauTrangThai(DonHang.TrangThai trangThai) {
        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {
            return R.color.warning;
        }
        if (trangThai == DonHang.TrangThai.DANG_CHUAN_BI) {
            return R.color.brand_orange;
        }
        if (trangThai == DonHang.TrangThai.SAN_SANG_PHUC_VU) {
            return R.color.primary;
        }
        if (trangThai == DonHang.TrangThai.HOAN_THANH) {
            return R.color.success;
        }
        return R.color.error;
    }

    private String dinhDangGia(String chuoiGiaGoc) {
        if (TextUtils.isEmpty(chuoiGiaGoc)) {
            return "0đ";
        }
        String chuSo = chuoiGiaGoc.replaceAll("[^0-9]", "");
        if (chuSo.isEmpty()) {
            return "0đ";
        }
        long soTien;
        try {
            soTien = Long.parseLong(chuSo);
        } catch (NumberFormatException ex) {
            return "0đ";
        }
        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        kyHieu.setGroupingSeparator('.');
        DecimalFormat dinhDangSo = new DecimalFormat("#,###", kyHieu);
        return dinhDangSo.format(soTien) + "đ";
    }
}
