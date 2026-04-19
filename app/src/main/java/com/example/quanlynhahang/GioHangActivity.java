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

import com.google.android.material.button.MaterialButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MonTrongGioAdapter;
import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DichVuKhachHangHelper;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Locale;

public class GioHangActivity extends AppCompatActivity {

    private static final int SO_BAN_TOI_DA = 20;

    private RecyclerView rvCartItems;
    private TextView tvCartTotal;
    private TextView tvCartSubtotal;
    private View layoutTableNumber;
    private RadioGroup rgCartOrderType;
    private RadioButton rbCartDineIn;
    private RadioButton rbCartTakeAway;
    private MaterialButton btnCartSelectTable;
    private EditText etCartNote;
    private Button btnCheckout;
    private Button btnClearCart;
    private Button btnContinueShopping;

    private MonTrongGioAdapter boDieuHopGioHang;
    private QuanLyGioHang quanLyGioHang;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private SessionManager quanLyPhienBan;

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

        sessionManager = new SessionManager(this);
        quanLyPhienBan = sessionManager;
        quanLyGioHang = layGioKhachHang();
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);

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

    private QuanLyGioHang layGioKhachHang() {
        return QuanLyGioHang.layInstance(sessionManager.layKhoaPhienKhachHang());
    }

    private void khoiTaoView() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        tvCartSubtotal = findViewById(R.id.tvCartSubtotal);
        layoutTableNumber = findViewById(R.id.layoutTableNumber);
        rgCartOrderType = findViewById(R.id.rgCartOrderType);
        rbCartDineIn = findViewById(R.id.rbCartDineIn);
        rbCartTakeAway = findViewById(R.id.rbCartTakeAway);
        btnCartSelectTable = findViewById(R.id.btnCartSelectTable);
        etCartNote = findViewById(R.id.etCartNote);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void thietLapRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        boDieuHopGioHang = new MonTrongGioAdapter(
                quanLyGioHang.layDanhSachMon(),
                new MonTrongGioAdapter.OnHanhDongSoLuongListener() {
                    @Override
                    public void khiTangSoLuong(QuanLyGioHang.MonTrongGio item) {
                        quanLyGioHang.tangSoLuong(quanLyGioHang.layKhoaMon(item));
                        capNhatHienThiGioHang();
                    }

                    @Override
                    public void khiGiamSoLuong(QuanLyGioHang.MonTrongGio item) {
                        quanLyGioHang.giamSoLuong(quanLyGioHang.layKhoaMon(item));
                        capNhatHienThiGioHang();
                    }

                    @Override
                    public void khiXoaMon(QuanLyGioHang.MonTrongGio item) {
                        xoaTungMon(item);
                    }
                }
        );

        rvCartItems.setAdapter(boDieuHopGioHang);
    }

    private void thietLapNguCanhDonHang() {
        rgCartOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean dangAnTaiQuan = checkedId == R.id.rbCartDineIn;
            capNhatHienThiTheoHinhThucDon(dangAnTaiQuan, true);
        });
        btnCartSelectTable.setOnClickListener(v -> moDialogChonBan());
    }

    private void thietLapNutDatHang() {
        btnCheckout.setOnClickListener(v -> moManXacNhanDonHang());
    }

    private void thietLapNutHanhDong() {
        btnClearCart.setOnClickListener(v -> xoaToanBoGioHang());
        btnContinueShopping.setOnClickListener(v -> finish());
    }

    private void dongBoNguCanhLenForm() {
        QuanLyGioHang.NguCanhDonHang nguCanhDonHang = quanLyGioHang.layNguCanhDonHang();
        if (nguCanhDonHang.layHinhThucDon() == DonHang.HinhThucDon.AN_TAI_QUAN) {
            rbCartDineIn.setChecked(true);
        } else {
            rbCartTakeAway.setChecked(true);
        }
        String soBanUuTien = !TextUtils.isEmpty(nguCanhDonHang.laySoBan())
                ? nguCanhDonHang.laySoBan()
                : quanLyPhienBan.layBanHienTai();
        capNhatNhanBan(soBanUuTien);
        etCartNote.setText(nguCanhDonHang.layGhiChu());
        capNhatHienThiTheoHinhThucDon(nguCanhDonHang.laAnTaiQuan(), false);
    }

    private void capNhatHienThiTheoHinhThucDon(boolean dangAnTaiQuan, boolean xoaSoBanNeuKhongCan) {
        layoutTableNumber.setVisibility(dangAnTaiQuan ? View.VISIBLE : View.GONE);
        btnCheckout.setText(getString(
                dangAnTaiQuan ? R.string.cart_checkout_dine_in : R.string.cart_checkout_takeaway
        ));

        if (!dangAnTaiQuan && xoaSoBanNeuKhongCan) {
            capNhatNhanBan(null);
        }
    }

    private void capNhatHienThiGioHang() {
        List<QuanLyGioHang.MonTrongGio> danhSachMon = quanLyGioHang.layDanhSachMon();
        boDieuHopGioHang.capNhatDuLieu(danhSachMon);

        long tongTien = tinhTongTien(danhSachMon);
        String tongTienDaDinhDang = dinhDangGia(tongTien);
        tvCartSubtotal.setText(getString(R.string.cart_subtotal_label, tongTienDaDinhDang));
        tvCartTotal.setText(getString(R.string.cart_total_label, tongTienDaDinhDang));

        boolean gioHangRong = danhSachMon.isEmpty();
        rvCartItems.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        btnContinueShopping.setVisibility(gioHangRong ? View.VISIBLE : View.GONE);
        btnClearCart.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        btnCheckout.setEnabled(!gioHangRong);
        btnCheckout.setAlpha(gioHangRong ? 0.5f : 1f);
    }

    private long tinhTongTien(List<QuanLyGioHang.MonTrongGio> danhSachMon) {
        long tongTien = 0;
        for (QuanLyGioHang.MonTrongGio monTrongGio : danhSachMon) {
            long giaTien = tachGiaTienTuChuoi(monTrongGio.layMonAn().layGiaBan());
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
        return String.format(Locale.forLanguageTag("vi-VN"), "%,d đ", giaTien).replace(',', '.');
    }

    private void xoaTungMon(@NonNull QuanLyGioHang.MonTrongGio monTrongGio) {
        String tenMon = monTrongGio.layMonAn().layTenMon();
        quanLyGioHang.xoaMon(quanLyGioHang.layKhoaMon(monTrongGio));
        capNhatHienThiGioHang();
        Toast.makeText(this, getString(R.string.cart_item_removed, tenMon), Toast.LENGTH_SHORT).show();
    }

    private void xoaToanBoGioHang() {
        if (quanLyGioHang.laGioHangRong()) {
            capNhatHienThiGioHang();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.cart_clear_confirm_title)
                .setMessage(R.string.cart_clear_confirm_message)
                .setPositiveButton(R.string.cart_clear_confirm_action, (dialog, which) -> {
                    quanLyGioHang.xoaToanBoGio();
                    quanLyPhienBan.xoaBanHienTai();
                    dongBoNguCanhLenForm();
                    capNhatHienThiGioHang();
                    Toast.makeText(this, R.string.cart_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void moManXacNhanDonHang() {
        List<QuanLyGioHang.MonTrongGio> danhSachMon = quanLyGioHang.layDanhSachMon();
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
        String soBan = btnCartSelectTable.getText() == null
                ? ""
                : btnCartSelectTable.getText().toString().trim();
        if (TextUtils.equals(soBan, getString(R.string.cart_table_unselected))) {
            soBan = "";
        }
        String ghiChu = layTextDaCatKhoangTrang(etCartNote);

        if (hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN && TextUtils.isEmpty(soBan)) {
            if (kiemTraBatBuoc) {
                Toast.makeText(this, R.string.cart_validation_table_required, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        quanLyGioHang.capNhatNguCanhDonHang(hinhThucDon, soBan, ghiChu);
        if (hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN) {
            quanLyPhienBan.luuBanHienTai(soBan);
        } else {
            quanLyPhienBan.xoaBanHienTai();
        }
        return true;
    }

    private void moDialogChonBan() {
        List<MucBanUi> danhSachBan = taoDanhSachBanUi();
        ArrayAdapter<MucBanUi> adapter = new ArrayAdapter<MucBanUi>(this, android.R.layout.simple_list_item_1, danhSachBan) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull android.view.ViewGroup parent) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                int padding = getResources().getDimensionPixelSize(R.dimen.space_md);
                row.setPadding(padding, padding, padding, padding);

                MaterialTextView tenBan = new MaterialTextView(getContext());
                tenBan.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                tenBan.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);

                MaterialTextView badge = new MaterialTextView(getContext());
                badge.setPadding(padding, padding / 3, padding, padding / 3);
                badge.setTextSize(12f);

                MucBanUi mucBan = getItem(position);
                if (mucBan == null) {
                    return row;
                }
                tenBan.setText(mucBan.tenBan);
                tenBan.setTextColor(ContextCompat.getColor(getContext(), mucBan.chonDuoc ? R.color.text_primary : R.color.text_secondary));

                if (mucBan.laBanHienTai) {
                    tenBan.setText(mucBan.tenBan + " ✓");
                    row.setBackgroundResource(R.drawable.bg_button_danger_outline);
                    row.getBackground().setTint(ContextCompat.getColor(getContext(), R.color.table_current_border));
                }

                if (!TextUtils.isEmpty(mucBan.nhanTrangThai)) {
                    badge.setText(mucBan.nhanTrangThai);
                    badge.setBackgroundResource(R.drawable.bg_search_rounded);
                    if (mucBan.loaiTrangThai == LoaiTrangThaiBan.DA_GIU) {
                        badge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.table_reserved_badge_bg)));
                        badge.setTextColor(ContextCompat.getColor(getContext(), R.color.table_reserved_badge_text));
                    } else if (mucBan.loaiTrangThai == LoaiTrangThaiBan.DANG_DUNG) {
                        badge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.table_busy_badge_bg)));
                        badge.setTextColor(ContextCompat.getColor(getContext(), R.color.table_busy_badge_text));
                    } else {
                        badge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.brand_primary_container)));
                        badge.setTextColor(ContextCompat.getColor(getContext(), R.color.brand_primary_dark));
                    }
                    row.addView(tenBan);
                    row.addView(badge);
                    return row;
                }

                row.addView(tenBan);
                return row;
            }
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.cart_table_dialog_title)
                .setAdapter(adapter, (dialog, which) -> {
                    MucBanUi mucBan = adapter.getItem(which);
                    if (mucBan == null || !mucBan.chonDuoc) {
                        Toast.makeText(this, mucBan != null && mucBan.loaiTrangThai == LoaiTrangThaiBan.DANG_DUNG
                                ? R.string.table_status_busy
                                : R.string.reservation_table_unavailable, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    capNhatNhanBan(mucBan.tenBan);
                })
                .show();
    }

    private List<MucBanUi> taoDanhSachBanUi() {
        List<MucBanUi> danhSachBan = new ArrayList<>();
        Set<String> banDaGiu = new HashSet<>();
        for (DatBan datBan : databaseHelper.layTatCaDatBan()) {
            if (datBan != null && datBan.laDangHieuLuc()) {
                banDaGiu.add(datBan.laySoBan());
            }
        }

        Set<String> banDangDung = new HashSet<>();
        for (DonHang donHang : databaseHelper.layTatCaDonHang()) {
            if (DichVuKhachHangHelper.laDonTaiQuanDangHoatDong(donHang) && donHang.coBanAn()) {
                banDangDung.add(donHang.laySoBan());
            }
        }

        String banHienTai = DichVuKhachHangHelper.timBanHienTai(
                quanLyPhienBan.layBanHienTai(),
                DichVuKhachHangHelper.timDonHangTaiQuanDangHoatDong(databaseHelper.layDonHangTheoNguoiDung(sessionManager.layIdNguoiDungHienTai())),
                () -> quanLyGioHang.layNguCanhDonHang().laAnTaiQuan()
                        ? quanLyGioHang.layNguCanhDonHang().laySoBan()
                        : null
        );

        for (int index = 0; index < SO_BAN_TOI_DA; index++) {
            String tenBan = getString(R.string.reservation_table_option_format, index + 1);
            boolean laBanHienTai = DichVuKhachHangHelper.laBanHienTai(banHienTai, tenBan);
            boolean dangDung = banDangDung.contains(tenBan) && !laBanHienTai;
            boolean daGiu = banDaGiu.contains(tenBan) && !laBanHienTai && !dangDung;
            LoaiTrangThaiBan loaiTrangThai = laBanHienTai
                    ? LoaiTrangThaiBan.BAN_HIEN_TAI
                    : dangDung ? LoaiTrangThaiBan.DANG_DUNG
                    : daGiu ? LoaiTrangThaiBan.DA_GIU
                    : LoaiTrangThaiBan.TRONG;
            danhSachBan.add(new MucBanUi(
                    tenBan,
                    loaiTrangThai,
                    loaiTrangThai == LoaiTrangThaiBan.TRONG || loaiTrangThai == LoaiTrangThaiBan.BAN_HIEN_TAI,
                    laBanHienTai,
                    layNhanTrangThaiBan(loaiTrangThai)
            ));
        }
        return danhSachBan;
    }

    private String layNhanTrangThaiBan(LoaiTrangThaiBan loaiTrangThaiBan) {
        if (loaiTrangThaiBan == LoaiTrangThaiBan.BAN_HIEN_TAI) {
            return getString(R.string.table_status_current);
        }
        if (loaiTrangThaiBan == LoaiTrangThaiBan.DANG_DUNG) {
            return getString(R.string.table_status_busy);
        }
        if (loaiTrangThaiBan == LoaiTrangThaiBan.DA_GIU) {
            return getString(R.string.table_status_reserved);
        }
        return "";
    }

    private void capNhatNhanBan(@Nullable String soBan) {
        String soBanDaCatKhoangTrang = soBan == null ? "" : soBan.trim();
        btnCartSelectTable.setText(TextUtils.isEmpty(soBanDaCatKhoangTrang)
                ? getString(R.string.cart_table_unselected)
                : soBanDaCatKhoangTrang);
    }

    private enum LoaiTrangThaiBan {
        TRONG,
        DANG_DUNG,
        DA_GIU,
        BAN_HIEN_TAI
    }

    private static final class MucBanUi {
        private final String tenBan;
        private final LoaiTrangThaiBan loaiTrangThai;
        private final boolean chonDuoc;
        private final boolean laBanHienTai;
        private final String nhanTrangThai;

        private MucBanUi(String tenBan,
                         LoaiTrangThaiBan loaiTrangThai,
                         boolean chonDuoc,
                         boolean laBanHienTai,
                         String nhanTrangThai) {
            this.tenBan = tenBan;
            this.loaiTrangThai = loaiTrangThai;
            this.chonDuoc = chonDuoc;
            this.laBanHienTai = laBanHienTai;
            this.nhanTrangThai = nhanTrangThai;
        }

        @NonNull
        @Override
        public String toString() {
            return tenBan;
        }
    }

    private String layTextDaCatKhoangTrang(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
