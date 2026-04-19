package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public class TrungTamQuanTriActivity extends AppCompatActivity {

    private static final String TAG_MON_AN = "mon_an_quan_tri";
    private static final String TAG_NGUOI_DUNG = "nguoi_dung_quan_tri";
    private static final String TAG_BAO_CAO = "bao_cao_quan_tri";
    private static final String TAG_CAI_DAT = "cai_dat_quan_tri";

    private MaterialToolbar toolbarQuanTri;
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

        toolbarQuanTri = findViewById(R.id.toolbarQuanTri);
        setSupportActionBar(toolbarQuanTri);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();

        if (!xacThucPhienQuanTri()) {
            return;
        }

        String sectionDuocYeuCau = getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI);
        String sectionDuocHienThi = chonSectionQuanTriHienThi(sectionDuocYeuCau);
        getIntent().putExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI, sectionDuocHienThi);
        capNhatTieuDe(sectionDuocHienThi);

        if (savedInstanceState == null) {
            moSection(sectionDuocHienThi);
        }
    }

    private void capNhatTieuDe(String section) {
        if (toolbarQuanTri == null) {
            return;
        }
        if (DieuHuongNoiBoHelper.SECTION_MON.equals(section)) {
            toolbarQuanTri.setTitle(R.string.admin_dishes_title);
            return;
        }
        if (DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG.equals(section)) {
            toolbarQuanTri.setTitle(R.string.admin_users_title);
            return;
        }
        if (DieuHuongNoiBoHelper.SECTION_CAI_DAT.equals(section)) {
            toolbarQuanTri.setTitle(R.string.admin_settings_title);
            return;
        }
        toolbarQuanTri.setTitle(R.string.admin_reports_title);
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

    private void moSection(String section) {
        String sectionHopLe = DieuHuongNoiBoHelper.chuanHoaSection(section);
        sessionManager.luuDuongDanNoiBoCuoi(DieuHuongNoiBoHelper.taoRouteQuanTri(sectionHopLe));

        if (DieuHuongNoiBoHelper.SECTION_MON.equals(sectionHopLe)) {
            capNhatTieuDe(sectionHopLe);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new MonAnQuanTriFragment(), TAG_MON_AN)
                    .commitNow();
            return;
        }

        if (DieuHuongNoiBoHelper.SECTION_NGUOI_DUNG.equals(sectionHopLe)) {
            capNhatTieuDe(sectionHopLe);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new NguoiDungQuanTriFragment(), TAG_NGUOI_DUNG)
                    .commitNow();
            return;
        }

        if (DieuHuongNoiBoHelper.SECTION_CAI_DAT.equals(sectionHopLe)) {
            capNhatTieuDe(sectionHopLe);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quanTriFragmentContainer, new CaiDatQuanTriFragment(), TAG_CAI_DAT)
                    .commitNow();
            return;
        }

        capNhatTieuDe(sectionHopLe);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quanTriFragmentContainer, new BaoCaoQuanTriFragment(), TAG_BAO_CAO)
                .commitNow();
    }
}
