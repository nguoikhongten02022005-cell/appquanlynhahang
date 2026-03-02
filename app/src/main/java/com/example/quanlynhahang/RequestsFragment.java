package com.example.quanlynhahang;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.ReservationAdapter;
import com.example.quanlynhahang.adapter.ServiceRequestAdapter;
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
    private EditText etGuestCount;
    private EditText etReservationNote;

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

        initViews(view);
        setupMockData();
        setupDateTimePicker();
        setupReservationList(view);
        setupServiceRequestList(view);
        setupActions(view);
    }

    private void initViews(View view) {
        tvReservationDate = view.findViewById(R.id.tvReservationDate);
        tvReservationTime = view.findViewById(R.id.tvReservationTime);
        etGuestCount = view.findViewById(R.id.etGuestCount);
        etReservationNote = view.findViewById(R.id.etReservationNote);

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

    private void setupReservationList(View view) {
        RecyclerView rvReservations = view.findViewById(R.id.rvReservations);
        rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));

        reservationAdapter = new ReservationAdapter(reservations, (reservation, position) -> {
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

        String note = etReservationNote.getText() == null ? "" : etReservationNote.getText().toString().trim();
        String reservationDateTime = getFormattedDateTime(selectedDateTime);

        Reservation newReservation = new Reservation(
                reservationDateTime,
                guestCount,
                note,
                Reservation.Status.PENDING_APPROVAL
        );

        reservationAdapter.addReservation(newReservation);

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

    private void setupMockData() {
        reservations.clear();
        reservations.add(new Reservation(
                "05/03/2026 19:00",
                4,
                "Ngồi gần cửa sổ",
                Reservation.Status.PENDING_APPROVAL
        ));
        reservations.add(new Reservation(
                "01/03/2026 18:30",
                2,
                "Kỷ niệm ngày cưới",
                Reservation.Status.CONFIRMED
        ));
        reservations.add(new Reservation(
                "25/02/2026 12:00",
                6,
                "",
                Reservation.Status.COMPLETED
        ));

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
