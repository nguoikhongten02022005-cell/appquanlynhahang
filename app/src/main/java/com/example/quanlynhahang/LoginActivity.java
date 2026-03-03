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
import com.example.quanlynhahang.model.User;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_RETURN_TO_CALLER = "extra_return_to_caller";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private EditText etLoginEmail;
    private EditText etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

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

        User authenticatedUser = databaseHelper.checkLogin(email, password);
        if (authenticatedUser == null) {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.saveLoginSession(authenticatedUser.getId());

        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

        if (getIntent().getBooleanExtra(EXTRA_RETURN_TO_CALLER, false)) {
            setResult(RESULT_OK);
            finish();
            return;
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String getTrimmedText(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
