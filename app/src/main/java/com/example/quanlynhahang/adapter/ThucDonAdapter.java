package com.example.quanlynhahang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.List;

public class ThucDonAdapter extends RecyclerView.Adapter<ThucDonAdapter.MenuViewHolder> {

    public interface OnThemMonClickListener {
        void onThemMonClick(MonAnDeXuat monAn);
    }

    private final List<MonAnDeXuat> danhSachMon = new ArrayList<>();
    private final List<String> danhSachMoTa = new ArrayList<>();
    private final OnThemMonClickListener onThemMonClickListener;

    public ThucDonAdapter(List<MonAnDeXuat> danhSachMon,
                       List<String> danhSachMoTa,
                       OnThemMonClickListener onThemMonClickListener) {
        this.danhSachMon.addAll(danhSachMon);
        this.danhSachMoTa.addAll(danhSachMoTa);
        this.onThemMonClickListener = onThemMonClickListener;
    }

    public void capNhatDuLieu(List<MonAnDeXuat> danhSachMonMoi, List<String> danhSachMoTaMoi) {
        danhSachMon.clear();
        danhSachMoTa.clear();
        danhSachMon.addAll(danhSachMonMoi);
        danhSachMoTa.addAll(danhSachMoTaMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thuc_don, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MonAnDeXuat monAn = danhSachMon.get(position);

        holder.ivMenuDishImage.setImageResource(monAn.layIdAnhTaiNguyen());
        holder.tvMenuDishName.setText(monAn.layTenMon());
        holder.tvMenuDishDescription.setText(danhSachMoTa.get(position));
        holder.tvMenuDishPrice.setText(monAn.layGiaBan());
        holder.tvMenuDishMeta.setText(monAn.layTenDanhMuc());
        holder.tvMenuDishAvailability.setText(monAn.laConPhucVu()
                ? R.string.dish_status_available
                : R.string.dish_status_unavailable);
        holder.tvMenuDishAvailability.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(),
                monAn.laConPhucVu() ? R.color.success : R.color.error
        ));

        holder.btnMenuAddDish.setEnabled(monAn.laConPhucVu());
        holder.btnMenuAddDish.setAlpha(monAn.laConPhucVu() ? 1f : 0.5f);
        holder.btnMenuAddDish.setOnClickListener(v -> {
            if (monAn.laConPhucVu()) {
                onThemMonClickListener.onThemMonClick(monAn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMenuDishImage;
        private final TextView tvMenuDishName;
        private final TextView tvMenuDishDescription;
        private final TextView tvMenuDishPrice;
        private final TextView tvMenuDishMeta;
        private final TextView tvMenuDishAvailability;
        private final ImageButton btnMenuAddDish;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuDishImage = itemView.findViewById(R.id.ivMenuDishImage);
            tvMenuDishName = itemView.findViewById(R.id.tvMenuDishName);
            tvMenuDishDescription = itemView.findViewById(R.id.tvMenuDishDescription);
            tvMenuDishPrice = itemView.findViewById(R.id.tvMenuDishPrice);
            tvMenuDishMeta = itemView.findViewById(R.id.tvMenuDishMeta);
            tvMenuDishAvailability = itemView.findViewById(R.id.tvMenuDishAvailability);
            btnMenuAddDish = itemView.findViewById(R.id.btnMenuAddDish);
        }
    }
}
