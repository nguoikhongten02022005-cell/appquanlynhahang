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

import com.example.quanlynhahang.adapter.ReservationAdapter;
import com.example.quanlynhahang.adapter.ServiceRequestAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.model.Reservation;
import com.example.quanlynhahang.model.ServiceRequest;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestsFragment extends Fragment {

    public static final String ARG_EMBEDDED = "embedded";
    private static final int SO_KHACH_TOI_DA = 20;
    private static final SimpleDateFormat DINH_DANG_THOI_GIAN = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private final List<Reservation> reservations = new ArrayList<>();
    private final List<ServiceRequest> serviceRequests = new ArrayList<>();

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

    private ReservationAdapter reservationAdapter;
    private ServiceRequestAdapter serviceRequestAdapter;
    private boolean embedded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        embedded = getArguments() != null && getArguments().getBoolean(ARG_EMBEDDED, false);

        initViews(view);
        if (embedded) {
            View titleView = view.findViewById(R.id.tvRequestsTitle);
            if (titleView != null) {
                titleView.setVisibility(View.GONE);
            }
        }
        setupDateTimePicker();
        setupReservationList(view);
        setupServiceRequestList(view);
        loadReservations();
        loadServiceRequests();
        setupActions(view);
        refreshEmptyStates();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReservations();
        loadServiceRequests();
        if (reservationAdapter != null) {
            reservationAdapter.setReservations(reservations);
        }
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        refreshEmptyStates();
    }

    private void initViews(View view) {
        tvReservationDate = view.findViewById(R.id.tvReservationDate);
        tvReservationTime = view.findViewById(R.id.tvReservationTime);
        spinnerReservationArea = view.findViewById(R.id.spinnerReservationArea);
        etGuestCount = view.findViewById(R.id.etGuestCount);
        etReservationNote = view.findViewById(R.id.etReservationNote);
        tvReservationEmptyState = view.findViewById(R.id.tvReservationEmptyState);
        tvServiceRequestEmptyState = view.findViewById(R.id.tvServiceRequestEmptyState);

        setupAreaSelector();

        selectedDateTime = Calendar.getInstance();
        selectedDateTime.add(Calendar.DAY_OF_MONTH, 1);
        selectedDateTime.set(Calendar.HOUR_OF_DAY, 18);
        selectedDateTime.set(Calendar.MINUTE, 30);
        selectedDateTime.set(Calendar.SECOND, 0);
        selectedDateTime.set(Calendar.MILLISECOND, 0);

        updateDateLabel();
        updateTimeLabel();
    }

    private void setupDateTimePicker() {
        tvReservationDate.setOnClickListener(v -> showDatePicker());
        tvReservationTime.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
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
                    updateDateLabel();
                    updateTimeLabel();
                },
                year,
                month,
                day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000L);
        datePickerDialog.show();
    }

    private void showTimePicker() {
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
                    updateDateLabel();
                    updateTimeLabel();
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void updateDateLabel() {
        String dateText = String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d",
                selectedDateTime.get(Calendar.DAY_OF_MONTH),
                selectedDateTime.get(Calendar.MONTH) + 1,
                selectedDateTime.get(Calendar.YEAR)
        );
        tvReservationDate.setText(dateText);
    }

    private void updateTimeLabel() {
        String timeText = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE)
        );
        tvReservationTime.setText(timeText);
    }

    private void setupAreaSelector() {
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

    private void setupReservationList(View view) {
        RecyclerView rvReservations = view.findViewById(R.id.rvReservations);
        rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));

        reservationAdapter = new ReservationAdapter(reservations, (reservation, position) -> {
            boolean canceled = databaseHelper.cancelReservation(reservation.getId());
            if (!canceled) {
                Toast.makeText(
                        requireContext(),
                        getString(R.string.db_operation_failed),
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            reservation.cancel();
            reservationAdapter.notifyItemChanged(position);
            refreshEmptyStates();
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_cancel_success),
                    Toast.LENGTH_SHORT
            ).show();
        });

        rvReservations.setAdapter(reservationAdapter);
    }

    private void setupServiceRequestList(View view) {
        RecyclerView rvServiceRequests = view.findViewById(R.id.rvServiceRequests);
        rvServiceRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        serviceRequestAdapter = new ServiceRequestAdapter(serviceRequests);
        rvServiceRequests.setAdapter(serviceRequestAdapter);
    }

    private void setupActions(View view) {
        MaterialButton btnSubmitReservation = view.findViewById(R.id.btnSubmitReservation);
        btnSubmitReservation.setOnClickListener(v -> submitReservation());

        MaterialButton btnRequestCallStaff = view.findViewById(R.id.btnRequestCallStaff);
        MaterialButton btnRequestMoreWater = view.findViewById(R.id.btnRequestMoreWater);
        MaterialButton btnRequestPayment = view.findViewById(R.id.btnRequestPayment);

        btnRequestCallStaff.setOnClickListener(v -> submitQuickServiceRequest(
                getString(R.string.service_request_quick_call_staff)
        ));
        btnRequestMoreWater.setOnClickListener(v -> submitQuickServiceRequest(
                getString(R.string.service_request_quick_more_water)
        ));
        btnRequestPayment.setOnClickListener(v -> submitQuickServiceRequest(
                getString(R.string.service_request_quick_payment)
        ));
    }

    private void submitReservation() {
        long currentUserId = sessionManager.getCurrentUserId();
        if (!sessionManager.isLoggedIn() || currentUserId <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_login_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String guestCountText = etGuestCount.getText() == null ? "" : etGuestCount.getText().toString().trim();
        if (TextUtils.isEmpty(guestCountText)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_guest_count),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int guestCount;
        try {
            guestCount = Integer.parseInt(guestCountText);
        } catch (NumberFormatException ex) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_guest_count),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (guestCount <= 0 || guestCount > SO_KHACH_TOI_DA) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_guest_count_range, SO_KHACH_TOI_DA),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!isReservationTimeValid()) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_future_time),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String selectedArea = spinnerReservationArea.getSelectedItem() == null
                ? ""
                : spinnerReservationArea.getSelectedItem().toString().trim();
        if (TextUtils.isEmpty(selectedArea)) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_area_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String note = etReservationNote.getText() == null ? "" : etReservationNote.getText().toString().trim();
        String reservationDateTime = getFormattedDateTime(selectedDateTime);

        long newReservationId = databaseHelper.insertReservation(
                currentUserId,
                reservationDateTime,
                selectedArea,
                guestCount,
                note,
                Reservation.Status.PENDING_APPROVAL
        );

        if (newReservationId <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        loadReservations();
        if (reservationAdapter != null) {
            reservationAdapter.setReservations(reservations);
        }
        refreshEmptyStates();

        etGuestCount.setText("");
        etReservationNote.setText("");

        Toast.makeText(
                requireContext(),
                getString(R.string.reservation_submit_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void submitQuickServiceRequest(String requestContent) {
        long currentUserId = sessionManager.getCurrentUserId();
        if (!sessionManager.isLoggedIn() || currentUserId <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.service_request_login_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String thoiGianGui = getCurrentTimeText();
        long requestId = databaseHelper.insertServiceRequest(
                currentUserId,
                requestContent,
                thoiGianGui,
                ServiceRequest.Status.PROCESSING
        );
        if (requestId <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        loadServiceRequests();
        if (serviceRequestAdapter != null) {
            serviceRequestAdapter.capNhatDanhSach(serviceRequests);
        }
        refreshEmptyStates();

        Toast.makeText(
                requireContext(),
                getString(R.string.service_request_submit_success, requestContent),
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean isReservationTimeValid() {
        Calendar now = Calendar.getInstance();
        return selectedDateTime != null && selectedDateTime.after(now);
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

    private String getCurrentTimeText() {
        Calendar now = Calendar.getInstance();
        return getFormattedDateTime(now);
    }

    public String getFormattedDateTime(Calendar calendar) {
        return DINH_DANG_THOI_GIAN.format(calendar.getTime());
    }

    private void loadReservations() {
        reservations.clear();

        long userId = sessionManager.getCurrentUserId();
        if (userId <= 0 || !sessionManager.isLoggedIn()) {
            return;
        }

        reservations.addAll(databaseHelper.getReservationsByUserId(userId));
        reservations.sort((first, second) -> Long.compare(
                parseDateTime(second.getTime()),
                parseDateTime(first.getTime())
        ));
    }

    private void loadServiceRequests() {
        serviceRequests.clear();

        long userId = sessionManager.getCurrentUserId();
        if (userId <= 0 || !sessionManager.isLoggedIn()) {
            return;
        }

        serviceRequests.addAll(databaseHelper.getServiceRequestsByUserId(userId));
        serviceRequests.sort((first, second) -> Long.compare(
                parseDateTime(second.getThoiGianGui()),
                parseDateTime(first.getThoiGianGui())
        ));
    }

    private void refreshEmptyStates() {
        if (tvReservationEmptyState != null) {
            tvReservationEmptyState.setVisibility(reservations.isEmpty() ? View.VISIBLE : View.GONE);
        }
        if (tvServiceRequestEmptyState != null) {
            tvServiceRequestEmptyState.setVisibility(serviceRequests.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    public long parseDateTime(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0L;
        }
        try {
            Date date = DINH_DANG_THOI_GIAN.parse(value);
            return date == null ? 0L : date.getTime();
        } catch (ParseException e) {
            return 0L;
        }
    }
}
