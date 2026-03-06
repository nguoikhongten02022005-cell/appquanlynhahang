package com.example.quanlynhahang.model;

public class Reservation {

    public enum Status {
        PENDING_APPROVAL,
        CONFIRMED,
        COMPLETED,
        CANCELED
    }

    private final long id;
    private final String time;
    private final String tableNumber;
    private final int guestCount;
    private final String note;
    private Status status;

    public Reservation(long id,
                       String time,
                       String tableNumber,
                       int guestCount,
                       String note,
                       Status status) {
        this.id = id;
        this.time = time;
        this.tableNumber = tableNumber;
        this.guestCount = guestCount;
        this.note = note;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getTableNumber() {
        return tableNumber;
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
