package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MonAnQuanTriAdapter extends RecyclerView.Adapter<MonAnQuanTriAdapter.ViewHolderMonQuanTri> {

    public interface HanhDongListener {
        void khiSua(DatabaseHelper.DishRecord banGhiMon);

        void khiXoa(DatabaseHelper.DishRecord banGhiMon);

        void khiBatTatTrangThaiPhucVu(DatabaseHelper.DishRecord banGhiMon);
    }

    private final List<DatabaseHelper.DishRecord> danhSachMon = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public MonAnQuanTriAdapter(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<DatabaseHelper.DishRecord> danhSachMoi) {
        danhSachMon.clear();
        danhSachMon.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderMonQuanTri onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_dish, parent, false);
        return new ViewHolderMonQuanTri(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMonQuanTri holder, int position) {
        holder.ganDuLieu(danhSachMon.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachMon.size();
    }

    class ViewHolderMonQuanTri extends RecyclerView.ViewHolder {
        private final ImageView ivAnhMon;
        private final TextView tvTen;
        private final TextView tvGia;
        private final TextView tvDanhMuc;
        private final TextView tvMoTa;
        private final TextView tvTrangThai;
        private final TextView btnSua;
        private final TextView btnXoa;
        private final TextView btnBatTat;

        ViewHolderMonQuanTri(@NonNull View itemView) {
            super(itemView);
            ivAnhMon = itemView.findViewById(R.id.ivAdminDishImage);
            tvTen = itemView.findViewById(R.id.tvAdminDishName);
            tvGia = itemView.findViewById(R.id.tvAdminDishPrice);
            tvDanhMuc = itemView.findViewById(R.id.tvAdminDishCategory);
            tvMoTa = itemView.findViewById(R.id.tvAdminDishDescription);
            tvTrangThai = itemView.findViewById(R.id.tvAdminDishStatus);
            btnSua = itemView.findViewById(R.id.btnAdminDishEdit);
            btnXoa = itemView.findViewById(R.id.btnAdminDishDelete);
            btnBatTat = itemView.findViewById(R.id.btnAdminDishToggle);
        }

        void ganDuLieu(DatabaseHelper.DishRecord banGhiMon) {
            Context context = itemView.getContext();
            hienAnhMon(banGhiMon);
            tvTen.setText(banGhiMon.layMonAn().layTenMon());
            tvGia.setText(banGhiMon.layMonAn().layGiaBan());
            tvDanhMuc.setText(context.getString(R.string.admin_dish_category_format, banGhiMon.layMonAn().layTenDanhMuc()));
            tvMoTa.setText(banGhiMon.layMoTa());
            boolean conPhucVu = banGhiMon.layMonAn().laConPhucVu();
            tvTrangThai.setText(conPhucVu ? R.string.dish_status_available : R.string.dish_status_unavailable);
            ViewCompat.setBackgroundTintList(tvTrangThai, ColorStateList.valueOf(ContextCompat.getColor(context, conPhucVu ? R.color.success : R.color.error)));
            btnBatTat.setText(conPhucVu ? R.string.admin_toggle_dish_unavailable : R.string.admin_toggle_dish_available);
            btnSua.setOnClickListener(v -> hanhDongListener.khiSua(banGhiMon));
            btnXoa.setOnClickListener(v -> hanhDongListener.khiXoa(banGhiMon));
            btnBatTat.setOnClickListener(v -> hanhDongListener.khiBatTatTrangThaiPhucVu(banGhiMon));
        }

        private void hienAnhMon(DatabaseHelper.DishRecord banGhiMon) {
            String tenAnh = banGhiMon.layTenAnhTaiNguyen();
            if (!TextUtils.isEmpty(tenAnh) && tenAnh.startsWith("content://")) {
                ivAnhMon.setImageURI(Uri.parse(tenAnh));
                return;
            }
            int idAnh = banGhiMon.layMonAn().layIdAnhTaiNguyen();
            if (idAnh == 0) {
                ivAnhMon.setImageDrawable(null);
                ivAnhMon.setBackgroundResource(R.drawable.bg_dish_image_placeholder);
                return;
            }
            ivAnhMon.setImageResource(idAnh);
        }
    }
}
