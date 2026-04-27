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
import com.example.quanlynhahang.databinding.ItemYeuCauPhucVuNhanVienBinding;
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
        ItemYeuCauPhucVuNhanVienBinding binding = ItemYeuCauPhucVuNhanVienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderYeuCauPhucVuNhanVien(binding);
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
        private final ItemYeuCauPhucVuNhanVienBinding binding;

        ViewHolderYeuCauPhucVuNhanVien(@NonNull ItemYeuCauPhucVuNhanVienBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(YeuCauPhucVu yeuCau) {
            Context context = itemView.getContext();
            binding.tvEmployeeServiceRequestContent.setText(yeuCau.layNoiDung());
            binding.tvEmployeeServiceRequestTime.setText(yeuCau.layThoiGianGui());
            binding.tvEmployeeServiceRequestTable.setVisibility(yeuCau.coBanLienQuan() ? View.VISIBLE : View.GONE);
            if (yeuCau.coBanLienQuan()) {
                binding.tvEmployeeServiceRequestTable.setText(context.getString(R.string.order_table_format, yeuCau.laySoBan()));
            }
            binding.tvEmployeeServiceRequestStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiYeuCau(yeuCau.layTrangThai()));
            ViewCompat.setBackgroundTintList(binding.tvEmployeeServiceRequestStatus, ColorStateList.valueOf(ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiYeuCau(yeuCau.layTrangThai()))));
            binding.btnEmployeeServiceRequestReceive.setVisibility(HanhDongNghiepVuHelper.nhanVienCoTheNhanXuLyYeuCau(yeuCau) ? View.VISIBLE : View.GONE);
            binding.btnEmployeeServiceRequestReceive.setOnClickListener(HanhDongNghiepVuHelper.nhanVienCoTheNhanXuLyYeuCau(yeuCau) ? v -> hanhDongListener.khiNhanXuLy(yeuCau) : null);
            binding.btnEmployeeServiceRequestDone.setText(yeuCau.layLoaiYeuCau() == YeuCauPhucVu.LoaiYeuCau.THANH_TOAN
                    ? R.string.employee_service_request_action_confirm_payment
                    : R.string.employee_service_request_action_done);
            binding.btnEmployeeServiceRequestDone.setVisibility(HanhDongNghiepVuHelper.nhanVienCoTheHoanTatYeuCau(yeuCau) ? View.VISIBLE : View.GONE);
            binding.btnEmployeeServiceRequestDone.setOnClickListener(HanhDongNghiepVuHelper.nhanVienCoTheHoanTatYeuCau(yeuCau) ? v -> hanhDongListener.khiDanhDauDaXong(yeuCau) : null);
            binding.btnEmployeeServiceRequestCancel.setVisibility(HanhDongNghiepVuHelper.nhanVienCoTheHuyYeuCau(yeuCau) ? View.VISIBLE : View.GONE);
            binding.btnEmployeeServiceRequestCancel.setOnClickListener(HanhDongNghiepVuHelper.nhanVienCoTheHuyYeuCau(yeuCau) ? v -> hanhDongListener.khiHuy(yeuCau) : null);
        }
    }
}
