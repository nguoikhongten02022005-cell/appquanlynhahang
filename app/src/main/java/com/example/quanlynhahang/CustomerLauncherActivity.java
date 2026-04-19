package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

public class CustomerLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        Intent intent;
        if (sessionManager.daDangNhap() && sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            VaiTroNguoiDung vaiTroDangNhap = sessionManager.layVaiTroSessionHopLe();
            if (vaiTroDangNhap == VaiTroNguoiDung.NHAN_VIEN || vaiTroDangNhap == VaiTroNguoiDung.ADMIN) {
                intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, vaiTroDangNhap);
            } else {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true);
            }
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_CHO_PHEP_XEM_GIAO_DIEN_KHACH, true);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
