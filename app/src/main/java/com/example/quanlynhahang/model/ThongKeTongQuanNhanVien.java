package com.example.quanlynhahang.model;

public class ThongKeTongQuanNhanVien {
    private final int pendingDonHangs;
    private final int pendingReservations;
    private final int processingServiceRequests;

    public ThongKeTongQuanNhanVien(int pendingDonHangs, int pendingReservations, int processingServiceRequests) {
        this.pendingDonHangs = pendingDonHangs;
        this.pendingReservations = pendingReservations;
        this.processingServiceRequests = processingServiceRequests;
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
