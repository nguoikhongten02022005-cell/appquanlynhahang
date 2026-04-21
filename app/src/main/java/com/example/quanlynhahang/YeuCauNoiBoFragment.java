package com.example.quanlynhahang;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.YeuCauPhucVuNhanVienAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.List;

public class YeuCauNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private YeuCauPhucVuNhanVienAdapter yeuCauAdapter;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yeu_cau_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        TextView tvTitle = view.findViewById(R.id.tvYeuCauNoiBoTitle);
        tvTitle.setText(R.string.employee_service_requests_title);
        tvEmptyState = view.findViewById(R.id.tvYeuCauNoiBoEmptyState);

        RecyclerView rvYeuCau = view.findViewById(R.id.rvYeuCauNoiBo);
        rvYeuCau.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvYeuCau.setNestedScrollingEnabled(false);

        yeuCauAdapter = new YeuCauPhucVuNhanVienAdapter(new YeuCauPhucVuNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiNhanXuLy(YeuCauPhucVu yeuCau) {
                capNhatTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DANG_XU_LY);
            }

            @Override
            public void khiDanhDauDaXong(YeuCauPhucVu yeuCau) {
                capNhatTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DA_XU_LY);
            }

            @Override
            public void khiHuy(YeuCauPhucVu yeuCau) {
                xacNhanHuyYeuCau(yeuCau);
            }
        });
        rvYeuCau.setAdapter(yeuCauAdapter);

        taiDanhSachYeuCau();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachYeuCau();
    }

    private void taiDanhSachYeuCau() {
        List<YeuCauPhucVu> danhSachYeuCau = databaseHelper.layTatCaYeuCauPhucVu();
        yeuCauAdapter.capNhatDanhSach(danhSachYeuCau);
        tvEmptyState.setVisibility(danhSachYeuCau.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void capNhatTrangThaiYeuCau(YeuCauPhucVu yeuCau, YeuCauPhucVu.TrangThai trangThai) {
        boolean daCapNhat = databaseHelper.capNhatTrangThaiYeuCauPhucVu(yeuCau.layId(), trangThai);
        Toast.makeText(
                requireContext(),
                daCapNhat ? R.string.employee_service_request_status_update_success : R.string.employee_status_update_failed,
                Toast.LENGTH_SHORT
        ).show();
        if (daCapNhat) {
            taiDanhSachYeuCau();
        }
    }

    private void xacNhanHuyYeuCau(YeuCauPhucVu yeuCau) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.employee_service_request_cancel_confirm_title)
                .setMessage(R.string.employee_service_request_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) ->
                        capNhatTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DA_HUY))
                .show();
    }
}
