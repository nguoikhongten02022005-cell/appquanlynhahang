package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
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
    private static final String TAG_CAI_DAT = "cai_dat_quan_tri";

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

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
                || DieuHuongNoiBoHelper.SECTION_BAO_CAO.equals(sectionDaRutGon)
                || DieuHuongNoiBoHelper.SECTION_CAI_DAT.equals(sectionDaRutGon)) {
            return sectionDaRutGon;
        }
        return DieuHuongNoiBoHelper.SECTION_BAO_CAO;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trung_tam_quan_tri);

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
        ganDieuHuongMenu(R.id.navAdminOverview, DieuHuongNoiBoHelper.SECTION_BAO_CAO);
        ganDieuHuongMenu(R.id.navAdminOrders, DieuHuongNoiBoHelper.SECTION_DON_HANG);
        ganDieuHuongMenu(R.id.navAdminTables, DieuHuongNoiBoHelper.SECTION_BAN);
        ganDieuHuongMenu(R.id.navAdminDishes, DieuHuongNoiBoHelper.SECTION_MON);
        ganDieuHuongMenu(R.id.navAdminAccounts, DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG);
    }

    private void ganDieuHuongMenu(int viewId, String section) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> moSection(section));
        }
    }

    private void capNhatTrangThaiMenu(String section) {
        capNhatTrangThaiBottomNav(
                R.id.navAdminOverview,
                R.id.iconAdminOverview,
                R.id.tvAdminOverviewLabel,
                DieuHuongNoiBoHelper.SECTION_BAO_CAO.equals(section)
        );
        capNhatTrangThaiBottomNav(
                R.id.navAdminOrders,
                R.id.iconAdminOrders,
                R.id.tvAdminOrdersLabel,
                DieuHuongNoiBoHelper.SECTION_DON_HANG.equals(section)
        );
        capNhatTrangThaiBottomNav(
                R.id.navAdminTables,
                R.id.iconAdminTables,
                R.id.tvAdminTablesLabel,
                DieuHuongNoiBoHelper.SECTION_BAN.equals(section)
        );
        capNhatTrangThaiBottomNav(
                R.id.navAdminDishes,
                R.id.iconAdminDishes,
                R.id.tvAdminDishesLabel,
                DieuHuongNoiBoHelper.SECTION_MON.equals(section)
        );
        capNhatTrangThaiBottomNav(
                R.id.navAdminAccounts,
                R.id.iconAdminAccounts,
                R.id.tvAdminAccountsLabel,
                DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG.equals(section)
        );
    }

    private void capNhatTrangThaiBottomNav(int itemId, int iconId, int labelId, boolean dangDuocChon) {
        LinearLayout item = findViewById(itemId);
        ImageView icon = findViewById(iconId);
        TextView label = findViewById(labelId);
        if (item == null || icon == null || label == null) {
            return;
        }
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
        if (sessionManager.layVaiTroSessionHopLe() != VaiTroNguoiDung.ADMIN) {
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

    public void dieuHuongDenSection(String section) {
        moSection(section);
    }

    private void moSection(String section) {
        String sectionHopLe = DieuHuongNoiBoHelper.chuanHoaSection(section);
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

        if (DieuHuongNoiBoHelper.SECTION_CAI_DAT.equals(sectionHopLe)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new CaiDatQuanTriFragment(), TAG_CAI_DAT)
                    .commitNow();
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quanTriFragmentContainer, new BaoCaoQuanTriFragment(), TAG_BAO_CAO)
                .commitNow();
    }
}
