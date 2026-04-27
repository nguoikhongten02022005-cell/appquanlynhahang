package com.example.quanlynhahang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.data.QuanLyGioHang;
import com.example.quanlynhahang.databinding.ItemMonTrongDonBinding;

import java.util.ArrayList;
import java.util.List;

public class MonTrongGioAdapter extends RecyclerView.Adapter<MonTrongGioAdapter.ViewHolderMonTrongGio> {

    public interface OnHanhDongSoLuongListener {
        void khiTangSoLuong(QuanLyGioHang.MonTrongGio monTrongGio);

        void khiGiamSoLuong(QuanLyGioHang.MonTrongGio monTrongGio);

        void khiXoaMon(QuanLyGioHang.MonTrongGio monTrongGio);
    }

    private final List<QuanLyGioHang.MonTrongGio> danhSachMon = new ArrayList<>();
    private final OnHanhDongSoLuongListener onHanhDongSoLuongListener;

    public MonTrongGioAdapter(List<QuanLyGioHang.MonTrongGio> danhSachMon,
                              OnHanhDongSoLuongListener onHanhDongSoLuongListener) {
        if (danhSachMon != null) {
            this.danhSachMon.addAll(danhSachMon);
        }
        this.onHanhDongSoLuongListener = onHanhDongSoLuongListener;
    }

    public void capNhatDuLieu(List<QuanLyGioHang.MonTrongGio> danhSachMoi) {
        danhSachMon.clear();
        if (danhSachMoi != null) {
            danhSachMon.addAll(danhSachMoi);
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolderMonTrongGio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMonTrongDonBinding binding = ItemMonTrongDonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderMonTrongGio(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMonTrongGio holder, int position) {
        QuanLyGioHang.MonTrongGio monTrongGio = danhSachMon.get(position);

        holder.binding.ivMonTrongDonImage.setImageResource(monTrongGio.layMonAn().layIdAnhTaiNguyen());
        holder.binding.tvMonTrongDonName.setText(monTrongGio.layMonAn().layTenMon());
        holder.binding.tvMonTrongDonQuantity.setText(
                holder.itemView.getContext().getString(R.string.order_quantity_format, monTrongGio.laySoLuong())
        );
        holder.binding.tvMonTrongDonPrice.setText(
                holder.itemView.getContext().getString(R.string.order_price_format, monTrongGio.layMonAn().layGiaBan())
        );

        holder.binding.layoutMonTrongDonActions.setVisibility(View.VISIBLE);
        holder.binding.btnMonTrongDonIncrease.setOnClickListener(v -> {
            if (onHanhDongSoLuongListener != null) {
                onHanhDongSoLuongListener.khiTangSoLuong(monTrongGio);
            }
        });
        holder.binding.btnMonTrongDonDecrease.setOnClickListener(v -> {
            if (onHanhDongSoLuongListener != null) {
                onHanhDongSoLuongListener.khiGiamSoLuong(monTrongGio);
            }
        });
        holder.binding.btnMonTrongDonRemove.setOnClickListener(v -> {
            if (onHanhDongSoLuongListener != null) {
                onHanhDongSoLuongListener.khiXoaMon(monTrongGio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    static class ViewHolderMonTrongGio extends RecyclerView.ViewHolder {
        private final ItemMonTrongDonBinding binding;

        ViewHolderMonTrongGio(@NonNull ItemMonTrongDonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
