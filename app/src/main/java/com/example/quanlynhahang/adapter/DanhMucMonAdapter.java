package com.example.quanlynhahang.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
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

    public DanhMucMonAdapter(List<DanhMucMon> danhSachDanhMuc, OnDanhMucClickListener onDanhMucClickListener) {
        this(danhSachDanhMuc, onDanhMucClickListener, KHONG_CO_DANH_MUC_DANG_CHON);
    }

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_danh_muc_mon, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        DanhMucMon danhMuc = danhSachDanhMuc.get(position);
        boolean dangChon = position == viTriDangChon;

        holder.ivCategoryIcon.setImageResource(danhMuc.layIdIconTaiNguyen());
        holder.tvCategoryName.setText(danhMuc.layTenHienThi());

        holder.itemView.setSelected(dangChon);
        holder.iconContainer.setSelected(dangChon);

        int mauIcon = ContextCompat.getColor(
                holder.itemView.getContext(),
                dangChon ? R.color.category_selected_icon : R.color.category_unselected_icon
        );
        int mauText = ContextCompat.getColor(
                holder.itemView.getContext(),
                dangChon ? R.color.on_surface : R.color.on_surface_variant
        );

        holder.ivCategoryIcon.setImageTintList(ColorStateList.valueOf(mauIcon));
        holder.tvCategoryName.setTextColor(mauText);

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
        private final FrameLayout iconContainer;
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.categoryIconContainer);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
