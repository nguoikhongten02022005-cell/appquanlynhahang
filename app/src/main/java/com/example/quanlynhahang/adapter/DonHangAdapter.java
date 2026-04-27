package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.ChiTietDonHangActivity;
import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemTomTatDonHangBinding;
import com.example.quanlynhahang.helper.HanhDongNghiepVuHelper;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.List;

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
        ItemTomTatDonHangBinding binding = ItemTomTatDonHangBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DonHangViewHolder(binding);
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
        private final ItemTomTatDonHangBinding binding;

        DonHangViewHolder(@NonNull ItemTomTatDonHangBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(DonHang donHang) {
            Context context = itemView.getContext();

            binding.tvDonHangCode.setText(donHang.layMaDon());
            binding.tvDonHangTime.setText(donHang.layThoiGian());
            int trangThaiRes = TrangThaiHienThiHelper.layTextTrangThaiDon(donHang);
            binding.tvDonHangStatus.setText(trangThaiRes);
            binding.tvDonHangType.setText(context.getString(
                    R.string.order_card_summary_format,
                    layTextHinhThuc(context, donHang),
                    donHang.layDanhSachMon().size(),
                    dinhDangGia(context, donHang.layTongTien())
            ));

            int mauTrangThai = ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiDon(donHang.layTrangThai()));
            ViewCompat.setBackgroundTintList(binding.tvDonHangStatus, ColorStateList.valueOf(mauTrangThai));

            binding.btnDonHangDetail.setText(R.string.order_view_details);
            binding.btnDonHangDetail.setOnClickListener(v -> moChiTietDonHang(context, donHang));

            boolean coTheHuy = HanhDongNghiepVuHelper.khachCoTheHuyDon(donHang);
            binding.btnDonHangCancel.setVisibility(coTheHuy ? View.VISIBLE : View.GONE);
            binding.btnDonHangCancel.setOnClickListener(v -> {
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

    private String dinhDangGia(Context context, String chuoiGiaGoc) {
        long soTien = chuoiGiaGoc == null ? 0L : MoneyUtils.tachGiaTienTuChuoi(chuoiGiaGoc);
        return soTien <= 0L ? context.getString(R.string.currency_zero) : MoneyUtils.dinhDangTienViet(soTien);
    }

    private String layTextHinhThuc(Context context, DonHang donHang) {
        return context.getString(donHang.laAnTaiQuan()
                ? R.string.order_type_dine_in
                : R.string.order_type_take_away);
    }

}
