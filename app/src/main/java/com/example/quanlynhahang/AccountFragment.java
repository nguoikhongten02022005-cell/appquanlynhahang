package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.model.User;
import com.google.android.material.button.MaterialButton;

public class AccountFragment extends Fragment {

    private static final String PREFS_AUTH = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private User currentUser;

    private TextView tvAccountName;
    private TextView tvAccountEmail;
    private TextView tvAccountPhone;

    private View layoutEditProfile;
    private View layoutChangePassword;

    private EditText etEditName;
    private EditText etEditPhone;

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initDefaultUser();
        bindUserData();
        setupActions(view);
    }

    private void initViews(View view) {
        tvAccountName = view.findViewById(R.id.tvAccountName);
        tvAccountEmail = view.findViewById(R.id.tvAccountEmail);
        tvAccountPhone = view.findViewById(R.id.tvAccountPhone);

        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);

        etEditName = view.findViewById(R.id.etEditName);
        etEditPhone = view.findViewById(R.id.etEditPhone);

        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
    }

    private void initDefaultUser() {
        currentUser = new User(
                getString(R.string.account_default_name),
                getString(R.string.account_default_email),
                getString(R.string.account_default_phone),
                getString(R.string.account_default_password)
        );
    }

    private void bindUserData() {
        tvAccountName.setText(currentUser.getName());
        tvAccountEmail.setText(currentUser.getEmail());
        tvAccountPhone.setText(currentUser.getPhone());
    }

    private void setupActions(View view) {
        MaterialButton btnEditProfile = view.findViewById(R.id.btnEditProfile);
        MaterialButton btnSaveProfileChanges = view.findViewById(R.id.btnSaveProfileChanges);
        MaterialButton btnOpenChangePassword = view.findViewById(R.id.btnOpenChangePassword);
        MaterialButton btnSubmitChangePassword = view.findViewById(R.id.btnSubmitChangePassword);
        MaterialButton btnContactSupport = view.findViewById(R.id.btnContactSupport);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        btnEditProfile.setOnClickListener(v -> showEditProfileForm());
        btnSaveProfileChanges.setOnClickListener(v -> saveProfileChanges());
        btnOpenChangePassword.setOnClickListener(v -> showChangePasswordForm());
        btnSubmitChangePassword.setOnClickListener(v -> submitPasswordChange());

        btnContactSupport.setOnClickListener(v -> Toast.makeText(
                requireContext(),
                getString(R.string.account_support_placeholder),
                Toast.LENGTH_SHORT
        ).show());

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(
                    PREFS_AUTH,
                    Context.MODE_PRIVATE
            );
            sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();

            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_logout_placeholder),
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void showEditProfileForm() {
        layoutEditProfile.setVisibility(View.VISIBLE);
        layoutChangePassword.setVisibility(View.GONE);

        etEditName.setText(currentUser.getName());
        etEditPhone.setText(currentUser.getPhone());
    }

    private void saveProfileChanges() {
        String name = getTrimmedText(etEditName);
        String phone = getTrimmedText(etEditPhone);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_profile_validation_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        currentUser.updateProfile(name, phone);
        bindUserData();
        layoutEditProfile.setVisibility(View.GONE);

        Toast.makeText(
                requireContext(),
                getString(R.string.account_profile_update_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showChangePasswordForm() {
        layoutChangePassword.setVisibility(View.VISIBLE);
        layoutEditProfile.setVisibility(View.GONE);
    }

    private void submitPasswordChange() {
        String currentPassword = getTrimmedText(etCurrentPassword);
        String newPassword = getTrimmedText(etNewPassword);
        String confirmPassword = getTrimmedText(etConfirmPassword);

        if (TextUtils.isEmpty(currentPassword)
                || TextUtils.isEmpty(newPassword)
                || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!currentUser.getPassword().equals(currentPassword)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_old_wrong),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_confirm_mismatch),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        currentUser.changePassword(newPassword);
        clearChangePasswordForm();
        layoutChangePassword.setVisibility(View.GONE);

        Toast.makeText(
                requireContext(),
                getString(R.string.account_password_change_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void clearChangePasswordForm() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    private String getTrimmedText(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
