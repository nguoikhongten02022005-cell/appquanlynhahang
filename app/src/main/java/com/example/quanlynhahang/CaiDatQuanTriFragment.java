package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.google.android.material.button.MaterialButton;

public class CaiDatQuanTriFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cai_dat_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnCustomerPreview = view.findViewById(R.id.btnQuanTriXemGiaoDienKhach);
        MaterialButton btnLogout = view.findViewById(R.id.btnQuanTriDangXuat);

        btnCustomerPreview.setOnClickListener(v -> moGiaoDienKhachHang());
        btnLogout.setOnClickListener(v -> dangXuat());
    }

    private void moGiaoDienKhachHang() {
        if (!isAdded()) {
            return;
        }

        SessionManager sessionManager = new SessionManager(requireContext());
        String duongDanNoiBo = sessionManager.layDuongDanNoiBoCuoi();
        if (duongDanNoiBo == null || duongDanNoiBo.trim().isEmpty()) {
            duongDanNoiBo = DieuHuongNoiBoHelper.taoRouteQuanTri(DieuHuongNoiBoHelper.SECTION_CAI_DAT);
        }

        Intent intent = DieuHuongNoiBoHelper.taoIntentPreviewKhachHang(requireContext(), duongDanNoiBo);
        startActivity(intent);
    }

    private void dangXuat() {
        if (!isAdded()) {
            return;
        }

        SessionManager sessionManager = new SessionManager(requireContext());
        sessionManager.xoaPhienNoiBo();
        Toast.makeText(requireContext(), R.string.admin_settings_logout_success, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireContext(), StaffLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
