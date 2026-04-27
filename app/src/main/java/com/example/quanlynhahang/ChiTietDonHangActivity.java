package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quanlynhahang.adapter.MonTrongDonAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.ActivityChiTietDonHangBinding;
import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.helper.HanhDongNghiepVuHelper;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;


public class ChiTietDonHangActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private DonHang donHang;

    private ActivityChiTietDonHangBinding binding;
    private MonTrongDonAdapter monTrongDonAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChiTietDonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        binding.rvOrderDetailItems.setLayoutManager(new LinearLayoutManager(this));
        monTrongDonAdapter = new MonTrongDonAdapter(java.util.Collections.emptyList());
        binding.rvOrderDetailItems.setAdapter(monTrongDonAdapter);

        binding.btnOrderDetailBack.setOnClickListener(v -> finish());
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
        binding.tvOrderDetailTitle.setText(donHang.layMaDon());
        int statusRes = TrangThaiHienThiHelper.layTextTrangThaiDon(donHang);
        binding.tvOrderDetailStatus.setText(statusRes);
        ViewCompat.setBackgroundTintList(
                binding.tvOrderDetailStatus,
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, TrangThaiHienThiHelper.layMauTrangThaiDon(donHang.layTrangThai())))
        );

        boolean daHuy = donHang.layTrangThai() == DonHang.TrangThai.DA_HUY;
        binding.tvOrderDetailProgressLabel.setVisibility(daHuy ? android.view.View.GONE : android.view.View.VISIBLE);
        binding.tvOrderDetailProgressSteps.setVisibility(daHuy ? android.view.View.GONE : android.view.View.VISIBLE);
        if (!daHuy) {
            binding.tvOrderDetailProgressLabel.setText(getString(
                    donHang.laAnTaiQuan() ? R.string.order_status_label_dine_in : R.string.order_status_label_takeaway,
                    getString(statusRes)
            ));
            binding.tvOrderDetailProgressSteps.setText(donHang.laAnTaiQuan() ? R.string.order_timeline_dine_in : R.string.order_timeline_takeaway);
        }

        binding.tvOrderDetailTable.setText(donHang.coBanAn()
                ? getString(R.string.order_table_format, donHang.laySoBan())
                : getString(R.string.order_table_not_required));
        binding.tvOrderDetailPayment.setText(layTextThanhToan(donHang));
        binding.tvOrderDetailNote.setText(donHang.coGhiChu()
                ? getString(R.string.order_note_format, donHang.layGhiChu())
                : getString(R.string.order_note_empty));
        binding.tvOrderDetailTotal.setText(dinhDangGia(donHang.layTongTien()));
        monTrongDonAdapter.capNhatDuLieu(donHang.layDanhSachMon());
    }

    private void thietLapNutHuyDon() {
        boolean hienNut = HanhDongNghiepVuHelper.khachCoTheHuyDon(donHang);
        binding.btnOrderDetailCancel.setVisibility(hienNut ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.btnOrderDetailCancel.setEnabled(hienNut);
        if (!hienNut) {
            binding.btnOrderDetailCancel.setOnClickListener(null);
            return;
        }
        binding.btnOrderDetailCancel.setOnClickListener(v -> new androidx.appcompat.app.AlertDialog.Builder(this)
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

        binding.btnOrderDetailRequestPayment.setVisibility(hienNut ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.btnOrderDetailRequestPayment.setEnabled(hienNut);
        if (!hienNut) {
            binding.btnOrderDetailRequestPayment.setOnClickListener(null);
            return;
        }

        binding.btnOrderDetailRequestPayment.setOnClickListener(v -> {
            if (databaseHelper.coYeuCauThanhToanDangHoatDongTheoBan(sessionManager.layIdNguoiDungHienTai(), donHang.laySoBan())) {
                Toast.makeText(this, R.string.service_request_payment_duplicate_by_table, Toast.LENGTH_SHORT).show();
                binding.btnOrderDetailRequestPayment.setVisibility(android.view.View.GONE);
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
            binding.btnOrderDetailRequestPayment.setEnabled(false);
            binding.btnOrderDetailRequestPayment.setVisibility(android.view.View.GONE);
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
