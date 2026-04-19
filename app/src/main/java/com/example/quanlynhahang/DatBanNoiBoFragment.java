package com.example.quanlynhahang;

import android.text.TextUtils;
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

import com.example.quanlynhahang.adapter.DatBanNhanVienAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.DatBan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatBanNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private DatBanNhanVienAdapter datBanAdapter;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dat_ban_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        TextView tvTitle = view.findViewById(R.id.tvDatBanNoiBoTitle);
        tvTitle.setText(R.string.employee_reservations_title);
        tvEmptyState = view.findViewById(R.id.tvDatBanNoiBoEmptyState);

        RecyclerView rvDatBan = view.findViewById(R.id.rvDatBanNoiBo);
        rvDatBan.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDatBan.setNestedScrollingEnabled(false);

        datBanAdapter = new DatBanNhanVienAdapter(new DatBanNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiXacNhan(DatBan reservation) {
                capNhatTrangThaiDatBan(reservation, DatBan.TrangThai.ACTIVE);
            }

            @Override
            public void khiHoanTat(DatBan reservation) {
                capNhatTrangThaiDatBan(reservation, DatBan.TrangThai.COMPLETED);
            }

            @Override
            public void khiHuy(DatBan reservation) {
                xacNhanHuyDatBan(reservation);
            }

            @Override
            public void khiDoiBan(DatBan reservation) {
                hienDialogDoiBan(reservation);
            }
        });
        rvDatBan.setAdapter(datBanAdapter);

        taiDanhSachDatBan();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachDatBan();
    }

    private void taiDanhSachDatBan() {
        List<DatBan> danhSachDatBan = databaseHelper.layTatCaDatBan();
        datBanAdapter.capNhatDanhSach(danhSachDatBan);
        tvEmptyState.setVisibility(danhSachDatBan.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void capNhatTrangThaiDatBan(DatBan datBan, DatBan.TrangThai trangThai) {
        if (datBan == null || trangThai == null) {
            Toast.makeText(requireContext(), R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        if (trangThai == DatBan.TrangThai.COMPLETED && datBan.layIdDonHangLienKet() <= 0) {
            Toast.makeText(requireContext(), R.string.employee_reservation_complete_requires_order, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean daCapNhat = databaseHelper.capNhatTrangThaiDatBan(datBan.layId(), trangThai);
        Toast.makeText(
                requireContext(),
                daCapNhat ? R.string.employee_reservation_status_update_success : R.string.employee_status_update_failed,
                Toast.LENGTH_SHORT
        ).show();
        if (daCapNhat) {
            taiDanhSachDatBan();
        }
    }

    private void xacNhanHuyDatBan(DatBan datBan) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.employee_reservation_cancel_confirm_title)
                .setMessage(R.string.employee_reservation_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) ->
                        capNhatTrangThaiDatBan(datBan, DatBan.TrangThai.CANCELLED))
                .show();
    }

    private void hienDialogDoiBan(DatBan datBan) {
        if (datBan == null) {
            Toast.makeText(requireContext(), R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> danhSachBanTrong = new ArrayList<>();
        for (int soBan = 1; soBan <= 20; soBan++) {
            String tenBan = getString(R.string.reservation_table_option_format, soBan);
            if (tenBan.equalsIgnoreCase(datBan.laySoBan())
                    || !databaseHelper.layDanhSachBanDaDat(datBan.layThoiGian(), datBan.layId()).contains(tenBan)) {
                danhSachBanTrong.add(tenBan);
            }
        }
        Collections.sort(danhSachBanTrong);
        if (danhSachBanTrong.isEmpty()) {
            Toast.makeText(requireContext(), R.string.employee_reservation_change_table_no_options, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = danhSachBanTrong.toArray(new String[0]);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.employee_reservation_change_table_title)
                .setMessage(R.string.employee_reservation_change_table_message)
                .setItems(items, (dialog, which) -> {
                    boolean daCapNhat = databaseHelper.capNhatBanDatBan(datBan.layId(), items[which]);
                    Toast.makeText(
                            requireContext(),
                            daCapNhat ? R.string.employee_reservation_change_table_success : R.string.employee_status_update_failed,
                            Toast.LENGTH_SHORT
                    ).show();
                    if (daCapNhat) {
                        taiDanhSachDatBan();
                    }
                })
                .setNegativeButton(R.string.dialog_close, null)
                .show();
    }
}
