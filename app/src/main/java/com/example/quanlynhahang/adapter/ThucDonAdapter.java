package com.example.quanlynhahang.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemThucDonBinding;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.List;

public class ThucDonAdapter extends RecyclerView.Adapter<ThucDonAdapter.MenuViewHolder> {

    public interface OnThemMonClickListener {
        void onThemMonClick(MonAnDeXuat monAn);
    }

    private final List<MonAnDeXuat> danhSachMon = new ArrayList<>();
    private final List<String> danhSachMoTa = new ArrayList<>();
    private final List<String> danhSachTenAnh = new ArrayList<>();
    private final OnThemMonClickListener onThemMonClickListener;

    public ThucDonAdapter(List<MonAnDeXuat> danhSachMon,
                       List<String> danhSachMoTa,
                       OnThemMonClickListener onThemMonClickListener) {
        this(danhSachMon, danhSachMoTa, new ArrayList<>(), onThemMonClickListener);
    }

    public ThucDonAdapter(List<MonAnDeXuat> danhSachMon,
                       List<String> danhSachMoTa,
                       List<String> danhSachTenAnh,
                       OnThemMonClickListener onThemMonClickListener) {
        this.danhSachMon.addAll(danhSachMon);
        this.danhSachMoTa.addAll(danhSachMoTa);
        this.danhSachTenAnh.addAll(danhSachTenAnh);
        this.onThemMonClickListener = onThemMonClickListener;
    }

    public void capNhatDuLieu(List<MonAnDeXuat> danhSachMonMoi, List<String> danhSachMoTaMoi) {
        capNhatDuLieu(danhSachMonMoi, danhSachMoTaMoi, new ArrayList<>());
    }

    public void capNhatDuLieu(List<MonAnDeXuat> danhSachMonMoi, List<String> danhSachMoTaMoi, List<String> danhSachTenAnhMoi) {
        danhSachMon.clear();
        danhSachMoTa.clear();
        danhSachTenAnh.clear();
        danhSachMon.addAll(danhSachMonMoi);
        danhSachMoTa.addAll(danhSachMoTaMoi);
        danhSachTenAnh.addAll(danhSachTenAnhMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemThucDonBinding binding = ItemThucDonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MonAnDeXuat monAn = danhSachMon.get(position);

        hienAnhMon(holder.binding.ivMenuDishImage, monAn, position);
        holder.binding.tvMenuDishName.setText(monAn.layTenMon());
        holder.binding.tvMenuDishDescription.setText(danhSachMoTa.get(position));
        holder.binding.tvMenuDishPrice.setText(monAn.layGiaBan());
        holder.binding.tvMenuDishMeta.setText(monAn.layTenDanhMuc());
        holder.binding.tvMenuDishAvailability.setText(monAn.laConPhucVu()
                ? R.string.dish_status_available
                : R.string.dish_status_unavailable);
        holder.binding.tvMenuDishAvailability.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(),
                monAn.laConPhucVu() ? R.color.success : R.color.error
        ));

        holder.binding.btnMenuAddDish.setEnabled(monAn.laConPhucVu());
        holder.binding.btnMenuAddDish.setAlpha(monAn.laConPhucVu() ? 1f : 0.5f);
        holder.binding.btnMenuAddDish.setOnClickListener(v -> {
            if (monAn.laConPhucVu()) {
                onThemMonClickListener.onThemMonClick(monAn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    private void hienAnhMon(ImageView imageView, MonAnDeXuat monAn, int position) {
        String tenAnh = position < danhSachTenAnh.size() ? danhSachTenAnh.get(position) : "";
        if (!TextUtils.isEmpty(tenAnh) && tenAnh.startsWith("content://")) {
            imageView.setImageURI(Uri.parse(tenAnh));
            return;
        }
        int idAnh = monAn.layIdAnhTaiNguyen();
        if (idAnh == 0) {
            imageView.setImageDrawable(null);
            imageView.setBackgroundResource(R.drawable.bg_dish_image_placeholder);
            return;
        }
        imageView.setImageResource(idAnh);
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ItemThucDonBinding binding;

        MenuViewHolder(@NonNull ItemThucDonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
