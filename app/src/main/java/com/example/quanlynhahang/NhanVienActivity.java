package com.example.quanlynhahang;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NhanVienActivity extends AppCompatActivity {

    public static final String EXTRA_TAB_MUC_TIEU = "extra_target_tab";
    public static final String TAB_DON_HANG = "orders";
    public static final String TAB_DAT_BAN = "reservations";
    public static final String TAB_YEU_CAU = "service_requests";

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private MaterialButton btnTabOverview;
    private MaterialButton btnTabDonHangs;
    private MaterialButton btnTabReservations;
    private MaterialButton btnTabServiceRequests;
    private MaterialButton btnSidebarOverview;
    private MaterialButton btnSidebarOrders;
    private MaterialButton btnSidebarReservations;
    private MaterialButton btnSidebarServiceRequests;
    private View drawerLayoutContainer;
    private View layoutOverview;
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
    private YeuCauPhucVuNhanVienAdapter boDieuHopYeuCauPhucVu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_vien);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.chuyenDuLieuDangNhapCuNeuCan(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!xacThucPhienNoiBo(true)) {
            return;
        }

        khoiTaoView();
        thietLapRecyclerView();
        thietLapDieuHuong();
        thietLapHanhDong();
        thietLapDangXuat();
        lamMoiToanBoDuLieuNhanVien();
        moTabMacDinh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!xacThucPhienNoiBo(false)) {
            return;
        }
        lamMoiToanBoDuLieuNhanVien();
    }

    private void moTabMacDinh() {
        String tabMucTieu = getIntent().getStringExtra(EXTRA_TAB_MUC_TIEU);
        if (TAB_DON_HANG.equals(tabMucTieu)) {
            hienTabDonHang();
            return;
        }
        if (TAB_DAT_BAN.equals(tabMucTieu)) {
            hienTabDatBan();
            return;
        }
        if (TAB_YEU_CAU.equals(tabMucTieu)) {
            hienTabYeuCauPhucVu();
            return;
        }
        hienTabDonHang();
    }

    private void khoiTaoView() {
        drawerLayoutContainer = findViewById(R.id.drawerEmployeeRoot);
        btnTabOverview = findViewById(R.id.btnEmployeeTabOverview);
        btnTabDonHangs = findViewById(R.id.btnEmployeeTabDonHangs);
        btnTabReservations = findViewById(R.id.btnEmployeeTabReservations);
        btnTabServiceRequests = findViewById(R.id.btnEmployeeTabServiceRequests);
        btnSidebarOverview = findViewById(R.id.btnEmployeeSidebarOverview);
        btnSidebarOrders = findViewById(R.id.btnEmployeeSidebarOrders);
        btnSidebarReservations = findViewById(R.id.btnEmployeeSidebarReservations);
        btnSidebarServiceRequests = findViewById(R.id.btnEmployeeSidebarServiceRequests);
        layoutOverview = findViewById(R.id.layoutEmployeeOverview);
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
                xacNhanHuyDonHang(order);
            }
        });
        reservationAdapter = new DatBanNhanVienAdapter(new DatBanNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiXacNhan(DatBan reservation) {
                xuLyTrangThaiDatBan(reservation, DatBan.TrangThai.ACTIVE);
            }

            @Override
            public void khiHoanTat(DatBan reservation) {
                xuLyTrangThaiDatBan(reservation, DatBan.TrangThai.COMPLETED);
            }

            @Override
            public void khiHuy(DatBan reservation) {
                xacNhanHuyDatBan(reservation);
            }

            @Override
            public void khiDoiBan(DatBan reservation) {
                hienDialogDoiBan(reservation);
            }
        });
        boDieuHopYeuCauPhucVu = new YeuCauPhucVuNhanVienAdapter(new YeuCauPhucVuNhanVienAdapter.HanhDongListener() {
            @Override
            public void khiNhanXuLy(YeuCauPhucVu yeuCau) {
                xuLyTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DANG_XU_LY);
            }

            @Override
            public void khiDanhDauDaXong(YeuCauPhucVu yeuCau) {
                xuLyTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DA_XU_LY);
            }

            @Override
            public void khiHuy(YeuCauPhucVu yeuCau) {
                xacNhanHuyYeuCau(yeuCau);
            }
        });

        rvDonHangs.setAdapter(orderAdapter);
        rvReservations.setAdapter(reservationAdapter);
        rvServiceRequests.setAdapter(boDieuHopYeuCauPhucVu);
    }

    private void thietLapDieuHuong() {
        btnTabOverview.setOnClickListener(v -> hienTabTongQuan());
        btnTabDonHangs.setOnClickListener(v -> hienTabDonHang());
        btnTabReservations.setOnClickListener(v -> hienTabDatBan());
        btnTabServiceRequests.setOnClickListener(v -> hienTabYeuCauPhucVu());
        btnSidebarOverview.setOnClickListener(v -> hienTabTongQuan());
        btnSidebarOrders.setOnClickListener(v -> hienTabDonHang());
        btnSidebarReservations.setOnClickListener(v -> hienTabDatBan());
        btnSidebarServiceRequests.setOnClickListener(v -> hienTabYeuCauPhucVu());
    }

    private void thietLapHanhDong() {
        ImageView btnOpenSidebar = findViewById(R.id.btnEmployeeOpenSidebar);
        MaterialButton btnBackToCustomer = findViewById(R.id.btnBackToCustomerFromEmployee);
        MaterialButton btnCustomerView = findViewById(R.id.btnEmployeeSidebarCustomerView);
        if (btnOpenSidebar != null) {
            btnOpenSidebar.setOnClickListener(v -> moSidebar());
        }
        btnBackToCustomer.setOnClickListener(v -> moGiaoDienKhachHang());
        btnCustomerView.setOnClickListener(v -> {
            dongSidebarNeuCan();
            moGiaoDienKhachHang();
        });
    }

    private void thietLapDangXuat() {
        MaterialButton btnLogout = findViewById(R.id.btnEmployeeLogout);
        btnLogout.setOnClickListener(v -> {
            dongSidebarNeuCan();
            thucHienDangXuat();
        });
    }

    private void moSidebar() {
        if (drawerLayoutContainer instanceof DrawerLayout) {
            ((DrawerLayout) drawerLayoutContainer).openDrawer(GravityCompat.START);
        }
    }

    private void dongSidebarNeuCan() {
        if (drawerLayoutContainer instanceof DrawerLayout) {
            DrawerLayout drawerLayout = (DrawerLayout) drawerLayoutContainer;
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }

    private void moGiaoDienKhachHang() {
        Intent intent = new Intent(this, CustomerLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void thucHienDangXuat() {
        sessionManager.xoaPhienDangNhap();
        Intent intent = new Intent(this, CustomerLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean xacThucPhienNoiBo(boolean hienToast) {
        if (!sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            if (hienToast) {
                Toast.makeText(this, getString(R.string.session_invalid), Toast.LENGTH_SHORT).show();
            }
            dieuHuongSaiVaiTro();
            return false;
        }

        VaiTroNguoiDung vaiTroSession = sessionManager.layVaiTroSessionHopLe();
        boolean coQuyenNoiBo = sessionManager.daDangNhap()
                && (vaiTroSession == VaiTroNguoiDung.NHAN_VIEN || vaiTroSession == VaiTroNguoiDung.ADMIN);
        if (!coQuyenNoiBo) {
            if (hienToast) {
                Toast.makeText(this, getString(R.string.role_guard_employee_denied), Toast.LENGTH_SHORT).show();
            }
            dieuHuongSaiVaiTro();
            return false;
        }
        return true;
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
        boDieuHopYeuCauPhucVu.capNhatDanhSach(danhSachYeuCau);
        tvServiceRequestsEmpty.setVisibility(danhSachYeuCau.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void hienTabTongQuan() {
        layoutOverview.setVisibility(View.VISIBLE);
        layoutDonHangs.setVisibility(View.GONE);
        layoutReservations.setVisibility(View.GONE);
        layoutServiceRequests.setVisibility(View.GONE);
        capNhatTrangThaiTab(btnTabOverview, true);
        capNhatTrangThaiTab(btnTabDonHangs, false);
        capNhatTrangThaiTab(btnTabReservations, false);
        capNhatTrangThaiTab(btnTabServiceRequests, false);
        capNhatTrangThaiSidebar(btnSidebarOverview, true, R.drawable.ic_menu_24);
        capNhatTrangThaiSidebar(btnSidebarOrders, false, R.drawable.ic_receipt_24);
        capNhatTrangThaiSidebar(btnSidebarReservations, false, R.drawable.ic_calendar_24);
        capNhatTrangThaiSidebar(btnSidebarServiceRequests, false, R.drawable.ic_person_24);
        dongSidebarNeuCan();
    }

    private void hienTabDonHang() {
        layoutOverview.setVisibility(View.GONE);
        layoutDonHangs.setVisibility(View.VISIBLE);
        layoutReservations.setVisibility(View.GONE);
        layoutServiceRequests.setVisibility(View.GONE);
        capNhatTrangThaiTab(btnTabOverview, false);
        capNhatTrangThaiTab(btnTabDonHangs, true);
        capNhatTrangThaiTab(btnTabReservations, false);
        capNhatTrangThaiTab(btnTabServiceRequests, false);
        capNhatTrangThaiSidebar(btnSidebarOverview, false, R.drawable.ic_menu_24);
        capNhatTrangThaiSidebar(btnSidebarOrders, true, R.drawable.ic_receipt_24);
        capNhatTrangThaiSidebar(btnSidebarReservations, false, R.drawable.ic_calendar_24);
        capNhatTrangThaiSidebar(btnSidebarServiceRequests, false, R.drawable.ic_person_24);
        dongSidebarNeuCan();
    }

    private void hienTabDatBan() {
        layoutOverview.setVisibility(View.GONE);
        layoutDonHangs.setVisibility(View.GONE);
        layoutReservations.setVisibility(View.VISIBLE);
        layoutServiceRequests.setVisibility(View.GONE);
        capNhatTrangThaiTab(btnTabOverview, false);
        capNhatTrangThaiTab(btnTabDonHangs, false);
        capNhatTrangThaiTab(btnTabReservations, true);
        capNhatTrangThaiTab(btnTabServiceRequests, false);
        capNhatTrangThaiSidebar(btnSidebarOverview, false, R.drawable.ic_menu_24);
        capNhatTrangThaiSidebar(btnSidebarOrders, false, R.drawable.ic_receipt_24);
        capNhatTrangThaiSidebar(btnSidebarReservations, true, R.drawable.ic_calendar_24);
        capNhatTrangThaiSidebar(btnSidebarServiceRequests, false, R.drawable.ic_person_24);
        dongSidebarNeuCan();
    }

    private void hienTabYeuCauPhucVu() {
        layoutOverview.setVisibility(View.GONE);
        layoutDonHangs.setVisibility(View.GONE);
        layoutReservations.setVisibility(View.GONE);
        layoutServiceRequests.setVisibility(View.VISIBLE);
        capNhatTrangThaiTab(btnTabOverview, false);
        capNhatTrangThaiTab(btnTabDonHangs, false);
        capNhatTrangThaiTab(btnTabReservations, false);
        capNhatTrangThaiTab(btnTabServiceRequests, true);
        capNhatTrangThaiSidebar(btnSidebarOverview, false, R.drawable.ic_menu_24);
        capNhatTrangThaiSidebar(btnSidebarOrders, false, R.drawable.ic_receipt_24);
        capNhatTrangThaiSidebar(btnSidebarReservations, false, R.drawable.ic_calendar_24);
        capNhatTrangThaiSidebar(btnSidebarServiceRequests, true, R.drawable.ic_person_24);
        dongSidebarNeuCan();
    }

    private void capNhatTrangThaiTab(MaterialButton button, boolean duocChon) {
        if (button == null) {
            return;
        }
        button.setClickable(!duocChon);
        button.setEnabled(true);
        button.setBackgroundResource(duocChon ? R.drawable.bg_button_orange : android.R.color.transparent);
        int mauChu = ContextCompat.getColor(this, duocChon ? R.color.white : R.color.on_surface);
        button.setTextColor(mauChu);
        button.setStrokeWidth(duocChon ? 0 : dp(1));
        if (!duocChon) {
            button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.outline_variant)));
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
        } else {
            button.setStrokeColor(null);
            button.setBackgroundTintList(null);
        }
    }

    private void capNhatTrangThaiSidebar(MaterialButton button, boolean duocChon, int iconRes) {
        if (button == null) {
            return;
        }
        button.setIconResource(iconRes);
        button.setCheckable(false);
        button.setClickable(!duocChon);
        button.setEnabled(true);
        button.setBackgroundResource(duocChon ? R.drawable.bg_button_orange : android.R.color.transparent);
        int textColor = ContextCompat.getColor(this, duocChon ? R.color.white : R.color.admin_sidebar_text);
        button.setTextColor(textColor);
        button.setIconTint(ColorStateList.valueOf(textColor));
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
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
        if (datBan == null || trangThai == null) {
            Toast.makeText(this, R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        if (trangThai == DatBan.TrangThai.COMPLETED && datBan.layIdDonHangLienKet() <= 0) {
            Toast.makeText(this, R.string.employee_reservation_complete_requires_order, Toast.LENGTH_SHORT).show();
            return;
        }
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

    private void xacNhanHuyDonHang(DonHang donHang) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.employee_order_cancel_confirm_title)
                .setMessage(R.string.employee_order_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) -> xuLyTrangThaiDonHang(donHang, DonHang.TrangThai.DA_HUY))
                .show();
    }

    private void xacNhanHuyDatBan(DatBan datBan) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.employee_reservation_cancel_confirm_title)
                .setMessage(R.string.employee_reservation_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) -> xuLyTrangThaiDatBan(datBan, DatBan.TrangThai.CANCELLED))
                .show();
    }

    private void xacNhanHuyYeuCau(YeuCauPhucVu yeuCau) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.employee_service_request_cancel_confirm_title)
                .setMessage(R.string.employee_service_request_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_action_cancel, (dialog, which) -> xuLyTrangThaiYeuCau(yeuCau, YeuCauPhucVu.TrangThai.DA_HUY))
                .show();
    }

    private void hienDialogDoiBan(DatBan datBan) {
        if (datBan == null) {
            Toast.makeText(this, R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> danhSachBanTrong = new ArrayList<>();
        for (int soBan = 1; soBan <= 20; soBan++) {
            String tenBan = getString(R.string.reservation_table_option_format, soBan);
            if (tenBan.equalsIgnoreCase(datBan.laySoBan())
                    || !databaseHelper.layDanhSachBanDaDat(datBan.layThoiGian(), datBan.layId()).contains(tenBan)) {
                danhSachBanTrong.add(tenBan);
            }
        }
        Collections.sort(danhSachBanTrong);
        if (danhSachBanTrong.isEmpty()) {
            Toast.makeText(this, R.string.employee_reservation_change_table_no_options, Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialAutoCompleteTextView input = new MaterialAutoCompleteTextView(this);
        input.setSimpleItems(danhSachBanTrong.toArray(new String[0]));
        input.setText(datBan.laySoBan(), false);
        input.setPadding(dp(4), dp(8), dp(4), dp(8));

        new AlertDialog.Builder(this)
                .setTitle(R.string.employee_reservation_change_table_title)
                .setMessage(getString(R.string.employee_reservation_change_table_message))
                .setView(input)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.employee_reservation_action_change_table, (dialog, which) -> {
                    String soBanMoi = input.getText() == null ? "" : input.getText().toString().trim();
                    if (TextUtils.isEmpty(soBanMoi)) {
                        Toast.makeText(this, R.string.reservation_validation_area_required, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean daCapNhat = databaseHelper.capNhatBanDatBan(datBan.layId(), soBanMoi);
                    Toast.makeText(this, daCapNhat ? R.string.employee_reservation_change_table_success : R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
                    if (daCapNhat) {
                        lamMoiToanBoDuLieuNhanVien();
                    }
                })
                .show();
    }

    private void dieuHuongSaiVaiTro() {
        Intent intent = DieuHuongVaiTroHelper.taoIntentSaiVaiTro(this, sessionManager, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
