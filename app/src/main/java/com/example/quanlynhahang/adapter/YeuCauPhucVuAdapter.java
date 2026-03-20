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

    private final List<YeuCauPhucVu> danhSachYeuCau = new ArrayList<>();

    public YeuCauPhucVuAdapter(List<YeuCauPhucVu> danhSachYeuCau) {
        this.danhSachYeuCau.addAll(danhSachYeuCau);
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

    static class ServiceRequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvServiceRequestContent;
        private final TextView tvServiceRequestTime;
        private final TextView tvServiceRequestStatus;

        ServiceRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceRequestContent = itemView.findViewById(R.id.tvServiceRequestContent);
            tvServiceRequestTime = itemView.findViewById(R.id.tvServiceRequestTime);
            tvServiceRequestStatus = itemView.findViewById(R.id.tvServiceRequestStatus);
        }

        void ganDuLieu(YeuCauPhucVu yeuCau) {
            Context context = itemView.getContext();
            tvServiceRequestContent.setText(yeuCau.layNoiDung());
            tvServiceRequestTime.setText(yeuCau.layThoiGianGui());

            if (yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.PROCESSING) {
                tvServiceRequestStatus.setText(R.string.service_request_status_processing);
                int mauDangXuLy = ContextCompat.getColor(context, R.color.brand_orange);
                ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(mauDangXuLy));
            } else {
                tvServiceRequestStatus.setText(R.string.service_request_status_done);
                int mauDaXong = ContextCompat.getColor(context, R.color.brand_green);
                ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(mauDaXong));
            }
        }
    }
}
