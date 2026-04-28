package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.ActivityTrungTamQuanTriBinding;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public class TrungTamQuanTriActivity extends AppCompatActivity {

    private static final String TAG_MON_AN = "mon_an_quan_tri";
    private static final String TAG_BAN = "quan_ly_ban_quan_tri";
    private static final String TAG_DON_HANG = "don_hang_quan_tri";
    private static final String TAG_HOA_DON = "hoa_don_quan_tri";
    private static final String TAG_YEU_CAU = "yeu_cau_quan_tri";
    private static final String TAG_NGUOI_DUNG = "nguoi_dung_quan_tri";
    private static final String TAG_BAO_CAO = "bao_cao_quan_tri";

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private ActivityTrungTamQuanTriBinding binding;

    public static Intent taoIntent(Context context, String section) {
        Intent intent = new Intent(context, TrungTamQuanTriActivity.class);
        intent.putExtra(
                DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI,
                DieuHuongNoiBoHelper.chuanHoaSection(section)
        );
        return intent;
    }

    private static String chonSectionQuanTriHienThi(@Nullable String sectionDuocYeuCau) {
        if (sectionDuocYeuCau == null || sectionDuocYeuCau.trim().isEmpty()) {
            return DieuHuongNoiBoHelper.SECTION_BAO_CAO;
        }
        String sectionDaRutGon = sectionDuocYeuCau.trim().toLowerCase(java.util.Locale.ROOT);
        if (DieuHuongNoiBoHelper.SECTION_MON.equals(sectionDaRutGon)
                || DieuHuongNoiBoHelper.SECTION_BAN.equals(sectionDaRutGon)
                || DieuHuongNoiBoHelper.SECTION_DON_HANG.equals(sectionDaRutGon)
                || DieuHuongNoiBoHelper.SECTION_HOA_DON.equals(sectionDaRutGon)
                || DieuHuongNoiBoHelper.SECTION_YEU_CAU.equals(sectionDaRutGon)
                || DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG.equals(sectionDaRutGon)
                || DieuHuongNoiBoHelper.SECTION_BAO_CAO.equals(sectionDaRutGon)) {
            return sectionDaRutGon;
        }
        return DieuHuongNoiBoHelper.SECTION_BAO_CAO;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrungTamQuanTriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();

        if (!xacThucPhienQuanTri()) {
            return;
        }

        thietLapMenuQuanTri();
        capNhatThongTinSidebar();

        String sectionDuocYeuCau = getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI);
        String sectionDuocHienThi = chonSectionQuanTriHienThi(sectionDuocYeuCau);
        getIntent().putExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI, sectionDuocHienThi);
        capNhatTieuDe(sectionDuocHienThi);
        capNhatTrangThaiMenu(sectionDuocHienThi);

        if (savedInstanceState == null) {
            moSection(sectionDuocHienThi);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager == null || databaseHelper == null) {
            return;
        }
        if (!xacThucPhienQuanTri()) {
            return;
        }
        capNhatThongTinSidebar();
    }

    private void thietLapMenuQuanTri() {
        boolean laAdmin = sessionManager.laAdmin();
        
        ganDieuHuongMenu(binding.navAdminOverview, DieuHuongNoiBoHelper.SECTION_BAO_CAO);
        ganDieuHuongMenu(binding.navAdminOrders, DieuHuongNoiBoHelper.SECTION_DON_HANG);
        ganDieuHuongMenu(binding.navAdminTables, DieuHuongNoiBoHelper.SECTION_BAN);
        
        // Yêu cầu - chỉ hiển thị cho Nhân viên
        binding.navAdminServiceRequest.setVisibility(laAdmin ? android.view.View.GONE : android.view.View.VISIBLE);
        if (!laAdmin) {
            ganDieuHuongMenu(binding.navAdminServiceRequest, DieuHuongNoiBoHelper.SECTION_YEU_CAU);
        }
        
        // Thực đơn - chỉ hiển thị cho Admin
        binding.navAdminDishes.setVisibility(laAdmin ? android.view.View.VISIBLE : android.view.View.GONE);
        if (laAdmin) {
            ganDieuHuongMenu(binding.navAdminDishes, DieuHuongNoiBoHelper.SECTION_MON);
        }
        
        // Tài khoản - chỉ hiển thị cho Admin  
        binding.navAdminAccounts.setVisibility(laAdmin ? android.view.View.VISIBLE : android.view.View.GONE);
        if (laAdmin) {
            ganDieuHuongMenu(binding.navAdminAccounts, DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG);
        }
        
        binding.btnAdminLogout.setOnClickListener(v -> hienThiDialogDangXuat());
    }

    private void ganDieuHuongMenu(LinearLayout item, String section) {
        item.setOnClickListener(v -> moSection(section));
    }

    private void capNhatTrangThaiMenu(String section) {
        boolean laAdmin = sessionManager.laAdmin();
        
        capNhatTrangThaiBottomNav(
                binding.navAdminOverview,
                binding.iconAdminOverview,
                binding.tvAdminOverviewLabel,
                DieuHuongNoiBoHelper.SECTION_BAO_CAO.equals(section)
        );
        capNhatTrangThaiBottomNav(
                binding.navAdminOrders,
                binding.iconAdminOrders,
                binding.tvAdminOrdersLabel,
                DieuHuongNoiBoHelper.SECTION_DON_HANG.equals(section)
        );
        capNhatTrangThaiBottomNav(
                binding.navAdminTables,
                binding.iconAdminTables,
                binding.tvAdminTablesLabel,
                DieuHuongNoiBoHelper.SECTION_BAN.equals(section)
        );
        
        // Yêu cầu - active cho Nhân viên
        if (!laAdmin) {
            capNhatTrangThaiBottomNav(
                    binding.navAdminServiceRequest,
                    binding.iconAdminServiceRequest,
                    binding.tvAdminServiceRequestLabel,
                    DieuHuongNoiBoHelper.SECTION_YEU_CAU.equals(section)
            );
        }
        
        // Thực đơn - active cho Admin
        if (laAdmin) {
            capNhatTrangThaiBottomNav(
                    binding.navAdminDishes,
                    binding.iconAdminDishes,
                    binding.tvAdminDishesLabel,
                    DieuHuongNoiBoHelper.SECTION_MON.equals(section)
            );
            capNhatTrangThaiBottomNav(
                    binding.navAdminAccounts,
                    binding.iconAdminAccounts,
                    binding.tvAdminAccountsLabel,
                    DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG.equals(section)
            );
        }
    }

    private void capNhatTrangThaiBottomNav(LinearLayout item, ImageView icon, TextView label, boolean dangDuocChon) {
        int mau = ContextCompat.getColor(this, dangDuocChon ? R.color.brand_primary : R.color.nav_unselected);
        item.setBackgroundResource(dangDuocChon ? R.drawable.bg_admin_bottom_nav_active : android.R.color.transparent);
        icon.setImageTintList(ColorStateList.valueOf(mau));
        label.setTextColor(mau);
        label.setTypeface(label.getTypeface(), dangDuocChon ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void capNhatThongTinSidebar() {
    }

    private void capNhatTieuDe(String section) {
        Integer subtitleRes = R.string.admin_reports_subtitle;
        if (!DieuHuongNoiBoHelper.SECTION_BAO_CAO.equals(section)) {
            subtitleRes = null;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitleRes == null ? null : getString(subtitleRes));
        }
    }

    private boolean xacThucPhienQuanTri() {
        if (!sessionManager.daDangNhapNoiBo()) {
            return chuyenVeStaffLauncher();
        }
        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            return chuyenVeStaffLauncher();
        }
        VaiTroNguoiDung vaiTroSession = sessionManager.layVaiTroSessionHopLe();
        if (vaiTroSession != VaiTroNguoiDung.ADMIN && vaiTroSession != VaiTroNguoiDung.NHAN_VIEN) {
            return chuyenVeStaffLauncher();
        }
        return true;
    }

    private boolean chuyenVeStaffLauncher() {
        Intent intent = new Intent(this, StaffLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        return false;
    }

    private void dangXuat() {
        sessionManager.xoaPhienNoiBo();
        Intent intent = new Intent(this, StaffLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void hienThiDialogDangXuat() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.account_logout_confirm_title)
                .setMessage(R.string.account_logout_confirm_message)
                .setPositiveButton(R.string.account_logout, (dialog, which) -> dangXuat())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    public void dieuHuongDenSection(String section) {
        moSection(section);
    }

    private void moSection(String section) {
        String sectionHopLe = DieuHuongNoiBoHelper.chuanHoaSection(section);
        boolean laAdmin = sessionManager.laAdmin();
        
        // Chặn truy cập section không được phép theo role
        if (!laAdmin && (DieuHuongNoiBoHelper.SECTION_MON.equals(sectionHopLe) 
                || DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG.equals(sectionHopLe))) {
            sectionHopLe = DieuHuongNoiBoHelper.SECTION_BAO_CAO;
        }
        if (laAdmin && DieuHuongNoiBoHelper.SECTION_YEU_CAU.equals(sectionHopLe)) {
            sectionHopLe = DieuHuongNoiBoHelper.SECTION_BAO_CAO;
        }
        
        getIntent().putExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI, sectionHopLe);
        sessionManager.luuDuongDanNoiBoCuoi(DieuHuongNoiBoHelper.taoRouteQuanTri(sectionHopLe));
        capNhatTieuDe(sectionHopLe);
        capNhatTrangThaiMenu(sectionHopLe);

        if (DieuHuongNoiBoHelper.SECTION_DON_HANG.equals(sectionHopLe)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new DonHangNoiBoFragment(), TAG_DON_HANG)
                    .commitNow();
            return;
        }

        if (DieuHuongNoiBoHelper.SECTION_HOA_DON.equals(sectionHopLe)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new HoaDonQuanTriFragment(), TAG_HOA_DON)
                    .commitNow();
            return;
        }

        if (DieuHuongNoiBoHelper.SECTION_YEU_CAU.equals(sectionHopLe)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new YeuCauNoiBoFragment(), TAG_YEU_CAU)
                    .commitNow();
            return;
        }

        if (DieuHuongNoiBoHelper.SECTION_MON.equals(sectionHopLe)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new MonAnQuanTriFragment(), TAG_MON_AN)
                    .commitNow();
            return;
        }

        if (DieuHuongNoiBoHelper.SECTION_BAN.equals(sectionHopLe)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new QuanLyBanQuanTriFragment(), TAG_BAN) // new QuanLyBanQuanTriFragment()
                    .commitNow();
            return;
        }

        if (DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG.equals(sectionHopLe)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new NguoiDungQuanTriFragment(), TAG_NGUOI_DUNG)
                    .commitNow();
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quanTriFragmentContainer, new BaoCaoQuanTriFragment(), TAG_BAO_CAO)
                .commitNow();
    }
}
