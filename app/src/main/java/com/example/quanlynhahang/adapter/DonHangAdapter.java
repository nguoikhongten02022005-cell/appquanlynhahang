package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.DonHang;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
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
        private final TextView tvDonHangTotal;
        private final TextView tvDonHangStatus;
        private final Button btnDonHangDetail;
        private final Button btnDonHangCancel;
        private final LinearLayout layoutDonHangDetails;
        private final MonTrongDonAdapter orderDishAdapter;

        DonHangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonHangCode = itemView.findViewById(R.id.tvDonHangCode);
            tvDonHangTime = itemView.findViewById(R.id.tvDonHangTime);
            tvDonHangTotal = itemView.findViewById(R.id.tvDonHangTotal);
            tvDonHangStatus = itemView.findViewById(R.id.tvDonHangStatus);
            btnDonHangDetail = itemView.findViewById(R.id.btnDonHangDetail);
            btnDonHangCancel = itemView.findViewById(R.id.btnDonHangCancel);
            layoutDonHangDetails = itemView.findViewById(R.id.layoutDonHangDetails);

            RecyclerView rvMonTrongDon = itemView.findViewById(R.id.rvMonTrongDon);
            rvMonTrongDon.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvMonTrongDon.setNestedScrollingEnabled(false);

            orderDishAdapter = new MonTrongDonAdapter(Collections.emptyList());
            rvMonTrongDon.setAdapter(orderDishAdapter);
        }

        void ganDuLieu(DonHang donHang) {
            Context context = itemView.getContext();

            tvDonHangCode.setText(donHang.layMaDon());
            tvDonHangTime.setText(donHang.layThoiGian());
            tvDonHangTotal.setText(dinhDangGia(donHang.layTongTien()));
            tvDonHangStatus.setText(layTextTrangThai(donHang.layTrangThai()));

            int mauTrangThai = ContextCompat.getColor(context, layMauTrangThai(donHang.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvDonHangStatus, ColorStateList.valueOf(mauTrangThai));

            orderDishAdapter.capNhatDuLieu(donHang.layDanhSachMon());

            layoutDonHangDetails.setVisibility(donHang.dangMoRong() ? View.VISIBLE : View.GONE);
            btnDonHangDetail.setText(donHang.dangMoRong() ? R.string.order_hide_details : R.string.order_view_details);
            btnDonHangDetail.setOnClickListener(v -> chuyenTrangThaiChiTiet(donHang));

            btnDonHangCancel.setVisibility(donHang.coTheHuy() ? View.VISIBLE : View.GONE);
            btnDonHangCancel.setOnClickListener(v -> {
                int viTriAdapter = getBindingAdapterPosition();
                if (viTriAdapter == RecyclerView.NO_POSITION || !donHang.coTheHuy()) {
                    return;
                }
                onHuyDonClickListener.onHuyDon(donHang, viTriAdapter);
            });
        }

        private void chuyenTrangThaiChiTiet(DonHang donHang) {
            int viTriAdapter = getBindingAdapterPosition();
            if (viTriAdapter == RecyclerView.NO_POSITION) {
                return;
            }

            donHang.datTrangThaiMoRong(!donHang.dangMoRong());
            notifyItemChanged(viTriAdapter);
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

    private int layTextTrangThai(DonHang.TrangThai trangThai) {
        if (trangThai == DonHang.TrangThai.PENDING_CONFIRMATION) {
            return R.string.order_status_pending;
        }
        if (trangThai == DonHang.TrangThai.CONFIRMED) {
            return R.string.order_status_confirmed;
        }
        if (trangThai == DonHang.TrangThai.COMPLETED) {
            return R.string.order_status_completed;
        }
        return R.string.order_status_canceled;
    }

    private int layMauTrangThai(DonHang.TrangThai trangThai) {
        if (trangThai == DonHang.TrangThai.PENDING_CONFIRMATION) {
            return R.color.brand_orange;
        }
        if (trangThai == DonHang.TrangThai.CONFIRMED) {
            return R.color.brand_green;
        }
        if (trangThai == DonHang.TrangThai.COMPLETED) {
            return R.color.hero_top;
        }
        return R.color.brand_red;
    }
}
