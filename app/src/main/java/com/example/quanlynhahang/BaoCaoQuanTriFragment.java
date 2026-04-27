package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.databinding.FragmentBaoCaoQuanTriBinding;
import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.example.quanlynhahang.model.BanAn;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.ThongKeTongQuanQuanTri;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BaoCaoQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private FragmentBaoCaoQuanTriBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBaoCaoQuanTriBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        ganDieuHuong(binding.navAdminTables, DieuHuongNoiBoHelper.SECTION_BAN);
        ganDieuHuong(binding.btnAdminAlertAction, DieuHuongNoiBoHelper.SECTION_DON_HANG);
        ganDieuHuong(binding.btnAdminRevenueDetails, DieuHuongNoiBoHelper.SECTION_HOA_DON);
        ganDieuHuong(binding.btnAdminHeroQuickAction, DieuHuongNoiBoHelper.SECTION_DON_HANG);

        capNhatNgayTongQuan();
        capNhatDashboard();
    }

    @Override
    public void onResume() {
        super.onResume();
        capNhatNgayTongQuan();
        capNhatDashboard();
    }

    private void ganDieuHuong(@Nullable View view, @NonNull String section) {
        if (view != null) {
            view.setOnClickListener(v -> {
                if (requireActivity() instanceof TrungTamQuanTriActivity) {
                    ((TrungTamQuanTriActivity) requireActivity()).dieuHuongDenSection(section);
                    return;
                }
                startActivity(TrungTamQuanTriActivity.taoIntent(requireContext(), section));
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void capNhatNgayTongQuan() {
        if (binding == null) {
            return;
        }
        String ngayHienTai = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"))
                .format(new Date());
        String vietHoa = ngayHienTai.substring(0, 1).toUpperCase(new Locale("vi", "VN")) + ngayHienTai.substring(1);
        binding.tvBaoCaoQuanTriDate.setText(vietHoa);
    }

    private void capNhatDashboard() {
        if (databaseHelper == null || binding == null) {
            return;
        }

        ThongKeTongQuanQuanTri thongKe = databaseHelper.layThongKeTongQuanQuanTri();
        List<DonHang> tatCaDonHang = new ArrayList<>(databaseHelper.layTatCaDonHang());
        List<BanAn> tatCaBan = new ArrayList<>(databaseHelper.layTatCaBanAn());
        List<YeuCauPhucVu> tatCaYeuCau = new ArrayList<>(databaseHelper.layTatCaYeuCauPhucVu());

        binding.tvBaoCaoTongNguoiDung.setText(String.valueOf(thongKe.layTongNguoiDung()));
        binding.tvBaoCaoTongDonHang.setText(String.valueOf(demDonHangHomNay(tatCaDonHang)));
        binding.tvBaoCaoDonHangChoXacNhan.setText(String.valueOf(thongKe.laySoDonHangChoXacNhan()));
        binding.tvBaoCaoDatBanChoDuyet.setText(String.valueOf(thongKe.laySoDatBanChoDuyet()));
        binding.tvBaoCaoYeuCauDangXuLy.setText(String.valueOf(thongKe.laySoYeuCauDangXuLy()));

        long doanhThuHomNay = tinhDoanhThuHomNay(tatCaDonHang);
        String doanhThuText = MoneyUtils.dinhDangTienViet(doanhThuHomNay);
        binding.tvAdminRevenueToday.setText(doanhThuText);
        binding.tvAdminRevenueSummary.setText("Đơn vị: nghìn đồng");
        capNhatBieuDoDoanhThu(tatCaDonHang);

        int soBanDangDung = 0;
        for (BanAn banAn : tatCaBan) {
            if (banAn.layTrangThai() == BanAn.TrangThai.DANG_PHUC_VU) {
                soBanDangDung++;
            }
        }
        binding.tvAdminOccupiedTables.setText(soBanDangDung + "/" + tatCaBan.size());

        capNhatCanhBao(tatCaDonHang, tatCaYeuCau);
        capNhatTinhTrangDon(tatCaDonHang);
        capNhatDonGanDay(tatCaDonHang);
    }

    private int demDonHangHomNay(List<DonHang> donHangs) {
        String ngayHienTai = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        int tong = 0;
        for (DonHang donHang : donHangs) {
            String thoiGian = donHang.layThoiGian();
            if (thoiGian != null && thoiGian.startsWith(ngayHienTai)) {
                tong++;
            }
        }
        return tong;
    }

    private long tinhDoanhThuHomNay(List<DonHang> donHangs) {
        String ngayHienTai = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        long tong = 0L;
        for (DonHang donHang : donHangs) {
            String thoiGian = donHang.layThoiGian();
            if (thoiGian != null && thoiGian.startsWith(ngayHienTai)) {
                tong += MoneyUtils.tachGiaTienTuChuoi(donHang.layTongTien());
            }
        }
        return tong;
    }

    private void capNhatBieuDoDoanhThu(List<DonHang> donHangs) {
        if (binding == null) {
            return;
        }

        binding.layoutAdminRevenueBars.removeAllViews();
        List<DoanhThuNgay> doanhThuTheoNgay = taoDoanhThuBayNgayGanNhat(donHangs);
        long doanhThuCaoNhat = 0L;
        for (DoanhThuNgay item : doanhThuTheoNgay) {
            doanhThuCaoNhat = Math.max(doanhThuCaoNhat, item.doanhThu);
        }

        for (DoanhThuNgay item : doanhThuTheoNgay) {
            binding.layoutAdminRevenueBars.addView(taoCotDoanhThu(item, doanhThuCaoNhat));
        }
    }

    private List<DoanhThuNgay> taoDoanhThuBayNgayGanNhat(List<DonHang> donHangs) {
        List<DoanhThuNgay> ketQua = new ArrayList<>();
        SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar lich = Calendar.getInstance();
        lich.add(Calendar.DAY_OF_YEAR, -6);

        String ngayHomNay = dinhDangNgay.format(new Date());
        for (int i = 0; i < 7; i++) {
            Date ngay = lich.getTime();
            String ngayKey = dinhDangNgay.format(ngay);
            ketQua.add(new DoanhThuNgay(taoNhanNgay(lich), ngayKey.equals(ngayHomNay), tinhDoanhThuTheoNgay(donHangs, ngayKey)));
            lich.add(Calendar.DAY_OF_YEAR, 1);
        }
        return ketQua;
    }

    private long tinhDoanhThuTheoNgay(List<DonHang> donHangs, String ngayKey) {
        long tong = 0L;
        for (DonHang donHang : donHangs) {
            String thoiGian = donHang.layThoiGian();
            if (thoiGian != null && thoiGian.startsWith(ngayKey)) {
                tong += MoneyUtils.tachGiaTienTuChuoi(donHang.layTongTien());
            }
        }
        return tong;
    }

    private String taoNhanNgay(Calendar lich) {
        if (laHomNay(lich)) {
            return "Hôm\nnay";
        }
        int thu = lich.get(Calendar.DAY_OF_WEEK);
        if (thu == Calendar.SUNDAY) {
            return "CN";
        }
        return "T" + thu;
    }

    private boolean laHomNay(Calendar lich) {
        Calendar homNay = Calendar.getInstance();
        return lich.get(Calendar.YEAR) == homNay.get(Calendar.YEAR)
                && lich.get(Calendar.DAY_OF_YEAR) == homNay.get(Calendar.DAY_OF_YEAR);
    }

    private View taoCotDoanhThu(DoanhThuNgay item, long doanhThuCaoNhat) {
        LinearLayout cot = new LinearLayout(requireContext());
        cot.setOrientation(LinearLayout.VERTICAL);
        cot.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        cot.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        cot.setPadding(dp(2), 0, dp(2), 0);

        TextView giaTri = new TextView(requireContext());
        giaTri.setText(item.doanhThu > 0L || item.laHomNay ? rutGonTien(item.doanhThu) : "");
        giaTri.setGravity(android.view.Gravity.CENTER);
        giaTri.setSingleLine(true);
        giaTri.setTextSize(12);
        giaTri.setTypeface(null, item.laHomNay ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        giaTri.setTextColor(ContextCompat.getColor(requireContext(), item.laHomNay ? R.color.brand_primary : R.color.on_surface_variant));
        cot.addView(giaTri, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(22)));

        LinearLayout vungCot = new LinearLayout(requireContext());
        vungCot.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams vungCotParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        cot.addView(vungCot, vungCotParams);

        View thanh = new View(requireContext());
        int chieuCao = item.doanhThu <= 0L || doanhThuCaoNhat <= 0L ? dp(12) : dp(24 + Math.round((item.doanhThu * 116f) / doanhThuCaoNhat));
        thanh.setBackgroundResource(item.laHomNay ? R.drawable.bg_admin_revenue_bar_today : R.drawable.bg_admin_revenue_bar);
        vungCot.addView(thanh, new LinearLayout.LayoutParams(dp(item.laHomNay ? 30 : 28), chieuCao));

        TextView ngay = new TextView(requireContext());
        ngay.setText(item.nhanNgay);
        ngay.setGravity(android.view.Gravity.CENTER);
        ngay.setTextSize(12);
        ngay.setTypeface(null, item.laHomNay ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        ngay.setTextColor(ContextCompat.getColor(requireContext(), item.laHomNay ? R.color.brand_primary : R.color.on_surface_variant));
        cot.addView(ngay, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(36)));

        return cot;
    }

    private static class DoanhThuNgay {
        private final String nhanNgay;
        private final boolean laHomNay;
        private final long doanhThu;

        private DoanhThuNgay(String nhanNgay, boolean laHomNay, long doanhThu) {
            this.nhanNgay = nhanNgay;
            this.laHomNay = laHomNay;
            this.doanhThu = doanhThu;
        }
    }

    private void capNhatCanhBao(List<DonHang> donHangs, List<YeuCauPhucVu> yeuCaus) {
        DonHang donCho = null;
        for (DonHang donHang : donHangs) {
            if (donHang.layTrangThai() == DonHang.TrangThai.CHO_XAC_NHAN) {
                donCho = donHang;
                break;
            }
        }
        if (donCho != null) {
            binding.tvAdminAlertTitle.setText(R.string.admin_alert_pending_order);
            String soBan = donCho.coBanAn() ? chuanHoaNhanBan(donCho.laySoBan()) : donCho.layMaDon();
            binding.tvAdminAlertSubtitle.setText(soBan + " · " + formatKhoangThoiGian(donCho.layThoiGian()));
            return;
        }

        for (YeuCauPhucVu yeuCau : yeuCaus) {
            if (yeuCau.dangHoatDong()) {
                binding.tvAdminAlertTitle.setText(R.string.admin_alert_pending_request);
                String soBan = yeuCau.coBanLienQuan() ? chuanHoaNhanBan(yeuCau.laySoBan()) : getString(R.string.admin_internal_area_label);
                binding.tvAdminAlertSubtitle.setText(soBan + " · " + formatKhoangThoiGian(yeuCau.layThoiGianGui()));
                return;
            }
        }

        binding.tvAdminAlertTitle.setText(R.string.admin_alert_no_new_title);
        binding.tvAdminAlertSubtitle.setText(R.string.admin_alert_no_new_subtitle);
    }

    private void capNhatTinhTrangDon(List<DonHang> donHangs) {
        int pending = 0;
        int serving = 0;
        int completed = 0;
        for (DonHang donHang : donHangs) {
            if (donHang.layTrangThai() == DonHang.TrangThai.CHO_XAC_NHAN) {
                pending++;
            } else if (donHang.layTrangThai() == DonHang.TrangThai.DANG_CHUAN_BI
                    || donHang.layTrangThai() == DonHang.TrangThai.SAN_SANG_PHUC_VU) {
                serving++;
            } else if (donHang.layTrangThai() == DonHang.TrangThai.HOAN_THANH) {
                completed++;
            }
        }
        int tong = pending + serving + completed;
        binding.tvAdminOrderStatusTotal.setText(String.valueOf(tong));
        binding.tvAdminOrderPendingCount.setText(String.valueOf(pending));
        binding.tvAdminOrderServingCount.setText(String.valueOf(serving));
        binding.tvAdminOrderCompletedCount.setText(String.valueOf(completed));
        binding.progressAdminOrderStatus.setProgress(tong == 0 ? 0 : Math.min(100, Math.round((pending * 100f) / tong)));
    }

    private void capNhatDonGanDay(List<DonHang> donHangs) {
        if (binding == null) {
            return;
        }
        binding.layoutAdminRecentOrdersList.removeAllViews();
        if (donHangs.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText(R.string.admin_recent_orders_empty);
            empty.setPadding(dp(16), dp(16), dp(16), dp(16));
            binding.layoutAdminRecentOrdersList.addView(empty);
            return;
        }

        Collections.sort(donHangs, Comparator.comparingLong((DonHang item) -> DateTimeUtils.parseDonHangTimeToMillis(item.layThoiGian())).reversed());
        int soLuong = Math.min(3, donHangs.size());
        for (int i = 0; i < soLuong; i++) {
            binding.layoutAdminRecentOrdersList.addView(taoDongDonGanDay(donHangs.get(i), i < soLuong - 1));
        }
    }

    private View taoDongDonGanDay(DonHang donHang, boolean coDivider) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));

        TextView ma = new TextView(requireContext());
        ma.setText(donHang.layMaDon());
        ma.setTextSize(14);
        ma.setTypeface(null, android.graphics.Typeface.BOLD);
        ma.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_tertiary));
        ma.setBackgroundResource(R.drawable.bg_admin_alert);
        ma.setPadding(dp(12), dp(8), dp(12), dp(8));
        row.addView(ma);

        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        infoParams.setMargins(dp(12), 0, dp(12), 0);
        info.setLayoutParams(infoParams);

        TextView title = new TextView(requireContext());
        String moTaBan = donHang.coBanAn() ? chuanHoaNhanBan(donHang.laySoBan()) : getString(R.string.admin_takeaway_label);
        title.setText(getString(R.string.admin_recent_order_title, moTaBan, donHang.layDanhSachMon().size()));
        title.setSingleLine(true);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
        info.addView(title);

        TextView subtitle = new TextView(requireContext());
        subtitle.setText(donHang.layTongTien() + " · " + formatKhoangThoiGian(donHang.layThoiGian()));
        subtitle.setSingleLine(true);
        subtitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        subtitle.setTextSize(14);
        subtitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        info.addView(subtitle);
        row.addView(info);

        TextView status = new TextView(requireContext());
        status.setText(layNhanTrangThai(donHang));
        status.setTextSize(14);
        status.setTypeface(null, android.graphics.Typeface.BOLD);
        status.setPadding(dp(12), dp(8), dp(12), dp(8));
        ganMauTrangThai(status, donHang.layTrangThai());
        row.addView(status);

        row.setOnClickListener(v -> {
            if (requireActivity() instanceof TrungTamQuanTriActivity) {
                ((TrungTamQuanTriActivity) requireActivity()).dieuHuongDenSection(DieuHuongNoiBoHelper.SECTION_DON_HANG);
                return;
            }
            startActivity(TrungTamQuanTriActivity.taoIntent(requireContext(), DieuHuongNoiBoHelper.SECTION_DON_HANG));
        });
        root.addView(row);

        if (coDivider) {
            View divider = new View(requireContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));
            divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.outline_variant));
            root.addView(divider);
        }
        return root;
    }

    private String layNhanTrangThai(DonHang donHang) {
        DonHang.TrangThai trangThai = donHang.layTrangThai();
        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {
            return getString(R.string.admin_filter_pending);
        }
        if (trangThai == DonHang.TrangThai.HOAN_THANH) {
            return getString(R.string.admin_filter_completed);
        }
        return getString(R.string.admin_filter_serving);
    }

    private void ganMauTrangThai(TextView view, DonHang.TrangThai trangThai) {
        int mauNen;
        int mauChu;
        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {
            mauNen = ContextCompat.getColor(requireContext(), R.color.admin_metric_yellow_bg);
            mauChu = ContextCompat.getColor(requireContext(), R.color.admin_warning);
        } else if (trangThai == DonHang.TrangThai.HOAN_THANH) {
            mauNen = ContextCompat.getColor(requireContext(), R.color.admin_metric_green_bg);
            mauChu = ContextCompat.getColor(requireContext(), R.color.success);
        } else {
            mauNen = ContextCompat.getColor(requireContext(), R.color.admin_metric_blue_bg);
            mauChu = ContextCompat.getColor(requireContext(), R.color.brand_primary);
        }
        androidx.core.view.ViewCompat.setBackgroundTintList(view, android.content.res.ColorStateList.valueOf(mauNen));
        view.setBackgroundResource(R.drawable.bg_admin_status_pill);
        view.setTextColor(mauChu);
    }

    private String chuanHoaNhanBan(String soBanRaw) {
        if (soBanRaw == null || soBanRaw.trim().isEmpty()) {
            return getString(R.string.admin_invoice_table_default);
        }
        String soBan = soBanRaw.trim();
        if (soBan.toLowerCase(new Locale("vi", "VN")).startsWith("bàn")) {
            return soBan;
        }
        return getString(R.string.admin_invoice_table_prefix, soBan);
    }

    private String rutGonTien(long soTien) {
        if (soTien >= 1_000_000L) {
            return String.format(Locale.getDefault(), "%.2fM", soTien / 1_000_000f);
        }
        if (soTien >= 1_000L) {
            return String.format(Locale.getDefault(), "%.0fk", soTien / 1_000f);
        }
        return soTien + "đ";
    }

    private String formatKhoangThoiGian(String thoiGianRaw) {
        long moc = DateTimeUtils.parseDonHangTimeToMillis(thoiGianRaw);
        if (moc <= 0L) {
            return getString(R.string.admin_time_just_now);
        }
        long phut = Math.max(1L, (System.currentTimeMillis() - moc) / 60000L);
        if (phut < 60L) {
            return getString(R.string.admin_time_minutes_ago, phut);
        }
        long gio = phut / 60L;
        if (gio < 24L) {
            return getString(R.string.admin_time_hours_ago, gio);
        }
        long ngay = gio / 24L;
        if (ngay == 1L) {
            return getString(R.string.admin_time_yesterday);
        }
        if (ngay < 7L) {
            return getString(R.string.admin_time_days_ago, ngay);
        }
        return new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date(moc));
    }

    private int dp(int value) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}
