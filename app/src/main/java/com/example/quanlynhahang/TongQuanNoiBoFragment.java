package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.databinding.FragmentTongQuanNoiBoBinding;
import com.example.quanlynhahang.model.ThongKeTongQuanNhanVien;

public class TongQuanNoiBoFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private FragmentTongQuanNoiBoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTongQuanNoiBoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        ThongKeTongQuanNhanVien thongKe = databaseHelper.layThongKeTongQuanNhanVien();

        binding.tvNoiBoOverviewTitle.setText(R.string.internal_shell_overview_title);
        binding.tvNoiBoPendingOrdersCount.setText(String.valueOf(thongKe.getPendingDonHangs()));
        binding.tvNoiBoPendingReservationsCount.setText(String.valueOf(thongKe.getPendingReservations()));
        binding.tvNoiBoProcessingRequestsCount.setText(String.valueOf(thongKe.getProcessingServiceRequests()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
