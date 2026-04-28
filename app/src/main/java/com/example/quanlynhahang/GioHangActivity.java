package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.ActivityGioHangBinding;
import com.example.quanlynhahang.helper.DichVuKhachHangHelper;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.example.quanlynhahang.model.BanAn;
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

public class GioHangActivity extends AppCompatActivity {

    private ActivityGioHangBinding binding;
    private MonTrongGioAdapter boDieuHopGioHang;
    private QuanLyGioHang quanLyGioHang;
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
        binding = ActivityGioHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        quanLyGioHang = layGioKhachHang();
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);

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

    private void thietLapRecyclerView() {
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));

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

        binding.rvCartItems.setAdapter(boDieuHopGioHang);
    }

    private void thietLapNguCanhDonHang() {
        binding.rgCartOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean dangAnTaiQuan = checkedId == R.id.rbCartDineIn;
            capNhatHienThiTheoHinhThucDon(dangAnTaiQuan, true);
        });
        binding.btnCartSelectTable.setOnClickListener(v -> moDialogChonBan());
    }

    private void thietLapNutDatHang() {
        binding.btnCheckout.setOnClickListener(v -> moManXacNhanDonHang());
    }

    private void thietLapNutHanhDong() {
        binding.btnClearCart.setOnClickListener(v -> xoaToanBoGioHang());
        binding.btnContinueShopping.setOnClickListener(v -> finish());
    }

    private void dongBoNguCanhLenForm() {
        QuanLyGioHang.NguCanhDonHang nguCanhDonHang = quanLyGioHang.layNguCanhDonHang();
        if (nguCanhDonHang.layHinhThucDon() == DonHang.HinhThucDon.AN_TAI_QUAN) {
            binding.rbCartDineIn.setChecked(true);
        } else {
            binding.rbCartTakeAway.setChecked(true);
        }
        String soBanUuTien = !TextUtils.isEmpty(nguCanhDonHang.laySoBan())
                ? nguCanhDonHang.laySoBan()
                : sessionManager.layBanHienTai();
        capNhatNhanBan(soBanUuTien);
        binding.etCartNote.setText(nguCanhDonHang.layGhiChu());
        capNhatHienThiTheoHinhThucDon(nguCanhDonHang.laAnTaiQuan(), false);
    }

    private void capNhatHienThiTheoHinhThucDon(boolean dangAnTaiQuan, boolean xoaSoBanNeuKhongCan) {
        binding.layoutTableNumber.setVisibility(dangAnTaiQuan ? View.VISIBLE : View.GONE);
        binding.btnCheckout.setText(getString(
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
        binding.tvCartSubtotal.setText(getString(R.string.cart_subtotal_label, tongTienDaDinhDang));
        binding.tvCartTotal.setText(getString(R.string.cart_total_label, tongTienDaDinhDang));

        boolean gioHangRong = danhSachMon.isEmpty();
        binding.rvCartItems.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        binding.layoutCartEmpty.setVisibility(gioHangRong ? View.VISIBLE : View.GONE);
        binding.btnContinueShopping.setVisibility(gioHangRong ? View.VISIBLE : View.GONE);
        binding.btnClearCart.setVisibility(gioHangRong ? View.GONE : View.VISIBLE);
        binding.btnCheckout.setEnabled(!gioHangRong);
        binding.btnCheckout.setAlpha(gioHangRong ? 0.5f : 1f);
    }

    private long tinhTongTien(List<QuanLyGioHang.MonTrongGio> danhSachMon) {
        long tongTien = 0;
        for (QuanLyGioHang.MonTrongGio monTrongGio : danhSachMon) {
            long giaTien = MoneyUtils.tachGiaTienTuChuoi(monTrongGio.layMonAn().layGiaBan());
            tongTien += giaTien * monTrongGio.laySoLuong();
        }
        return tongTien;
    }

    private String dinhDangGia(long giaTien) {
        return MoneyUtils.dinhDangTienViet(giaTien);
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
                    sessionManager.xoaBanHienTai();
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
        DonHang.HinhThucDon hinhThucDon = binding.rbCartDineIn.isChecked()
                ? DonHang.HinhThucDon.AN_TAI_QUAN
                : DonHang.HinhThucDon.MANG_DI;
        String soBan = binding.btnCartSelectTable.getText() == null
                ? ""
                : binding.btnCartSelectTable.getText().toString().trim();
        if (TextUtils.equals(soBan, getString(R.string.cart_table_unselected))) {
            soBan = "";
        }
        String ghiChu = layTextDaCatKhoangTrang(binding.etCartNote);

        if (hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN && TextUtils.isEmpty(soBan)) {
            if (kiemTraBatBuoc) {
                Toast.makeText(this, R.string.cart_validation_table_required, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        quanLyGioHang.capNhatNguCanhDonHang(hinhThucDon, soBan, ghiChu);
        if (hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN) {
            sessionManager.luuBanHienTai(soBan);
        } else {
            sessionManager.xoaBanHienTai();
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
                sessionManager.layBanHienTai(),
                DichVuKhachHangHelper.timDonHangTaiQuanDangHoatDong(databaseHelper.layDonHangTheoNguoiDung(sessionManager.layIdNguoiDungHienTai())),
                () -> quanLyGioHang.layNguCanhDonHang().laAnTaiQuan()
                        ? quanLyGioHang.layNguCanhDonHang().laySoBan()
                        : null
        );

        for (BanAn banAn : databaseHelper.layTatCaBanAn()) {
            String tenBan = banAn.layTenBan();
            if (TextUtils.isEmpty(tenBan)) {
                continue;
            }
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
        binding.btnCartSelectTable.setText(TextUtils.isEmpty(soBanDaCatKhoangTrang)
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
