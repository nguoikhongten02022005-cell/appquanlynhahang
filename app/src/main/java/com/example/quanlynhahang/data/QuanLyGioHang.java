package com.example.quanlynhahang.data;

import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuanLyGioHang {

    public interface LangNgheGioHang {
        void khiGioHangThayDoi();
    }

    public static class MonTrongGio {
        private final MonAnDeXuat monAn;
        private int soLuong;

        public MonTrongGio(MonAnDeXuat monAn, int soLuong) {
            this.monAn = monAn;
            this.soLuong = soLuong;
        }

        public MonAnDeXuat layMonAn() {
            return monAn;
        }

        public int laySoLuong() {
            return soLuong;
        }

        public void tangSoLuong() {
            soLuong++;
        }

        public void giamSoLuong() {
            if (soLuong > 0) {
                soLuong--;
            }
        }
    }

    public static class NguCanhDonHang {
        private DonHang.HinhThucDon hinhThucDon = DonHang.HinhThucDon.MANG_DI;
        private String soBan = "";
        private String ghiChu = "";

        public DonHang.HinhThucDon layHinhThucDon() {
            return hinhThucDon;
        }

        public String laySoBan() {
            return soBan;
        }

        public String layGhiChu() {
            return ghiChu;
        }

        public boolean laAnTaiQuan() {
            return hinhThucDon == DonHang.HinhThucDon.AN_TAI_QUAN;
        }

        public boolean hopLeDeDatHang() {
            return !laAnTaiQuan() || !soBan.trim().isEmpty();
        }

        private void capNhat(DonHang.HinhThucDon hinhThucDon, String soBan, String ghiChu) {
            this.hinhThucDon = hinhThucDon == null ? DonHang.HinhThucDon.MANG_DI : hinhThucDon;
            this.soBan = soBan == null ? "" : soBan.trim();
            this.ghiChu = ghiChu == null ? "" : ghiChu.trim();
            if (this.hinhThucDon == DonHang.HinhThucDon.MANG_DI) {
                this.soBan = "";
            }
        }

        private void datMacDinh() {
            capNhat(DonHang.HinhThucDon.MANG_DI, "", "");
        }
    }

    private static QuanLyGioHang instance;

    private final Map<String, MonTrongGio> bangMonTrongGio = new LinkedHashMap<>();
    private final List<LangNgheGioHang> danhSachLangNghe = new ArrayList<>();
    private final NguCanhDonHang nguCanhDonHang = new NguCanhDonHang();

    private QuanLyGioHang() {
    }

    public static synchronized QuanLyGioHang layInstance() {
        if (instance == null) {
            instance = new QuanLyGioHang();
        }
        return instance;
    }

    public synchronized void themVaoGio(MonAnDeXuat monAn) {
        if (monAn == null) {
            return;
        }

        String khoaMon = taoKhoaMon(monAn);
        MonTrongGio monTrongGioHienTai = bangMonTrongGio.get(khoaMon);
        if (monTrongGioHienTai == null) {
            bangMonTrongGio.put(khoaMon, new MonTrongGio(monAn, 1));
        } else {
            monTrongGioHienTai.tangSoLuong();
        }

        thongBaoGioHangThayDoi();
    }

    public synchronized void tangSoLuong(String khoaMon) {
        MonTrongGio monTrongGio = bangMonTrongGio.get(khoaMon);
        if (monTrongGio == null) {
            return;
        }

        monTrongGio.tangSoLuong();
        thongBaoGioHangThayDoi();
    }

    public synchronized void giamSoLuong(String khoaMon) {
        MonTrongGio monTrongGio = bangMonTrongGio.get(khoaMon);
        if (monTrongGio == null) {
            return;
        }

        monTrongGio.giamSoLuong();
        if (monTrongGio.laySoLuong() <= 0) {
            bangMonTrongGio.remove(khoaMon);
        }

        thongBaoGioHangThayDoi();
    }

    public synchronized void xoaMon(String khoaMon) {
        if (bangMonTrongGio.remove(khoaMon) != null) {
            thongBaoGioHangThayDoi();
        }
    }

    public synchronized void xoaToanBoGio() {
        if (bangMonTrongGio.isEmpty() && laNguCanhMacDinh()) {
            return;
        }

        bangMonTrongGio.clear();
        nguCanhDonHang.datMacDinh();
        thongBaoGioHangThayDoi();
    }

    public synchronized List<MonTrongGio> layDanhSachMon() {
        return new ArrayList<>(bangMonTrongGio.values());
    }

    public synchronized int layTongSoLuong() {
        int tongSoLuong = 0;
        for (MonTrongGio monTrongGio : bangMonTrongGio.values()) {
            tongSoLuong += monTrongGio.laySoLuong();
        }
        return tongSoLuong;
    }

    public synchronized boolean laGioHangRong() {
        return bangMonTrongGio.isEmpty();
    }

    public synchronized void themLangNghe(LangNgheGioHang langNgheGioHang) {
        if (langNgheGioHang == null || danhSachLangNghe.contains(langNgheGioHang)) {
            return;
        }
        danhSachLangNghe.add(langNgheGioHang);
    }

    public synchronized void xoaLangNghe(LangNgheGioHang langNgheGioHang) {
        danhSachLangNghe.remove(langNgheGioHang);
    }

    public synchronized String layKhoaMon(MonTrongGio monTrongGio) {
        return taoKhoaMon(monTrongGio.layMonAn());
    }

    public synchronized void capNhatNguCanhDonHang(DonHang.HinhThucDon hinhThucDon, String soBan, String ghiChu) {
        nguCanhDonHang.capNhat(hinhThucDon, soBan, ghiChu);
        thongBaoGioHangThayDoi();
    }

    public synchronized NguCanhDonHang layNguCanhDonHang() {
        NguCanhDonHang banSao = new NguCanhDonHang();
        banSao.capNhat(nguCanhDonHang.layHinhThucDon(), nguCanhDonHang.laySoBan(), nguCanhDonHang.layGhiChu());
        return banSao;
    }

    public synchronized void datNguCanhMacDinh() {
        nguCanhDonHang.datMacDinh();
        thongBaoGioHangThayDoi();
    }

    private boolean laNguCanhMacDinh() {
        return nguCanhDonHang.layHinhThucDon() == DonHang.HinhThucDon.MANG_DI
                && nguCanhDonHang.laySoBan().isEmpty()
                && nguCanhDonHang.layGhiChu().isEmpty();
    }

    private String taoKhoaMon(MonAnDeXuat monAn) {
        if (monAn == null) {
            return "";
        }
        return monAn.layIdAnhTaiNguyen()
                + "|" + (monAn.layTenMon() == null ? "" : monAn.layTenMon().trim())
                + "|" + (monAn.layGiaBan() == null ? "" : monAn.layGiaBan().trim())
                + "|" + (monAn.layTenDanhMuc() == null ? "" : monAn.layTenDanhMuc().trim());
    }

    private void thongBaoGioHangThayDoi() {
        List<LangNgheGioHang> danhSachLangNgheTam = new ArrayList<>(danhSachLangNghe);
        for (LangNgheGioHang langNgheGioHang : danhSachLangNgheTam) {
            langNgheGioHang.khiGioHangThayDoi();
        }
    }
}
