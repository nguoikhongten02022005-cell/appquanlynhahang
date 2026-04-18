package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.google.android.material.button.MaterialButton;

public class ChonVaiTroNoiBoActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_vai_tro_noi_bo);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        MaterialButton btnNhanVien = findViewById(R.id.btnRoleEmployee);
        MaterialButton btnQuanLy = findViewById(R.id.btnRoleManager);

        btnNhanVien.setOnClickListener(v -> moManNoiBo(VaiTroNguoiDung.NHAN_VIEN));
        btnQuanLy.setOnClickListener(v -> moManNoiBo(VaiTroNguoiDung.ADMIN));
    }

    private void moManNoiBo(VaiTroNguoiDung vaiTro) {
        sessionManager.luuVaiTroNoiBo(vaiTro);

        if (sessionManager.daDangNhap() && sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            VaiTroNguoiDung vaiTroDangNhap = sessionManager.layVaiTroSessionHopLe();
            if (vaiTroDangNhap == VaiTroNguoiDung.ADMIN
                    || (vaiTroDangNhap == VaiTroNguoiDung.NHAN_VIEN && vaiTro == VaiTroNguoiDung.NHAN_VIEN)) {
                Intent intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, vaiTro);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            }
            Toast.makeText(this, getString(vaiTro == VaiTroNguoiDung.ADMIN
                    ? R.string.role_guard_admin_denied
                    : R.string.role_guard_employee_denied), Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
