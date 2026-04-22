package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.ContextCompat;

import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MonTrongDonAdapter;
import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class XacNhanDonHangActivity extends AppCompatActivity {

    private RecyclerView rvConfirmOrderItems;
    private TextView tvConfirmOrderType;
    private TextView tvConfirmTableNumber;
    private TextView tvConfirmNote;
    private TextView tvConfirmSubtotal;
    private TextView tvConfirmTotal;
    private TextView tvConfirmPaymentHint;
    private MaterialButton btnSendOrder;
    private MaterialButton btnSecondaryAction;

    private MonTrongDonAdapter monTrongDonAdapter;
    private QuanLyGioHang quanLyGioHang;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private DonHang.PhuongThucThanhToan phuongThucThanhToanMangDi = DonHang.PhuongThucThanhToan.TIEN_MAT_KHI_NHAN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xac_nhan_don_hang);

        sessionManager = new SessionManager(this);
        quanLyGioHang = layGioKhachHang();
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

    private QuanLyGioHang layGioKhachHang() {
        return QuanLyGioHang.layInstance(sessionManager.layKhoaPhienKhachHang());
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
        btnSecondaryAction = findViewById(R.id.btnSecondaryAction);
    }

    private boolean kiemTraDuLieuDauVao() {
        if (!sessionManager.daDangNhap() || sessionManager.layIdNguoiDungHienTai() <= 0) {
            Toast.makeText(this, R.string.cart_checkout_requires_login, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        if (quanLyGioHang.laGioHangRong()) {
            Toast.makeText(this, R.string.cart_empty_message, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        QuanLyGioHang.NguCanhDonHang nguCanhDonHang = quanLyGioHang.layNguCanhDonHang();
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
        QuanLyGioHang.NguCanhDonHang nguCanhDonHang = quanLyGioHang.layNguCanhDonHang();
        long tongTien = tinhTongTien(quanLyGioHang.layDanhSachMon());
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
        boolean laAnTaiQuan = nguCanhDonHang.laAnTaiQuan();
        tvConfirmPaymentHint.setText(laAnTaiQuan
                ? getString(R.string.order_confirmation_payment_hint_dine_in)
                : getString(R.string.order_confirmation_payment_hint_takeaway));
        btnSendOrder.setText(getString(laAnTaiQuan ? R.string.order_send : R.string.cart_checkout_takeaway));
        btnSecondaryAction.setText(getString(laAnTaiQuan ? R.string.order_back_to_edit : R.string.order_back_to_edit));
    }

    private void thietLapNutHanhDong() {
        btnSendOrder.setOnClickListener(v -> xuLyTheoNguCanh());
        btnSecondaryAction.setOnClickListener(v -> finish());
    }

    private void xuLyTheoNguCanh() {
        QuanLyGioHang.NguCanhDonHang nguCanhDonHang = quanLyGioHang.layNguCanhDonHang();
        if (nguCanhDonHang.laAnTaiQuan()) {
            xuLyTaoDonHang(CheDoGuiDon.GUI_DON);
            return;
        }
        moLuaChonThanhToanMangDi();
    }

    private void xuLyTaoDonHang(CheDoGuiDon cheDoGuiDon) {
        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        List<QuanLyGioHang.MonTrongGio> danhSachMon = quanLyGioHang.layDanhSachMon();
        QuanLyGioHang.NguCanhDonHang nguCanhDonHang = quanLyGioHang.layNguCanhDonHang();
        if (idNguoiDung <= 0 || danhSachMon.isEmpty() || !nguCanhDonHang.hopLeDeDatHang()) {
            hienThiPhanHoiNgan(R.string.order_submit_failed);
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
            trangThaiThanhToan = DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN;
            phuongThucThanhToan = DonHang.PhuongThucThanhToan.TAI_QUAY;
        } else if (cheDoGuiDon == CheDoGuiDon.XAC_NHAN_THANH_TOAN_MANG_DI) {
            trangThaiThanhToan = DonHang.TrangThaiThanhToan.CHUA_THANH_TOAN;
            phuongThucThanhToan = phuongThucThanhToanMangDi;
        }

        long reservationId = timReservationIdApDung(nguCanhDonHang, thoiGianDat);
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
                reservationId,
                danhSachMonDat
        );

        if (idDonHang <= 0) {
            datTrangThaiDangGui(false, cheDoGuiDon);
            hienThiPhanHoiNgan(R.string.order_submit_failed);
            return;
        }

        if (nguCanhDonHang.laAnTaiQuan()) {
            sessionManager.luuBanHienTai(nguCanhDonHang.laySoBan());
        }
        quanLyGioHang.xoaToanBoGio();
        setResult(RESULT_OK);
        hienThiPhanHoiNgan(layThongBaoThanhCong(cheDoGuiDon));
        moTrungTamTheoDoi();
    }

    private void moTrungTamTheoDoi() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MO_TAB_TRUNG_TAM_HOAT_DONG, TrungTamHoatDongFragment.TAB_ORDERS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private int layThongBaoThanhCong(CheDoGuiDon cheDoGuiDon) {
        if (cheDoGuiDon == CheDoGuiDon.THANH_TOAN_NGAY) {
            return R.string.order_submit_success_paid_mock;
        }
        if (cheDoGuiDon == CheDoGuiDon.GOI_THANH_TOAN) {
            return R.string.order_submit_success_payment_requested;
        }
        if (cheDoGuiDon == CheDoGuiDon.XAC_NHAN_THANH_TOAN_MANG_DI) {
            return R.string.order_submit_success;
        }
        return R.string.order_submit_success;
    }

    private void datTrangThaiDangGui(boolean dangGui, CheDoGuiDon cheDoGuiDon) {
        btnSendOrder.setEnabled(!dangGui);
        btnSecondaryAction.setEnabled(!dangGui);

        btnSendOrder.setText(dangGui ? getString(R.string.order_submitting) : getString(layNhanNutTheoCheDo(cheDoGuiDon)));
        btnSecondaryAction.setText(R.string.order_back_to_edit);
    }

    private int layNhanNutTheoCheDo(CheDoGuiDon cheDoGuiDon) {
        if (cheDoGuiDon == CheDoGuiDon.THANH_TOAN_NGAY) {
            return R.string.order_pay_now;
        }
        if (cheDoGuiDon == CheDoGuiDon.GOI_THANH_TOAN) {
            return R.string.order_request_payment;
        }
        if (cheDoGuiDon == CheDoGuiDon.XAC_NHAN_THANH_TOAN_MANG_DI) {
            return R.string.order_confirm_payment;
        }
        return quanLyGioHang.layNguCanhDonHang().laAnTaiQuan()
                ? R.string.order_send
                : R.string.cart_checkout_takeaway;
    }

    private List<DonHang.MonTrongDon> xayDanhSachMonTrongDon() {
        List<DonHang.MonTrongDon> danhSachMonDat = new ArrayList<>();
        for (QuanLyGioHang.MonTrongGio monTrongGio : quanLyGioHang.layDanhSachMon()) {
            MonAnDeXuat monAn = monTrongGio.layMonAn();
            danhSachMonDat.add(new DonHang.MonTrongDon(monAn, monTrongGio.laySoLuong()));
        }
        return danhSachMonDat;
    }

    private long tinhTongTien(List<QuanLyGioHang.MonTrongGio> danhSachMon) {
        long tongTien = 0;
        for (QuanLyGioHang.MonTrongGio monTrongGio : danhSachMon) {
            tongTien += MoneyUtils.tachGiaTienTuChuoi(monTrongGio.layMonAn().layGiaBan()) * monTrongGio.laySoLuong();
        }
        return tongTien;
    }

    private String dinhDangGia(long giaTien) {
        return MoneyUtils.dinhDangTienViet(giaTien);
    }

    private void hienThiPhanHoiNgan(int messageRes) {
        hienThiPhanHoiNgan(getString(messageRes));
    }

    private void hienThiPhanHoiNgan(String message) {
        View root = findViewById(android.R.id.content);
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private long timReservationIdApDung(QuanLyGioHang.NguCanhDonHang nguCanhDonHang, String thoiGianDat) {
        if (nguCanhDonHang == null || !nguCanhDonHang.laAnTaiQuan()) {
            return 0;
        }
        long userId = sessionManager.layIdNguoiDungHienTai();
        if (userId <= 0 || TextUtils.isEmpty(nguCanhDonHang.laySoBan())) {
            return 0;
        }
        com.example.quanlynhahang.model.DatBan datBanHieuLuc = databaseHelper.timDatBanHieuLucTheoNguoiDung(
                userId,
                nguCanhDonHang.laySoBan(),
                thoiGianDat
        );
        if (datBanHieuLuc == null || datBanHieuLuc.layIdDonHangLienKet() > 0) {
            return 0;
        }
        return datBanHieuLuc.layId();
    }

    private String taoMaDonHang() {
        return getString(R.string.cart_order_code_prefix) + System.currentTimeMillis() % 100000;
    }

    private String layThoiGianHienTai() {
        return DateTimeUtils.layThoiGianHienTai();
    }

    private void moLuaChonThanhToanMangDi() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.space_lg);
        container.setPadding(padding, padding / 2, padding, 0);

        TextView message = new TextView(this);
        message.setText(R.string.order_choose_payment_message);
        message.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        container.addView(message);

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setPadding(0, padding / 2, 0, 0);

        AppCompatRadioButton rbCash = new AppCompatRadioButton(this);
        rbCash.setId(View.generateViewId());
        rbCash.setText(R.string.order_payment_option_cash);
        radioGroup.addView(rbCash);

        AppCompatRadioButton rbBank = new AppCompatRadioButton(this);
        rbBank.setId(View.generateViewId());
        rbBank.setText(R.string.order_payment_option_bank_transfer);
        radioGroup.addView(rbBank);

        AppCompatRadioButton rbWallet = new AppCompatRadioButton(this);
        rbWallet.setId(View.generateViewId());
        rbWallet.setText(R.string.order_payment_option_ewallet);
        radioGroup.addView(rbWallet);

        int checkedId = rbCash.getId();
        if (phuongThucThanhToanMangDi == DonHang.PhuongThucThanhToan.CHUYEN_KHOAN_NGAN_HANG) {
            checkedId = rbBank.getId();
        } else if (phuongThucThanhToanMangDi == DonHang.PhuongThucThanhToan.VI_DIEN_TU) {
            checkedId = rbWallet.getId();
        }
        radioGroup.check(checkedId);
        container.addView(radioGroup);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.order_choose_payment_title)
                .setView(container)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.order_confirm_payment, null)
                .create();

        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == rbBank.getId()) {
                phuongThucThanhToanMangDi = DonHang.PhuongThucThanhToan.CHUYEN_KHOAN_NGAN_HANG;
            } else if (selectedId == rbWallet.getId()) {
                phuongThucThanhToanMangDi = DonHang.PhuongThucThanhToan.VI_DIEN_TU;
            } else {
                phuongThucThanhToanMangDi = DonHang.PhuongThucThanhToan.TIEN_MAT_KHI_NHAN;
            }
            dialog.dismiss();
            xuLyTaoDonHang(CheDoGuiDon.XAC_NHAN_THANH_TOAN_MANG_DI);
        }));
        dialog.show();
    }

    private enum CheDoGuiDon {
        GUI_DON,
        THANH_TOAN_NGAY,
        GOI_THANH_TOAN,
        XAC_NHAN_THANH_TOAN_MANG_DI
    }
}
