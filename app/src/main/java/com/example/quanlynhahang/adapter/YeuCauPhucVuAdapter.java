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
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.ArrayList;
import java.util.List;

public class YeuCauPhucVuAdapter extends RecyclerView.Adapter<YeuCauPhucVuAdapter.ServiceRequestViewHolder> {

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
    public ServiceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yeu_cau_phuc_vu, parent, false);
        return new ServiceRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRequestViewHolder holder, int position) {
        holder.ganDuLieu(danhSachYeuCau.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachYeuCau.size();
    }

    class ServiceRequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvServiceRequestContent;
        private final TextView tvServiceRequestType;
        private final TextView tvServiceRequestTime;
        private final TextView tvServiceRequestTable;
        private final TextView tvServiceRequestStatus;
        private final com.google.android.material.button.MaterialButton btnCancelServiceRequest;

        ServiceRequestViewHolder(@NonNull View itemView) {
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
            tvServiceRequestType.setText(layTextLoaiYeuCau(context, yeuCau.layLoaiYeuCau()));
            tvServiceRequestTime.setText(yeuCau.layThoiGianGui());
            tvServiceRequestTable.setVisibility(yeuCau.coBanLienQuan() ? View.VISIBLE : View.GONE);
            if (yeuCau.coBanLienQuan()) {
                tvServiceRequestTable.setText(context.getString(R.string.order_table_format, yeuCau.laySoBan()));
            }

            if (yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_XU_LY) {
                tvServiceRequestStatus.setText(R.string.service_request_status_processing);
                int mauDangXuLy = ContextCompat.getColor(context, R.color.warning);
                ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(mauDangXuLy));
            } else if (yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DA_HUY) {
                tvServiceRequestStatus.setText(R.string.service_request_status_canceled);
                int mauDaHuy = ContextCompat.getColor(context, R.color.error);
                ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(mauDaHuy));
            } else {
                tvServiceRequestStatus.setText(R.string.service_request_status_done);
                int mauDaXong = ContextCompat.getColor(context, R.color.success);
                ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(mauDaXong));
            }

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

        private String layTextLoaiYeuCau(Context context, YeuCauPhucVu.LoaiYeuCau loaiYeuCau) {
            if (loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.THEM_NUOC) {
                return context.getString(R.string.service_request_type_more_water);
            }
            if (loaiYeuCau == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN) {
                return context.getString(R.string.service_request_type_payment);
            }
            return context.getString(R.string.service_request_type_call_staff);
        }
    }
}
