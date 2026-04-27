package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quanlynhahang.adapter.HoaDonQuanTriAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.databinding.FragmentHoaDonQuanTriBinding;
import com.example.quanlynhahang.helper.DatabaseTaskRunner;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.example.quanlynhahang.model.DonHang;

import java.util.List;

public class HoaDonQuanTriFragment extends Fragment {

    private FragmentHoaDonQuanTriBinding binding;
    private DatabaseHelper databaseHelper;
    private final DatabaseTaskRunner databaseTaskRunner = new DatabaseTaskRunner();
    private HoaDonQuanTriAdapter hoaDonQuanTriAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHoaDonQuanTriBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();
        binding.rvHoaDonQuanTri.setLayoutManager(new LinearLayoutManager(requireContext()));
        hoaDonQuanTriAdapter = new HoaDonQuanTriAdapter(this::xacNhanDaThanhToan);
        binding.rvHoaDonQuanTri.setAdapter(hoaDonQuanTriAdapter);

        taiDanhSachHoaDon();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && databaseHelper != null && hoaDonQuanTriAdapter != null) {
            taiDanhSachHoaDon();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void taiDanhSachHoaDon() {
        if (!isAdded() || binding == null || databaseHelper == null || hoaDonQuanTriAdapter == null) {
            return;
        }
        databaseTaskRunner.execute(
                () -> databaseHelper.layTatCaDonHang(),
                danhSachHoaDon -> {
                    if (!isAdded() || binding == null || hoaDonQuanTriAdapter == null) {
                        return;
                    }
                    hoaDonQuanTriAdapter.capNhatDanhSach(danhSachHoaDon);
                    binding.tvHoaDonQuanTriEmpty.setVisibility(danhSachHoaDon.isEmpty() ? View.VISIBLE : View.GONE);
                    capNhatTongQuanHoaDon(danhSachHoaDon);
                }
        );
    }

    private void capNhatTongQuanHoaDon(List<DonHang> danhSachHoaDon) {
        long tongDoanhThu = 0L;
        long tongTienMat = 0L;
        long tongChuyenKhoan = 0L;
        long tongViDienTu = 0L;
        int soDaThanhToan = 0;
        int soChuaThanhToan = 0;
        int soHoaDonHopLe = 0;

        for (DonHang donHang : danhSachHoaDon) {
            if (donHang.layTrangThai() == DonHang.TrangThai.DA_HUY) {
                continue;
            }
            long giaTri = MoneyUtils.tachGiaTienTuChuoi(donHang.layTongTien());
            if (donHang.layTrangThaiThanhToan() == DonHang.TrangThaiThanhToan.DA_THANH_TOAN) {
                tongDoanhThu += giaTri;
                soHoaDonHopLe++;
                soDaThanhToan++;
                if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUYEN_KHOAN_NGAN_HANG) {
                    tongChuyenKhoan += giaTri;
                } else if (donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.VI_DIEN_TU) {
                    tongViDienTu += giaTri;
                } else {
                    tongTienMat += giaTri;
                }
            } else {
                soChuaThanhToan++;
            }
        }

        if (binding == null) {
            return;
        }
        binding.tvAdminInvoiceRevenueTotal.setText(MoneyUtils.dinhDangTienViet(tongDoanhThu));
        binding.tvAdminInvoicePaidCount.setText(String.valueOf(soDaThanhToan));
        binding.tvAdminInvoiceUnpaidCount.setText(String.valueOf(soChuaThanhToan));
        binding.tvAdminInvoiceAverageAmount.setText(MoneyUtils.dinhDangTienViet(soHoaDonHopLe == 0 ? 0 : tongDoanhThu / soHoaDonHopLe));
        binding.tvAdminInvoicePaymentBreakdownSubtitle.setText(getString(R.string.admin_invoice_payment_breakdown_subtitle_format, soDaThanhToan));

        long tongDaThanhToan = tongTienMat + tongChuyenKhoan + tongViDienTu;
        capNhatDongPhuongThuc(binding.tvAdminInvoiceCashSummary, binding.progressAdminInvoiceCash, getString(R.string.admin_invoice_method_cash), tongTienMat, tongDaThanhToan);
        capNhatDongPhuongThuc(binding.tvAdminInvoiceBankSummary, binding.progressAdminInvoiceBank, getString(R.string.admin_invoice_method_bank), tongChuyenKhoan, tongDaThanhToan);
        capNhatDongPhuongThuc(binding.tvAdminInvoiceDigitalSummary, binding.progressAdminInvoiceDigital, getString(R.string.admin_invoice_method_digital), tongViDienTu, tongDaThanhToan);
    }

    private void capNhatDongPhuongThuc(TextView textView, ProgressBar progressBar, String nhan, long giaTri, long tong) {
        int tyLe = tong <= 0 ? 0 : (int) Math.round((giaTri * 100d) / tong);
        textView.setText(getString(R.string.admin_invoice_method_summary_format, nhan, tyLe, MoneyUtils.dinhDangTienViet(giaTri)));
        progressBar.setProgress(tyLe);
    }

    private void xacNhanDaThanhToan(DonHang donHang) {
        if (!isAdded()) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_invoices_title)
                .setMessage(R.string.admin_invoice_payment_confirm_message)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_invoice_mark_paid, (dialog, which) -> danhDauDaThanhToan(donHang))
                .show();
    }

    private void danhDauDaThanhToan(DonHang donHang) {
        DonHang.PhuongThucThanhToan phuongThucThanhToan = donHang.layPhuongThucThanhToan() == DonHang.PhuongThucThanhToan.CHUA_CHON
                ? DonHang.PhuongThucThanhToan.TAI_QUAY
                : donHang.layPhuongThucThanhToan();
        boolean daCapNhat = databaseHelper.capNhatThanhToanDonHang(
                donHang.layId(),
                DonHang.TrangThaiThanhToan.DA_THANH_TOAN,
                phuongThucThanhToan
        );
        Toast.makeText(
                requireContext(),
                daCapNhat ? R.string.admin_invoice_payment_success : R.string.admin_action_failed,
                Toast.LENGTH_SHORT
        ).show();
        if (daCapNhat) {
            taiDanhSachHoaDon();
        }
    }
}
