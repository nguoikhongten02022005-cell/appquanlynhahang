package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemYeuCauPhucVuBinding;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.ArrayList;
import java.util.List;

public class YeuCauPhucVuAdapter extends RecyclerView.Adapter<YeuCauPhucVuAdapter.ViewHolderYeuCauPhucVu> {

    public interface OnHuyYeuCauClickListener {
        void onHuyYeuCau(YeuCauPhucVu yeuCauPhucVu, int viTri);
    }

    private final List<YeuCauPhucVu> danhSachYeuCau = new ArrayList<>();
    private final OnHuyYeuCauClickListener onHuyYeuCauClickListener;

    public YeuCauPhucVuAdapter(List<YeuCauPhucVu> danhSachYeuCau,
                               OnHuyYeuCauClickListener onHuyYeuCauClickListener) {
        this.danhSachYeuCau.addAll(danhSachYeuCau);
        this.onHuyYeuCauClickListener = onHuyYeuCauClickListener;
    }

    public void capNhatDanhSach(List<YeuCauPhucVu> danhSachMoi) {
        danhSachYeuCau.clear();
        danhSachYeuCau.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    public void themYeuCau(YeuCauPhucVu yeuCau) {
        danhSachYeuCau.add(0, yeuCau);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ViewHolderYeuCauPhucVu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemYeuCauPhucVuBinding binding = ItemYeuCauPhucVuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderYeuCauPhucVu(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderYeuCauPhucVu holder, int position) {
        holder.ganDuLieu(danhSachYeuCau.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachYeuCau.size();
    }

    class ViewHolderYeuCauPhucVu extends RecyclerView.ViewHolder {
        private final ItemYeuCauPhucVuBinding binding;

        ViewHolderYeuCauPhucVu(@NonNull ItemYeuCauPhucVuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(YeuCauPhucVu yeuCau) {
            Context context = itemView.getContext();
            binding.tvServiceRequestContent.setText(yeuCau.layNoiDung());
            binding.tvServiceRequestType.setText(TrangThaiHienThiHelper.layTextLoaiYeuCau(yeuCau.layLoaiYeuCau()));
            binding.tvServiceRequestTime.setText(yeuCau.layThoiGianGui());
            binding.tvServiceRequestTable.setVisibility(yeuCau.coBanLienQuan() ? View.VISIBLE : View.GONE);
            if (yeuCau.coBanLienQuan()) {
                binding.tvServiceRequestTable.setText(context.getString(R.string.order_table_format, yeuCau.laySoBan()));
            }

            binding.tvServiceRequestStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiYeuCau(yeuCau.layTrangThai()));
            int mauTrangThai = ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiYeuCau(yeuCau.layTrangThai()));
            ViewCompat.setBackgroundTintList(binding.tvServiceRequestStatus, ColorStateList.valueOf(mauTrangThai));

            boolean coTheHuy = yeuCau.coTheHuy() && onHuyYeuCauClickListener != null;
            binding.btnCancelServiceRequest.setVisibility(coTheHuy ? View.VISIBLE : View.GONE);
            binding.btnCancelServiceRequest.setOnClickListener(coTheHuy ? v -> {
                int viTri = getBindingAdapterPosition();
                if (viTri == RecyclerView.NO_POSITION) {
                    return;
                }
                onHuyYeuCauClickListener.onHuyYeuCau(yeuCau, viTri);
            } : null);
        }

    }
}
