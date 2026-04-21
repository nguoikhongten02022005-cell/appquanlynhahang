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
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.ThongKeTongQuanQuanTri;

public class BaoCaoQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bao_cao_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        ThongKeTongQuanQuanTri thongKe = databaseHelper.layThongKeTongQuanQuanTri();

        TextView tvTieuDe = view.findViewById(R.id.tvBaoCaoQuanTriTitle);
        TextView tvTongNguoiDung = view.findViewById(R.id.tvBaoCaoTongNguoiDung);
        TextView tvTongMonAn = view.findViewById(R.id.tvBaoCaoTongMonAn);
        TextView tvTongDonHang = view.findViewById(R.id.tvBaoCaoTongDonHang);
        TextView tvDonHangChoXacNhan = view.findViewById(R.id.tvBaoCaoDonHangChoXacNhan);
        TextView tvDatBanChoDuyet = view.findViewById(R.id.tvBaoCaoDatBanChoDuyet);
        TextView tvYeuCauDangXuLy = view.findViewById(R.id.tvBaoCaoYeuCauDangXuLy);
        TextView btnMoQuanLyBan = view.findViewById(R.id.btnMoQuanLyBan);

        tvTieuDe.setText(R.string.admin_reports_title);
        tvTongNguoiDung.setText(String.valueOf(thongKe.layTongNguoiDung()));
        tvTongMonAn.setText(String.valueOf(thongKe.layTongMonAn()));
        tvTongDonHang.setText(String.valueOf(thongKe.layTongDonHang()));
        tvDonHangChoXacNhan.setText(String.valueOf(thongKe.laySoDonHangChoXacNhan()));
        tvDatBanChoDuyet.setText(String.valueOf(thongKe.laySoDatBanChoDuyet()));
        tvYeuCauDangXuLy.setText(String.valueOf(thongKe.laySoYeuCauDangXuLy()));
        btnMoQuanLyBan.setOnClickListener(v -> {
            startActivity(TrungTamQuanTriActivity.taoIntent(requireContext(), DieuHuongNoiBoHelper.SECTION_BAN));
            requireActivity().finish();
        });
    }
}
