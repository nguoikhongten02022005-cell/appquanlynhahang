package com.example.quanlynhahang.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemMonAnDeXuatBinding;
import com.example.quanlynhahang.model.MonAnDeXuat;

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
        ItemMonAnDeXuatBinding binding = ItemMonAnDeXuatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecommendedDishViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedDishViewHolder holder, int position) {
        MonAnDeXuat monAn = danhSachMon.get(position);
        boolean conPhucVu = monAn.laConPhucVu();

        holder.binding.ivDishImage.setImageResource(monAn.layIdAnhTaiNguyen());
        holder.binding.tvDishName.setText(monAn.layTenMon());
        holder.binding.tvDishPrice.setText(monAn.layGiaBan());
        holder.binding.tvDishStatus.setText(conPhucVu ? R.string.dish_status_available : R.string.dish_status_unavailable);
        holder.binding.tvDishStatus.setBackgroundResource(
                conPhucVu ? R.drawable.bg_status_available : R.drawable.bg_status_unavailable
        );
        holder.binding.tvDishStatus.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(),
                conPhucVu ? R.color.status_available_text : R.color.status_unavailable_text
        ));

        holder.binding.btnAddDish.setEnabled(conPhucVu);
        holder.binding.btnAddDish.setAlpha(conPhucVu ? 1f : 0.85f);
        holder.binding.btnAddDish.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                holder.itemView.getContext(),
                conPhucVu ? R.color.add_button_icon_enabled : R.color.add_button_icon_disabled
        )));

        View.OnClickListener suKienClickMon = v -> {
            if (hanhDongMonListener != null) {
                hanhDongMonListener.khiChonMon(monAn);
            }
        };
        holder.binding.cardRecommendedDish.setOnClickListener(suKienClickMon);
        holder.itemView.setOnClickListener(suKienClickMon);

        holder.binding.btnAddDish.setOnClickListener(v -> {
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
        private final ItemMonAnDeXuatBinding binding;

        RecommendedDishViewHolder(@NonNull ItemMonAnDeXuatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
