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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.databinding.ItemDonHangNhanVienBinding;
import com.example.quanlynhahang.helper.HanhDongNghiepVuHelper;
import com.example.quanlynhahang.helper.TrangThaiHienThiHelper;
import com.example.quanlynhahang.model.DonHang;

import java.util.ArrayList;
import java.util.List;

public class DonHangNhanVienAdapter extends RecyclerView.Adapter<DonHangNhanVienAdapter.DonHangNhanVienViewHolder> {

    public interface HanhDongListener {
        void khiXacNhan(DonHang order);

        void khiHoanTat(DonHang order);

        void khiHuy(DonHang order);
    }

    private final List<DonHang> danhSachDon = new ArrayList<>();
    private final HanhDongListener hanhDongListener;

    public DonHangNhanVienAdapter(HanhDongListener hanhDongListener) {
        this.hanhDongListener = hanhDongListener;
    }

    public void capNhatDanhSach(List<DonHang> danhSachMoi) {
        danhSachDon.clear();
        danhSachDon.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DonHangNhanVienViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDonHangNhanVienBinding binding = ItemDonHangNhanVienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DonHangNhanVienViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DonHangNhanVienViewHolder holder, int position) {
        holder.ganDuLieu(danhSachDon.get(position));
    }

    @Override
    public int getItemCount() {
        return danhSachDon.size();
    }

    class DonHangNhanVienViewHolder extends RecyclerView.ViewHolder {
        private final ItemDonHangNhanVienBinding binding;
        private final MonTrongDonAdapter orderDishAdapter;

        DonHangNhanVienViewHolder(@NonNull ItemDonHangNhanVienBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.rvEmployeeMonTrongDon.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            binding.rvEmployeeMonTrongDon.setNestedScrollingEnabled(false);
            orderDishAdapter = new MonTrongDonAdapter(new ArrayList<>());
            binding.rvEmployeeMonTrongDon.setAdapter(orderDishAdapter);
        }

        void ganDuLieu(DonHang donHang) {
            Context context = itemView.getContext();
            binding.tvEmployeeDonHangCode.setText(donHang.layMaDon());
            binding.tvEmployeeDonHangTime.setText(donHang.layThoiGian());
            binding.tvEmployeeDonHangTotal.setText(donHang.layTongTien());
            binding.tvEmployeeDonHangStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiDon(donHang));
            binding.tvEmployeeDonHangType.setText(donHang.laAnTaiQuan()
                    ? context.getString(R.string.order_type_dine_in)
                    : context.getString(R.string.order_type_take_away));
            binding.tvEmployeeDonHangTable.setText(donHang.coBanAn()
                    ? context.getString(R.string.order_table_format, donHang.laySoBan())
                    : context.getString(R.string.order_table_not_required));
            binding.tvEmployeeDonHangPayment.setText(layTextThanhToan(context, donHang));
            binding.tvEmployeeDonHangNote.setText(donHang.coGhiChu()
                    ? context.getString(R.string.order_note_format, donHang.layGhiChu())
                    : context.getString(R.string.order_note_empty));
            ViewCompat.setBackgroundTintList(binding.tvEmployeeDonHangStatus, ColorStateList.valueOf(ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiDon(donHang.layTrangThai()))));

            orderDishAdapter.capNhatDuLieu(donHang.layDanhSachMon());
            boolean coMon = !donHang.layDanhSachMon().isEmpty();
            binding.tvEmployeeDonHangEmptyDishes.setVisibility(coMon ? View.GONE : View.VISIBLE);
            binding.layoutEmployeeDonHangDetails.setVisibility(donHang.dangMoRong() ? View.VISIBLE : View.GONE);
            binding.tvEmployeeDonHangToggleDetails.setText(donHang.dangMoRong() ? R.string.employee_order_toggle_hide : R.string.employee_order_toggle_view);
            binding.tvEmployeeDonHangToggleDetails.setOnClickListener(v -> chuyenTrangThaiChiTiet(donHang));

            binding.btnEmployeeDonHangConfirm.setText(R.string.employee_order_action_accept);
            boolean hienThiXacNhan = HanhDongNghiepVuHelper.nhanVienCoTheNhanDon(donHang);
            boolean hienThiHoanTat = HanhDongNghiepVuHelper.nhanVienCoTheChuyenSangPhucVu(donHang)
                    || HanhDongNghiepVuHelper.nhanVienCoTheHoanTatDon(donHang);
            boolean hienThiHuy = HanhDongNghiepVuHelper.nhanVienCoTheHuyDon(donHang);

            ganHanhDong(binding.btnEmployeeDonHangConfirm, hienThiXacNhan, v -> hanhDongListener.khiXacNhan(donHang));

            binding.btnEmployeeDonHangComplete.setText(HanhDongNghiepVuHelper.layTextHanhDongChinhDon(donHang));
            ganHanhDong(binding.btnEmployeeDonHangComplete, hienThiHoanTat, v -> hanhDongListener.khiHoanTat(donHang));
            ganHanhDong(binding.btnEmployeeDonHangCancel, hienThiHuy, v -> hanhDongListener.khiHuy(donHang));
            capNhatKhoangCachNutHanhDong();
        }

        private void chuyenTrangThaiChiTiet(DonHang donHang) {
            int viTriAdapter = getBindingAdapterPosition();
            if (viTriAdapter == RecyclerView.NO_POSITION) {
                return;
            }
            donHang.datTrangThaiMoRong(!donHang.dangMoRong());
            notifyItemChanged(viTriAdapter);
        }

        private void ganHanhDong(TextView view, boolean hienThi, View.OnClickListener suKienClick) {
            view.setVisibility(hienThi ? View.VISIBLE : View.GONE);
            view.setOnClickListener(hienThi ? suKienClick : null);
        }

        private void capNhatKhoangCachNutHanhDong() {
            datKhoangCachTrai(binding.btnEmployeeDonHangConfirm, false);
            datKhoangCachTrai(binding.btnEmployeeDonHangComplete, binding.btnEmployeeDonHangConfirm.getVisibility() == View.VISIBLE);
            datKhoangCachTrai(
                    binding.btnEmployeeDonHangCancel,
                    binding.btnEmployeeDonHangConfirm.getVisibility() == View.VISIBLE || binding.btnEmployeeDonHangComplete.getVisibility() == View.VISIBLE
            );
        }

        private void datKhoangCachTrai(TextView view, boolean coKhoangCach) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            int khoangCach = coKhoangCach ? dp(view.getContext(), 8) : 0;
            params.setMarginStart(khoangCach);
            view.setLayoutParams(params);
        }

        private int dp(Context context, int value) {
            return Math.round(value * context.getResources().getDisplayMetrics().density);
        }
    }

    private String layTextThanhToan(Context context, DonHang donHang) {
        if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_THANH_TOAN) {
            return context.getString(R.string.order_payment_status_paid);
        }
        if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_GOI_THANH_TOAN) {
            return context.getString(R.string.order_payment_status_requested);
        }
        if (!donHang.laAnTaiQuan()) {
            if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.TIEN_MAT_KHI_NHAN
                    || donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.TAI_QUAY) {
                return context.getString(R.string.order_payment_status_pay_on_pickup);
            }
            if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUYEN_KHOAN_NGAN_HANG) {
                return context.getString(R.string.order_payment_status_bank_transfer);
            }
            if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.VI_DIEN_TU) {
                return context.getString(R.string.order_payment_status_ewallet);
            }
        }
        return context.getString(R.string.order_payment_status_unpaid);
    }

}
