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

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder> {

    public interface HanhDongListener {
        void khiDoiVaiTro(NguoiDung user);

        void khiBatTatTrangThaiHoatDong(NguoiDung user);
    }

    private final List<NguoiDung> danhSachNguoiDung = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public AdminUserAdapter(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<NguoiDung> danhSachMoi) {
        danhSachNguoiDung.clear();
        danhSachNguoiDung.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        holder.ganDuLieu(danhSachNguoiDung.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachNguoiDung.size();
    }

    class AdminUserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvPhone;
        private final TextView tvRole;
        private final TextView tvStatus;
        private final TextView btnRole;
        private final TextView btnToggle;

        AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminUserName);
            tvEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            tvPhone = itemView.findViewById(R.id.tvAdminUserPhone);
            tvRole = itemView.findViewById(R.id.tvAdminVaiTroNguoiDung);
            tvStatus = itemView.findViewById(R.id.tvAdminUserStatus);
            btnRole = itemView.findViewById(R.id.btnAdminVaiTroNguoiDung);
            btnToggle = itemView.findViewById(R.id.btnAdminUserToggleActive);
        }

        void ganDuLieu(NguoiDung nguoiDung) {
            Context context = itemView.getContext();
            tvName.setText(nguoiDung.layTen());
            tvEmail.setText(nguoiDung.layEmail());
            tvPhone.setText(context.getString(R.string.admin_user_phone_format, nguoiDung.laySoDienThoai()));
            tvRole.setText(context.getString(R.string.admin_user_role_format, layNhanVaiTro(nguoiDung)));
            tvStatus.setText(nguoiDung.dangHoatDong() ? R.string.admin_user_status_active : R.string.admin_user_status_locked);
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, nguoiDung.dangHoatDong() ? R.color.success : R.color.error)));
            btnToggle.setText(nguoiDung.dangHoatDong() ? R.string.admin_lock_user : R.string.admin_unlock_user);
            btnRole.setOnClickListener(v -> hanhDongListener.khiDoiVaiTro(nguoiDung));
            btnToggle.setOnClickListener(v -> hanhDongListener.khiBatTatTrangThaiHoatDong(nguoiDung));
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
