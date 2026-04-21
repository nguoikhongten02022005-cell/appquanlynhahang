package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.BanAn;

import java.util.ArrayList;
import java.util.List;

public class BoDieuHopBanAnQuanTri extends RecyclerView.Adapter<BoDieuHopBanAnQuanTri.ViewHolderBanAnQuanTri> {

    public interface HanhDongListener {
        void khiXemChiTiet(BanAn banAn);

        void khiSua(BanAn banAn);

        void khiXoa(BanAn banAn);
    }

    private final List<BanAn> danhSachBan = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public BoDieuHopBanAnQuanTri(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<BanAn> danhSachMoi) {
        danhSachBan.clear();
        danhSachBan.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderBanAnQuanTri onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ban_an_quan_ly, parent, false);
        return new ViewHolderBanAnQuanTri(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBanAnQuanTri holder, int position) {
        holder.ganDuLieu(danhSachBan.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachBan.size();
    }

    class ViewHolderBanAnQuanTri extends RecyclerView.ViewHolder {
        private final TextView tvTenBan;
        private final TextView tvTrangThaiBan;
        private final TextView tvThongTinBan;
        private final TextView btnXemChiTietBan;
        private final TextView btnSuaBan;
        private final TextView btnXoaBan;

        ViewHolderBanAnQuanTri(@NonNull View itemView) {
            super(itemView);
            tvTenBan = itemView.findViewById(R.id.tvTenBan);
            tvTrangThaiBan = itemView.findViewById(R.id.tvTrangThaiBan);
            tvThongTinBan = itemView.findViewById(R.id.tvThongTinBan);
            btnXemChiTietBan = itemView.findViewById(R.id.btnXemChiTietBan);
            btnSuaBan = itemView.findViewById(R.id.btnSuaBan);
            btnXoaBan = itemView.findViewById(R.id.btnXoaBan);
        }

        void ganDuLieu(BanAn banAn) {
            Context context = itemView.getContext();
            tvTenBan.setText(context.getString(R.string.quan_ly_ban_ten_va_ma_ban, banAn.layTenBan(), banAn.layMaBan()));
            tvThongTinBan.setText(context.getString(R.string.quan_ly_ban_thong_tin_ban, banAn.laySoCho(), banAn.layKhuVuc()));
            tvTrangThaiBan.setText(layTextTrangThai(banAn.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvTrangThaiBan, ColorStateList.valueOf(ContextCompat.getColor(context, layMauTrangThai(banAn.layTrangThai()))));
            btnXemChiTietBan.setOnClickListener(v -> hanhDongListener.khiXemChiTiet(banAn));
            btnSuaBan.setOnClickListener(v -> hanhDongListener.khiSua(banAn));
            btnXoaBan.setOnClickListener(v -> hanhDongListener.khiXoa(banAn));
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