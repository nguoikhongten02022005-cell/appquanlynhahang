package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
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
        void khiSua(NguoiDung nguoiDung);

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

    public static String taoMoTaHienThi(NguoiDung nguoiDung) {
        return nguoiDung.layHoTen() + " · " + taoNhanVaiTro(nguoiDung) + " · " + taoTrangThaiHienThi(nguoiDung);
    }

    public static String taoNhanVaiTro(NguoiDung nguoiDung) {
        if (nguoiDung.laAdmin()) {
            return "Admin";
        }
        if (nguoiDung.laNhanVien()) {
            return "NV";
        }
        return "QL khách";
    }

    public static String taoChuCaiDaiDien(NguoiDung nguoiDung) {
        return taoChuCaiDaiDienTuTen(nguoiDung.layHoTen());
    }

    public static String taoTrangThaiHienThi(NguoiDung nguoiDung) {
        return nguoiDung.dangHoatDong() ? "online" : "offline";
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
        private final TextView tvTen;
        private final TextView tvEmail;
        private final TextView tvSoDienThoai;
        private final TextView tvVaiTro;
        private final TextView tvTrangThai;
        private final TextView tvAvatar;
        private final View viewTrangThai;
        private final TextView btnHanhDong;

        ViewHolderNguoiDungQuanTri(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvAdminUserName);
            tvEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            tvSoDienThoai = itemView.findViewById(R.id.tvAdminUserPhone);
            tvVaiTro = itemView.findViewById(R.id.tvAdminVaiTro);
            tvTrangThai = itemView.findViewById(R.id.tvAdminUserStatus);
            tvAvatar = itemView.findViewById(R.id.tvAdminUserAvatar);
            viewTrangThai = itemView.findViewById(R.id.viewAdminUserStatusDot);
            btnHanhDong = itemView.findViewById(R.id.btnAdminUserActions);
        }

        void ganDuLieu(NguoiDung nguoiDung) {
            Context context = itemView.getContext();
            int mauTrangThai = ContextCompat.getColor(context, nguoiDung.dangHoatDong() ? R.color.success : R.color.error);

            tvTen.setText(nguoiDung.layHoTen());
            tvEmail.setText(nguoiDung.layEmail());
            tvSoDienThoai.setText(context.getString(R.string.admin_user_phone_format, nguoiDung.laySoDienThoai()));
            tvVaiTro.setText(layNhanVaiTroHienThi(context, nguoiDung));
            tvTrangThai.setText(nguoiDung.dangHoatDong() ? R.string.admin_user_status_active : R.string.admin_user_status_locked);
            tvAvatar.setText(layChuCaiDaiDien(nguoiDung.layHoTen()));

            GradientDrawable nenAvatar = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.bg_admin_user_avatar).mutate();
            nenAvatar.setColor(Color.parseColor(taoMauNenAvatar(nguoiDung)));
            tvAvatar.setBackground(nenAvatar);
            tvAvatar.setTextColor(Color.parseColor(taoMauChuAvatar(nguoiDung)));

            ViewCompat.setBackgroundTintList(viewTrangThai, ColorStateList.valueOf(mauTrangThai));
            btnHanhDong.setOnClickListener(v -> hanhDongListener.khiSua(nguoiDung));
            itemView.setOnLongClickListener(v -> {
                hanhDongListener.khiSua(nguoiDung);
                return true;
            });
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
