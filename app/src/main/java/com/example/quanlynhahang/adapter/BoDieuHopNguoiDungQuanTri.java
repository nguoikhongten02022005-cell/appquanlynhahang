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
import com.example.quanlynhahang.model.NguoiDung;

import java.util.ArrayList;
import java.util.List;

public class BoDieuHopNguoiDungQuanTri extends RecyclerView.Adapter<BoDieuHopNguoiDungQuanTri.ViewHolderNguoiDungQuanTri> {

    public interface HanhDongListener {
        void khiDoiVaiTro(NguoiDung nguoiDung);

        void khiBatTatTrangThaiHoatDong(NguoiDung nguoiDung);
    }

    private final List<NguoiDung> danhSachNguoiDung = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public BoDieuHopNguoiDungQuanTri(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<NguoiDung> danhSachMoi) {
        danhSachNguoiDung.clear();
        danhSachNguoiDung.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderNguoiDungQuanTri onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolderNguoiDungQuanTri(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNguoiDungQuanTri holder, int position) {
        holder.ganDuLieu(danhSachNguoiDung.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachNguoiDung.size();
    }

    class ViewHolderNguoiDungQuanTri extends RecyclerView.ViewHolder {
        private final TextView tvTen;
        private final TextView tvEmail;
        private final TextView tvSoDienThoai;
        private final TextView tvVaiTro;
        private final TextView tvTrangThai;
        private final TextView btnDoiVaiTro;
        private final TextView btnBatTat;

        ViewHolderNguoiDungQuanTri(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvAdminUserName);
            tvEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            tvSoDienThoai = itemView.findViewById(R.id.tvAdminUserPhone);
            tvVaiTro = itemView.findViewById(R.id.tvAdminVaiTro);
            tvTrangThai = itemView.findViewById(R.id.tvAdminUserStatus);
            btnDoiVaiTro = itemView.findViewById(R.id.btnAdminDoiVaiTro);
            btnBatTat = itemView.findViewById(R.id.btnAdminUserToggleActive);
        }

        void ganDuLieu(NguoiDung nguoiDung) {
            Context context = itemView.getContext();
            tvTen.setText(nguoiDung.layHoTen());
            tvEmail.setText(nguoiDung.layEmail());
            tvSoDienThoai.setText(context.getString(R.string.admin_user_phone_format, nguoiDung.laySoDienThoai()));
            tvVaiTro.setText(context.getString(R.string.admin_user_role_format, layNhanVaiTro(nguoiDung)));
            tvTrangThai.setText(nguoiDung.dangHoatDong() ? R.string.admin_user_status_active : R.string.admin_user_status_locked);
            ViewCompat.setBackgroundTintList(tvTrangThai, ColorStateList.valueOf(ContextCompat.getColor(context, nguoiDung.dangHoatDong() ? R.color.success : R.color.error)));
            btnBatTat.setText(nguoiDung.dangHoatDong() ? R.string.admin_lock_user : R.string.admin_unlock_user);
            btnDoiVaiTro.setOnClickListener(v -> hanhDongListener.khiDoiVaiTro(nguoiDung));
            btnBatTat.setOnClickListener(v -> hanhDongListener.khiBatTatTrangThaiHoatDong(nguoiDung));
        }

        private int layNhanVaiTro(NguoiDung nguoiDung) {
            if (nguoiDung.laAdmin()) {
                return R.string.admin_role_admin;
            }
            if (nguoiDung.laNhanVien()) {
                return R.string.admin_role_employee;
            }
            return R.string.admin_role_customer;
        }
    }
}
