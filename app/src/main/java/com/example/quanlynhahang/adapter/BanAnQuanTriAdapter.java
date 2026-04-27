package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemBanAnQuanLyBinding;
import com.example.quanlynhahang.model.BanAn;

import java.util.ArrayList;
import java.util.List;

public class BanAnQuanTriAdapter extends RecyclerView.Adapter<BanAnQuanTriAdapter.ViewHolderBanAn> {

    public interface HanhDongListener {
        void khiXemChiTiet(BanAn banAn);

        void khiSua(BanAn banAn);

        void khiXoa(BanAn banAn);
    }

    private final List<BanAn> danhSachBan = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public BanAnQuanTriAdapter(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<BanAn> danhSachMoi) {
        danhSachBan.clear();
        danhSachBan.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderBanAn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBanAnQuanLyBinding binding = ItemBanAnQuanLyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderBanAn(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBanAn holder, int position) {
        holder.ganDuLieu(danhSachBan.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachBan.size();
    }

    class ViewHolderBanAn extends RecyclerView.ViewHolder {
        private final ItemBanAnQuanLyBinding binding;

        ViewHolderBanAn(@NonNull ItemBanAnQuanLyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(BanAn banAn) {
            Context context = itemView.getContext();
            binding.tvTenBan.setText(context.getString(R.string.quan_ly_ban_ten_va_ma_ban, banAn.layTenBan(), banAn.layMaBan()));
            binding.tvThongTinBan.setText(context.getString(R.string.quan_ly_ban_thong_tin_ban, banAn.laySoCho(), banAn.layKhuVuc()));
            binding.tvTrangThaiBan.setText(layTextTrangThai(banAn.layTrangThai()));
            ViewCompat.setBackgroundTintList(binding.tvTrangThaiBan, ColorStateList.valueOf(ContextCompat.getColor(context, layMauTrangThai(banAn.layTrangThai()))));
            binding.btnXemChiTietBan.setOnClickListener(v -> hanhDongListener.khiXemChiTiet(banAn));
            binding.btnSuaBan.setOnClickListener(v -> hanhDongListener.khiSua(banAn));
            binding.btnXoaBan.setOnClickListener(v -> hanhDongListener.khiXoa(banAn));
        }

        private int layTextTrangThai(BanAn.TrangThai trangThai) {
            if (trangThai == BanAn.TrangThai.DANG_PHUC_VU) {
                return R.string.quan_ly_ban_trang_thai_dang_phuc_vu;
            }
            if (trangThai == BanAn.TrangThai.DA_DAT) {
                return R.string.quan_ly_ban_trang_thai_da_dat;
            }
            return R.string.quan_ly_ban_trang_thai_trong;
        }

        private int layMauTrangThai(BanAn.TrangThai trangThai) {
            if (trangThai == BanAn.TrangThai.DANG_PHUC_VU) {
                return R.color.warning;
            }
            if (trangThai == BanAn.TrangThai.DA_DAT) {
                return R.color.table_booked_badge;
            }
            return R.color.success;
        }
    }
}