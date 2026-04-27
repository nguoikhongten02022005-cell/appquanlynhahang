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
import com.example.quanlynhahang.databinding.ItemTrangThaiDatBanBinding;
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
        ItemTrangThaiDatBanBinding binding = ItemTrangThaiDatBanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderDatBan(binding);
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
        private final ItemTrangThaiDatBanBinding binding;

        ViewHolderDatBan(@NonNull ItemTrangThaiDatBanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(DatBan datBan) {
            Context context = itemView.getContext();
            binding.tvReservationTime.setText(datBan.layThoiGian());
            binding.tvReservationCode.setText(datBan.coMaDatBan()
                    ? context.getString(R.string.reservation_code_format, datBan.layMaDatBan())
                    : "");
            binding.tvReservationCode.setVisibility(datBan.coMaDatBan() ? View.VISIBLE : View.GONE);
            binding.tvReservationTable.setText(
                    context.getString(R.string.reservation_table_format_display, datBan.laySoBan())
            );
            binding.tvReservationGuestCount.setText(
                    context.getString(R.string.reservation_guest_count_format, datBan.laySoKhach())
            );

            if (datBan.coGhiChu()) {
                binding.tvReservationNote.setText(context.getString(R.string.reservation_note_format, datBan.layGhiChu()));
            } else {
                binding.tvReservationNote.setText(context.getString(R.string.reservation_note_empty));
            }

            binding.tvReservationStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiDatBan(datBan.layTrangThai()));
            int mauTrangThai = ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiDatBan(datBan.layTrangThai()));
            ViewCompat.setBackgroundTintList(binding.tvReservationStatus, ColorStateList.valueOf(mauTrangThai));

            binding.btnCancelReservation.setVisibility(datBan.coTheHuy() ? View.VISIBLE : View.GONE);
            binding.btnCancelReservation.setOnClickListener(v -> {
                int viTriAdapter = getBindingAdapterPosition();
                if (viTriAdapter == RecyclerView.NO_POSITION || !datBan.coTheHuy()) {
                    return;
                }
                onHuyDatBanClickListener.onHuyDatBan(datBan, viTriAdapter);
            });
        }
    }

}
