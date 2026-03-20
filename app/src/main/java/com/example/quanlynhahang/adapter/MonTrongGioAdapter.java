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
import com.example.quanlynhahang.data.CartManager;

import java.util.ArrayList;
import java.util.List;

public class MonTrongGioAdapter extends RecyclerView.Adapter<MonTrongGioAdapter.CartDishViewHolder> {

    public interface OnHanhDongSoLuongListener {
        void khiTangSoLuong(CartManager.CartItem item);

        void khiGiamSoLuong(CartManager.CartItem item);

        void khiXoaMon(CartManager.CartItem item);
    }

    private final List<CartManager.CartItem> danhSachMon = new ArrayList<>();
    private final OnHanhDongSoLuongListener onHanhDongSoLuongListener;

    public MonTrongGioAdapter(List<CartManager.CartItem> danhSachMon,
                             OnHanhDongSoLuongListener onHanhDongSoLuongListener) {
        this.danhSachMon.addAll(danhSachMon);
        this.onHanhDongSoLuongListener = onHanhDongSoLuongListener;
    }

    public void capNhatDuLieu(List<CartManager.CartItem> danhSachMoi) {
        danhSachMon.clear();
        danhSachMon.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mon_trong_don, parent, false);
        return new CartDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartDishViewHolder holder, int position) {
        CartManager.CartItem monTrongGio = danhSachMon.get(position);

        holder.ivDishImage.setImageResource(monTrongGio.layMonAn().layImageResId());
        holder.tvDishName.setText(monTrongGio.layMonAn().layTen());
        holder.tvDishQuantity.setText(
                holder.itemView.getContext().getString(R.string.order_quantity_format, monTrongGio.laySoLuong())
        );
        holder.tvDishPrice.setText(
                holder.itemView.getContext().getString(R.string.order_price_format, monTrongGio.layMonAn().layGia())
        );

        holder.layoutActions.setVisibility(View.VISIBLE);
        holder.btnIncrease.setOnClickListener(v -> onHanhDongSoLuongListener.khiTangSoLuong(monTrongGio));
        holder.btnDecrease.setOnClickListener(v -> onHanhDongSoLuongListener.khiGiamSoLuong(monTrongGio));
        holder.btnRemove.setOnClickListener(v -> onHanhDongSoLuongListener.khiXoaMon(monTrongGio));
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    static class CartDishViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDishImage;
        private final TextView tvDishName;
        private final TextView tvDishQuantity;
        private final TextView tvDishPrice;
        private final LinearLayout layoutActions;
        private final ImageButton btnIncrease;
        private final ImageButton btnDecrease;
        private final ImageButton btnRemove;

        CartDishViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDishImage = itemView.findViewById(R.id.ivMonTrongDonImage);
            tvDishName = itemView.findViewById(R.id.tvMonTrongDonName);
            tvDishQuantity = itemView.findViewById(R.id.tvMonTrongDonQuantity);
            tvDishPrice = itemView.findViewById(R.id.tvMonTrongDonPrice);
            layoutActions = itemView.findViewById(R.id.layoutMonTrongDonActions);
            btnIncrease = itemView.findViewById(R.id.btnMonTrongDonIncrease);
            btnDecrease = itemView.findViewById(R.id.btnMonTrongDonDecrease);
            btnRemove = itemView.findViewById(R.id.btnMonTrongDonRemove);
        }
    }
}
