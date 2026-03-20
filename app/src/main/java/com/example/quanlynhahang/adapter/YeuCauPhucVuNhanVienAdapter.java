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

public class YeuCauPhucVuNhanVienAdapter extends RecyclerView.Adapter<YeuCauPhucVuNhanVienAdapter.EmployeeServiceRequestViewHolder> {

    public interface HanhDongListener {
        void khiDanhDauDaXong(YeuCauPhucVu request);
    }

    private final List<YeuCauPhucVu> danhSachYeuCau = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public YeuCauPhucVuNhanVienAdapter(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<YeuCauPhucVu> danhSachMoi) {
        danhSachYeuCau.clear();
        danhSachYeuCau.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeServiceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yeu_cau_phuc_vu_nhan_vien, parent, false);
        return new EmployeeServiceRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeServiceRequestViewHolder holder, int position) {
        holder.ganDuLieu(danhSachYeuCau.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachYeuCau.size();
    }

    class EmployeeServiceRequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;
        private final TextView tvTime;
        private final TextView tvStatus;
        private final TextView btnDone;

        EmployeeServiceRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvEmployeeServiceRequestContent);
            tvTime = itemView.findViewById(R.id.tvEmployeeServiceRequestTime);
            tvStatus = itemView.findViewById(R.id.tvEmployeeServiceRequestStatus);
            btnDone = itemView.findViewById(R.id.btnEmployeeServiceRequestDone);
        }

        void ganDuLieu(YeuCauPhucVu yeuCau) {
            Context context = itemView.getContext();
            tvContent.setText(yeuCau.layNoiDung());
            tvTime.setText(yeuCau.layThoiGianGui());
            boolean dangXuLy = yeuCau.layTrangThai() == YeuCauPhucVu.TrangThai.DANG_XU_LY;
            tvStatus.setText(dangXuLy ? R.string.service_request_status_processing : R.string.service_request_status_done);
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, dangXuLy ? R.color.warning : R.color.success)));
            btnDone.setVisibility(dangXuLy ? View.VISIBLE : View.GONE);
            btnDone.setOnClickListener(dangXuLy ? v -> hanhDongListener.khiDanhDauDaXong(yeuCau) : null);
        }
    }
}
