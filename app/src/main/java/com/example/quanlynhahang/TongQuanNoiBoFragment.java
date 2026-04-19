package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.ThongKeTongQuanNhanVien;

public class TongQuanNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tong_quan_noi_bo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        ThongKeTongQuanNhanVien thongKe = databaseHelper.layThongKeTongQuanNhanVien();

        TextView tvPendingOrders = view.findViewById(R.id.tvNoiBoPendingOrdersCount);
        TextView tvPendingReservations = view.findViewById(R.id.tvNoiBoPendingReservationsCount);
        TextView tvProcessingRequests = view.findViewById(R.id.tvNoiBoProcessingRequestsCount);
        TextView tvOverviewTitle = view.findViewById(R.id.tvNoiBoOverviewTitle);

        tvOverviewTitle.setText(R.string.internal_shell_overview_title);
        tvPendingOrders.setText(String.valueOf(thongKe.getPendingDonHangs()));
        tvPendingReservations.setText(String.valueOf(thongKe.getPendingReservations()));
        tvProcessingRequests.setText(String.valueOf(thongKe.getProcessingServiceRequests()));
    }
}
