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
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.google.android.material.button.MaterialButton;

public class DangNhapActivity extends AppCompatActivity {

    public static final String EXTRA_RETURN_TO_CALLER = "extra_return_to_caller";
    private static final String TAI_KHOAN_KHACH_HANG_MAC_DINH = "kh1";
    private static final String TAI_KHOAN_NHAN_VIEN_MAC_DINH = "nv1";
    private static final String TAI_KHOAN_ADMIN_MAC_DINH = "admin1";
    private static final String MAT_KHAU_MAC_DINH = "1";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private EditText oNhapEmailDangNhap;
    private EditText oNhapMatKhauDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);

        oNhapEmailDangNhap = findViewById(R.id.etLoginEmail);
        oNhapMatKhauDangNhap = findViewById(R.id.etLoginPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton nutDangNhapNhanhKhachHang = findViewById(R.id.btnQuickLoginCustomer);
        MaterialButton nutDangNhapNhanhNhanVien = findViewById(R.id.btnQuickLoginEmployee);
        MaterialButton nutDangNhapNhanhQuanTri = findViewById(R.id.btnQuickLoginAdmin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> xuLyDangNhap());
        nutDangNhapNhanhKhachHang.setOnClickListener(v -> dangNhapMacDinh(TAI_KHOAN_KHACH_HANG_MAC_DINH));
        nutDangNhapNhanhNhanVien.setOnClickListener(v -> dangNhapMacDinh(TAI_KHOAN_NHAN_VIEN_MAC_DINH));
        nutDangNhapNhanhQuanTri.setOnClickListener(v -> dangNhapMacDinh(TAI_KHOAN_ADMIN_MAC_DINH));
        tvGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, DangKyActivity.class)));
    }

    private void dangNhapMacDinh(String taiKhoanMacDinh) {
        oNhapEmailDangNhap.setText(taiKhoanMacDinh);
        oNhapMatKhauDangNhap.setText(MAT_KHAU_MAC_DINH);
        xuLyDangNhap();
    }

    private void xuLyDangNhap() {
        String emailHoacSoDienThoai = layTextDaCatKhoangTrang(oNhapEmailDangNhap);
        String matKhau = layTextDaCatKhoangTrang(oNhapMatKhauDangNhap);

        if (TextUtils.isEmpty(emailHoacSoDienThoai) || TextUtils.isEmpty(matKhau)) {
            Toast.makeText(this, getString(R.string.login_validation_required), Toast.LENGTH_SHORT).show();
            return;
        }

        NguoiDung nguoiDungDaXacThuc = databaseHelper.kiemTraDangNhap(emailHoacSoDienThoai, matKhau);
        if (nguoiDungDaXacThuc == null || !nguoiDungDaXacThuc.dangHoatDong()) {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.luuPhienDangNhap(nguoiDungDaXacThuc.layId(), nguoiDungDaXacThuc.layVaiTro());

        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

        boolean quayVeCaller = getIntent().getBooleanExtra(EXTRA_RETURN_TO_CALLER, false);
        VaiTroNguoiDung vaiTroDangNhap = nguoiDungDaXacThuc.layVaiTro();
        if (quayVeCaller && vaiTroDangNhap == VaiTroNguoiDung.KHACH_HANG) {
            setResult(RESULT_OK);
            finish();
            return;
        }

        Intent intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, nguoiDungDaXacThuc.layVaiTro());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String layTextDaCatKhoangTrang(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
