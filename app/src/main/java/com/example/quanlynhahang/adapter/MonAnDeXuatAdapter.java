package com.example.quanlynhahang.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MonAnDeXuatAdapter extends RecyclerView.Adapter<MonAnDeXuatAdapter.RecommendedDishViewHolder> {

    public interface HanhDongMonListener {
        void khiChonMon(MonAnDeXuat item);

        void khiThemMon(MonAnDeXuat item);
    }

    private final List<MonAnDeXuat> danhSachMon;
    private final HanhDongMonListener hanhDongMonListener;

    public MonAnDeXuatAdapter(List<MonAnDeXuat> danhSachMon,
                                   HanhDongMonListener hanhDongMonListener) {
        this.danhSachMon = danhSachMon;
        this.hanhDongMonListener = hanhDongMonListener;
    }

    @NonNull
    @Override
    public RecommendedDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mon_an_de_xuat, parent, false);
        return new RecommendedDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedDishViewHolder holder, int position) {
        MonAnDeXuat monAn = danhSachMon.get(position);
        boolean conPhucVu = monAn.laConPhucVu();

        holder.ivDishImage.setImageResource(monAn.layImageResId());
        holder.tvDishName.setText(monAn.layTenMon());
        holder.tvDishPrice.setText(monAn.layGiaBan());
        holder.tvDishStatus.setText(conPhucVu ? R.string.dish_status_available : R.string.dish_status_unavailable);
        holder.tvDishStatus.setBackgroundResource(
                conPhucVu ? R.drawable.bg_status_available : R.drawable.bg_status_unavailable
        );
        holder.tvDishStatus.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(),
                conPhucVu ? R.color.status_available_text : R.color.status_unavailable_text
        ));

        holder.btnAddDish.setEnabled(conPhucVu);
        holder.btnAddDish.setAlpha(conPhucVu ? 1f : 0.85f);
        holder.btnAddDish.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                holder.itemView.getContext(),
                conPhucVu ? R.color.add_button_icon_enabled : R.color.add_button_icon_disabled
        )));

        View.OnClickListener suKienClickMon = v -> {
            if (hanhDongMonListener != null) {
                hanhDongMonListener.khiChonMon(monAn);
            }
        };
        holder.cardRecommendedDish.setOnClickListener(suKienClickMon);
        holder.itemView.setOnClickListener(suKienClickMon);

        holder.btnAddDish.setOnClickListener(v -> {
            if (conPhucVu && hanhDongMonListener != null) {
                hanhDongMonListener.khiThemMon(monAn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    static class RecommendedDishViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardRecommendedDish;
        private final ImageView ivDishImage;
        private final TextView tvDishName;
        private final TextView tvDishPrice;
        private final TextView tvDishStatus;
        private final ImageButton btnAddDish;

        RecommendedDishViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRecommendedDish = itemView.findViewById(R.id.cardRecommendedDish);
            ivDishImage = itemView.findViewById(R.id.ivDishImage);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvDishPrice = itemView.findViewById(R.id.tvDishPrice);
            tvDishStatus = itemView.findViewById(R.id.tvDishStatus);
            btnAddDish = itemView.findViewById(R.id.btnAddDish);
        }
    }
}
