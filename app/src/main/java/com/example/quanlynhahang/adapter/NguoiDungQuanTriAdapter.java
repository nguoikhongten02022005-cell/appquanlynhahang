package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemAdminUserBinding;
import com.example.quanlynhahang.model.NguoiDung;

import java.util.ArrayList;
import java.util.List;

public class NguoiDungQuanTriAdapter extends RecyclerView.Adapter<NguoiDungQuanTriAdapter.ViewHolderNguoiDungQuanTri> {

    public interface HanhDongListener {
        void khiSua(NguoiDung nguoiDung);

        void khiXoa(NguoiDung nguoiDung);

        void khiBatTatTrangThaiHoatDong(NguoiDung nguoiDung);
    }

    private final List<NguoiDung> danhSachNguoiDung = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public NguoiDungQuanTriAdapter(HanhDongListener hanhDongListener) {
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
        ItemAdminUserBinding binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderNguoiDungQuanTri(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNguoiDungQuanTri holder, int position) {
        holder.ganDuLieu(danhSachNguoiDung.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachNguoiDung.size();
    }

    public static String taoChuCaiDaiDien(NguoiDung nguoiDung) {
        return taoChuCaiDaiDienTuTen(nguoiDung.layHoTen());
    }

    private static String taoChuCaiDaiDienTuTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return "ND";
        }
        String[] phanTen = hoTen.trim().split("\\s+");
        if (phanTen.length == 1) {
            String ten = phanTen[0];
            return ten.substring(0, Math.min(2, ten.length())).toUpperCase();
        }
        String kyTuDau = phanTen[0].substring(0, 1);
        String kyTuThuHai = phanTen[1].substring(0, 1);
        return (kyTuDau + kyTuThuHai).toUpperCase();
    }

    class ViewHolderNguoiDungQuanTri extends RecyclerView.ViewHolder {
        private final ItemAdminUserBinding binding;

        ViewHolderNguoiDungQuanTri(@NonNull ItemAdminUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void ganDuLieu(NguoiDung nguoiDung) {
            Context context = itemView.getContext();
            int mauTrangThai = ContextCompat.getColor(context, nguoiDung.dangHoatDong() ? R.color.success : R.color.error);

            binding.tvAdminUserName.setText(nguoiDung.layHoTen());
            binding.tvAdminUserEmail.setText(nguoiDung.layEmail());
            binding.tvAdminUserPhone.setText(context.getString(R.string.admin_user_phone_format, nguoiDung.laySoDienThoai()));
            binding.tvAdminVaiTro.setText(layNhanVaiTroHienThi(context, nguoiDung));
            binding.tvAdminUserStatus.setText(nguoiDung.dangHoatDong() ? R.string.admin_user_status_active : R.string.admin_user_status_locked);
            binding.tvAdminUserAvatar.setText(layChuCaiDaiDien(nguoiDung.layHoTen()));

            GradientDrawable nenAvatar = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.bg_admin_user_avatar).mutate();
            nenAvatar.setColor(Color.parseColor(taoMauNenAvatar(nguoiDung)));
            binding.tvAdminUserAvatar.setBackground(nenAvatar);
            binding.tvAdminUserAvatar.setTextColor(Color.parseColor(taoMauChuAvatar(nguoiDung)));

            ViewCompat.setBackgroundTintList(binding.viewAdminUserStatusDot, ColorStateList.valueOf(mauTrangThai));
            binding.btnAdminUserToggle.setText(nguoiDung.dangHoatDong() ? R.string.admin_user_lock_account : R.string.admin_user_unlock_account);
            binding.btnAdminUserEdit.setOnClickListener(v -> hanhDongListener.khiSua(nguoiDung));
            binding.btnAdminUserDelete.setOnClickListener(v -> hanhDongListener.khiXoa(nguoiDung));
            binding.btnAdminUserToggle.setOnClickListener(v -> hanhDongListener.khiBatTatTrangThaiHoatDong(nguoiDung));
            itemView.setOnClickListener(v -> hanhDongListener.khiSua(nguoiDung));
        }

        private String layNhanVaiTroHienThi(Context context, NguoiDung nguoiDung) {
            if (nguoiDung.laAdmin()) {
                return context.getString(R.string.admin_user_role_admin_short);
            }
            if (nguoiDung.laNhanVien()) {
                return context.getString(R.string.admin_user_role_employee_short);
            }
            return context.getString(R.string.admin_role_customer);
        }

        private String layChuCaiDaiDien(String hoTen) {
            return taoChuCaiDaiDienTuTen(hoTen);
        }

        private String taoMauNenAvatar(NguoiDung nguoiDung) {
            if (!nguoiDung.dangHoatDong()) {
                return "#EFE9E2";
            }
            if (nguoiDung.laAdmin()) {
                return "#F3D7CA";
            }
            if (nguoiDung.laNhanVien()) {
                return "#DFF0E4";
            }
            return "#E5F3FF";
        }

        private String taoMauChuAvatar(NguoiDung nguoiDung) {
            if (!nguoiDung.dangHoatDong()) {
                return "#6F655C";
            }
            if (nguoiDung.laAdmin()) {
                return "#B9653A";
            }
            if (nguoiDung.laNhanVien()) {
                return "#4A8A5A";
            }
            return "#418AD6";
        }
    }
}
