package com.example.quanlynhahang;

import android.content.DialogInterface;
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

import com.example.quanlynhahang.adapter.BoDieuHopMonQuanTri;
import com.example.quanlynhahang.data.DatabaseHelper;

import java.util.List;

public class MonAnQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private BoDieuHopMonQuanTri boDieuHopMon;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mon_an_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        RecyclerView recyclerView = view.findViewById(R.id.rvMonAnQuanTri);
        tvEmptyState = view.findViewById(R.id.tvMonAnQuanTriEmpty);

        boDieuHopMon = new BoDieuHopMonQuanTri(new BoDieuHopMonQuanTri.HanhDongListener() {
            @Override
            public void khiSua(DatabaseHelper.DishRecord banGhiMon) {
                hienGoiYChinhSua(banGhiMon);
            }

            @Override
            public void khiXoa(DatabaseHelper.DishRecord banGhiMon) {
                xacNhanXoaMon(banGhiMon);
            }

            @Override
            public void khiBatTatTrangThaiPhucVu(DatabaseHelper.DishRecord banGhiMon) {
                boolean trangThaiMoi = !banGhiMon.layMonAn().laConPhucVu();
                boolean daCapNhat = databaseHelper.capNhatTrangThaiPhucVuMon(banGhiMon.layId(), trangThaiMoi);
                Toast.makeText(requireContext(), daCapNhat ? R.string.admin_dish_availability_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                if (daCapNhat) {
                    taiDanhSachMon();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(boDieuHopMon);

        taiDanhSachMon();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null && boDieuHopMon != null) {
            taiDanhSachMon();
        }
    }

    private void taiDanhSachMon() {
        List<DatabaseHelper.DishRecord> danhSachMon = databaseHelper.layTatCaMonAn();
        boDieuHopMon.capNhatDanhSach(danhSachMon);
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(danhSachMon.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void hienGoiYChinhSua(DatabaseHelper.DishRecord banGhiMon) {
        if (!isAdded()) {
            return;
        }
        Toast.makeText(requireContext(), "Chức năng sửa món đang tạm tắt", Toast.LENGTH_SHORT).show();
    }

    private void xacNhanXoaMon(DatabaseHelper.DishRecord banGhiMon) {
        if (!isAdded()) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_delete_confirm_title)
                .setMessage(R.string.admin_delete_confirm_message)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_delete_dish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean daXoa = databaseHelper.xoaMonAnTheoId(banGhiMon.layId());
                        Toast.makeText(requireContext(), daXoa ? R.string.admin_dish_delete_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                        if (daXoa) {
                            taiDanhSachMon();
                        }
                    }
                })
                .show();
    }
}
