package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yeu_cau_phuc_vu, parent, false);
        return new ViewHolderYeuCauPhucVu(view);
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
        private final TextView tvServiceRequestContent;
        private final TextView tvServiceRequestType;
        private final TextView tvServiceRequestTime;
        private final TextView tvServiceRequestTable;
        private final TextView tvServiceRequestStatus;
        private final com.google.android.material.button.MaterialButton btnCancelServiceRequest;

        ViewHolderYeuCauPhucVu(@NonNull View itemView) {
            super(itemView);
            tvServiceRequestContent = itemView.findViewById(R.id.tvServiceRequestContent);
            tvServiceRequestType = itemView.findViewById(R.id.tvServiceRequestType);
            tvServiceRequestTime = itemView.findViewById(R.id.tvServiceRequestTime);
            tvServiceRequestTable = itemView.findViewById(R.id.tvServiceRequestTable);
            tvServiceRequestStatus = itemView.findViewById(R.id.tvServiceRequestStatus);
            btnCancelServiceRequest = itemView.findViewById(R.id.btnCancelServiceRequest);
        }

        void ganDuLieu(YeuCauPhucVu yeuCau) {
            Context context = itemView.getContext();
            tvServiceRequestContent.setText(yeuCau.layNoiDung());
            tvServiceRequestType.setText(TrangThaiHienThiHelper.layTextLoaiYeuCau(yeuCau.layLoaiYeuCau()));
            tvServiceRequestTime.setText(yeuCau.layThoiGianGui());
            tvServiceRequestTable.setVisibility(yeuCau.coBanLienQuan() ? View.VISIBLE : View.GONE);
            if (yeuCau.coBanLienQuan()) {
                tvServiceRequestTable.setText(context.getString(R.string.order_table_format, yeuCau.laySoBan()));
            }

            tvServiceRequestStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiYeuCau(yeuCau.layTrangThai()));
            int mauTrangThai = ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiYeuCau(yeuCau.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(mauTrangThai));

            boolean coTheHuy = yeuCau.coTheHuy() && onHuyYeuCauClickListener != null;
            btnCancelServiceRequest.setVisibility(coTheHuy ? View.VISIBLE : View.GONE);
            btnCancelServiceRequest.setOnClickListener(coTheHuy ? v -> {
                int viTri = getBindingAdapterPosition();
                if (viTri == RecyclerView.NO_POSITION) {
                    return;
                }
                onHuyYeuCauClickListener.onHuyYeuCau(yeuCau, viTri);
            } : null);
        }

    }
}
