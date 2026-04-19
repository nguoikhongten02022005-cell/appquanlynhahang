package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.google.android.material.button.MaterialButton;

public class DangKyActivity extends AppCompatActivity {

    private static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private EditText etRegisterFullName;
    private EditText etRegisterEmail;
    private EditText etRegisterPhone;
    private EditText etRegisterPassword;
    private EditText etRegisterConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);

        etRegisterFullName = findViewById(R.id.etRegisterFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPhone = findViewById(R.id.etRegisterPhone);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);

        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> xuLyDangKy());
        tvGoToLogin.setOnClickListener(v -> dieuHuongDenDangNhap());
    }

    private void xuLyDangKy() {
        String hoTen = layTextDaCatKhoangTrang(etRegisterFullName);
        String email = layTextDaCatKhoangTrang(etRegisterEmail);
        String soDienThoai = layTextDaCatKhoangTrang(etRegisterPhone);
        String matKhau = layTextDaCatKhoangTrang(etRegisterPassword);
        String xacNhanMatKhau = layTextDaCatKhoangTrang(etRegisterConfirmPassword);

        if (TextUtils.isEmpty(hoTen)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(soDienThoai)
                || TextUtils.isEmpty(matKhau)
                || TextUtils.isEmpty(xacNhanMatKhau)) {
            Toast.makeText(this, getString(R.string.register_validation_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.validation_email_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!laSoDienThoaiHopLe(soDienThoai)) {
            Toast.makeText(this, getString(R.string.validation_phone_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        if (matKhau.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            Toast.makeText(this, getString(R.string.validation_password_too_short, DO_DAI_MAT_KHAU_TOI_THIEU), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            Toast.makeText(this, getString(R.string.register_password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.layNguoiDungTheoSoDienThoai(soDienThoai) != null) {
            Toast.makeText(this, getString(R.string.register_phone_exists), Toast.LENGTH_SHORT).show();
            return;
        }

        long idNguoiDungMoi = databaseHelper.insertUser(hoTen, email, soDienThoai, matKhau, VaiTroNguoiDung.KHACH_HANG, true);
        if (idNguoiDungMoi <= 0) {
            Toast.makeText(this, getString(R.string.register_failed_generic), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
        dieuHuongDenDangNhap();
    }

    private void dieuHuongDenDangNhap() {
        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private boolean laSoDienThoaiHopLe(String phone) {
        return !TextUtils.isEmpty(phone)
                && phone.length() == 10
                && phone.startsWith("0")
                && TextUtils.isDigitsOnly(phone);
    }

    private String layTextDaCatKhoangTrang(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
