package com.example.quanlynhahang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREFS_AUTH = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_REGISTERED_EMAIL = "registered_email";
    private static final String KEY_REGISTERED_PASSWORD = "registered_password";

    private EditText etRegisterFullName;
    private EditText etRegisterEmail;
    private EditText etRegisterPhone;
    private EditText etRegisterPassword;
    private EditText etRegisterConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.register_password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_AUTH, MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(KEY_REGISTERED_EMAIL, email)
                .putString(KEY_REGISTERED_PASSWORD, password)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();

        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private String getTrimmedText(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
