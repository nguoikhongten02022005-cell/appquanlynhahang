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

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_AUTH = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_REGISTERED_EMAIL = "registered_email";
    private static final String KEY_REGISTERED_PASSWORD = "registered_password";

    private EditText etLoginEmail;
    private EditText etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> handleLogin());
        tvGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void handleLogin() {
        String email = getTrimmedText(etLoginEmail);
        String password = getTrimmedText(etLoginPassword);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.login_validation_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidCredential(email, password)) {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_AUTH, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply();

        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean isValidCredential(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_AUTH, MODE_PRIVATE);

        String savedEmail = sharedPreferences.getString(
                KEY_REGISTERED_EMAIL,
                getString(R.string.account_default_email)
        );
        String savedPassword = sharedPreferences.getString(
                KEY_REGISTERED_PASSWORD,
                getString(R.string.account_default_password)
        );

        return email.equalsIgnoreCase(savedEmail) && password.equals(savedPassword);
    }

    private String getTrimmedText(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
