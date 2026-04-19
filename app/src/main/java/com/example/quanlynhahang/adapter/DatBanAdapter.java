package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.DatBan;

import java.util.ArrayList;
import java.util.List;

public class DatBanAdapter extends RecyclerView.Adapter<DatBanAdapter.ViewHolderDatBan> {

    public interface OnHuyDatBanClickListener {
        void onHuyDatBan(DatBan datBan, int viTri);
    }

    private final List<DatBan> danhSachDatBan = new ArrayList<>();
    private final OnHuyDatBanClickListener onHuyDatBanClickListener;

    public DatBanAdapter(List<DatBan> danhSachDatBan,
                         OnHuyDatBanClickListener onHuyDatBanClickListener) {
        this.danhSachDatBan.addAll(danhSachDatBan);
        this.onHuyDatBanClickListener = onHuyDatBanClickListener;
    }

    public void capNhatDanhSachDatBan(List<DatBan> danhSachMoi) {
        danhSachDatBan.clear();
        danhSachDatBan.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    public void themDatBan(DatBan datBan) {
        danhSachDatBan.add(0, datBan);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ViewHolderDatBan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trang_thai_dat_ban, parent, false);
        return new ViewHolderDatBan(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatBan holder, int position) {
        holder.ganDuLieu(danhSachDatBan.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachDatBan.size();
    }

    class ViewHolderDatBan extends RecyclerView.ViewHolder {
        private final TextView tvReservationTime;
        private final TextView tvReservationCode;
        private final TextView tvReservationTable;
        private final TextView tvReservationGuestCount;
        private final TextView tvReservationNote;
        private final TextView tvReservationStatus;
        private final Button btnCancelReservation;

        ViewHolderDatBan(@NonNull View itemView) {
            super(itemView);
            tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
            tvReservationCode = itemView.findViewById(R.id.tvReservationCode);
            tvReservationTable = itemView.findViewById(R.id.tvReservationTable);
            tvReservationGuestCount = itemView.findViewById(R.id.tvReservationGuestCount);
            tvReservationNote = itemView.findViewById(R.id.tvReservationNote);
            tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
            btnCancelReservation = itemView.findViewById(R.id.btnCancelReservation);
        }

        void ganDuLieu(DatBan datBan) {
            Context context = itemView.getContext();
            tvReservationTime.setText(datBan.layThoiGian());
            tvReservationCode.setText(datBan.coMaDatBan()
                    ? context.getString(R.string.reservation_code_format, datBan.layMaDatBan())
                    : "");
            tvReservationCode.setVisibility(datBan.coMaDatBan() ? View.VISIBLE : View.GONE);
            tvReservationTable.setText(
                    context.getString(R.string.reservation_table_format_display, datBan.laySoBan())
            );
            tvReservationGuestCount.setText(
                    context.getString(R.string.reservation_guest_count_format, datBan.laySoKhach())
            );

            if (datBan.coGhiChu()) {
                tvReservationNote.setText(context.getString(R.string.reservation_note_format, datBan.layGhiChu()));
            } else {
                tvReservationNote.setText(context.getString(R.string.reservation_note_empty));
            }

            tvReservationStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiDatBan(datBan.layTrangThai()));
            int mauTrangThai = ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiDatBan(datBan.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvReservationStatus, ColorStateList.valueOf(mauTrangThai));

            btnCancelReservation.setVisibility(datBan.coTheHuy() ? View.VISIBLE : View.GONE);
            btnCancelReservation.setOnClickListener(v -> {
                int viTriAdapter = getBindingAdapterPosition();
                if (viTriAdapter == RecyclerView.NO_POSITION || !datBan.coTheHuy()) {
                    return;
                }
                onHuyDatBanClickListener.onHuyDatBan(datBan, viTriAdapter);
            });
        }
    }

}
