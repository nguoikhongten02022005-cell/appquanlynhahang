package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemDatBanNhanVienBinding;
import com.example.quanlynhahang.helper.HanhDongNghiepVuHelper;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.DatBan;

import java.util.ArrayList;
import java.util.List;

public class DatBanNhanVienAdapter extends RecyclerView.Adapter<DatBanNhanVienAdapter.EmployeeReservationViewHolder> {

    public interface HanhDongListener {
        void khiXacNhan(DatBan reservation);

        void khiHoanTat(DatBan reservation);

        void khiHuy(DatBan reservation);

        void khiDoiBan(DatBan reservation);
    }

    private final List<DatBan> danhSachDatBan = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public DatBanNhanVienAdapter(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<DatBan> danhSachMoi) {
        danhSachDatBan.clear();
        danhSachDatBan.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDatBanNhanVienBinding binding = ItemDatBanNhanVienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EmployeeReservationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeReservationViewHolder holder, int position) {
        holder.ganDuLieu(danhSachDatBan.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachDatBan.size();
    }

    class EmployeeReservationViewHolder extends RecyclerView.ViewHolder {
        private final ItemDatBanNhanVienBinding binding;

        EmployeeReservationViewHolder(@NonNull ItemDatBanNhanVienBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(DatBan datBan) {
            Context context = itemView.getContext();
            binding.tvEmployeeReservationTime.setText(datBan.layThoiGian());
            binding.tvEmployeeReservationTable.setText(context.getString(R.string.reservation_table_format_display, datBan.laySoBan()));
            binding.tvEmployeeReservationGuestCount.setText(context.getString(R.string.reservation_guest_count_format, datBan.laySoKhach()));
            String ghiChu = datBan.layGhiChu();
            binding.tvEmployeeReservationNote.setText(TextUtils.isEmpty(ghiChu)
                    ? context.getString(R.string.reservation_note_empty)
                    : context.getString(R.string.reservation_note_format, ghiChu));
            binding.tvEmployeeReservationStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiDatBan(datBan.layTrangThai()));
            ViewCompat.setBackgroundTintList(binding.tvEmployeeReservationStatus, ColorStateList.valueOf(ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiDatBan(datBan.layTrangThai()))));

            binding.btnEmployeeReservationConfirm.setText(R.string.employee_reservation_action_confirm);
            binding.btnEmployeeReservationComplete.setText(R.string.employee_reservation_action_complete);
            binding.btnEmployeeReservationChangeTable.setText(R.string.employee_reservation_action_change_table);
            ganHanhDong(binding.btnEmployeeReservationConfirm, HanhDongNghiepVuHelper.nhanVienCoTheXacNhanDatBan(datBan), v -> hanhDongListener.khiXacNhan(datBan));
            ganHanhDong(binding.btnEmployeeReservationComplete, HanhDongNghiepVuHelper.nhanVienCoTheHoanTatDatBan(datBan) && datBan.layIdDonHangLienKet() > 0, v -> hanhDongListener.khiHoanTat(datBan));
            ganHanhDong(binding.btnEmployeeReservationChangeTable, HanhDongNghiepVuHelper.nhanVienCoTheDoiBan(datBan), v -> hanhDongListener.khiDoiBan(datBan));
            ganHanhDong(binding.btnEmployeeReservationCancel,
                    HanhDongNghiepVuHelper.nhanVienCoTheHuyDatBan(datBan),
                    v -> hanhDongListener.khiHuy(datBan));
        }

        private void ganHanhDong(TextView view, boolean hienThi, View.OnClickListener suKienClick) {
            view.setVisibility(hienThi ? View.VISIBLE : View.GONE);
            view.setOnClickListener(hienThi ? suKienClick : null);
        }
    }

}
