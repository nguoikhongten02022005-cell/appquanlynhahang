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

import com.example.quanlynhahang.adapter.DonHangNhanVienAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.DonHang;

import java.util.List;

public class DonHangNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private DonHangNhanVienAdapter donHangAdapter;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_don_hang_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        TextView tvTitle = view.findViewById(R.id.tvDonHangNoiBoTitle);
        tvTitle.setText(R.string.employee_orders_title);
        tvEmptyState = view.findViewById(R.id.tvDonHangNoiBoEmptyState);

        RecyclerView rvDonHang = view.findViewById(R.id.rvDonHangNoiBo);
        rvDonHang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDonHang.setNestedScrollingEnabled(false);

        donHangAdapter = new DonHangNhanVienAdapter(new DonHangNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiXacNhan(DonHang order) {
                capNhatTrangThaiDonHang(order, DonHang.TrangThai.DANG_CHUAN_BI);
            }

            @Override
            public void khiHoanTat(DonHang order) {
                DonHang.TrangThai trangThaiDich = order.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI
                        ? DonHang.TrangThai.SAN_SANG_PHUC_VU
                        : DonHang.TrangThai.HOAN_THANH;
                capNhatTrangThaiDonHang(order, trangThaiDich);
            }

            @Override
            public void khiHuy(DonHang order) {
                xacNhanHuyDonHang(order);
            }
        });
        rvDonHang.setAdapter(donHangAdapter);

        taiDanhSachDonHang();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachDonHang();
    }

    private void taiDanhSachDonHang() {
        List<DonHang> danhSachDon = databaseHelper.layTatCaDonHang();
        donHangAdapter.capNhatDanhSach(danhSachDon);
        tvEmptyState.setVisibility(danhSachDon.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void capNhatTrangThaiDonHang(DonHang donHang, DonHang.TrangThai trangThai) {
        boolean daCapNhat = databaseHelper.capNhatTrangThaiDonHang(donHang.layId(), trangThai);
        Toast.makeText(
                requireContext(),
                daCapNhat ? R.string.employee_order_status_update_success : R.string.employee_status_update_failed,
                Toast.LENGTH_SHORT
        ).show();
        if (daCapNhat) {
            taiDanhSachDonHang();
        }
    }

    private void xacNhanHuyDonHang(DonHang donHang) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.employee_order_cancel_confirm_title)
                .setMessage(R.string.employee_order_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) ->
                        capNhatTrangThaiDonHang(donHang, DonHang.TrangThai.DA_HUY))
                .show();
    }
}
