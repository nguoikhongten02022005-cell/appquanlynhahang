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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.DatBanAdapter;
import com.example.quanlynhahang.adapter.YeuCauPhucVuAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class YeuCauFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";
    private static final int SO_KHACH_TOI_DA = 20;
    private static final SimpleDateFormat DINH_DANG_THOI_GIAN = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private final List<DatBan> reservations = new ArrayList<>();
    private final List<YeuCauPhucVu> serviceRequests = new ArrayList<>();

    private Calendar selectedDateTime;
    private TextView tvReservationDate;
    private TextView tvReservationTime;
    private Spinner spinnerReservationArea;
    private EditText etGuestCount;
    private EditText etReservationNote;
    private TextView tvReservationEmptyState;
    private TextView tvServiceRequestEmptyState;

    private final List<String> areaOptions = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private DatBanAdapter reservationAdapter;
    private YeuCauPhucVuAdapter serviceRequestAdapter;
    private boolean embedded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yeu_cau, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        khoiTaoView(view);
        if (embedded) {
            View titleView = view.findViewById(R.id.tvRequestsTitle);
            if (titleView != null) {
                titleView.setVisibility(View.GONE);
            }
        }
        thietLapBoChonNgayGio();
        thietLapDanhSachDatBan(view);
        thietLapDanhSachYeuCau(view);
        taiDanhSachDatBan();
        taiDanhSachYeuCau();
        thietLapHanhDong(view);
        capNhatTrangThaiRong();
    }

    @Override
    public void onResume() {
        super.onResume();
        taiDanhSachDatBan();
        taiDanhSachYeuCau();
        if (reservationAdapter != null) {
            reservationAdapter.capNhatDanhSachDatBan(reservations);
        }
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        capNhatTrangThaiRong();
    }

    private void khoiTaoView(View view) {
        tvReservationDate = view.findViewById(R.id.tvReservationDate);
        tvReservationTime = view.findViewById(R.id.tvReservationTime);
        spinnerReservationArea = view.findViewById(R.id.spinnerReservationArea);
        etGuestCount = view.findViewById(R.id.etGuestCount);
        etReservationNote = view.findViewById(R.id.etReservationNote);
        tvReservationEmptyState = view.findViewById(R.id.tvReservationEmptyState);
        tvServiceRequestEmptyState = view.findViewById(R.id.tvServiceRequestEmptyState);

        thietLapLuaChonKhuVuc();

        selectedDateTime = Calendar.getInstance();
        selectedDateTime.add(Calendar.DAY_OF_MONTH, 1);
        selectedDateTime.set(Calendar.HOUR_OF_DAY, 18);
        selectedDateTime.set(Calendar.MINUTE, 30);
        selectedDateTime.set(Calendar.SECOND, 0);
        selectedDateTime.set(Calendar.MILLISECOND, 0);

        capNhatNhanNgay();
        capNhatNhanGio();
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

    private void thietLapLuaChonKhuVuc() {
        areaOptions.clear();
        areaOptions.add(getString(R.string.reservation_area_ground_floor));
        areaOptions.add(getString(R.string.reservation_area_balcony));
        areaOptions.add(getString(R.string.reservation_area_vip_room));
        areaOptions.add(getString(R.string.reservation_area_near_window));

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                areaOptions
        );
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReservationArea.setAdapter(areaAdapter);
    }

    private void thietLapDanhSachDatBan(View view) {
        RecyclerView rvReservations = view.findViewById(R.id.rvReservations);
        rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));

        reservationAdapter = new DatBanAdapter(reservations, (reservation, position) -> {
            boolean daHuy = databaseHelper.huyDatBan(reservation.layId());
            if (!daHuy) {
                Toast.makeText(
                        requireContext(),
                        getString(R.string.db_operation_failed),
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            reservation.huyDatBan();
            reservationAdapter.notifyItemChanged(position);
            capNhatTrangThaiRong();
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_cancel_success),
                    Toast.LENGTH_SHORT
            ).show();
        });

        rvReservations.setAdapter(reservationAdapter);
    }

    private void thietLapDanhSachYeuCau(View view) {
        RecyclerView rvServiceRequests = view.findViewById(R.id.rvServiceRequests);
        rvServiceRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        serviceRequestAdapter = new YeuCauPhucVuAdapter(serviceRequests);
        rvServiceRequests.setAdapter(serviceRequestAdapter);
    }

    private void thietLapHanhDong(View view) {
        MaterialButton btnSubmitReservation = view.findViewById(R.id.btnSubmitReservation);
        btnSubmitReservation.setOnClickListener(v -> guiYeuCauDatBan());

        MaterialButton btnRequestCallStaff = view.findViewById(R.id.btnRequestCallStaff);
        MaterialButton btnRequestMoreWater = view.findViewById(R.id.btnRequestMoreWater);
        MaterialButton btnRequestPayment = view.findViewById(R.id.btnRequestPayment);

        btnRequestCallStaff.setOnClickListener(v -> guiYeuCauPhucVuNhanh(
                getString(R.string.service_request_quick_call_staff)
        ));
        btnRequestMoreWater.setOnClickListener(v -> guiYeuCauPhucVuNhanh(
                getString(R.string.service_request_quick_more_water)
        ));
        btnRequestPayment.setOnClickListener(v -> guiYeuCauPhucVuNhanh(
                getString(R.string.service_request_quick_payment)
        ));
    }

    private void guiYeuCauDatBan() {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (!sessionManager.daDangNhap() || idNguoiDungHienTai <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_login_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String chuoiSoKhach = etGuestCount.getText() == null ? "" : etGuestCount.getText().toString().trim();
        if (TextUtils.isEmpty(chuoiSoKhach)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_guest_count),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int soKhach;
        try {
            soKhach = Integer.parseInt(chuoiSoKhach);
        } catch (NumberFormatException ex) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_guest_count),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (soKhach <= 0 || soKhach > SO_KHACH_TOI_DA) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_guest_count_range, SO_KHACH_TOI_DA),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!laThoiGianDatBanHopLe()) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_future_time),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String khuVucDaChon = spinnerReservationArea.getSelectedItem() == null
                ? ""
                : spinnerReservationArea.getSelectedItem().toString().trim();
        if (TextUtils.isEmpty(khuVucDaChon)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_area_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String ghiChu = etReservationNote.getText() == null ? "" : etReservationNote.getText().toString().trim();
        String thoiGianDatBan = layChuoiThoiGian(selectedDateTime);

        long idDatBanMoi = databaseHelper.themDatBan(
                idNguoiDungHienTai,
                thoiGianDatBan,
                khuVucDaChon,
                soKhach,
                ghiChu,
                DatBan.TrangThai.PENDING_APPROVAL
        );

        if (idDatBanMoi <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        taiDanhSachDatBan();
        if (reservationAdapter != null) {
            reservationAdapter.capNhatDanhSachDatBan(reservations);
        }
        capNhatTrangThaiRong();

        etGuestCount.setText("");
        etReservationNote.setText("");

        Toast.makeText(
                requireContext(),
                getString(R.string.reservation_submit_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void guiYeuCauPhucVuNhanh(String noiDungYeuCau) {
        long idNguoiDungHienTai = sessionManager.layIdNguoiDungHienTai();
        if (!sessionManager.daDangNhap() || idNguoiDungHienTai <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.service_request_login_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String thoiGianGui = layChuoiThoiGianHienTai();
        long idYeuCau = databaseHelper.themYeuCauPhucVu(
                idNguoiDungHienTai,
                noiDungYeuCau,
                thoiGianGui,
                YeuCauPhucVu.TrangThai.PROCESSING
        );
        if (idYeuCau <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        taiDanhSachYeuCau();
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        capNhatTrangThaiRong();

        Toast.makeText(
                requireContext(),
                getString(R.string.service_request_submit_success, noiDungYeuCau),
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean laThoiGianDatBanHopLe() {
        if (selectedDateTime == null) {
            return false;
        }
        Calendar minimumTime = Calendar.getInstance();
        minimumTime.add(Calendar.MINUTE, 15);
        minimumTime.set(Calendar.SECOND, 0);
        minimumTime.set(Calendar.MILLISECOND, 0);
        return !selectedDateTime.before(minimumTime);
    }

    private boolean normalizeSelectedDateTime(boolean giuGioDaChon) {
        Calendar now = Calendar.getInstance();
        Calendar mucToiThieu = (Calendar) now.clone();
        mucToiThieu.add(Calendar.MINUTE, 15);
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

    private String layChuoiThoiGianHienTai() {
        Calendar now = Calendar.getInstance();
        return layChuoiThoiGian(now);
    }

    public String layChuoiThoiGian(Calendar calendar) {
        return DINH_DANG_THOI_GIAN.format(calendar.getTime());
    }

    private void taiDanhSachDatBan() {
        reservations.clear();

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDung <= 0 || !sessionManager.daDangNhap()) {
            return;
        }

        reservations.addAll(databaseHelper.layDatBanTheoNguoiDung(idNguoiDung));
    }

    private void taiDanhSachYeuCau() {
        serviceRequests.clear();

        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        if (idNguoiDung <= 0 || !sessionManager.daDangNhap()) {
            return;
        }

        serviceRequests.addAll(databaseHelper.layYeuCauTheoNguoiDung(idNguoiDung));
    }

    private void capNhatTrangThaiRong() {
        if (tvReservationEmptyState != null) {
            tvReservationEmptyState.setVisibility(reservations.isEmpty() ? View.VISIBLE : View.GONE);
        }
        if (tvServiceRequestEmptyState != null) {
            tvServiceRequestEmptyState.setVisibility(serviceRequests.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
