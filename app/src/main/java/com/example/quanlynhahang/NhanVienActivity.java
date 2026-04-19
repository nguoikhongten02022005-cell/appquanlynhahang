package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public class NhanVienActivity extends AppCompatActivity {

    public static final String EXTRA_TAB_MUC_TIEU = "extra_target_tab";
    public static final String TAB_DON_HANG = "orders";
    public static final String TAB_DAT_BAN = "reservations";
    public static final String TAB_YEU_CAU = "service_requests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!xacThucPhienNoiBo(sessionManager, databaseHelper)) {
            return;
        }

        Intent intentGoc = getIntent();
        String tabMucTieuCu = intentGoc.getStringExtra(EXTRA_TAB_MUC_TIEU);
        String tabMucTieu = DieuHuongNoiBoHelper.mapTabNhanVienCu(tabMucTieuCu);
        Intent intent = DieuHuongNoiBoHelper.taoIntentTrungTamNoiBo(this, tabMucTieu);
        intent.putExtra(EXTRA_TAB_MUC_TIEU, tabMucTieuCu);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean xacThucPhienNoiBo(SessionManager sessionManager, DatabaseHelper databaseHelper) {
        if (!sessionManager.daDangNhap()) {
            dieuHuongDangNhap();
            return false;
        }

        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            dieuHuongDangNhap();
            return false;
        }

        VaiTroNguoiDung vaiTroSession = sessionManager.layVaiTroSessionHopLe();
        if (vaiTroSession != VaiTroNguoiDung.NHAN_VIEN && vaiTroSession != VaiTroNguoiDung.ADMIN) {
            dieuHuongSaiVaiTro(sessionManager);
            return false;
        }
        return true;
    }

    private void dieuHuongDangNhap() {
        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void dieuHuongSaiVaiTro(SessionManager sessionManager) {
        Intent intent = DieuHuongVaiTroHelper.taoIntentSaiVaiTro(this, sessionManager, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
