package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MonTrongGioAdapter;
import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GioHangActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private TextView tvCartTotal;
    private TextView tvCartEmpty;
    private Button btnCheckout;
    private Button btnClearCart;
    private Button btnContinueShopping;

    private MonTrongGioAdapter cartAdapter;
    private CartManager cartManager;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private boolean choDatHangSauDangNhap;

    private final ActivityResultLauncher<Intent> boMoDangNhap = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (choDatHangSauDangNhap && sessionManager.daDangNhap()) {
                    choDatHangSauDangNhap = false;
                    xuLyDatHang();
                    return;
                }
                choDatHangSauDangNhap = false;
                capNhatHienThiGioHang();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        cartManager = CartManager.getInstance();
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        khoiTaoView();
        thietLapRecyclerView();
        thietLapNutDatHang();
        thietLapNutHanhDong();
        capNhatHienThiGioHang();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capNhatHienThiGioHang();
    }

    private void khoiTaoView() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        tvCartEmpty = findViewById(R.id.tvCartEmpty);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void thietLapRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new MonTrongGioAdapter(
                cartManager.layDanhSachMon(),
                new MonTrongGioAdapter.OnHanhDongSoLuongListener() {
                    @Override
                    public void khiTangSoLuong(CartManager.CartItem item) {
                        String key = cartManager.layKhoaMon(item);
                        cartManager.tangSoLuong(key);
                        capNhatHienThiGioHang();
                    }

                    @Override
                    public void khiGiamSoLuong(CartManager.CartItem item) {
                        String key = cartManager.layKhoaMon(item);
                        cartManager.giamSoLuong(key);
                        capNhatHienThiGioHang();
                    }

                    @Override
                    public void khiXoaMon(CartManager.CartItem item) {
                        xoaTungMon(item);
                    }
                }
        );

        rvCartItems.setAdapter(cartAdapter);
    }

    private void thietLapNutDatHang() {
        btnCheckout.setOnClickListener(v -> xuLyDatHang());
    }

    private void thietLapNutHanhDong() {
        btnClearCart.setOnClickListener(v -> xoaToanBoGioHang());
        btnContinueShopping.setOnClickListener(v -> finish());
    }

    private void capNhatHienThiGioHang() {
        List<CartManager.CartItem> danhSachMon = cartManager.layDanhSachMon();
        cartAdapter.capNhatDuLieu(danhSachMon);

        long tongTien = tinhTongTien(danhSachMon);
        tvCartTotal.setText(getString(R.string.cart_total_label, dinhDangGia(tongTien)));

        boolean gioHangRong = danhSachMon.isEmpty();
        rvCartItems.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        tvCartEmpty.setVisibility(gioHangRong ? View.VISIBLE : View.GONE);
        btnContinueShopping.setVisibility(gioHangRong ? View.VISIBLE : View.GONE);
        btnClearCart.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        btnCheckout.setEnabled(!gioHangRong);
        btnCheckout.setAlpha(gioHangRong ? 0.5f : 1f);
    }

    private long tinhTongTien(List<CartManager.CartItem> danhSachMon) {
        long tongTien = 0;
        for (CartManager.CartItem monTrongGio : danhSachMon) {
            long giaTien = tachGiaTienTuChuoi(monTrongGio.layMonAn().layGia());
            tongTien += giaTien * monTrongGio.laySoLuong();
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

    private void xoaTungMon(@NonNull CartManager.CartItem monTrongGio) {
        String tenMon = monTrongGio.layMonAn().layTen();
        String khoaMon = cartManager.layKhoaMon(monTrongGio);
        cartManager.xoaMon(khoaMon);
        capNhatHienThiGioHang();
        Toast.makeText(this, getString(R.string.cart_item_removed, tenMon), Toast.LENGTH_SHORT).show();
    }

    private void xoaToanBoGioHang() {
        if (cartManager.laGioHangRong()) {
            capNhatHienThiGioHang();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage(R.string.cart_clear)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    cartManager.xoaToanBoGio();
                    capNhatHienThiGioHang();
                    Toast.makeText(this, R.string.cart_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void xuLyDatHang() {
        List<CartManager.CartItem> danhSachMon = cartManager.layDanhSachMon();
        if (danhSachMon.isEmpty()) {
            Toast.makeText(this, R.string.cart_empty_message, Toast.LENGTH_SHORT).show();
            capNhatHienThiGioHang();
            return;
        }

        if (!sessionManager.daDangNhap()) {
            choDatHangSauDangNhap = true;
            Toast.makeText(this, R.string.cart_checkout_requires_login, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DangNhapActivity.class);
            intent.putExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true);
            boMoDangNhap.launch(intent);
            return;
        }

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDung <= 0) {
            choDatHangSauDangNhap = true;
            sessionManager.xoaPhienDangNhap();
            Toast.makeText(this, R.string.session_invalid, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DangNhapActivity.class);
            intent.putExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true);
            boMoDangNhap.launch(intent);
            return;
        }

        String maDonHang = taoMaDonHang();
        String thoiGianDat = layThoiGianHienTai();
        long tongTien = tinhTongTien(danhSachMon);
        String chuoiTongTien = dinhDangGia(tongTien);

        List<DonHang.MonTrongDon> danhSachMonDat = new ArrayList<>();
        for (CartManager.CartItem monTrongGio : danhSachMon) {
            MonAnDeXuat monAn = monTrongGio.layMonAn();
            danhSachMonDat.add(new DonHang.MonTrongDon(monAn, monTrongGio.laySoLuong()));
        }

        long idDonHang = databaseHelper.themDonHang(
                (int) idNguoiDung,
                maDonHang,
                thoiGianDat,
                chuoiTongTien,
                DonHang.TrangThai.PENDING_CONFIRMATION,
                danhSachMonDat
        );

        if (idDonHang > 0) {
            choDatHangSauDangNhap = false;
            Toast.makeText(this, R.string.cart_checkout_success, Toast.LENGTH_SHORT).show();
            cartManager.xoaToanBoGio();
            capNhatHienThiGioHang();
            finish();
        } else {
            Toast.makeText(this, R.string.db_operation_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private String taoMaDonHang() {
        long dauThoiGian = System.currentTimeMillis();
        return getString(R.string.cart_order_code_prefix) + dauThoiGian % 100000;
    }

    private String layThoiGianHienTai() {
        SimpleDateFormat dinhDangNgayGio = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return dinhDangNgayGio.format(new Date());
    }
}
