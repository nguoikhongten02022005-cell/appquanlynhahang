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
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        etRegisterFullName = findViewById(R.id.etRegisterFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPhone = findViewById(R.id.etRegisterPhone);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);

        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> handleRegister());
        tvGoToLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void handleRegister() {
        String fullName = getTrimmedText(etRegisterFullName);
        String email = getTrimmedText(etRegisterEmail);
        String phone = getTrimmedText(etRegisterPhone);
        String password = getTrimmedText(etRegisterPassword);
        String confirmPassword = getTrimmedText(etRegisterConfirmPassword);

        if (TextUtils.isEmpty(fullName)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, getString(R.string.register_validation_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.validation_email_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!laSoDienThoaiHopLe(phone)) {
            Toast.makeText(this, getString(R.string.validation_phone_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            Toast.makeText(this, getString(R.string.validation_password_too_short, DO_DAI_MAT_KHAU_TOI_THIEU), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.register_password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        long newUserId = databaseHelper.insertUser(fullName, email, phone, password);
        if (newUserId <= 0) {
            Toast.makeText(this, getString(R.string.register_email_exists), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private boolean laSoDienThoaiHopLe(String phone) {
        return phone.matches("0\\d{9,10}");
    }

    private String getTrimmedText(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
