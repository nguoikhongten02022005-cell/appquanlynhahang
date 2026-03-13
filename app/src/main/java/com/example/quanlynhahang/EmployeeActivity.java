package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.google.android.material.button.MaterialButton;

public class EmployeeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!sessionManager.isLoggedIn() || !sessionManager.laNhanVien()) {
            Toast.makeText(this, getString(R.string.role_guard_employee_denied), Toast.LENGTH_SHORT).show();
            dieuHuongSaiVaiTro();
            return;
        }

        MaterialButton btnLogout = findViewById(R.id.btnEmployeeLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void dieuHuongSaiVaiTro() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.getVaiTroHienTai());
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
