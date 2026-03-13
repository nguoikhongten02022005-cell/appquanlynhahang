package com.example.quanlynhahang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountFragment extends Fragment {

    private static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;
    private static final int DO_DAI_SO_DIEN_THOAI = 10;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private User currentUser;

    private View layoutAccountLoggedIn;

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

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                updateAuthStateUi();
                refreshHeaderState();
                if (!isAdded() || sessionManager == null || sessionManager.isLoggedIn()) {
                    return;
                }
                navigateToHomeTab();
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupActions(view);
        updateAuthStateUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAuthStateUi();
    }

    private void initViews(View view) {
        layoutAccountLoggedIn = view.findViewById(R.id.layoutAccountLoggedIn);

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

    private void setupActions(View view) {
        MaterialButton btnEditProfile = view.findViewById(R.id.btnEditProfile);
        MaterialButton btnSaveProfileChanges = view.findViewById(R.id.btnSaveProfileChanges);
        MaterialButton btnCancelEditProfile = view.findViewById(R.id.btnCancelEditProfile);
        MaterialButton btnOpenChangePassword = view.findViewById(R.id.btnOpenChangePassword);
        MaterialButton btnSubmitChangePassword = view.findViewById(R.id.btnSubmitChangePassword);
        MaterialButton btnCancelChangePassword = view.findViewById(R.id.btnCancelChangePassword);
        MaterialButton btnContactSupport = view.findViewById(R.id.btnContactSupport);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        btnEditProfile.setOnClickListener(v -> showEditProfileForm());
        btnSaveProfileChanges.setOnClickListener(v -> saveProfileChanges());
        btnCancelEditProfile.setOnClickListener(v -> hideEditProfileForm());
        btnOpenChangePassword.setOnClickListener(v -> showChangePasswordForm());
        btnSubmitChangePassword.setOnClickListener(v -> submitPasswordChange());
        btnCancelChangePassword.setOnClickListener(v -> hideChangePasswordForm());

        btnContactSupport.setOnClickListener(v -> openSupportChannel());

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showEditProfileForm() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        layoutEditProfile.setVisibility(View.VISIBLE);
        hideChangePasswordForm();

        etEditName.setText(currentUser.getName());
        etEditPhone.setText(currentUser.getPhone());
    }

    private void hideEditProfileForm() {
        layoutEditProfile.setVisibility(View.GONE);
        clearEditProfileForm();
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

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

        if (!laSoDienThoaiHopLe(phone)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.validation_phone_invalid),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        boolean isUpdated = databaseHelper.updateUserProfile(currentUser.getId(), name, phone);
        if (!isUpdated) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        User refreshedUser = databaseHelper.getUserById(currentUser.getId());
        if (refreshedUser == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            sessionManager.clearSession();
            updateAuthStateUi();
            refreshHeaderState();
            return;
        }

        currentUser = refreshedUser;
        bindUserData(currentUser);
        hideEditProfileForm();
        refreshHeaderState();

        Toast.makeText(
                requireContext(),
                getString(R.string.account_profile_update_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showChangePasswordForm() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }
        layoutChangePassword.setVisibility(View.VISIBLE);
        hideEditProfileForm();
    }

    private void hideChangePasswordForm() {
        layoutChangePassword.setVisibility(View.GONE);
        clearChangePasswordForm();
    }

    private void submitPasswordChange() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

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

        User matchedUser = databaseHelper.checkLogin(currentUser.getEmail(), currentPassword);
        if (matchedUser == null) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_old_wrong),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (TextUtils.equals(currentPassword, newPassword)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.account_password_validation_same_as_old),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (newPassword.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.validation_password_too_short, DO_DAI_MAT_KHAU_TOI_THIEU),
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

        boolean isUpdated = databaseHelper.updateUserPassword(currentUser.getId(), newPassword);
        if (!isUpdated) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        hideChangePasswordForm();
        refreshHeaderState();

        Toast.makeText(
                requireContext(),
                getString(R.string.account_password_change_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void openSupportChannel() {
        String phoneNumber = getString(R.string.account_support_phone_number_plain);
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (phoneIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(phoneIntent);
            return;
        }

        Toast.makeText(
                requireContext(),
                getString(R.string.account_support_fallback, getString(R.string.account_support_phone_number_display)),
                Toast.LENGTH_LONG
        ).show();
    }

    private void clearEditProfileForm() {
        etEditName.setText("");
        etEditPhone.setText("");
    }

    private void clearChangePasswordForm() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    public void onAccountTabSelected() {
        updateAuthStateUi();
        refreshHeaderState();
        if (sessionManager == null || sessionManager.isLoggedIn()) {
            return;
        }
        launchLogin();
    }

    private void launchLogin() {
        if (!isAdded()) {
            return;
        }
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_RETURN_TO_CALLER, true);
        loginLauncher.launch(intent);
    }

    public void updateAuthStateUi() {
        if (!isAdded() || layoutAccountLoggedIn == null) {
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            clearLoggedOutUi();
            return;
        }

        long currentUserId = sessionManager.getCurrentUserId();
        if (currentUserId <= 0) {
            sessionManager.clearSession();
            clearLoggedOutUi();
            Toast.makeText(requireContext(), getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        User user = databaseHelper.getUserById(currentUserId);
        if (user == null) {
            sessionManager.clearSession();
            clearLoggedOutUi();
            Toast.makeText(requireContext(), getString(R.string.account_user_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser = user;
        layoutAccountLoggedIn.setVisibility(View.VISIBLE);
        bindUserData(currentUser);
    }

    private void clearLoggedOutUi() {
        currentUser = null;
        layoutAccountLoggedIn.setVisibility(View.GONE);
        layoutEditProfile.setVisibility(View.GONE);
        layoutChangePassword.setVisibility(View.GONE);
        clearEditProfileForm();
        clearChangePasswordForm();
    }

    private void navigateToHomeTab() {
        if (!(requireActivity() instanceof MainActivity)) {
            return;
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void bindUserData(User user) {
        tvAccountName.setText(user.getName());
        tvAccountEmail.setText(user.getEmail());
        tvAccountPhone.setText(user.getPhone());
    }

    private void refreshHeaderState() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).refreshHeaderState();
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.account_logout_confirm_title)
                .setMessage(R.string.account_logout_confirm_message)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.account_logout, (dialog, which) -> performLogout())
                .show();
    }

    private void performLogout() {
        sessionManager.clearSession();
        clearLoggedOutUi();
        refreshHeaderState();
        navigateToHomeTab();

        Toast.makeText(
                requireContext(),
                getString(R.string.account_logout_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean laSoDienThoaiHopLe(String phone) {
        return !TextUtils.isEmpty(phone)
                && phone.length() == DO_DAI_SO_DIEN_THOAI
                && phone.startsWith("0")
                && TextUtils.isDigitsOnly(phone);
    }

    private String getTrimmedText(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
