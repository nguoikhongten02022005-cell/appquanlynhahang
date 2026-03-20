package com.example.quanlynhahang.model;

public class ThongKeTongQuanAdmin {
    private final int totalUsers;
    private final int customerCount;
    private final int employeeCount;
    private final int adminCount;
    private final int totalDishes;
    private final int totalDonHangs;
    private final int pendingDonHangs;
    private final int pendingReservations;
    private final int processingServiceRequests;

    public ThongKeTongQuanAdmin(int totalUsers,
                                int customerCount,
                                int employeeCount,
                                int adminCount,
                                int totalDishes,
                                int totalDonHangs,
                                int pendingDonHangs,
                                int pendingReservations,
                                int processingServiceRequests) {
        this.totalUsers = totalUsers;
        this.customerCount = customerCount;
        this.employeeCount = employeeCount;
        this.adminCount = adminCount;
        this.totalDishes = totalDishes;
        this.totalDonHangs = totalDonHangs;
        this.pendingDonHangs = pendingDonHangs;
        this.pendingReservations = pendingReservations;
        this.processingServiceRequests = processingServiceRequests;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public int getAdminCount() {
        return adminCount;
    }

    public int getTotalDishes() {
        return totalDishes;
    }

    public int getTotalDonHangs() {
        return totalDonHangs;
    }

    public int getPendingDonHangs() {
        return pendingDonHangs;
    }

    public int getPendingReservations() {
        return pendingReservations;
    }

    public int getProcessingServiceRequests() {
        return processingServiceRequests;
    }
}
