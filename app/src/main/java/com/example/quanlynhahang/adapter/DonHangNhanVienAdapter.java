package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_don_hang_nhan_vien, parent, false);
        return new DonHangNhanVienViewHolder(view);
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
        private final TextView tvDonHangCode;
        private final TextView tvDonHangTime;
        private final TextView tvDonHangTotal;
        private final TextView tvDonHangStatus;
        private final TextView tvDonHangType;
        private final TextView tvDonHangTable;
        private final TextView tvDonHangPayment;
        private final TextView tvDonHangNote;
        private final TextView tvToggleDetails;
        private final LinearLayout layoutDonHangDetails;
        private final TextView tvEmptyDishes;
        private final TextView btnConfirm;
        private final TextView btnComplete;
        private final TextView btnCancel;
        private final MonTrongDonAdapter orderDishAdapter;

        DonHangNhanVienViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonHangCode = itemView.findViewById(R.id.tvEmployeeDonHangCode);
            tvDonHangTime = itemView.findViewById(R.id.tvEmployeeDonHangTime);
            tvDonHangTotal = itemView.findViewById(R.id.tvEmployeeDonHangTotal);
            tvDonHangStatus = itemView.findViewById(R.id.tvEmployeeDonHangStatus);
            tvDonHangType = itemView.findViewById(R.id.tvEmployeeDonHangType);
            tvDonHangTable = itemView.findViewById(R.id.tvEmployeeDonHangTable);
            tvDonHangPayment = itemView.findViewById(R.id.tvEmployeeDonHangPayment);
            tvDonHangNote = itemView.findViewById(R.id.tvEmployeeDonHangNote);
            tvToggleDetails = itemView.findViewById(R.id.tvEmployeeDonHangToggleDetails);
            layoutDonHangDetails = itemView.findViewById(R.id.layoutEmployeeDonHangDetails);
            tvEmptyDishes = itemView.findViewById(R.id.tvEmployeeDonHangEmptyDishes);
            btnConfirm = itemView.findViewById(R.id.btnEmployeeDonHangConfirm);
            btnComplete = itemView.findViewById(R.id.btnEmployeeDonHangComplete);
            btnCancel = itemView.findViewById(R.id.btnEmployeeDonHangCancel);

            RecyclerView rvMonTrongDon = itemView.findViewById(R.id.rvEmployeeMonTrongDon);
            rvMonTrongDon.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvMonTrongDon.setNestedScrollingEnabled(false);
            orderDishAdapter = new MonTrongDonAdapter(new ArrayList<>());
            rvMonTrongDon.setAdapter(orderDishAdapter);
        }

        void ganDuLieu(DonHang donHang) {
            Context context = itemView.getContext();
            tvDonHangCode.setText(donHang.layMaDon());
            tvDonHangTime.setText(donHang.layThoiGian());
            tvDonHangTotal.setText(donHang.layTongTien());
            tvDonHangStatus.setText(TrangThaiHienThiHelper.layTextTrangThaiDon(donHang));
            tvDonHangType.setText(donHang.laAnTaiQuan()
                    ? context.getString(R.string.order_type_dine_in)
                    : context.getString(R.string.order_type_take_away));
            tvDonHangTable.setText(donHang.coBanAn()
                    ? context.getString(R.string.order_table_format, donHang.laySoBan())
                    : context.getString(R.string.order_table_not_required));
            tvDonHangPayment.setText(layTextThanhToan(context, donHang));
            tvDonHangNote.setText(donHang.coGhiChu()
                    ? context.getString(R.string.order_note_format, donHang.layGhiChu())
                    : context.getString(R.string.order_note_empty));
            ViewCompat.setBackgroundTintList(tvDonHangStatus, ColorStateList.valueOf(ContextCompat.getColor(context, TrangThaiHienThiHelper.layMauTrangThaiDon(donHang.layTrangThai()))));

            orderDishAdapter.capNhatDuLieu(donHang.layDanhSachMon());
            boolean coMon = !donHang.layDanhSachMon().isEmpty();
            tvEmptyDishes.setVisibility(coMon ? View.GONE : View.VISIBLE);
            layoutDonHangDetails.setVisibility(donHang.dangMoRong() ? View.VISIBLE : View.GONE);
            tvToggleDetails.setText(donHang.dangMoRong() ? R.string.employee_order_toggle_hide : R.string.employee_order_toggle_view);
            tvToggleDetails.setOnClickListener(v -> chuyenTrangThaiChiTiet(donHang));

            btnConfirm.setText(R.string.employee_order_action_accept);
            ganHanhDong(btnConfirm, HanhDongNghiepVuHelper.nhanVienCoTheNhanDon(donHang), v -> hanhDongListener.khiXacNhan(donHang));

            btnComplete.setText(HanhDongNghiepVuHelper.layTextHanhDongChinhDon(donHang));
            ganHanhDong(
                    btnComplete,
                    HanhDongNghiepVuHelper.nhanVienCoTheChuyenSangPhucVu(donHang)
                            || HanhDongNghiepVuHelper.nhanVienCoTheHoanTatDon(donHang),
                    v -> hanhDongListener.khiHoanTat(donHang)
            );
            ganHanhDong(btnCancel, HanhDongNghiepVuHelper.nhanVienCoTheHuyDon(donHang), v -> hanhDongListener.khiHuy(donHang));
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
    }

    private String layTextThanhToan(Context context, DonHang donHang) {
        if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_THANH_TOAN_MO_PHONG) {
            return context.getString(R.string.order_payment_status_paid_mock);
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
