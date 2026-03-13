package com.example.quanlynhahang.model;

public class AdminDashboardStats {
    private final int totalUsers;
    private final int customerCount;
    private final int employeeCount;
    private final int adminCount;
    private final int totalDishes;
    private final int totalOrders;
    private final int pendingOrders;
    private final int pendingReservations;
    private final int processingServiceRequests;

    public AdminDashboardStats(int totalUsers,
                               int customerCount,
                               int employeeCount,
                               int adminCount,
                               int totalDishes,
                               int totalOrders,
                               int pendingOrders,
                               int pendingReservations,
                               int processingServiceRequests) {
        this.totalUsers = totalUsers;
        this.customerCount = customerCount;
        this.employeeCount = employeeCount;
        this.adminCount = adminCount;
        this.totalDishes = totalDishes;
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
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

    public int getTotalOrders() {
        return totalOrders;
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
