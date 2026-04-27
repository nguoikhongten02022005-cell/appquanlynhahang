package com.example.quanlynhahang;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quanlynhahang.adapter.DatBanAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.databinding.FragmentDatBanBinding;
import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.model.BanAn;
import com.example.quanlynhahang.model.DatBan;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DatBanFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";

    private static final int SO_KHACH_TOI_DA = 20;

    private final List<DatBan> reservations = new ArrayList<>();
    private final List<String> tableOptions = new ArrayList<>();
    private final List<String> occupiedTables = new ArrayList<>();

    private Calendar selectedDateTime;
    private FragmentDatBanBinding binding;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private DatBanAdapter reservationAdapter;
    private boolean embedded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDatBanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        khoiTaoView();
        apDungCheDoNhung();
        thietLapBoChonNgayGio();
        thietLapDanhSachDatBan();
        thietLapHanhDong();
        taiDanhSachDatBan();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachDatBan();
        if (reservationAdapter != null) {
            reservationAdapter.capNhatDanhSachDatBan(reservations);
        }
        capNhatDanhSachBanTheoKhungGio();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void apDungCheDoNhung() {
        if (!embedded || binding == null) {
            return;
        }

        binding.tvReservationsTitle.setVisibility(View.GONE);
        binding.tvReservationSectionTitle.setVisibility(View.GONE);
        binding.tvReservationSectionSubtitle.setVisibility(View.GONE);

        int paddingNgang = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_horizontal);
        int paddingDoc = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_vertical);
        binding.layoutReservationRootContent.setPadding(paddingNgang, paddingDoc, paddingNgang, paddingDoc);
    }

    private void khoiTaoView() {
        thietLapLuaChonSoBan();

        selectedDateTime = Calendar.getInstance();
        selectedDateTime.add(Calendar.DAY_OF_MONTH, 1);
        selectedDateTime.set(Calendar.HOUR_OF_DAY, 18);
        selectedDateTime.set(Calendar.MINUTE, 30);
        selectedDateTime.set(Calendar.SECOND, 0);
        selectedDateTime.set(Calendar.MILLISECOND, 0);

        capNhatNhanNgay();
        capNhatNhanGio();
        capNhatDanhSachBanTheoKhungGio();
    }

    private void thietLapBoChonNgayGio() {
        binding.layoutReservationForm.tvReservationDate.setOnClickListener(v -> moBoChonNgay());
        binding.layoutReservationForm.tvReservationTime.setOnClickListener(v -> moBoChonGio());
    }

    private void moBoChonNgay() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (picker, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, selectedYear);
                    selectedDateTime.set(Calendar.MONTH, selectedMonth);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                    normalizeSelectedDateTime(true);
                    capNhatNhanNgay();
                    capNhatNhanGio();
                    capNhatDanhSachBanTheoKhungGio();
                },
                year,
                month,
                day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000L);
        datePickerDialog.show();
    }

    private void moBoChonGio() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (picker, selectedHour, selectedMinute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                    if (!normalizeSelectedDateTime(false)) {
                        Toast.makeText(
                                requireContext(),
                                getString(R.string.reservation_time_normalized_to_future),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    capNhatNhanNgay();
                    capNhatNhanGio();
                    capNhatDanhSachBanTheoKhungGio();
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void capNhatNhanNgay() {
        String dateText = String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d",
                selectedDateTime.get(Calendar.DAY_OF_MONTH),
                selectedDateTime.get(Calendar.MONTH) + 1,
                selectedDateTime.get(Calendar.YEAR)
        );
        binding.layoutReservationForm.tvReservationDate.setText(dateText);
    }

    private void capNhatNhanGio() {
        String timeText = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE)
        );
        binding.layoutReservationForm.tvReservationTime.setText(timeText);
    }

    private void thietLapLuaChonSoBan() {
        ArrayAdapter<String> tableAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                tableOptions
        );
        binding.layoutReservationForm.autoCompleteReservationTable.setAdapter(tableAdapter);
        binding.layoutReservationForm.autoCompleteReservationTable.setOnClickListener(v -> binding.layoutReservationForm.autoCompleteReservationTable.showDropDown());
        binding.layoutReservationForm.autoCompleteReservationTable.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding != null) {
                binding.layoutReservationForm.autoCompleteReservationTable.showDropDown();
            }
        });
    }

    private void capNhatDanhSachBanTheoKhungGio() {
        if (selectedDateTime == null || binding == null || databaseHelper == null) {
            return;
        }

        String banDangChon = binding.layoutReservationForm.autoCompleteReservationTable.getText() == null
                ? ""
                : binding.layoutReservationForm.autoCompleteReservationTable.getText().toString().trim();

        if (!isAdded()) {
            return;
        }

        occupiedTables.clear();
        occupiedTables.addAll(databaseHelper.layDanhSachBanDaDat(layChuoiThoiGian(selectedDateTime)));

        tableOptions.clear();
        Set<String> occupiedTableSet = new HashSet<>(occupiedTables);
        for (BanAn banAn : databaseHelper.layTatCaBanAn()) {
            String tenBan = banAn.layTenBan();
            if (!TextUtils.isEmpty(tenBan) && !occupiedTableSet.contains(tenBan)) {
                tableOptions.add(tenBan);
            }
        }

        Collections.sort(tableOptions);
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.layoutReservationForm.autoCompleteReservationTable.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (!TextUtils.isEmpty(banDangChon) && tableOptions.contains(banDangChon)) {
            binding.layoutReservationForm.autoCompleteReservationTable.setText(banDangChon, false);
        } else if (!tableOptions.isEmpty()) {
            binding.layoutReservationForm.autoCompleteReservationTable.setText(tableOptions.get(0), false);
        } else {
            binding.layoutReservationForm.autoCompleteReservationTable.setText("", false);
        }

        capNhatTrangThaiBan();
    }

    private void capNhatTrangThaiBan() {
        if (binding == null || databaseHelper == null) {
            return;
        }

        int tongSoBan = databaseHelper.layTatCaBanAn().size();
        if (tableOptions.isEmpty()) {
            binding.layoutReservationForm.tvReservationAvailableTables.setText(getString(R.string.reservation_no_tables_available));
        } else if (tongSoBan > 0 && tableOptions.size() == tongSoBan) {
            binding.layoutReservationForm.tvReservationAvailableTables.setText(getString(R.string.reservation_all_tables_available));
        } else {
            binding.layoutReservationForm.tvReservationAvailableTables.setText(getString(
                    R.string.reservation_available_tables_format,
                    tableOptions.size(),
                    TextUtils.join(", ", tableOptions)
            ));
        }

        if (occupiedTables.isEmpty()) {
            binding.layoutReservationForm.tvReservationOccupiedTables.setText(getString(R.string.reservation_no_tables_occupied));
        } else {
            binding.layoutReservationForm.tvReservationOccupiedTables.setText(getString(
                    R.string.reservation_occupied_tables_format,
                    occupiedTables.size(),
                    TextUtils.join(", ", occupiedTables)
            ));
        }
    }

    private void thietLapDanhSachDatBan() {
        binding.rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));

        reservationAdapter = new DatBanAdapter(reservations, this::xacNhanHuyDatBan);
        binding.rvReservations.setAdapter(reservationAdapter);
    }

    private void xacNhanHuyDatBan(DatBan datBan, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.reservation_cancel_confirm_title)
                .setMessage(R.string.reservation_cancel_confirm_message)
                .setNegativeButton(R.string.dialog_close, null)
                .setPositiveButton(R.string.reservation_cancel, (dialog, which) -> thucHienHuyDatBan(datBan, position))
                .show();
    }

    private void thucHienHuyDatBan(DatBan datBan, int position) {
        boolean daHuy = databaseHelper.huyDatBan(datBan.layId());
        if (!daHuy) {
            hienThiPhanHoiNgan(R.string.db_operation_failed);
            return;
        }

        taiDanhSachDatBan();
        reservationAdapter.capNhatDanhSachDatBan(reservations);
        capNhatDanhSachBanTheoKhungGio();
        hienThiPhanHoiNgan(R.string.reservation_cancel_success);
    }

    private void thietLapHanhDong() {
        binding.layoutReservationForm.btnSubmitReservation.setOnClickListener(v -> guiYeuCauDatBan());
    }

    private void guiYeuCauDatBan() {
        if (binding == null) {
            return;
        }
        datTrangThaiDangGui(true);
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (!sessionManager.daDangNhap() || idNguoiDungHienTai <= 0) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_login_required);
            return;
        }

        String chuoiSoKhach = binding.layoutReservationForm.etGuestCount.getText() == null ? "" : binding.layoutReservationForm.etGuestCount.getText().toString().trim();
        if (TextUtils.isEmpty(chuoiSoKhach)) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_validation_guest_count);
            return;
        }

        int soKhach;
        try {
            soKhach = Integer.parseInt(chuoiSoKhach);
        } catch (NumberFormatException ex) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_validation_guest_count);
            return;
        }

        if (soKhach <= 0 || soKhach > SO_KHACH_TOI_DA) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(getString(R.string.reservation_validation_guest_count_range, SO_KHACH_TOI_DA));
            return;
        }

        if (!laThoiGianDatBanHopLe()) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_validation_future_time);
            return;
        }

        String banDaChon = binding.layoutReservationForm.autoCompleteReservationTable.getText() == null
                ? ""
                : binding.layoutReservationForm.autoCompleteReservationTable.getText().toString().trim();
        if (TextUtils.isEmpty(banDaChon)) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_validation_area_required);
            return;
        }

        if (occupiedTables.contains(banDaChon)) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_table_unavailable);
            capNhatDanhSachBanTheoKhungGio();
            return;
        }

        String ghiChu = binding.layoutReservationForm.etReservationNote.getText() == null ? "" : binding.layoutReservationForm.etReservationNote.getText().toString().trim();
        String thoiGianDatBan = layChuoiThoiGian(selectedDateTime);

        long idDatBanMoi = databaseHelper.themDatBan(
                idNguoiDungHienTai,
                thoiGianDatBan,
                banDaChon,
                soKhach,
                ghiChu,
                DatBan.TrangThai.PENDING
        );

        if (idDatBanMoi <= 0) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_submit_failed);
            return;
        }

        taiDanhSachDatBan();
        if (reservationAdapter != null) {
            reservationAdapter.capNhatDanhSachDatBan(reservations);
        }

        binding.layoutReservationForm.etGuestCount.setText("");
        binding.layoutReservationForm.etReservationNote.setText("");
        capNhatDanhSachBanTheoKhungGio();

        datTrangThaiDangGui(false);
        Toast.makeText(
                requireContext(),
                getString(R.string.reservation_submit_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean laThoiGianDatBanHopLe() {
        if (selectedDateTime == null) {
            return false;
        }
        Calendar minimumTime = Calendar.getInstance();
        minimumTime.add(Calendar.MINUTE, 30);
        minimumTime.set(Calendar.SECOND, 0);
        minimumTime.set(Calendar.MILLISECOND, 0);
        return !selectedDateTime.before(minimumTime);
    }

    private boolean normalizeSelectedDateTime(boolean giuGioDaChon) {
        Calendar now = Calendar.getInstance();
        Calendar mucToiThieu = (Calendar) now.clone();
        mucToiThieu.add(Calendar.MINUTE, 30);
        mucToiThieu.set(Calendar.SECOND, 0);
        mucToiThieu.set(Calendar.MILLISECOND, 0);

        if (selectedDateTime == null) {
            selectedDateTime = (Calendar) mucToiThieu.clone();
            return false;
        }

        selectedDateTime.set(Calendar.SECOND, 0);
        selectedDateTime.set(Calendar.MILLISECOND, 0);

        if (selectedDateTime.after(mucToiThieu)) {
            return true;
        }

        if (!giuGioDaChon) {
            selectedDateTime.setTimeInMillis(mucToiThieu.getTimeInMillis());
            return false;
        }

        Calendar ngayDaChon = (Calendar) selectedDateTime.clone();
        ngayDaChon.set(Calendar.HOUR_OF_DAY, mucToiThieu.get(Calendar.HOUR_OF_DAY));
        ngayDaChon.set(Calendar.MINUTE, mucToiThieu.get(Calendar.MINUTE));
        ngayDaChon.set(Calendar.SECOND, 0);
        ngayDaChon.set(Calendar.MILLISECOND, 0);

        if (!ngayDaChon.before(mucToiThieu)) {
            selectedDateTime.setTimeInMillis(ngayDaChon.getTimeInMillis());
            return false;
        }

        selectedDateTime.setTimeInMillis(mucToiThieu.getTimeInMillis());
        return false;
    }

    private String layChuoiThoiGian(Calendar calendar) {
        return DateTimeUtils.formatCalendar(calendar);
    }

    private void taiDanhSachDatBan() {
        reservations.clear();

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDung <= 0 || !sessionManager.daDangNhap()) {
            return;
        }

        reservations.addAll(databaseHelper.layDatBanTheoNguoiDung(idNguoiDung));
    }

    private void datTrangThaiDangGui(boolean dangGui) {
        if (binding == null) {
            return;
        }
        binding.layoutReservationForm.btnSubmitReservation.setEnabled(!dangGui);
        binding.layoutReservationForm.btnSubmitReservation.setText(dangGui ? getString(R.string.order_submitting) : getString(R.string.reservation_submit));
    }

    private void hienThiPhanHoiNgan(int messageRes) {
        View root = getView();
        if (root != null) {
            Snackbar.make(root, messageRes, Snackbar.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(requireContext(), getString(messageRes), Toast.LENGTH_SHORT).show();
    }

    private void hienThiPhanHoiNgan(String message) {
        View root = getView();
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
