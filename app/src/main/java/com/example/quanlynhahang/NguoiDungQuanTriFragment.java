package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.BoDieuHopNguoiDungQuanTri;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.VaiTroNguoiDung;

import java.util.List;

public class NguoiDungQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private BoDieuHopNguoiDungQuanTri boDieuHopNguoiDung;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nguoi_dung_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager = new SessionManager(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.rvNguoiDungQuanTri);
        tvEmptyState = view.findViewById(R.id.tvNguoiDungQuanTriEmpty);

        boDieuHopNguoiDung = new BoDieuHopNguoiDungQuanTri(new BoDieuHopNguoiDungQuanTri.HanhDongListener() {
            @Override
            public void khiSua(NguoiDung nguoiDung) {
                hienDialogDoiVaiTro(nguoiDung);
            }

            @Override
            public void khiBatTatTrangThaiHoatDong(NguoiDung nguoiDung) {
                xuLyBatTatTrangThai(nguoiDung);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(boDieuHopNguoiDung);

        taiDanhSachNguoiDung();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null && boDieuHopNguoiDung != null) {
            taiDanhSachNguoiDung();
        }
    }

    private void taiDanhSachNguoiDung() {
        List<NguoiDung> danhSachNguoiDung = layDanhSachNguoiDungHienThi();
        boDieuHopNguoiDung.capNhatDanhSach(danhSachNguoiDung);
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(danhSachNguoiDung.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private List<NguoiDung> layDanhSachNguoiDungHienThi() {
        return databaseHelper.layTatCaNguoiDung();
    }

    private void hienDialogDoiVaiTro(NguoiDung nguoiDung) {
        if (!isAdded()) {
            return;
        }

        final VaiTroNguoiDung[] luaChonVaiTro = new VaiTroNguoiDung[]{
                VaiTroNguoiDung.KHACH_HANG,
                VaiTroNguoiDung.NHAN_VIEN,
                VaiTroNguoiDung.ADMIN
        };
        final String[] tenLuaChon = new String[]{
                getString(R.string.admin_role_customer),
                getString(R.string.admin_role_employee),
                getString(R.string.admin_role_admin)
        };
        final int[] viTriDangChon = new int[]{nguoiDung.laAdmin() ? 2 : nguoiDung.laNhanVien() ? 1 : 0};

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_change_role_title)
                .setMessage(R.string.admin_change_role_message)
                .setSingleChoiceItems(tenLuaChon, viTriDangChon[0], (dialog, which) -> viTriDangChon[0] = which)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_change_role, (dialog, which) -> capNhatVaiTro(nguoiDung, luaChonVaiTro[viTriDangChon[0]]))
                .show();
    }

    private void capNhatVaiTro(NguoiDung nguoiDung, VaiTroNguoiDung vaiTroMoi) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (nguoiDung.layId() == idNguoiDungHienTai && vaiTroMoi != VaiTroNguoiDung.ADMIN) {
            Toast.makeText(requireContext(), R.string.admin_self_demote_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daCapNhat = databaseHelper.capNhatVaiTroNguoiDung(nguoiDung.layId(), vaiTroMoi);
        Toast.makeText(requireContext(), daCapNhat ? R.string.admin_user_role_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            taiDanhSachNguoiDung();
        }
    }

    private void xuLyBatTatTrangThai(NguoiDung nguoiDung) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        boolean trangThaiMoi = !nguoiDung.dangHoatDong();
        if (nguoiDung.layId() == idNguoiDungHienTai && !trangThaiMoi) {
            Toast.makeText(requireContext(), R.string.admin_self_lock_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daCapNhat = databaseHelper.capNhatTrangThaiHoatDongNguoiDung(nguoiDung.layId(), trangThaiMoi);
        Toast.makeText(requireContext(), daCapNhat ? R.string.admin_user_active_update_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            taiDanhSachNguoiDung();
        }
    }
}
