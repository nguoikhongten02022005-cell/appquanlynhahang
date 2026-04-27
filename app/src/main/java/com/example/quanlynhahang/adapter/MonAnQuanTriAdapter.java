package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.databinding.ItemAdminDishBinding;

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
        ItemAdminDishBinding binding = ItemAdminDishBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderMonQuanTri(binding);
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
        private final ItemAdminDishBinding binding;

        ViewHolderMonQuanTri(@NonNull ItemAdminDishBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(DatabaseHelper.DishRecord banGhiMon) {
            Context context = itemView.getContext();
            hienAnhMon(banGhiMon);
            binding.tvAdminDishName.setText(banGhiMon.layMonAn().layTenMon());
            binding.tvAdminDishPrice.setText(banGhiMon.layMonAn().layGiaBan());
            binding.tvAdminDishCategory.setText(context.getString(R.string.admin_dish_category_format, banGhiMon.layMonAn().layTenDanhMuc()));
            binding.tvAdminDishDescription.setText(banGhiMon.layMoTa());
            boolean conPhucVu = banGhiMon.layMonAn().laConPhucVu();
            binding.tvAdminDishStatus.setText(conPhucVu ? R.string.dish_status_available : R.string.dish_status_unavailable);
            ViewCompat.setBackgroundTintList(binding.tvAdminDishStatus, ColorStateList.valueOf(ContextCompat.getColor(context, conPhucVu ? R.color.success : R.color.error)));
            binding.btnAdminDishToggle.setText(conPhucVu ? R.string.admin_toggle_dish_unavailable : R.string.admin_toggle_dish_available);
            binding.btnAdminDishEdit.setOnClickListener(v -> hanhDongListener.khiSua(banGhiMon));
            binding.btnAdminDishDelete.setOnClickListener(v -> hanhDongListener.khiXoa(banGhiMon));
            binding.btnAdminDishToggle.setOnClickListener(v -> hanhDongListener.khiBatTatTrangThaiPhucVu(banGhiMon));
        }

        private void hienAnhMon(DatabaseHelper.DishRecord banGhiMon) {
            String tenAnh = banGhiMon.layTenAnhTaiNguyen();
            if (!TextUtils.isEmpty(tenAnh) && tenAnh.startsWith("content://")) {
                binding.ivAdminDishImage.setImageURI(Uri.parse(tenAnh));
                return;
            }
            int idAnh = banGhiMon.layMonAn().layIdAnhTaiNguyen();
            if (idAnh == 0) {
                binding.ivAdminDishImage.setImageDrawable(null);
                binding.ivAdminDishImage.setBackgroundResource(R.drawable.bg_dish_image_placeholder);
                return;
            }
            binding.ivAdminDishImage.setImageResource(idAnh);
        }
    }
}
