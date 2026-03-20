package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.DonHangNhanVienAdapter;
import com.example.quanlynhahang.adapter.DatBanNhanVienAdapter;
import com.example.quanlynhahang.adapter.YeuCauPhucVuNhanVienAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.ThongKeTongQuanNhanVien;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class NhanVienActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private MaterialButton btnTabDonHangs;
    private MaterialButton btnTabReservations;
    private MaterialButton btnTabServiceRequests;
    private View layoutDonHangs;
    private View layoutReservations;
    private View layoutServiceRequests;
    private TextView tvPendingDonHangsCount;
    private TextView tvPendingReservationsCount;
    private TextView tvProcessingRequestsCount;
    private TextView tvDonHangsEmpty;
    private TextView tvReservationsEmpty;
    private TextView tvServiceRequestsEmpty;

    private DonHangNhanVienAdapter orderAdapter;
    private DatBanNhanVienAdapter reservationAdapter;
    private YeuCauPhucVuNhanVienAdapter serviceRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_vien);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!sessionManager.daDangNhap() || !sessionManager.laNhanVien()) {
            Toast.makeText(this, getString(R.string.role_guard_employee_denied), Toast.LENGTH_SHORT).show();
            dieuHuongSaiVaiTro();
            return;
        }

        khoiTaoView();
        thietLapRecyclerView();
        thietLapTab();
        thietLapDangXuat();
        lamMoiToanBoDuLieuNhanVien();
        hienTabDonHang();
    }

    private void khoiTaoView() {
        btnTabDonHangs = findViewById(R.id.btnEmployeeTabDonHangs);
        btnTabReservations = findViewById(R.id.btnEmployeeTabReservations);
        btnTabServiceRequests = findViewById(R.id.btnEmployeeTabServiceRequests);
        layoutDonHangs = findViewById(R.id.layoutEmployeeDonHangs);
        layoutReservations = findViewById(R.id.layoutEmployeeReservations);
        layoutServiceRequests = findViewById(R.id.layoutEmployeeServiceRequests);
        tvPendingDonHangsCount = findViewById(R.id.tvEmployeePendingDonHangsCount);
        tvPendingReservationsCount = findViewById(R.id.tvEmployeePendingReservationsCount);
        tvProcessingRequestsCount = findViewById(R.id.tvEmployeeProcessingRequestsCount);
        tvDonHangsEmpty = findViewById(R.id.tvEmployeeDonHangsEmpty);
        tvReservationsEmpty = findViewById(R.id.tvEmployeeReservationsEmpty);
        tvServiceRequestsEmpty = findViewById(R.id.tvEmployeeServiceRequestsEmpty);
    }

    private void thietLapRecyclerView() {
        RecyclerView rvDonHangs = findViewById(R.id.rvEmployeeDonHangs);
        RecyclerView rvReservations = findViewById(R.id.rvEmployeeReservations);
        RecyclerView rvServiceRequests = findViewById(R.id.rvEmployeeServiceRequests);

        rvDonHangs.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvServiceRequests.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new DonHangNhanVienAdapter(new DonHangNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiXacNhan(DonHang order) {
                xuLyTrangThaiDonHang(order, DonHang.TrangThai.DANG_CHUAN_BI);
            }

            @Override
            public void khiHoanTat(DonHang order) {
                DonHang.TrangThai trangThaiDich = order.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI
                        ? DonHang.TrangThai.SAN_SANG_PHUC_VU
                        : DonHang.TrangThai.HOAN_THANH;
                xuLyTrangThaiDonHang(order, trangThaiDich);
            }

            @Override
            public void khiHuy(DonHang order) {
                xuLyTrangThaiDonHang(order, DonHang.TrangThai.DA_HUY);
            }
        });
        reservationAdapter = new DatBanNhanVienAdapter(new DatBanNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiXacNhan(DatBan reservation) {
                xuLyTrangThaiDatBan(reservation, DatBan.TrangThai.DA_XAC_NHAN);
            }

            @Override
            public void khiHoanTat(DatBan reservation) {
                xuLyTrangThaiDatBan(reservation, DatBan.TrangThai.DA_PHUC_VU);
            }

            @Override
            public void khiHuy(DatBan reservation) {
                xuLyTrangThaiDatBan(reservation, DatBan.TrangThai.DA_HUY);
            }
        });
        serviceRequestAdapter = new YeuCauPhucVuNhanVienAdapter(request -> xuLyTrangThaiYeuCau(request, YeuCauPhucVu.TrangThai.DA_XU_LY));

        rvDonHangs.setAdapter(orderAdapter);
        rvReservations.setAdapter(reservationAdapter);
        rvServiceRequests.setAdapter(serviceRequestAdapter);
    }

    private void thietLapTab() {
        btnTabDonHangs.setOnClickListener(v -> hienTabDonHang());
        btnTabReservations.setOnClickListener(v -> hienTabDatBan());
        btnTabServiceRequests.setOnClickListener(v -> hienTabYeuCauPhucVu());
    }

    private void thietLapDangXuat() {
        MaterialButton btnLogout = findViewById(R.id.btnEmployeeLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.xoaPhienDangNhap();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void taiThongKeTongQuan() {
        ThongKeTongQuanNhanVien thongKe = databaseHelper.layThongKeTongQuanNhanVien();
        tvPendingDonHangsCount.setText(String.valueOf(thongKe.getPendingDonHangs()));
        tvPendingReservationsCount.setText(String.valueOf(thongKe.getPendingReservations()));
        tvProcessingRequestsCount.setText(String.valueOf(thongKe.getProcessingServiceRequests()));
    }

    private void taiDonHang() {
        List<DonHang> danhSachDon = databaseHelper.layTatCaDonHang();
        orderAdapter.capNhatDanhSach(danhSachDon);
        tvDonHangsEmpty.setVisibility(danhSachDon.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void taiDatBan() {
        List<DatBan> danhSachDatBan = databaseHelper.layTatCaDatBan();
        reservationAdapter.capNhatDanhSach(danhSachDatBan);
        tvReservationsEmpty.setVisibility(danhSachDatBan.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void taiYeuCauPhucVu() {
        List<YeuCauPhucVu> danhSachYeuCau = databaseHelper.layTatCaYeuCauPhucVu();
        serviceRequestAdapter.capNhatDanhSach(danhSachYeuCau);
        tvServiceRequestsEmpty.setVisibility(danhSachYeuCau.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void hienTabDonHang() {
        layoutDonHangs.setVisibility(View.VISIBLE);
        layoutReservations.setVisibility(View.GONE);
        layoutServiceRequests.setVisibility(View.GONE);
        btnTabDonHangs.setEnabled(false);
        btnTabReservations.setEnabled(true);
        btnTabServiceRequests.setEnabled(true);
    }

    private void hienTabDatBan() {
        layoutDonHangs.setVisibility(View.GONE);
        layoutReservations.setVisibility(View.VISIBLE);
        layoutServiceRequests.setVisibility(View.GONE);
        btnTabDonHangs.setEnabled(true);
        btnTabReservations.setEnabled(false);
        btnTabServiceRequests.setEnabled(true);
    }

    private void hienTabYeuCauPhucVu() {
        layoutDonHangs.setVisibility(View.GONE);
        layoutReservations.setVisibility(View.GONE);
        layoutServiceRequests.setVisibility(View.VISIBLE);
        btnTabDonHangs.setEnabled(true);
        btnTabReservations.setEnabled(true);
        btnTabServiceRequests.setEnabled(false);
    }

    private void lamMoiToanBoDuLieuNhanVien() {
        taiThongKeTongQuan();
        taiDonHang();
        taiDatBan();
        taiYeuCauPhucVu();
    }

    private void xuLyTrangThaiDonHang(DonHang donHang, DonHang.TrangThai trangThai) {
        boolean daCapNhat = databaseHelper.capNhatTrangThaiDonHang(donHang.layId(), trangThai);
        Toast.makeText(this, daCapNhat ? R.string.employee_order_status_update_success : R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            lamMoiToanBoDuLieuNhanVien();
        }
    }

    private void xuLyTrangThaiDatBan(DatBan datBan, DatBan.TrangThai trangThai) {
        boolean daCapNhat = databaseHelper.capNhatTrangThaiDatBan(datBan.layId(), trangThai);
        Toast.makeText(this, daCapNhat ? R.string.employee_reservation_status_update_success : R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            lamMoiToanBoDuLieuNhanVien();
        }
    }

    private void xuLyTrangThaiYeuCau(YeuCauPhucVu yeuCau, YeuCauPhucVu.TrangThai trangThai) {
        boolean daCapNhat = databaseHelper.capNhatTrangThaiYeuCauPhucVu(yeuCau.layId(), trangThai);
        Toast.makeText(this, daCapNhat ? R.string.employee_service_request_status_update_success : R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
        if (daCapNhat) {
            lamMoiToanBoDuLieuNhanVien();
        }
    }

    private void dieuHuongSaiVaiTro() {
        Intent intent;
        if (sessionManager.daDangNhap()) {
            intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.layVaiTroHienTai());
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
