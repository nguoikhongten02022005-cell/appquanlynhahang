package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.google.android.material.button.MaterialButton;

public class ChonVaiTroNoiBoActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_vai_tro_noi_bo);

        sessionManager = new SessionManager(this);

        MaterialButton btnNhanVien = findViewById(R.id.btnRoleEmployee);
        MaterialButton btnQuanLy = findViewById(R.id.btnRoleManager);

        btnNhanVien.setOnClickListener(v -> moManNoiBo(VaiTroNguoiDung.NHAN_VIEN));
        btnQuanLy.setOnClickListener(v -> moManNoiBo(VaiTroNguoiDung.ADMIN));
    }

    private void moManNoiBo(VaiTroNguoiDung vaiTro) {
        sessionManager.luuVaiTroNoiBo(vaiTro);
        Intent intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, vaiTro);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
