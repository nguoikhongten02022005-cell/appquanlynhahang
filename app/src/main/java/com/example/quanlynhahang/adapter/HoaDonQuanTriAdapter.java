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
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.List;

public class HoaDonQuanTriAdapter extends RecyclerView.Adapter<HoaDonQuanTriAdapter.ViewHolderHoaDonQuanTri> {

    public interface HanhDongListener {
        void khiXacNhanDaThanhToan(DonHang donHang);
    }

    private final List<DonHang> danhSachHoaDon = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public HoaDonQuanTriAdapter(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<DonHang> danhSachMoi) {
        danhSachHoaDon.clear();
        danhSachHoaDon.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderHoaDonQuanTri onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_invoice, parent, false);
        return new ViewHolderHoaDonQuanTri(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderHoaDonQuanTri holder, int position) {
        holder.ganDuLieu(danhSachHoaDon.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachHoaDon.size();
    }

    class ViewHolderHoaDonQuanTri extends RecyclerView.ViewHolder {
        private final TextView tvMaHoaDon;
        private final TextView tvMeta;
        private final TextView tvTongTien;
        private final TextView tvTrangThai;

        ViewHolderHoaDonQuanTri(@NonNull View itemView) {
            super(itemView);
            tvMaHoaDon = itemView.findViewById(R.id.tvAdminInvoiceCode);
            tvMeta = itemView.findViewById(R.id.tvAdminInvoiceMeta);
            tvTongTien = itemView.findViewById(R.id.tvAdminInvoiceAmount);
            tvTrangThai = itemView.findViewById(R.id.tvAdminInvoiceStatus);
        }

        void ganDuLieu(DonHang donHang) {
            Context context = itemView.getContext();
            tvMaHoaDon.setText(donHang.layMaDon());
            tvMeta.setText((donHang.coBanAn() ? chuanHoaNhanBan(donHang.laySoBan()) : context.getString(R.string.admin_takeaway_label))
                    + " · " + donHang.layThoiGian());
            tvTongTien.setText(donHang.layTongTien());

            boolean daThanhToan = donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_THANH_TOAN;
            boolean coTheXacNhan = !daThanhToan && donHang.layTrangThai() != DonHang.TrangThai.DA_HUY;
            int mauNen = ContextCompat.getColor(context, daThanhToan ? R.color.admin_metric_green_bg : R.color.admin_metric_yellow_bg);
            int mauChu = ContextCompat.getColor(context, daThanhToan ? R.color.success : R.color.admin_warning);
            ViewCompat.setBackgroundTintList(tvTrangThai, ColorStateList.valueOf(mauNen));
            tvTrangThai.setTextColor(mauChu);
            tvTrangThai.setText(layTextTrangThaiHoaDon(context, donHang));
            itemView.setAlpha(donHang.layTrangThai() == DonHang.TrangThai.DA_HUY ? 0.65f : 1f);

            View.OnClickListener onClickListener = coTheXacNhan ? v -> hanhDongListener.khiXacNhanDaThanhToan(donHang) : null;
            itemView.setOnClickListener(onClickListener);
            tvTrangThai.setOnClickListener(onClickListener);
        }

        private String chuanHoaNhanBan(String soBanRaw) {
            if (soBanRaw == null || soBanRaw.trim().isEmpty()) {
                return "Tại bàn";
            }
            String soBan = soBanRaw.trim();
            if (soBan.toLowerCase(new java.util.Locale("vi", "VN")).startsWith("bàn")) {
                return soBan;
            }
            return "Bàn " + soBan;
        }

        private String layTextTrangThaiHoaDon(Context context, DonHang donHang) {
            if (donHang.layTrangThai() == DonHang.TrangThai.DA_HUY) {
                return context.getString(R.string.admin_status_cancelled);
            }
            if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_THANH_TOAN) {
                if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUYEN_KHOAN_NGAN_HANG) {
                    return context.getString(R.string.admin_invoice_method_bank);
                }
                if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.VI_DIEN_TU) {
                    return context.getString(R.string.admin_invoice_method_digital);
                }
                return context.getString(R.string.order_payment_status_paid);
            }
            if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_GOI_THANH_TOAN) {
                return context.getString(R.string.order_payment_status_requested);
            }
            return context.getString(R.string.order_payment_status_unpaid);
        }
    }
}
