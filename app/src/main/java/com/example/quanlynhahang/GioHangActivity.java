package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.List;
import java.util.Locale;

public class GioHangActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private TextView tvCartSubtitle;
    private TextView tvCartTotal;
    private TextView tvCartSubtotal;
    private TextView tvCartContextHint;
    private TextView tvCartEmpty;
    private View layoutTableNumber;
    private RadioGroup rgCartOrderType;
    private RadioButton rbCartDineIn;
    private RadioButton rbCartTakeAway;
    private EditText etCartTableNumber;
    private EditText etCartNote;
    private Button btnCheckout;
    private Button btnClearCart;
    private Button btnContinueShopping;

    private MonTrongGioAdapter cartAdapter;
    private CartManager cartManager;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private boolean choXacNhanSauDangNhap;

    private final ActivityResultLauncher<Intent> boMoDangNhap = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (choXacNhanSauDangNhap && sessionManager.daDangNhap()) {
                    choXacNhanSauDangNhap = false;
                    moManXacNhanDonHang();
                    return;
                }
                choXacNhanSauDangNhap = false;
                capNhatHienThiGioHang();
                dongBoNguCanhLenForm();
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
        thietLapNguCanhDonHang();
        thietLapNutDatHang();
        thietLapNutHanhDong();
        dongBoNguCanhLenForm();
        capNhatHienThiGioHang();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capNhatHienThiGioHang();
        dongBoNguCanhLenForm();
    }

    private void khoiTaoView() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartSubtitle = findViewById(R.id.tvCartSubtitle);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        tvCartSubtotal = findViewById(R.id.tvCartSubtotal);
        tvCartContextHint = findViewById(R.id.tvCartContextHint);
        tvCartEmpty = findViewById(R.id.tvCartEmpty);
        layoutTableNumber = findViewById(R.id.layoutTableNumber);
        rgCartOrderType = findViewById(R.id.rgCartOrderType);
        rbCartDineIn = findViewById(R.id.rbCartDineIn);
        rbCartTakeAway = findViewById(R.id.rbCartTakeAway);
        etCartTableNumber = findViewById(R.id.etCartTableNumber);
        etCartNote = findViewById(R.id.etCartNote);
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
                        cartManager.tangSoLuong(cartManager.layKhoaMon(item));
                        capNhatHienThiGioHang();
                    }

                    @Override
                    public void khiGiamSoLuong(CartManager.CartItem item) {
                        cartManager.giamSoLuong(cartManager.layKhoaMon(item));
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

    private void thietLapNguCanhDonHang() {
        rgCartOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean dangAnTaiQuan = checkedId == R.id.rbCartDineIn;
            capNhatHienThiTheoHinhThucDon(dangAnTaiQuan, true);
        });
    }

    private void thietLapNutDatHang() {
        btnCheckout.setOnClickListener(v -> moManXacNhanDonHang());
    }

    private void thietLapNutHanhDong() {
        btnClearCart.setOnClickListener(v -> xoaToanBoGioHang());
        btnContinueShopping.setOnClickListener(v -> finish());
    }

    private void dongBoNguCanhLenForm() {
        CartManager.NguCanhDonHang nguCanhDonHang = cartManager.layNguCanhDonHang();
        if (nguCanhDonHang.layHinhThucDon() == DonHang.HinhThucDon.AN_TAI_QUAN) {
            rbCartDineIn.setChecked(true);
        } else {
            rbCartTakeAway.setChecked(true);
        }
        etCartTableNumber.setText(nguCanhDonHang.laySoBan());
        etCartNote.setText(nguCanhDonHang.layGhiChu());
        capNhatHienThiTheoHinhThucDon(nguCanhDonHang.laAnTaiQuan(), false);
    }

    private void capNhatHienThiTheoHinhThucDon(boolean dangAnTaiQuan, boolean xoaSoBanNeuKhongCan) {
        layoutTableNumber.setVisibility(dangAnTaiQuan ? View.VISIBLE : View.GONE);
        if (!dangAnTaiQuan && xoaSoBanNeuKhongCan) {
            etCartTableNumber.setText("");
        }
        tvCartContextHint.setText(getString(
                dangAnTaiQuan ? R.string.cart_context_hint_dine_in : R.string.cart_context_hint_takeaway
        ));
    }

    private void capNhatHienThiGioHang() {
        List<CartManager.CartItem> danhSachMon = cartManager.layDanhSachMon();
        cartAdapter.capNhatDuLieu(danhSachMon);

        long tongTien = tinhTongTien(danhSachMon);
        String tongTienDaDinhDang = dinhDangGia(tongTien);
        tvCartSubtotal.setText(getString(R.string.cart_subtotal_label, tongTienDaDinhDang));
        tvCartTotal.setText(getString(R.string.cart_total_label, tongTienDaDinhDang));

        boolean gioHangRong = danhSachMon.isEmpty();
        rvCartItems.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        tvCartEmpty.setVisibility(gioHangRong ? View.VISIBLE : View.GONE);
        btnContinueShopping.setVisibility(gioHangRong ? View.VISIBLE : View.GONE);
        btnClearCart.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        btnCheckout.setEnabled(!gioHangRong);
        btnCheckout.setAlpha(gioHangRong ? 0.5f : 1f);
        tvCartSubtitle.setText(getString(R.string.cart_subtitle));
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
        cartManager.xoaMon(cartManager.layKhoaMon(monTrongGio));
        capNhatHienThiGioHang();
        Toast.makeText(this, getString(R.string.cart_item_removed, tenMon), Toast.LENGTH_SHORT).show();
    }

    private void xoaToanBoGioHang() {
        if (cartManager.laGioHangRong()) {
            capNhatHienThiGioHang();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.cart_clear_confirm_title)
                .setMessage(R.string.cart_clear_confirm_message)
                .setPositiveButton(R.string.cart_clear_confirm_action, (dialog, which) -> {
                    cartManager.xoaToanBoGio();
                    dongBoNguCanhLenForm();
                    capNhatHienThiGioHang();
                    Toast.makeText(this, R.string.cart_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void moManXacNhanDonHang() {
        List<CartManager.CartItem> danhSachMon = cartManager.layDanhSachMon();
        if (danhSachMon.isEmpty()) {
            Toast.makeText(this, R.string.cart_empty_message, Toast.LENGTH_SHORT).show();
            capNhatHienThiGioHang();
            return;
        }

        luuNguCanhDonHangTuForm(false);

        if (!sessionManager.daDangNhap()) {
            choXacNhanSauDangNhap = true;
            Toast.makeText(this, R.string.cart_checkout_requires_login, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DangNhapActivity.class);
            intent.putExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true);
            boMoDangNhap.launch(intent);
            return;
        }

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDung <= 0) {
            choXacNhanSauDangNhap = true;
            sessionManager.xoaPhienDangNhap();
            Toast.makeText(this, R.string.session_invalid, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DangNhapActivity.class);
            intent.putExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true);
            boMoDangNhap.launch(intent);
            return;
        }

        if (!luuNguCanhDonHangTuForm(true)) {
            return;
        }

        startActivity(new Intent(this, XacNhanDonHangActivity.class));
    }

    private boolean luuNguCanhDonHangTuForm(boolean kiemTraBatBuoc) {
        DonHang.HinhThucDon hinhThucDon = rbCartDineIn.isChecked()
                ? DonHang.HinhThucDon.AN_TAI_QUAN
                : DonHang.HinhThucDon.MANG_DI;
        String soBan = layTextDaCatKhoangTrang(etCartTableNumber);
        String ghiChu = layTextDaCatKhoangTrang(etCartNote);

        if (hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN && TextUtils.isEmpty(soBan)) {
            if (kiemTraBatBuoc) {
                Toast.makeText(this, R.string.cart_validation_table_required, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        cartManager.capNhatNguCanhDonHang(hinhThucDon, soBan, ghiChu);
        return true;
    }

    private String layTextDaCatKhoangTrang(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
