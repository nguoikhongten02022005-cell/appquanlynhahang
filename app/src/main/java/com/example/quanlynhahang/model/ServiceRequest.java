package com.example.quanlynhahang.model;

public class ServiceRequest {

    public enum Status {
        PROCESSING,
        DONE
    }

    private final long id;
    private final String noiDung;
    private final String thoiGianGui;
    private Status trangThai;

    public ServiceRequest(String noiDung, String thoiGianGui, Status trangThai) {
        this(0L, noiDung, thoiGianGui, trangThai);
    }

    public ServiceRequest(long id, String noiDung, String thoiGianGui, Status trangThai) {
        this.id = id;
        this.noiDung = noiDung;
        this.thoiGianGui = thoiGianGui;
        this.trangThai = trangThai;
    }

    public long getId() {
        return id;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public String getThoiGianGui() {
        return thoiGianGui;
    }

    public Status getTrangThai() {
        return trangThai;
    }

    public String getContent() {
        return getNoiDung();
    }

    public String getSentTime() {
        return getThoiGianGui();
    }

    public Status getStatus() {
        return getTrangThai();
    }

    public void danhDauDaXong() {
        trangThai = Status.DONE;
    }

    public void markDone() {
        danhDauDaXong();
    }
}
