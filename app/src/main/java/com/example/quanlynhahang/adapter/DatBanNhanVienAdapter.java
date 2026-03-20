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
import com.example.quanlynhahang.model.DatBan;

import java.util.ArrayList;
import java.util.List;

public class DatBanNhanVienAdapter extends RecyclerView.Adapter<DatBanNhanVienAdapter.EmployeeReservationViewHolder> {

    public interface HanhDongListener {
        void khiXacNhan(DatBan reservation);

        void khiHoanTat(DatBan reservation);

        void khiHuy(DatBan reservation);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dat_ban_nhan_vien, parent, false);
        return new EmployeeReservationViewHolder(view);
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
        private final TextView tvTime;
        private final TextView tvTable;
        private final TextView tvGuestCount;
        private final TextView tvNote;
        private final TextView tvStatus;
        private final TextView btnConfirm;
        private final TextView btnComplete;
        private final TextView btnCancel;

        EmployeeReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvEmployeeReservationTime);
            tvTable = itemView.findViewById(R.id.tvEmployeeReservationTable);
            tvGuestCount = itemView.findViewById(R.id.tvEmployeeReservationGuestCount);
            tvNote = itemView.findViewById(R.id.tvEmployeeReservationNote);
            tvStatus = itemView.findViewById(R.id.tvEmployeeReservationStatus);
            btnConfirm = itemView.findViewById(R.id.btnEmployeeReservationConfirm);
            btnComplete = itemView.findViewById(R.id.btnEmployeeReservationComplete);
            btnCancel = itemView.findViewById(R.id.btnEmployeeReservationCancel);
        }

        void ganDuLieu(DatBan datBan) {
            Context context = itemView.getContext();
            tvTime.setText(datBan.layThoiGian());
            tvTable.setText(context.getString(R.string.reservation_table_format_display, datBan.laySoBan()));
            tvGuestCount.setText(context.getString(R.string.reservation_guest_count_format, datBan.laySoKhach()));
            String ghiChu = datBan.layGhiChu();
            tvNote.setText(ghiChu == null || ghiChu.trim().isEmpty()
                    ? context.getString(R.string.reservation_note_empty)
                    : context.getString(R.string.reservation_note_format, ghiChu));
            tvStatus.setText(layTextTrangThai(datBan.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, layMauTrangThai(datBan.layTrangThai()))));

            ganHanhDong(btnConfirm, datBan.layTrangThai() == DatBan.TrangThai.PENDING_APPROVAL, v -> hanhDongListener.khiXacNhan(datBan));
            ganHanhDong(btnComplete, datBan.layTrangThai() == DatBan.TrangThai.CONFIRMED, v -> hanhDongListener.khiHoanTat(datBan));
            ganHanhDong(btnCancel, datBan.layTrangThai() == DatBan.TrangThai.PENDING_APPROVAL || datBan.layTrangThai() == DatBan.TrangThai.CONFIRMED, v -> hanhDongListener.khiHuy(datBan));
        }

        private void ganHanhDong(TextView view, boolean hienThi, View.OnClickListener suKienClick) {
            view.setVisibility(hienThi ? View.VISIBLE : View.GONE);
            view.setOnClickListener(hienThi ? suKienClick : null);
        }
    }

    private int layTextTrangThai(DatBan.TrangThai trangThai) {
        if (trangThai == DatBan.TrangThai.PENDING_APPROVAL) {
            return R.string.reservation_status_pending;
        }
        if (trangThai == DatBan.TrangThai.CONFIRMED) {
            return R.string.reservation_status_confirmed;
        }
        if (trangThai == DatBan.TrangThai.COMPLETED) {
            return R.string.reservation_status_completed;
        }
        return R.string.reservation_status_canceled;
    }

    private int layMauTrangThai(DatBan.TrangThai trangThai) {
        if (trangThai == DatBan.TrangThai.PENDING_APPROVAL) {
            return R.color.warning;
        }
        if (trangThai == DatBan.TrangThai.CONFIRMED) {
            return R.color.success;
        }
        if (trangThai == DatBan.TrangThai.COMPLETED) {
            return R.color.primary;
        }
        return R.color.error;
    }
}
