package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.ChiTietDonHangActivity;
import com.example.quanlynhahang.R;
import com.example.quanlynhahang.helper.HanhDongNghiepVuHelper;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.DonHang;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.DonHangViewHolder> {

    public interface OnHuyDonClickListener {
        void onHuyDon(DonHang donHang, int viTri);
    }

    private final List<DonHang> danhSachDon = new ArrayList<>();
    private final OnHuyDonClickListener onHuyDonClickListener;

    public DonHangAdapter(List<DonHang> danhSachDon, OnHuyDonClickListener onHuyDonClickListener) {
        this.danhSachDon.addAll(danhSachDon);
        this.onHuyDonClickListener = onHuyDonClickListener;
    }

    public void capNhatDuLieu(List<DonHang> danhSachDonMoi) {
        danhSachDon.clear();
        danhSachDon.addAll(danhSachDonMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DonHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tom_tat_don_hang, parent, false);
        return new DonHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonHangViewHolder holder, int position) {
        holder.ganDuLieu(danhSachDon.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachDon.size();
    }

    class DonHangViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDonHangCode;
        private final TextView tvDonHangTime;
        private final TextView tvDonHangStatus;
        private final TextView tvDonHangType;
        private final Button btnDonHangDetail;
        private final Button btnDonHangCancel;

        DonHangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonHangCode = itemView.findViewById(R.id.tvDonHangCode);
            tvDonHangTime = itemView.findViewById(R.id.tvDonHangTime);
            tvDonHangStatus = itemView.findViewById(R.id.tvDonHangStatus);
            tvDonHangType = itemView.findViewById(R.id.tvDonHangType);
            btnDonHangDetail = itemView.findViewById(R.id.btnDonHangDetail);
            btnDonHangCancel = itemView.findViewById(R.id.btnDonHangCancel);
        }

        void ganDuLieu(DonHang donHang) {
            Context context = itemView.getContext();

            tvDonHangCode.setText(donHang.layMaDon());
            tvDonHangTime.setText(donHang.layThoiGian());
            int trangThaiRes = TrangThaiHienThiHelper.layTextTrangThaiDon(donHang);
            tvDonHangStatus.setText(trangThaiRes);
            tvDonHangType.setText(context.getString(
                    R.string.order_card_summary_format,
                    layTextHinhThuc(context, donHang),
                    donHang.layDanhSachMon().size(),
                    dinhDangGia(donHang.layTongTien())
            ));

            int mauTrangThai = ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiDon(donHang.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvDonHangStatus, ColorStateList.valueOf(mauTrangThai));

            btnDonHangDetail.setText(R.string.order_view_details);
            btnDonHangDetail.setOnClickListener(v -> moChiTietDonHang(context, donHang));

            boolean coTheHuy = HanhDongNghiepVuHelper.khachCoTheHuyDon(donHang);
            btnDonHangCancel.setVisibility(coTheHuy ? View.VISIBLE : View.GONE);
            btnDonHangCancel.setOnClickListener(v -> {
                int viTriAdapter = getBindingAdapterPosition();
                if (viTriAdapter == RecyclerView.NO_POSITION || !coTheHuy) {
                    return;
                }
                onHuyDonClickListener.onHuyDon(donHang, viTriAdapter);
            });
        }

        private void moChiTietDonHang(Context context, DonHang donHang) {
            Intent intent = new Intent(context, ChiTietDonHangActivity.class);
            intent.putExtra("maDon", donHang.layMaDon());
            context.startActivity(intent);
        }

    }

    private String dinhDangGia(String chuoiGiaGoc) {
        if (chuoiGiaGoc == null) {
            return "0đ";
        }

        String chuSo = chuoiGiaGoc.replaceAll("[^0-9]", "");
        if (chuSo.isEmpty()) {
            return "0đ";
        }

        long soTien;
        try {
            soTien = Long.parseLong(chuSo);
        } catch (NumberFormatException ex) {
            return "0đ";
        }

        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        kyHieu.setGroupingSeparator('.');
        DecimalFormat dinhDangSo = new DecimalFormat("#,###", kyHieu);
        return dinhDangSo.format(soTien) + "đ";
    }

    private String layTextHinhThuc(Context context, DonHang donHang) {
        return context.getString(donHang.laAnTaiQuan()
                ? R.string.order_type_dine_in
                : R.string.order_type_take_away);
    }

}
