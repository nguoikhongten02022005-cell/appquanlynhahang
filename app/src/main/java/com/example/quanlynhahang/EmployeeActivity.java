package com.example.quanlynhahang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.EmployeeOrderAdapter;
import com.example.quanlynhahang.adapter.EmployeeReservationAdapter;
import com.example.quanlynhahang.adapter.EmployeeServiceRequestAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongVaiTroHelper;
import com.example.quanlynhahang.model.EmployeeDashboardStats;
import com.example.quanlynhahang.model.Order;
import com.example.quanlynhahang.model.Reservation;
import com.example.quanlynhahang.model.ServiceRequest;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    private MaterialButton btnTabOrders;
    private MaterialButton btnTabReservations;
    private MaterialButton btnTabServiceRequests;
    private View layoutOrders;
    private View layoutReservations;
    private View layoutServiceRequests;
    private TextView tvPendingOrdersCount;
    private TextView tvPendingReservationsCount;
    private TextView tvProcessingRequestsCount;
    private TextView tvOrdersEmpty;
    private TextView tvReservationsEmpty;
    private TextView tvServiceRequestsEmpty;

    private EmployeeOrderAdapter orderAdapter;
    private EmployeeReservationAdapter reservationAdapter;
    private EmployeeServiceRequestAdapter serviceRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.migrateLegacyAuthIfNeeded(databaseHelper);
        sessionManager.damBaoVaiTroSession(databaseHelper);

        if (!sessionManager.isLoggedIn() || !sessionManager.laNhanVien()) {
            Toast.makeText(this, getString(R.string.role_guard_employee_denied), Toast.LENGTH_SHORT).show();
            dieuHuongSaiVaiTro();
            return;
        }

        initViews();
        setupRecyclerViews();
        setupTabs();
        setupLogout();
        refreshAllEmployeeData();
        showOrdersTab();
    }

    private void initViews() {
        btnTabOrders = findViewById(R.id.btnEmployeeTabOrders);
        btnTabReservations = findViewById(R.id.btnEmployeeTabReservations);
        btnTabServiceRequests = findViewById(R.id.btnEmployeeTabServiceRequests);
        layoutOrders = findViewById(R.id.layoutEmployeeOrders);
        layoutReservations = findViewById(R.id.layoutEmployeeReservations);
        layoutServiceRequests = findViewById(R.id.layoutEmployeeServiceRequests);
        tvPendingOrdersCount = findViewById(R.id.tvEmployeePendingOrdersCount);
        tvPendingReservationsCount = findViewById(R.id.tvEmployeePendingReservationsCount);
        tvProcessingRequestsCount = findViewById(R.id.tvEmployeeProcessingRequestsCount);
        tvOrdersEmpty = findViewById(R.id.tvEmployeeOrdersEmpty);
        tvReservationsEmpty = findViewById(R.id.tvEmployeeReservationsEmpty);
        tvServiceRequestsEmpty = findViewById(R.id.tvEmployeeServiceRequestsEmpty);
    }

    private void setupRecyclerViews() {
        RecyclerView rvOrders = findViewById(R.id.rvEmployeeOrders);
        RecyclerView rvReservations = findViewById(R.id.rvEmployeeReservations);
        RecyclerView rvServiceRequests = findViewById(R.id.rvEmployeeServiceRequests);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvServiceRequests.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new EmployeeOrderAdapter(new EmployeeOrderAdapter.ActionListener() {
            @Override
            public void onConfirm(Order order) {
                handleOrderStatus(order, Order.Status.CONFIRMED);
            }

            @Override
            public void onComplete(Order order) {
                handleOrderStatus(order, Order.Status.COMPLETED);
            }

            @Override
            public void onCancel(Order order) {
                handleOrderStatus(order, Order.Status.CANCELED);
            }
        });
        reservationAdapter = new EmployeeReservationAdapter(new EmployeeReservationAdapter.ActionListener() {
            @Override
            public void onConfirm(Reservation reservation) {
                handleReservationStatus(reservation, Reservation.Status.CONFIRMED);
            }

            @Override
            public void onComplete(Reservation reservation) {
                handleReservationStatus(reservation, Reservation.Status.COMPLETED);
            }

            @Override
            public void onCancel(Reservation reservation) {
                handleReservationStatus(reservation, Reservation.Status.CANCELED);
            }
        });
        serviceRequestAdapter = new EmployeeServiceRequestAdapter(request -> handleServiceRequestStatus(request, ServiceRequest.Status.DONE));

        rvOrders.setAdapter(orderAdapter);
        rvReservations.setAdapter(reservationAdapter);
        rvServiceRequests.setAdapter(serviceRequestAdapter);
    }

    private void setupTabs() {
        btnTabOrders.setOnClickListener(v -> showOrdersTab());
        btnTabReservations.setOnClickListener(v -> showReservationsTab());
        btnTabServiceRequests.setOnClickListener(v -> showServiceRequestsTab());
    }

    private void setupLogout() {
        MaterialButton btnLogout = findViewById(R.id.btnEmployeeLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadDashboardStats() {
        EmployeeDashboardStats stats = databaseHelper.getEmployeeDashboardStats();
        tvPendingOrdersCount.setText(String.valueOf(stats.getPendingOrders()));
        tvPendingReservationsCount.setText(String.valueOf(stats.getPendingReservations()));
        tvProcessingRequestsCount.setText(String.valueOf(stats.getProcessingServiceRequests()));
    }

    private void loadOrders() {
        List<Order> orders = databaseHelper.getAllOrders();
        orderAdapter.submitList(orders);
        tvOrdersEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadReservations() {
        List<Reservation> reservations = databaseHelper.getAllReservations();
        reservationAdapter.submitList(reservations);
        tvReservationsEmpty.setVisibility(reservations.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadServiceRequests() {
        List<ServiceRequest> requests = databaseHelper.getAllServiceRequests();
        serviceRequestAdapter.submitList(requests);
        tvServiceRequestsEmpty.setVisibility(requests.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showOrdersTab() {
        layoutOrders.setVisibility(View.VISIBLE);
        layoutReservations.setVisibility(View.GONE);
        layoutServiceRequests.setVisibility(View.GONE);
        btnTabOrders.setEnabled(false);
        btnTabReservations.setEnabled(true);
        btnTabServiceRequests.setEnabled(true);
    }

    private void showReservationsTab() {
        layoutOrders.setVisibility(View.GONE);
        layoutReservations.setVisibility(View.VISIBLE);
        layoutServiceRequests.setVisibility(View.GONE);
        btnTabOrders.setEnabled(true);
        btnTabReservations.setEnabled(false);
        btnTabServiceRequests.setEnabled(true);
    }

    private void showServiceRequestsTab() {
        layoutOrders.setVisibility(View.GONE);
        layoutReservations.setVisibility(View.GONE);
        layoutServiceRequests.setVisibility(View.VISIBLE);
        btnTabOrders.setEnabled(true);
        btnTabReservations.setEnabled(true);
        btnTabServiceRequests.setEnabled(false);
    }

    private void refreshAllEmployeeData() {
        loadDashboardStats();
        loadOrders();
        loadReservations();
        loadServiceRequests();
    }

    private void handleOrderStatus(Order order, Order.Status status) {
        boolean updated = databaseHelper.updateOrderStatus(order.getId(), status);
        Toast.makeText(this, updated ? R.string.employee_order_status_update_success : R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
        if (updated) {
            refreshAllEmployeeData();
        }
    }

    private void handleReservationStatus(Reservation reservation, Reservation.Status status) {
        boolean updated = databaseHelper.updateReservationStatus(reservation.getId(), status);
        Toast.makeText(this, updated ? R.string.employee_reservation_status_update_success : R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
        if (updated) {
            refreshAllEmployeeData();
        }
    }

    private void handleServiceRequestStatus(ServiceRequest request, ServiceRequest.Status status) {
        boolean updated = databaseHelper.updateServiceRequestStatus(request.getId(), status);
        Toast.makeText(this, updated ? R.string.employee_service_request_status_update_success : R.string.employee_status_update_failed, Toast.LENGTH_SHORT).show();
        if (updated) {
            refreshAllEmployeeData();
        }
    }

    private void dieuHuongSaiVaiTro() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = DieuHuongVaiTroHelper.taoIntentTheoVaiTro(this, sessionManager.getVaiTroHienTai());
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
