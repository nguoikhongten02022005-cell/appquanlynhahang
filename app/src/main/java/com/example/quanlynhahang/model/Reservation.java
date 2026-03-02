package com.example.quanlynhahang.model;

public class Reservation {

    public enum Status {
        PENDING_APPROVAL,
        CONFIRMED,
        COMPLETED,
        CANCELED
    }

    private final String time;
    private final int guestCount;
    private final String note;
    private Status status;

    public Reservation(String time, int guestCount, String note, Status status) {
        this.time = time;
        this.guestCount = guestCount;
        this.note = note;
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public String getNote() {
        return note;
    }

    public Status getStatus() {
        return status;
    }

    public boolean canCancel() {
        return status == Status.PENDING_APPROVAL;
    }

    public void cancel() {
        if (canCancel()) {
            status = Status.CANCELED;
        }
    }
}
