package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quanlynhahang.adapter.DonHangAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.FragmentDonHangBinding;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonHangFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private FragmentDonHangBinding binding;
    private DonHangAdapter orderAdapter;

    private boolean daGoiMoDangNhap;
    private boolean embedded;

    private final ActivityResultLauncher<Intent> boMoDangNhap = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                daGoiMoDangNhap = false;
                capNhatGiaoDienDonHang(false);
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDonHangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        apDungCheDoNhung();

        binding.btnCheckout.setVisibility(embedded ? View.GONE : View.VISIBLE);
        binding.btnCheckout.setOnClickListener(v -> moThucDon());

        thietLapRecyclerView();
        capNhatGiaoDienDonHang(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        capNhatGiaoDienDonHang(false);
    }

    private void apDungCheDoNhung() {
        if (!embedded || binding == null) {
            return;
        }

        binding.tvDonHangTitle.setVisibility(View.GONE);
        binding.tvDonHangCaption.setVisibility(View.GONE);

        int paddingNgang = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_horizontal);
        int paddingDoc = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_vertical);
        binding.getRoot().setPadding(paddingNgang, paddingDoc, paddingNgang, paddingDoc);
    }

    private void thietLapRecyclerView() {
        binding.rvDonHangs.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new DonHangAdapter(new ArrayList<>(), (donHang, viTri) -> huyDonHang(donHang));
        binding.rvDonHangs.setAdapter(orderAdapter);
    }

    private void capNhatGiaoDienDonHang(boolean tuDongMoDangNhap) {
        if (!isAdded() || binding == null || sessionManager == null || databaseHelper == null || orderAdapter == null) {
            return;
        }

        if (!sessionManager.daDangNhap()) {
            hienTrangThaiCanDangNhap();
            if (!embedded && tuDongMoDangNhap && !daGoiMoDangNhap) {
                daGoiMoDangNhap = true;
                moDangNhap();
            }
            return;
        }

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDung <= 0) {
            sessionManager.xoaPhienDangNhap();
            hienTrangThaiCanDangNhap();
            if (!embedded && tuDongMoDangNhap && !daGoiMoDangNhap) {
                daGoiMoDangNhap = true;
                moDangNhap();
            }
            return;
        }

        daGoiMoDangNhap = false;

        List<DonHang> danhSachDon = databaseHelper.layDonHangTheoNguoiDung(idNguoiDung);
        orderAdapter.capNhatDuLieu(danhSachDon);

        if (danhSachDon.isEmpty()) {
            hienTrangThaiRong(getString(R.string.order_empty_message));
            return;
        }

        hienTrangThaiDanhSach();
    }

    private void huyDonHang(DonHang donHang) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.order_cancel_confirm_title)
                .setMessage(R.string.order_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.order_cancel, (dialog, which) -> thucHienHuyDon(donHang))
                .show();
    }

    private void thucHienHuyDon(DonHang donHang) {
        boolean daHuy = databaseHelper.huyDonHang(donHang.layId());
        if (!daHuy) {
            Toast.makeText(requireContext(), getString(R.string.db_operation_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        capNhatGiaoDienDonHang(false);

        Toast.makeText(
                requireContext(),
                getString(R.string.order_cancel_success, donHang.layMaDon()),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void hienTrangThaiCanDangNhap() {
        orderAdapter.capNhatDuLieu(Collections.emptyList());
        hienTrangThaiRong(getString(R.string.order_login_required));
    }

    private void hienTrangThaiRong(String thongBao) {
        if (binding == null) {
            return;
        }
        binding.tvCartEmpty.setText(thongBao);
        binding.tvDonHangEmptyTitle.setText(R.string.order_empty_title);
        binding.layoutOrderEmptyState.setVisibility(View.VISIBLE);
        binding.tvCartEmpty.setVisibility(View.VISIBLE);
        binding.rvDonHangs.setVisibility(View.GONE);
        binding.btnCheckout.setVisibility(View.VISIBLE);
        binding.tvDonHangCaption.setVisibility(View.GONE);
    }

    private void hienTrangThaiDanhSach() {
        if (binding == null) {
            return;
        }
        binding.layoutOrderEmptyState.setVisibility(View.GONE);
        binding.tvCartEmpty.setVisibility(View.GONE);
        binding.rvDonHangs.setVisibility(View.VISIBLE);
        binding.btnCheckout.setVisibility(View.GONE);
        binding.tvDonHangCaption.setVisibility(embedded ? View.GONE : View.VISIBLE);
        binding.tvDonHangCaption.setText(getString(R.string.order_screen_caption));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void moThucDon() {
        if (!isAdded()) {
            return;
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).dieuHuongDenMenu();
        }
    }

    private void moDangNhap() {
        if (!isAdded()) {
            return;
        }

        Intent intent = new Intent(requireContext(), DangNhapActivity.class);
        intent.putExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true);
        boMoDangNhap.launch(intent);
    }
}
