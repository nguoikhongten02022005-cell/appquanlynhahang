package com.example.quanlynhahang;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.DatBanAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DateTimeUtils;
import com.example.quanlynhahang.model.BanAn;
import com.example.quanlynhahang.model.DatBan;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

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
    private TextView tvReservationDate;
    private TextView tvReservationTime;
    private MaterialAutoCompleteTextView autoCompleteReservationTable;
    private EditText etGuestCount;
    private EditText etReservationNote;
    private TextView tvReservationAvailableTables;
    private TextView tvReservationOccupiedTables;
    private MaterialButton btnSubmitReservation;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private DatBanAdapter reservationAdapter;
    private boolean embedded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dat_ban, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        khoiTaoView(view);
        apDungCheDoNhung(view);
        thietLapBoChonNgayGio();
        thietLapDanhSachDatBan(view);
        thietLapHanhDong(view);
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

    private void apDungCheDoNhung(@NonNull View view) {
        if (!embedded) {
            return;
        }

        View titleView = view.findViewById(R.id.tvReservationsTitle);
        if (titleView != null) {
            titleView.setVisibility(View.GONE);
        }

        View sectionTitle = view.findViewById(R.id.tvReservationSectionTitle);
        if (sectionTitle != null) {
            sectionTitle.setVisibility(View.GONE);
        }

        View sectionSubtitle = view.findViewById(R.id.tvReservationSectionSubtitle);
        if (sectionSubtitle != null) {
            sectionSubtitle.setVisibility(View.GONE);
        }

        View rootContent = view.findViewById(R.id.layoutReservationRootContent);
        if (rootContent != null) {
            int paddingNgang = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_horizontal);
            int paddingDoc = getResources().getDimensionPixelSize(R.dimen.hub_embedded_content_padding_vertical);
            rootContent.setPadding(paddingNgang, paddingDoc, paddingNgang, paddingDoc);
        }
    }

    private void khoiTaoView(View view) {
        tvReservationDate = view.findViewById(R.id.tvReservationDate);
        tvReservationTime = view.findViewById(R.id.tvReservationTime);
        autoCompleteReservationTable = view.findViewById(R.id.autoCompleteReservationTable);
        etGuestCount = view.findViewById(R.id.etGuestCount);
        etReservationNote = view.findViewById(R.id.etReservationNote);
        tvReservationAvailableTables = view.findViewById(R.id.tvReservationAvailableTables);
        tvReservationOccupiedTables = view.findViewById(R.id.tvReservationOccupiedTables);
        btnSubmitReservation = view.findViewById(R.id.btnSubmitReservation);

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
        tvReservationDate.setOnClickListener(v -> moBoChonNgay());
        tvReservationTime.setOnClickListener(v -> moBoChonGio());
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
        tvReservationDate.setText(dateText);
    }

    private void capNhatNhanGio() {
        String timeText = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE)
        );
        tvReservationTime.setText(timeText);
    }

    private void thietLapLuaChonSoBan() {
        ArrayAdapter<String> tableAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                tableOptions
        );
        autoCompleteReservationTable.setAdapter(tableAdapter);
        autoCompleteReservationTable.setOnClickListener(v -> autoCompleteReservationTable.showDropDown());
        autoCompleteReservationTable.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteReservationTable.showDropDown();
            }
        });
    }

    private void capNhatDanhSachBanTheoKhungGio() {
        if (selectedDateTime == null || autoCompleteReservationTable == null) {
            return;
        }

        String banDangChon = autoCompleteReservationTable.getText() == null
                ? ""
                : autoCompleteReservationTable.getText().toString().trim();

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
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) autoCompleteReservationTable.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (!TextUtils.isEmpty(banDangChon) && tableOptions.contains(banDangChon)) {
            autoCompleteReservationTable.setText(banDangChon, false);
        } else if (!tableOptions.isEmpty()) {
            autoCompleteReservationTable.setText(tableOptions.get(0), false);
        } else {
            autoCompleteReservationTable.setText("", false);
        }

        capNhatTrangThaiBan();
    }

    private void capNhatTrangThaiBan() {
        if (tvReservationAvailableTables == null || tvReservationOccupiedTables == null) {
            return;
        }

        int tongSoBan = databaseHelper.layTatCaBanAn().size();
        if (tableOptions.isEmpty()) {
            tvReservationAvailableTables.setText(getString(R.string.reservation_no_tables_available));
        } else if (tongSoBan > 0 && tableOptions.size() == tongSoBan) {
            tvReservationAvailableTables.setText(getString(R.string.reservation_all_tables_available));
        } else {
            tvReservationAvailableTables.setText(getString(
                    R.string.reservation_available_tables_format,
                    tableOptions.size(),
                    TextUtils.join(", ", tableOptions)
            ));
        }

        if (occupiedTables.isEmpty()) {
            tvReservationOccupiedTables.setText(getString(R.string.reservation_no_tables_occupied));
        } else {
            tvReservationOccupiedTables.setText(getString(
                    R.string.reservation_occupied_tables_format,
                    occupiedTables.size(),
                    TextUtils.join(", ", occupiedTables)
            ));
        }
    }

    private void thietLapDanhSachDatBan(View view) {
        RecyclerView rvReservations = view.findViewById(R.id.rvReservations);
        rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));

        reservationAdapter = new DatBanAdapter(reservations, this::xacNhanHuyDatBan);
        rvReservations.setAdapter(reservationAdapter);
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

    private void thietLapHanhDong(View view) {
        btnSubmitReservation.setOnClickListener(v -> guiYeuCauDatBan());
    }

    private void guiYeuCauDatBan() {
        datTrangThaiDangGui(true);
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (!sessionManager.daDangNhap() || idNguoiDungHienTai <= 0) {
            datTrangThaiDangGui(false);
            hienThiPhanHoiNgan(R.string.reservation_login_required);
            return;
        }

        String chuoiSoKhach = etGuestCount.getText() == null ? "" : etGuestCount.getText().toString().trim();
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

        String banDaChon = autoCompleteReservationTable.getText() == null
                ? ""
                : autoCompleteReservationTable.getText().toString().trim();
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

        String ghiChu = etReservationNote.getText() == null ? "" : etReservationNote.getText().toString().trim();
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

        etGuestCount.setText("");
        etReservationNote.setText("");
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
        if (btnSubmitReservation == null) {
            return;
        }
        btnSubmitReservation.setEnabled(!dangGui);
        btnSubmitReservation.setText(dangGui ? getString(R.string.order_submitting) : getString(R.string.reservation_submit));
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
