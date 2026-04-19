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
import com.example.quanlynhahang.helper.HanhDongNghiepVuHelper;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.util.ArrayList;
import java.util.List;

public class YeuCauPhucVuNhanVienAdapter extends RecyclerView.Adapter<YeuCauPhucVuNhanVienAdapter.ViewHolderYeuCauPhucVuNhanVien> {

    public interface HanhDongListener {
        void khiNhanXuLy(YeuCauPhucVu yeuCau);

        void khiDanhDauDaXong(YeuCauPhucVu yeuCau);

        void khiHuy(YeuCauPhucVu yeuCau);
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
    public ViewHolderYeuCauPhucVuNhanVien onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yeu_cau_phuc_vu_nhan_vien, parent, false);
        return new ViewHolderYeuCauPhucVuNhanVien(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderYeuCauPhucVuNhanVien holder, int position) {
        holder.ganDuLieu(danhSachYeuCau.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachYeuCau.size();
    }

    class ViewHolderYeuCauPhucVuNhanVien extends RecyclerView.ViewHolder {
        private final TextView tvContent;
        private final TextView tvTime;
        private final TextView tvStatus;
        private final TextView tvTable;
        private final TextView btnReceive;
        private final TextView btnDone;
        private final TextView btnCancel;

        ViewHolderYeuCauPhucVuNhanVien(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvEmployeeServiceRequestContent);
            tvTime = itemView.findViewById(R.id.tvEmployeeServiceRequestTime);
            tvStatus = itemView.findViewById(R.id.tvEmployeeServiceRequestStatus);
            tvTable = itemView.findViewById(R.id.tvEmployeeServiceRequestTable);
            btnReceive = itemView.findViewById(R.id.btnEmployeeServiceRequestReceive);
            btnDone = itemView.findViewById(R.id.btnEmployeeServiceRequestDone);
            btnCancel = itemView.findViewById(R.id.btnEmployeeServiceRequestCancel);
        }

        void ganDuLieu(YeuCauPhucVu yeuCau) {
            Context context = itemView.getContext();
            tvContent.setText(yeuCau.layNoiDung());
            tvTime.setText(yeuCau.layThoiGianGui());
            tvTable.setVisibility(yeuCau.coBanLienQuan() ? View.VISIBLE : View.GONE);
            if (yeuCau.coBanLienQuan()) {
                tvTable.setText(context.getString(R.string.order_table_format, yeuCau.laySoBan()));
            }
            tvStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiYeuCau(yeuCau.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiYeuCau(yeuCau.layTrangThai()))));
            btnReceive.setVisibility(HanhDongNghiepVuHelper.nhanVienCoTheNhanXuLyYeuCau(yeuCau) ? View.VISIBLE : View.GONE);
            btnReceive.setOnClickListener(HanhDongNghiepVuHelper.nhanVienCoTheNhanXuLyYeuCau(yeuCau) ? v -> hanhDongListener.khiNhanXuLy(yeuCau) : null);
            btnDone.setText(yeuCau.layLoaiYeuCau() == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN
                    ? R.string.employee_service_request_action_confirm_payment
                    : R.string.employee_service_request_action_done);
            btnDone.setVisibility(HanhDongNghiepVuHelper.nhanVienCoTheHoanTatYeuCau(yeuCau) ? View.VISIBLE : View.GONE);
            btnDone.setOnClickListener(HanhDongNghiepVuHelper.nhanVienCoTheHoanTatYeuCau(yeuCau) ? v -> hanhDongListener.khiDanhDauDaXong(yeuCau) : null);
            btnCancel.setVisibility(HanhDongNghiepVuHelper.nhanVienCoTheHuyYeuCau(yeuCau) ? View.VISIBLE : View.GONE);
            btnCancel.setOnClickListener(HanhDongNghiepVuHelper.nhanVienCoTheHuyYeuCau(yeuCau) ? v -> hanhDongListener.khiHuy(yeuCau) : null);
        }
    }
}
