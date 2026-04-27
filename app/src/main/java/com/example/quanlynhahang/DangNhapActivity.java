package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.ActivityDangNhapBinding;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import java.util.Arrays;
import java.util.List;

public class DangNhapActivity extends AppCompatActivity {

    public static final String EXTRA_RETURN_TO_CALLER = "extra_return_to_caller";
    public static final String EXTRA_ONLY_CUSTOMER_SESSION = "extra_only_customer_session";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private ActivityDangNhapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDangNhapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);

        boolean chiChoPhepPhienKhachHang = getIntent().getBooleanExtra(EXTRA_ONLY_CUSTOMER_SESSION, false);

        binding.btnLogin.setOnClickListener(v -> xuLyDangNhap(chiChoPhepPhienKhachHang));
        binding.tvGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, DangKyActivity.class)));
        napGoiYDangNhapNhanh(chiChoPhepPhienKhachHang);
    }

    private void napGoiYDangNhapNhanh(boolean chiChoPhepPhienKhachHang) {
        List<VaiTroNguoiDung> thuTuVaiTro = chiChoPhepPhienKhachHang
                ? Arrays.asList(VaiTroNguoiDung.KHACH_HANG)
                : Arrays.asList(VaiTroNguoiDung.KHACH_HANG, VaiTroNguoiDung.NHAN_VIEN, VaiTroNguoiDung.ADMIN);

        for (VaiTroNguoiDung vaiTro : thuTuVaiTro) {
            DatabaseHelper.GoiYDangNhapNhanh goiY = databaseHelper.layGoiYDangNhapNhanhTheoVaiTro(vaiTro);
            if (goiY != null) {
                binding.etLoginEmail.setText(goiY.email);
                binding.etLoginPassword.setText(goiY.matKhau);
                return;
            }
        }
    }

    private void xuLyDangNhap(boolean chiChoPhepPhienKhachHang) {
        String emailHoacSoDienThoai = layTextDaCatKhoangTrang(binding.etLoginEmail);
        String matKhau = layTextDaCatKhoangTrang(binding.etLoginPassword);

        if (TextUtils.isEmpty(emailHoacSoDienThoai) || TextUtils.isEmpty(matKhau)) {
            Toast.makeText(this, getString(R.string.login_validation_required), Toast.LENGTH_SHORT).show();
            return;
        }

        NguoiDung nguoiDungDaXacThuc = databaseHelper.kiemTraDangNhap(emailHoacSoDienThoai, matKhau);
        if (nguoiDungDaXacThuc == null || !nguoiDungDaXacThuc.dangHoatDong()) {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        if (chiChoPhepPhienKhachHang && nguoiDungDaXacThuc.layVaiTro() != VaiTroNguoiDung.KHACH_HANG) {
            Toast.makeText(this, getString(R.string.login_customer_only_blocked), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
