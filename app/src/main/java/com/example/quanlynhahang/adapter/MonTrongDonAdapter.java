package com.example.quanlynhahang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemMonTrongDonBinding;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.List;

public class MonTrongDonAdapter extends RecyclerView.Adapter<MonTrongDonAdapter.MonTrongDonViewHolder> {

    private final List<DonHang.MonTrongDon> danhSachMon = new ArrayList<>();

    public MonTrongDonAdapter(List<DonHang.MonTrongDon> danhSachMon) {
        if (danhSachMon != null) {
            this.danhSachMon.addAll(danhSachMon);
        }
    }

    public void capNhatDuLieu(List<DonHang.MonTrongDon> danhSachMoi) {
        danhSachMon.clear();
        if (danhSachMoi != null) {
            danhSachMon.addAll(danhSachMoi);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonTrongDonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMonTrongDonBinding binding = ItemMonTrongDonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MonTrongDonViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MonTrongDonViewHolder holder, int position) {
        DonHang.MonTrongDon monDat = danhSachMon.get(position);
        if (monDat == null || monDat.layMonAn() == null) {
            holder.binding.ivMonTrongDonImage.setImageResource(R.drawable.menu_1);
            holder.binding.tvMonTrongDonName.setText("");
            holder.binding.tvMonTrongDonQuantity.setText(
                    holder.itemView.getContext().getString(
                            R.string.order_quantity_format,
                            0
                    )
            );
            holder.binding.tvMonTrongDonPrice.setText(
                    holder.itemView.getContext().getString(
                            R.string.order_price_format,
                            "0"
                    )
            );
            holder.binding.layoutMonTrongDonActions.setVisibility(View.GONE);
            holder.binding.btnMonTrongDonIncrease.setOnClickListener(null);
            holder.binding.btnMonTrongDonDecrease.setOnClickListener(null);
            return;
        }

        holder.binding.ivMonTrongDonImage.setImageResource(monDat.layMonAn().layIdAnhTaiNguyen());
        holder.binding.tvMonTrongDonName.setText(monDat.layMonAn().layTenMon());
        holder.binding.tvMonTrongDonQuantity.setText(
                holder.itemView.getContext().getString(
                        R.string.order_quantity_format,
                        monDat.laySoLuong()
                )
        );
        holder.binding.tvMonTrongDonPrice.setText(
                holder.itemView.getContext().getString(
                        R.string.order_price_format,
                        monDat.layMonAn().layGiaBan()
                )
        );

        holder.binding.layoutMonTrongDonActions.setVisibility(View.GONE);
        holder.binding.btnMonTrongDonIncrease.setOnClickListener(null);
        holder.binding.btnMonTrongDonDecrease.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    static class MonTrongDonViewHolder extends RecyclerView.ViewHolder {
        private final ItemMonTrongDonBinding binding;

        MonTrongDonViewHolder(@NonNull ItemMonTrongDonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
