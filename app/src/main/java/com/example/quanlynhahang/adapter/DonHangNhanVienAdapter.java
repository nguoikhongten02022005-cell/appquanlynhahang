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
            tvDonHangStatus.setText(layTextTrangThai(donHang.layTrangThai()));
            ViewCompat.setBackgroundTintList(tvDonHangStatus, ColorStateList.valueOf(ContextCompat.getColor(context, layMauTrangThai(donHang.layTrangThai()))));

            orderDishAdapter.capNhatDuLieu(donHang.layDanhSachMon());
            boolean coMon = !donHang.layDanhSachMon().isEmpty();
            tvEmptyDishes.setVisibility(coMon ? View.GONE : View.VISIBLE);
            layoutDonHangDetails.setVisibility(donHang.dangMoRong() ? View.VISIBLE : View.GONE);
            tvToggleDetails.setText(donHang.dangMoRong() ? R.string.employee_order_toggle_hide : R.string.employee_order_toggle_view);
            tvToggleDetails.setOnClickListener(v -> chuyenTrangThaiChiTiet(donHang));

            ganHanhDong(btnConfirm, donHang.layTrangThai() == DonHang.TrangThai.PENDING_CONFIRMATION, v -> hanhDongListener.khiXacNhan(donHang));
            ganHanhDong(btnComplete, donHang.layTrangThai() == DonHang.TrangThai.CONFIRMED, v -> hanhDongListener.khiHoanTat(donHang));
            ganHanhDong(btnCancel, donHang.layTrangThai() == DonHang.TrangThai.PENDING_CONFIRMATION || donHang.layTrangThai() == DonHang.TrangThai.CONFIRMED, v -> hanhDongListener.khiHuy(donHang));
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

    private int layTextTrangThai(DonHang.TrangThai trangThai) {
        if (trangThai == DonHang.TrangThai.PENDING_CONFIRMATION) {
            return R.string.order_status_pending;
        }
        if (trangThai == DonHang.TrangThai.CONFIRMED) {
            return R.string.order_status_confirmed;
        }
        if (trangThai == DonHang.TrangThai.COMPLETED) {
            return R.string.order_status_completed;
        }
        return R.string.order_status_canceled;
    }

    private int layMauTrangThai(DonHang.TrangThai trangThai) {
        if (trangThai == DonHang.TrangThai.PENDING_CONFIRMATION) {
            return R.color.warning;
        }
        if (trangThai == DonHang.TrangThai.CONFIRMED) {
            return R.color.success;
        }
        if (trangThai == DonHang.TrangThai.COMPLETED) {
            return R.color.primary;
        }
        return R.color.error;
    }
}
