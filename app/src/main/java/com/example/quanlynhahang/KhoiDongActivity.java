package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;

public class KhoiDongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khoi_dong);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);

        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        Intent intentDieuHuong;
        if (sessionManager.daDangNhap()) {
            intentDieuHuong = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.layVaiTroHienTai());
        } else {
            intentDieuHuong = new Intent(this, MainActivity.class);
        }
        intentDieuHuong.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentDieuHuong);
        finish();
    }
}
