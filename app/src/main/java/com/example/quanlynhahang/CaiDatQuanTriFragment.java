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
import com.example.quanlynhahang.databinding.FragmentCaiDatQuanTriBinding;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;

public class CaiDatQuanTriFragment extends Fragment {

    private FragmentCaiDatQuanTriBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCaiDatQuanTriBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnQuanTriXemGiaoDienKhach.setOnClickListener(v -> moGiaoDienKhachHang());
        binding.btnQuanTriDangXuat.setOnClickListener(v -> dangXuat());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
