package com.example.quanlynhahang.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemDanhMucMonBinding;
import com.example.quanlynhahang.model.DanhMucMon;

import java.util.List;

public class DanhMucMonAdapter extends RecyclerView.Adapter<DanhMucMonAdapter.CategoryViewHolder> {

    public interface OnDanhMucClickListener {
        void onDanhMucClick(DanhMucMon item, int position);
    }

    private static final int KHONG_CO_DANH_MUC_DANG_CHON = -1;

    private final List<DanhMucMon> danhSachDanhMuc;
    private final OnDanhMucClickListener onDanhMucClickListener;
    private int viTriDangChon;

    public DanhMucMonAdapter(List<DanhMucMon> danhSachDanhMuc,
                           OnDanhMucClickListener onDanhMucClickListener,
                           int viTriDangChon) {
        this.danhSachDanhMuc = danhSachDanhMuc;
        this.onDanhMucClickListener = onDanhMucClickListener;
        this.viTriDangChon = isViTriHopLe(viTriDangChon) ? viTriDangChon : KHONG_CO_DANH_MUC_DANG_CHON;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDanhMucMonBinding binding = ItemDanhMucMonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        DanhMucMon danhMuc = danhSachDanhMuc.get(position);
        boolean dangChon = position == viTriDangChon;

        holder.binding.ivCategoryIcon.setImageResource(danhMuc.layIdIconTaiNguyen());
        holder.binding.tvCategoryName.setText(danhMuc.layTenHienThi());

        holder.itemView.setSelected(dangChon);
        holder.binding.categoryIconContainer.setSelected(dangChon);

        int mauIcon = ContextCompat.getColor(
                holder.itemView.getContext(),
                dangChon ? R.color.category_selected_icon : R.color.category_unselected_icon
        );
        int mauText = ContextCompat.getColor(
                holder.itemView.getContext(),
                dangChon ? R.color.on_surface : R.color.on_surface_variant
        );

        holder.binding.ivCategoryIcon.setImageTintList(ColorStateList.valueOf(mauIcon));
        holder.binding.tvCategoryName.setTextColor(mauText);

        holder.itemView.setOnClickListener(v -> {
            int viTriAdapter = holder.getBindingAdapterPosition();
            if (viTriAdapter == RecyclerView.NO_POSITION) {
                return;
            }
            capNhatViTriDangChon(viTriAdapter);
            if (onDanhMucClickListener != null) {
                onDanhMucClickListener.onDanhMucClick(danhSachDanhMuc.get(viTriAdapter), viTriAdapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachDanhMuc.size();
    }

    public void capNhatViTriDangChon(int viTriMoiDuocChon) {
        int viTriMoi = isViTriHopLe(viTriMoiDuocChon) ? viTriMoiDuocChon : KHONG_CO_DANH_MUC_DANG_CHON;
        if (viTriMoi == viTriDangChon) {
            return;
        }

        int viTriCu = viTriDangChon;
        viTriDangChon = viTriMoi;

        if (isViTriHopLe(viTriCu)) {
            notifyItemChanged(viTriCu);
        }
        if (isViTriHopLe(viTriDangChon)) {
            notifyItemChanged(viTriDangChon);
        }
    }

    private boolean isViTriHopLe(int position) {
        return position >= 0 && position < danhSachDanhMuc.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemDanhMucMonBinding binding;

        CategoryViewHolder(@NonNull ItemDanhMucMonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
