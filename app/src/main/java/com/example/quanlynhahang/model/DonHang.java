package com.example.quanlynhahang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonHang {
    public enum TrangThai {
        PENDING_CONFIRMATION,
        CONFIRMED,
        COMPLETED,
        CANCELED
    }

    private final long idDonHang;
    private final String maDon;
    private final String thoiGian;
    private final String tongTien;
    private final List<MonTrongDon> danhSachMon;
    private TrangThai trangThai;
    private boolean moRong;

    public DonHang(long id,
                   String code,
                   String time,
                   String totalPrice,
                   TrangThai status,
                   List<MonTrongDon> dishes) {
        this.idDonHang = id;
        this.maDon = code;
        this.thoiGian = time;
        this.tongTien = totalPrice;
        this.trangThai = status;
        this.danhSachMon = new ArrayList<>(dishes);
        this.moRong = false;
    }

    public long layId() {
        return idDonHang;
    }

    public String layMaDon() {
        return maDon;
    }

    public String layThoiGian() {
        return thoiGian;
    }

    public String layTongTien() {
        return tongTien;
    }

    public TrangThai layTrangThai() {
        return trangThai;
    }

    public List<MonTrongDon> layDanhSachMon() {
        return Collections.unmodifiableList(danhSachMon);
    }

    public boolean dangMoRong() {
        return moRong;
    }

    public void datTrangThaiMoRong(boolean moRong) {
        this.moRong = moRong;
    }

    public boolean coTheHuy() {
        return trangThai == TrangThai.PENDING_CONFIRMATION;
    }

    public void huyDon() {
        if (coTheHuy()) {
            trangThai = TrangThai.CANCELED;
        }
    }

    public static class MonTrongDon {
        private final MonAnDeXuat monAn;
        private final int soLuong;

        public MonTrongDon(MonAnDeXuat dishItem, int quantity) {
            this.monAn = dishItem;
            this.soLuong = quantity;
        }

        public MonAnDeXuat layMonAn() {
            return monAn;
        }

        public int laySoLuong() {
            return soLuong;
        }
    }
}
