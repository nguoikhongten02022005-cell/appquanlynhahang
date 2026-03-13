package com.example.quanlynhahang.model;

public class EmployeeDashboardStats {
    private final int pendingOrders;
    private final int pendingReservations;
    private final int processingServiceRequests;

    public EmployeeDashboardStats(int pendingOrders, int pendingReservations, int processingServiceRequests) {
        this.pendingOrders = pendingOrders;
        this.pendingReservations = pendingReservations;
        this.processingServiceRequests = processingServiceRequests;
    }

    public int getPendingOrders() {
        return pendingOrders;
    }

    public int getPendingReservations() {
        return pendingReservations;
    }

    public int getProcessingServiceRequests() {
        return processingServiceRequests;
    }
}
