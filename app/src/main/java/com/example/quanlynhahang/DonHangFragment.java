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
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.DonHangAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonHangFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private RecyclerView rvDonHangs;
    private TextView tvDonHangEmpty;
    private TextView tvDonHangCaption;
    private TextView tvDonHangEmptyTitle;
    private View btnCheckout;
    private DonHangAdapter orderAdapter;
    private View layoutOrderEmptyState;
    private View layoutCartFooter;
    private View titleView;

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
        return inflater.inflate(R.layout.fragment_don_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        rvDonHangs = view.findViewById(R.id.rvDonHangs);
        tvDonHangEmpty = view.findViewById(R.id.tvCartEmpty);
        tvDonHangCaption = view.findViewById(R.id.tvDonHangCaption);
        tvDonHangEmptyTitle = view.findViewById(R.id.tvDonHangEmptyTitle);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        layoutOrderEmptyState = view.findViewById(R.id.layoutOrderEmptyState);
        titleView = view.findViewById(R.id.tvDonHangTitle);
        layoutCartFooter = view.findViewById(R.id.layoutCartFooter);

        apDungCheDoNhung(view);

        if (layoutCartFooter != null) {
            layoutCartFooter.setVisibility(embedded ? View.GONE : View.VISIBLE);
        }

        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> moThucDon());
        }

        thietLapRecyclerView();
        capNhatGiaoDienDonHang(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        capNhatGiaoDienDonHang(false);
    }

    private void apDungCheDoNhung(@NonNull View view) {
        if (!embedded) {
            return;
        }

        if (titleView != null) {
            titleView.setVisibility(View.GONE);
        }
        if (tvDonHangCaption != null) {
            tvDonHangCaption.setVisibility(View.GONE);
        }

        int paddingNgang = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_horizontal);
        int paddingDoc = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_vertical);
        view.setPadding(paddingNgang, paddingDoc, paddingNgang, paddingDoc);
    }

    private void thietLapRecyclerView() {
        rvDonHangs.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new DonHangAdapter(new ArrayList<>(), this::huyDonHang);
        rvDonHangs.setAdapter(orderAdapter);
    }

    private void capNhatGiaoDienDonHang(boolean tuDongMoDangNhap) {
        if (!isAdded()) {
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

    private void huyDonHang(DonHang donHang, int viTri) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.order_cancel_confirm_title)
                .setMessage(R.string.order_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.order_cancel, (dialog, which) -> thucHienHuyDon(donHang, viTri))
                .show();
    }

    private void thucHienHuyDon(DonHang donHang, int viTri) {
        boolean daHuy = databaseHelper.huyDonHang(donHang.layId());
        if (!daHuy) {
            Toast.makeText(requireContext(), getString(R.string.db_operation_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        donHang.huyDon();
        orderAdapter.notifyItemChanged(viTri);

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
        tvDonHangEmpty.setText(thongBao);
        if (tvDonHangEmptyTitle != null) {
            tvDonHangEmptyTitle.setText(R.string.order_empty_title);
        }
        if (layoutOrderEmptyState != null) {
            layoutOrderEmptyState.setVisibility(View.VISIBLE);
        }
        tvDonHangEmpty.setVisibility(View.VISIBLE);
        rvDonHangs.setVisibility(View.GONE);
        if (btnCheckout != null) {
            btnCheckout.setVisibility(View.VISIBLE);
        }
        if (tvDonHangCaption != null) {
            tvDonHangCaption.setVisibility(View.GONE);
        }
    }

    private void hienTrangThaiDanhSach() {
        if (layoutOrderEmptyState != null) {
            layoutOrderEmptyState.setVisibility(View.GONE);
        }
        tvDonHangEmpty.setVisibility(View.GONE);
        rvDonHangs.setVisibility(View.VISIBLE);
        if (btnCheckout != null) {
            btnCheckout.setVisibility(View.GONE);
        }
        if (tvDonHangCaption != null) {
            tvDonHangCaption.setVisibility(embedded ? View.GONE : View.VISIBLE);
            tvDonHangCaption.setText(getString(R.string.order_screen_caption));
        }
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
