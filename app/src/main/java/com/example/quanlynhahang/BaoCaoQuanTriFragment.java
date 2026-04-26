package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.quanlynhahang.data.DatabaseHelper;
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
    private TextView tvTongNguoiDung;
    private TextView tvTongDonHang;
    private TextView tvDonHangChoXacNhan;
    private TextView tvDatBanChoDuyet;
    private TextView tvYeuCauDangXuLy;
    private TextView tvNgayTongQuan;
    private TextView tvDoanhThuHomNay;
    private TextView tvBanDangDung;
    private TextView tvCanhBaoTieuDe;
    private TextView tvCanhBaoPhuDe;
    private TextView tvTongTrangThaiDon;
    private TextView tvDonDangCho;
    private TextView tvDonDangPhucVu;
    private TextView tvDonHoanThanh;
    private TextView tvTomTatDoanhThu;
    private ProgressBar progressTrangThaiDon;
    private LinearLayout layoutDoanhThuTheoNgay;
    private LinearLayout layoutDonGanDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bao_cao_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        tvTongNguoiDung = view.findViewById(R.id.tvBaoCaoTongNguoiDung);
        tvTongDonHang = view.findViewById(R.id.tvBaoCaoTongDonHang);
        tvDonHangChoXacNhan = view.findViewById(R.id.tvBaoCaoDonHangChoXacNhan);
        tvDatBanChoDuyet = view.findViewById(R.id.tvBaoCaoDatBanChoDuyet);
        tvYeuCauDangXuLy = view.findViewById(R.id.tvBaoCaoYeuCauDangXuLy);
        tvNgayTongQuan = view.findViewById(R.id.tvBaoCaoQuanTriDate);
        tvDoanhThuHomNay = view.findViewById(R.id.tvAdminRevenueToday);
        tvBanDangDung = view.findViewById(R.id.tvAdminOccupiedTables);
        tvCanhBaoTieuDe = view.findViewById(R.id.tvAdminAlertTitle);
        tvCanhBaoPhuDe = view.findViewById(R.id.tvAdminAlertSubtitle);
        tvTongTrangThaiDon = view.findViewById(R.id.tvAdminOrderStatusTotal);
        tvDonDangCho = view.findViewById(R.id.tvAdminOrderPendingCount);
        tvDonDangPhucVu = view.findViewById(R.id.tvAdminOrderServingCount);
        tvDonHoanThanh = view.findViewById(R.id.tvAdminOrderCompletedCount);
        tvTomTatDoanhThu = view.findViewById(R.id.tvAdminRevenueSummary);
        progressTrangThaiDon = view.findViewById(R.id.progressAdminOrderStatus);
        layoutDoanhThuTheoNgay = view.findViewById(R.id.layoutAdminRevenueBars);
        layoutDonGanDay = view.findViewById(R.id.layoutAdminRecentOrdersList);

        View btnMoQuanLyBan = view.findViewById(R.id.navAdminTables);
        View btnAlertAction = view.findViewById(R.id.btnAdminAlertAction);
        View btnRecentOrdersMore = view.findViewById(R.id.btnAdminRecentOrdersMore);
        View btnRevenueDetails = view.findViewById(R.id.btnAdminRevenueDetails);
        View btnHeroQuickAction = view.findViewById(R.id.btnAdminHeroQuickAction);

        ganDieuHuong(btnMoQuanLyBan, DieuHuongNoiBoHelper.SECTION_BAN);
        ganDieuHuong(btnAlertAction, DieuHuongNoiBoHelper.SECTION_DON_HANG);
        ganDieuHuong(btnRevenueDetails, DieuHuongNoiBoHelper.SECTION_HOA_DON);
        ganDieuHuong(btnHeroQuickAction, DieuHuongNoiBoHelper.SECTION_DON_HANG);

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

    private void capNhatNgayTongQuan() {
        if (tvNgayTongQuan == null) {
            return;
        }
        String ngayHienTai = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"))
                .format(new Date());
        String vietHoa = ngayHienTai.substring(0, 1).toUpperCase(new Locale("vi", "VN")) + ngayHienTai.substring(1);
        tvNgayTongQuan.setText(vietHoa);
    }

    private void capNhatDashboard() {
        if (databaseHelper == null || tvTongNguoiDung == null) {
            return;
        }

        ThongKeTongQuanQuanTri thongKe = databaseHelper.layThongKeTongQuanQuanTri();
        List<DonHang> tatCaDonHang = new ArrayList<>(databaseHelper.layTatCaDonHang());
        List<BanAn> tatCaBan = new ArrayList<>(databaseHelper.layTatCaBanAn());
        List<YeuCauPhucVu> tatCaYeuCau = new ArrayList<>(databaseHelper.layTatCaYeuCauPhucVu());

        tvTongNguoiDung.setText(String.valueOf(thongKe.layTongNguoiDung()));
        tvTongDonHang.setText(String.valueOf(demDonHangHomNay(tatCaDonHang)));
        tvDonHangChoXacNhan.setText(String.valueOf(thongKe.laySoDonHangChoXacNhan()));
        tvDatBanChoDuyet.setText(String.valueOf(thongKe.laySoDatBanChoDuyet()));
        tvYeuCauDangXuLy.setText(String.valueOf(thongKe.laySoYeuCauDangXuLy()));

        long doanhThuHomNay = tinhDoanhThuHomNay(tatCaDonHang);
        String doanhThuText = MoneyUtils.dinhDangTienViet(doanhThuHomNay);
        tvDoanhThuHomNay.setText(doanhThuText);
        tvTomTatDoanhThu.setText("Đơn vị: nghìn đồng");
        capNhatBieuDoDoanhThu(tatCaDonHang);

        int soBanDangDung = 0;
        for (BanAn banAn : tatCaBan) {
            if (banAn.layTrangThai() == BanAn.TrangThai.DANG_PHUC_VU) {
                soBanDangDung++;
            }
        }
        tvBanDangDung.setText(soBanDangDung + "/" + tatCaBan.size());

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
        if (layoutDoanhThuTheoNgay == null) {
            return;
        }

        layoutDoanhThuTheoNgay.removeAllViews();
        List<DoanhThuNgay> doanhThuTheoNgay = taoDoanhThuBayNgayGanNhat(donHangs);
        long doanhThuCaoNhat = 0L;
        for (DoanhThuNgay item : doanhThuTheoNgay) {
            doanhThuCaoNhat = Math.max(doanhThuCaoNhat, item.doanhThu);
        }

        for (DoanhThuNgay item : doanhThuTheoNgay) {
            layoutDoanhThuTheoNgay.addView(taoCotDoanhThu(item, doanhThuCaoNhat));
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
            tvCanhBaoTieuDe.setText("1 đơn chờ xác nhận");
            String soBan = donCho.coBanAn() ? "Bàn " + donCho.laySoBan() : donCho.layMaDon();
            tvCanhBaoPhuDe.setText(soBan + " · " + formatKhoangThoiGian(donCho.layThoiGian()));
            return;
        }

        for (YeuCauPhucVu yeuCau : yeuCaus) {
            if (yeuCau.dangHoatDong()) {
                tvCanhBaoTieuDe.setText("1 yêu cầu cần xử lý");
                String soBan = yeuCau.coBanLienQuan() ? "Bàn " + yeuCau.laySoBan() : "Khu nội bộ";
                tvCanhBaoPhuDe.setText(soBan + " · " + formatKhoangThoiGian(yeuCau.layThoiGianGui()));
                return;
            }
        }

        tvCanhBaoTieuDe.setText("Không có cảnh báo mới");
        tvCanhBaoPhuDe.setText("Hệ thống đang ổn định");
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
        tvTongTrangThaiDon.setText(String.valueOf(tong));
        tvDonDangCho.setText(String.valueOf(pending));
        tvDonDangPhucVu.setText(String.valueOf(serving));
        tvDonHoanThanh.setText(String.valueOf(completed));
        if (progressTrangThaiDon != null) {
            progressTrangThaiDon.setProgress(tong == 0 ? 0 : Math.min(100, Math.round((pending * 100f) / tong)));
        }
    }

    private void capNhatDonGanDay(List<DonHang> donHangs) {
        if (layoutDonGanDay == null) {
            return;
        }
        layoutDonGanDay.removeAllViews();
        if (donHangs.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText(R.string.admin_recent_orders_empty);
            empty.setPadding(dp(16), dp(16), dp(16), dp(16));
            layoutDonGanDay.addView(empty);
            return;
        }

        Collections.sort(donHangs, Comparator.comparingLong((DonHang item) -> DateTimeUtils.parseDonHangTimeToMillis(item.layThoiGian())).reversed());
        int soLuong = Math.min(3, donHangs.size());
        for (int i = 0; i < soLuong; i++) {
            layoutDonGanDay.addView(taoDongDonGanDay(donHangs.get(i), i < soLuong - 1));
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
        String moTaBan = donHang.coBanAn() ? chuanHoaNhanBan(donHang.laySoBan()) : "Mang đi";
        title.setText(moTaBan + " · " + donHang.layDanhSachMon().size() + " món");
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

    private void ganMauTrangThai(TextView view, DonHang.TrangThai trangThai) {
        if (trangThai == DonHang.TrangThai.CHO_XAC_NHAN) {
            view.setBackgroundResource(R.drawable.bg_admin_alert);
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_tertiary));
            return;
        }
        if (trangThai == DonHang.TrangThai.HOAN_THANH) {
            view.setBackgroundResource(R.drawable.bg_admin_bottom_nav_active);
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_button_green);
        view.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
    }

    private String layNhanTrangThai(DonHang donHang) {
        if (donHang.layTrangThai() == DonHang.TrangThai.CHO_XAC_NHAN) {
            return "Chờ duyệt";
        }
        if (donHang.layTrangThai() == DonHang.TrangThai.HOAN_THANH) {
            return "Hoàn thành";
        }
        return "Đang phục vụ";
    }

    private String chuanHoaNhanBan(String soBanRaw) {
        if (soBanRaw == null || soBanRaw.trim().isEmpty()) {
            return "Tại bàn";
        }
        String soBan = soBanRaw.trim();
        if (soBan.toLowerCase(new Locale("vi", "VN")).startsWith("bàn")) {
            return soBan;
        }
        return "Bàn " + soBan;
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
            return "vừa xong";
        }
        long phut = Math.max(1L, (System.currentTimeMillis() - moc) / 60000L);
        if (phut < 60L) {
            return phut + " phút trước";
        }
        long gio = phut / 60L;
        if (gio < 24L) {
            return gio + " giờ trước";
        }
        long ngay = gio / 24L;
        if (ngay == 1L) {
            return "Hôm qua";
        }
        if (ngay < 7L) {
            return ngay + " ngày trước";
        }
        return new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date(moc));
    }

    private int dp(int value) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}
