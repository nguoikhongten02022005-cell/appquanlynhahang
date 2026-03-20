package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MonTrongDonAdapter;
import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XacNhanDonHangActivity extends AppCompatActivity {

    private RecyclerView rvConfirmOrderItems;
    private TextView tvConfirmOrderType;
    private TextView tvConfirmTableNumber;
    private TextView tvConfirmNote;
    private TextView tvConfirmSubtotal;
    private TextView tvConfirmTotal;
    private TextView tvConfirmPaymentHint;
    private MaterialButton btnSendOrder;
    private MaterialButton btnPayNow;
    private MaterialButton btnRequestPayment;

    private MonTrongDonAdapter monTrongDonAdapter;
    private CartManager cartManager;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xac_nhan_don_hang);

        cartManager = CartManager.getInstance();
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();

        khoiTaoView();
        if (!kiemTraDuLieuDauVao()) {
            return;
        }
        thietLapDanhSachMon();
        ganTomTatDonHang();
        thietLapNutHanhDong();
    }

    private void khoiTaoView() {
        rvConfirmOrderItems = findViewById(R.id.rvConfirmOrderItems);
        tvConfirmOrderType = findViewById(R.id.tvConfirmOrderType);
        tvConfirmTableNumber = findViewById(R.id.tvConfirmTableNumber);
        tvConfirmNote = findViewById(R.id.tvConfirmNote);
        tvConfirmSubtotal = findViewById(R.id.tvConfirmSubtotal);
        tvConfirmTotal = findViewById(R.id.tvConfirmTotal);
        tvConfirmPaymentHint = findViewById(R.id.tvConfirmPaymentHint);
        btnSendOrder = findViewById(R.id.btnSendOrder);
        btnPayNow = findViewById(R.id.btnPayNow);
        btnRequestPayment = findViewById(R.id.btnRequestPayment);
    }

    private boolean kiemTraDuLieuDauVao() {
        if (!sessionManager.daDangNhap() || sessionManager.layIdNguoiDungHienTai() <= 0) {
            Toast.makeText(this, R.string.cart_checkout_requires_login, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        if (cartManager.laGioHangRong()) {
            Toast.makeText(this, R.string.cart_empty_message, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        CartManager.NguCanhDonHang nguCanhDonHang = cartManager.layNguCanhDonHang();
        if (!nguCanhDonHang.hopLeDeDatHang()) {
            Toast.makeText(this, R.string.cart_validation_table_required, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    private void thietLapDanhSachMon() {
        rvConfirmOrderItems.setLayoutManager(new LinearLayoutManager(this));
        monTrongDonAdapter = new MonTrongDonAdapter(xayDanhSachMonTrongDon());
        rvConfirmOrderItems.setAdapter(monTrongDonAdapter);
    }

    private void ganTomTatDonHang() {
        CartManager.NguCanhDonHang nguCanhDonHang = cartManager.layNguCanhDonHang();
        long tongTien = tinhTongTien(cartManager.layDanhSachMon());
        String tongTienDaDinhDang = dinhDangGia(tongTien);

        tvConfirmOrderType.setText(getString(
                nguCanhDonHang.laAnTaiQuan() ? R.string.order_type_dine_in : R.string.order_type_take_away
        ));
        tvConfirmTableNumber.setText(nguCanhDonHang.laAnTaiQuan()
                ? getString(R.string.order_table_format, nguCanhDonHang.laySoBan())
                : getString(R.string.order_table_not_required));
        tvConfirmNote.setText(TextUtils.isEmpty(nguCanhDonHang.layGhiChu())
                ? getString(R.string.order_note_empty)
                : getString(R.string.order_note_format, nguCanhDonHang.layGhiChu()));
        tvConfirmSubtotal.setText(getString(R.string.cart_subtotal_label, tongTienDaDinhDang));
        tvConfirmTotal.setText(getString(R.string.cart_total_label, tongTienDaDinhDang));
        tvConfirmPaymentHint.setText(nguCanhDonHang.laAnTaiQuan()
                ? getString(R.string.order_confirmation_payment_hint_dine_in)
                : getString(R.string.order_confirmation_payment_hint_takeaway));
    }

    private void thietLapNutHanhDong() {
        btnSendOrder.setOnClickListener(v -> xuLyTaoDonHang(CheDoGuiDon.GUI_DON));
        btnPayNow.setOnClickListener(v -> xuLyTaoDonHang(CheDoGuiDon.THANH_TOAN_NGAY));
        btnRequestPayment.setOnClickListener(v -> xuLyTaoDonHang(CheDoGuiDon.GOI_THANH_TOAN));
    }

    private void xuLyTaoDonHang(CheDoGuiDon cheDoGuiDon) {
        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        List<CartManager.CartItem> danhSachMon = cartManager.layDanhSachMon();
        CartManager.NguCanhDonHang nguCanhDonHang = cartManager.layNguCanhDonHang();
        if (idNguoiDung <= 0 || danhSachMon.isEmpty() || !nguCanhDonHang.hopLeDeDatHang()) {
            Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        datTrangThaiDangGui(true, cheDoGuiDon);

        String maDonHang = taoMaDonHang();
        String thoiGianDat = layThoiGianHienTai();
        String tongTien = dinhDangGia(tinhTongTien(danhSachMon));
        List<DonHang.MonTrongDon> danhSachMonDat = xayDanhSachMonTrongDon();

        DonHang.TrangThaiThanhToan trangThaiThanhToan = DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN;
        DonHang.PhuongThucThanhToan phuongThucThanhToan = DonHang.PhuongThucThanhToan.CHUA_CHON;
        if (cheDoGuiDon == CheDoGuiDon.THANH_TOAN_NGAY) {
            trangThaiThanhToan = DonHang.TrangThaiThanhToan.DA_THANH_TOAN_MO_PHONG;
            phuongThucThanhToan = DonHang.PhuongThucThanhToan.THANH_TOAN_NGAY_MO_PHONG;
        } else if (cheDoGuiDon == CheDoGuiDon.GOI_THANH_TOAN) {
            trangThaiThanhToan = DonHang.TrangThaiThanhToan.DA_GOI_THANH_TOAN;
            phuongThucThanhToan = DonHang.PhuongThucThanhToan.TAI_QUAY;
        }

        long idDonHang = databaseHelper.themDonHang(
                (int) idNguoiDung,
                maDonHang,
                thoiGianDat,
                tongTien,
                DonHang.TrangThai.CHO_XAC_NHAN,
                nguCanhDonHang.layHinhThucDon(),
                nguCanhDonHang.laySoBan(),
                nguCanhDonHang.layGhiChu(),
                trangThaiThanhToan,
                phuongThucThanhToan,
                0,
                danhSachMonDat
        );

        if (idDonHang <= 0) {
            datTrangThaiDangGui(false, cheDoGuiDon);
            Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (cheDoGuiDon == CheDoGuiDon.GOI_THANH_TOAN) {
            long idYeuCau = databaseHelper.themYeuCauPhucVu(
                    idNguoiDung,
                    YeuCauPhucVu.LoaiYeuCau.THANH_TOAN,
                    getString(R.string.service_request_payment_for_order_format, maDonHang),
                    nguCanhDonHang.laySoBan(),
                    idDonHang,
                    thoiGianDat,
                    YeuCauPhucVu.TrangThai.DANG_XU_LY
            );
            if (idYeuCau <= 0) {
                datTrangThaiDangGui(false, cheDoGuiDon);
                Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        cartManager.xoaToanBoGio();
        setResult(RESULT_OK);
        Toast.makeText(this, layThongBaoThanhCong(cheDoGuiDon), Toast.LENGTH_SHORT).show();
        moTrungTamTheoDoi();
    }

    private void moTrungTamTheoDoi() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MO_TAB_TRUNG_TAM_HOAT_DONG, TrungTamHoatDongFragment.TAB_ORDERS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private String layThongBaoThanhCong(CheDoGuiDon cheDoGuiDon) {
        if (cheDoGuiDon == CheDoGuiDon.THANH_TOAN_NGAY) {
            return getString(R.string.order_submit_success_paid_mock);
        }
        if (cheDoGuiDon == CheDoGuiDon.GOI_THANH_TOAN) {
            return getString(R.string.order_submit_success_payment_requested);
        }
        return getString(R.string.order_submit_success);
    }

    private void datTrangThaiDangGui(boolean dangGui, CheDoGuiDon cheDoGuiDon) {
        btnSendOrder.setEnabled(!dangGui);
        btnPayNow.setEnabled(!dangGui);
        btnRequestPayment.setEnabled(!dangGui);

        btnSendOrder.setText(dangGui && cheDoGuiDon == CheDoGuiDon.GUI_DON
                ? getString(R.string.order_submitting)
                : getString(R.string.order_send));
        btnPayNow.setText(dangGui && cheDoGuiDon == CheDoGuiDon.THANH_TOAN_NGAY
                ? getString(R.string.order_submitting)
                : getString(R.string.order_pay_now));
        btnRequestPayment.setText(dangGui && cheDoGuiDon == CheDoGuiDon.GOI_THANH_TOAN
                ? getString(R.string.order_submitting)
                : getString(R.string.order_request_payment));
    }

    private List<DonHang.MonTrongDon> xayDanhSachMonTrongDon() {
        List<DonHang.MonTrongDon> danhSachMonDat = new ArrayList<>();
        for (CartManager.CartItem monTrongGio : cartManager.layDanhSachMon()) {
            MonAnDeXuat monAn = monTrongGio.layMonAn();
            danhSachMonDat.add(new DonHang.MonTrongDon(monAn, monTrongGio.laySoLuong()));
        }
        return danhSachMonDat;
    }

    private long tinhTongTien(List<CartManager.CartItem> danhSachMon) {
        long tongTien = 0;
        for (CartManager.CartItem monTrongGio : danhSachMon) {
            tongTien += tachGiaTienTuChuoi(monTrongGio.layMonAn().layGia()) * monTrongGio.laySoLuong();
        }
        return tongTien;
    }

    private long tachGiaTienTuChuoi(@Nullable String chuoiGia) {
        if (chuoiGia == null || chuoiGia.isEmpty()) {
            return 0;
        }
        String chuoiDaLamSach = chuoiGia.replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(chuoiDaLamSach);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String dinhDangGia(long giaTien) {
        return String.format(Locale.getDefault(), "%,d đ", giaTien);
    }

    private String taoMaDonHang() {
        return getString(R.string.cart_order_code_prefix) + System.currentTimeMillis() % 100000;
    }

    private String layThoiGianHienTai() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    private enum CheDoGuiDon {
        GUI_DON,
        THANH_TOAN_NGAY,
        GOI_THANH_TOAN
    }
}
