package com.example.quanlynhahang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.List;

public class MonTrongDonAdapter extends RecyclerView.Adapter<MonTrongDonAdapter.MonTrongDonViewHolder> {

    private final List<DonHang.MonTrongDon> danhSachMon = new ArrayList<>();

    public MonTrongDonAdapter(List<DonHang.MonTrongDon> danhSachMon) {
        this.danhSachMon.addAll(danhSachMon);
    }

    public void capNhatDuLieu(List<DonHang.MonTrongDon> danhSachMoi) {
        danhSachMon.clear();
        danhSachMon.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonTrongDonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mon_trong_don, parent, false);
        return new MonTrongDonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonTrongDonViewHolder holder, int position) {
        DonHang.MonTrongDon monDat = danhSachMon.get(position);

        holder.ivMonTrongDonImage.setImageResource(monDat.layMonAn().layImageResId());
        holder.tvMonTrongDonName.setText(monDat.layMonAn().layTen());
        holder.tvMonTrongDonQuantity.setText(
                holder.itemView.getContext().getString(
                        R.string.order_quantity_format,
                        monDat.laySoLuong()
                )
        );
        holder.tvMonTrongDonPrice.setText(
                holder.itemView.getContext().getString(
                        R.string.order_price_format,
                        monDat.layMonAn().layGia()
                )
        );

        holder.layoutMonTrongDonActions.setVisibility(View.GONE);
        holder.btnMonTrongDonIncrease.setOnClickListener(null);
        holder.btnMonTrongDonDecrease.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    static class MonTrongDonViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMonTrongDonImage;
        private final TextView tvMonTrongDonName;
        private final TextView tvMonTrongDonQuantity;
        private final TextView tvMonTrongDonPrice;
        private final LinearLayout layoutMonTrongDonActions;
        private final ImageButton btnMonTrongDonIncrease;
        private final ImageButton btnMonTrongDonDecrease;

        MonTrongDonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMonTrongDonImage = itemView.findViewById(R.id.ivMonTrongDonImage);
            tvMonTrongDonName = itemView.findViewById(R.id.tvMonTrongDonName);
            tvMonTrongDonQuantity = itemView.findViewById(R.id.tvMonTrongDonQuantity);
            tvMonTrongDonPrice = itemView.findViewById(R.id.tvMonTrongDonPrice);
            layoutMonTrongDonActions = itemView.findViewById(R.id.layoutMonTrongDonActions);
            btnMonTrongDonIncrease = itemView.findViewById(R.id.btnMonTrongDonIncrease);
            btnMonTrongDonDecrease = itemView.findViewById(R.id.btnMonTrongDonDecrease);
        }
    }
}
