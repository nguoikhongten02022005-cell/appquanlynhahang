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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RequestsFragment extends Fragment {

    private final List<Reservation> reservations = new ArrayList<>();
    private final List<ServiceRequest> serviceRequests = new ArrayList<>();

    private Calendar selectedDateTime;
    private TextView tvReservationDate;
    private TextView tvReservationTime;
    private Spinner spinnerReservationArea;
    private EditText etGuestCount;
    private EditText etReservationNote;

    private final List<String> areaOptions = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private ReservationAdapter reservationAdapter;
    private ServiceRequestAdapter serviceRequestAdapter;

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
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);

        initViews(view);
        setupServiceRequestMockData();
        loadReservations();
        setupDateTimePicker();
        setupReservationList(view);
        setupServiceRequestList(view);
        setupActions(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReservations();
        if (reservationAdapter != null) {
            reservationAdapter.setReservations(reservations);
        }
    }

    private void initViews(View view) {
        tvReservationDate = view.findViewById(R.id.tvReservationDate);
        tvReservationTime = view.findViewById(R.id.tvReservationTime);
        spinnerReservationArea = view.findViewById(R.id.spinnerReservationArea);
        etGuestCount = view.findViewById(R.id.etGuestCount);
        etReservationNote = view.findViewById(R.id.etReservationNote);

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
                    updateDateLabel();
                },
                year,
                month,
                day
        );
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

        if (guestCount <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.reservation_validation_guest_count),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String selectedArea = spinnerReservationArea.getSelectedItem() == null
                ? getString(R.string.reservation_area_ground_floor)
                : spinnerReservationArea.getSelectedItem().toString();
        String note = etReservationNote.getText() == null ? "" : etReservationNote.getText().toString().trim();
        String reservationDateTime = getFormattedDateTime(selectedDateTime);

        long newReservationId = databaseHelper.insertReservation(
                (int) currentUserId,
                reservationDateTime,
                selectedArea,
                guestCount,
                note
        );

        if (newReservationId <= 0) {
            Toast.makeText(
                    requireContext(),
                    getString(R.string.db_operation_failed),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        reservations.clear();
        reservations.addAll(databaseHelper.getReservationsByUserId((int) currentUserId));
        reservationAdapter.setReservations(reservations);

        etGuestCount.setText("");
        etReservationNote.setText("");

        Toast.makeText(
                requireContext(),
                getString(R.string.reservation_submit_success),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void submitQuickServiceRequest(String requestContent) {
        ServiceRequest serviceRequest = new ServiceRequest(
                requestContent,
                getCurrentTimeText(),
                ServiceRequest.Status.PROCESSING
        );
        serviceRequestAdapter.addServiceRequest(serviceRequest);

        Toast.makeText(
                requireContext(),
                getString(R.string.service_request_submit_success, requestContent),
                Toast.LENGTH_SHORT
        ).show();
    }

    private String getCurrentTimeText() {
        Calendar now = Calendar.getInstance();
        return String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d %02d:%02d",
                now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.MONTH) + 1,
                now.get(Calendar.YEAR),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE)
        );
    }

    private String getFormattedDateTime(Calendar calendar) {
        return String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d %02d:%02d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        );
    }

    private void loadReservations() {
        reservations.clear();

        long userId = sessionManager.getCurrentUserId();
        if (userId <= 0 || !sessionManager.isLoggedIn()) {
            return;
        }

        reservations.addAll(databaseHelper.getReservationsByUserId(userId));
    }

    private void setupServiceRequestMockData() {
        serviceRequests.clear();
        serviceRequests.add(new ServiceRequest(
                getString(R.string.service_request_quick_more_water),
                "02/03/2026 12:05",
                ServiceRequest.Status.PROCESSING
        ));
        serviceRequests.add(new ServiceRequest(
                getString(R.string.service_request_quick_call_staff),
                "02/03/2026 11:48",
                ServiceRequest.Status.DONE
        ));
    }
}
