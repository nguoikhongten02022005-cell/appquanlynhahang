package com.example.quanlynhahang.data;

import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    public interface CartListener {
        void onCartChanged();
    }

    public static class CartItem {
        private final MonAnDeXuat dish;
        private int quantity;

        public CartItem(MonAnDeXuat dish, int quantity) {
            this.dish = dish;
            this.quantity = quantity;
        }

        public MonAnDeXuat getDish() {
            return dish;
        }

        public MonAnDeXuat layMonAn() {
            return getDish();
        }

        public int getQuantity() {
            return quantity;
        }

        public int laySoLuong() {
            return getQuantity();
        }

        public void increaseQuantity() {
            quantity++;
        }

        public void tangSoLuong() {
            increaseQuantity();
        }

        public void decreaseQuantity() {
            if (quantity > 0) {
                quantity--;
            }
        }

        public void giamSoLuong() {
            decreaseQuantity();
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

    private static CartManager instance;

    private final Map<String, CartItem> itemMap = new LinkedHashMap<>();
    private final List<CartListener> listeners = new ArrayList<>();
    private final NguCanhDonHang nguCanhDonHang = new NguCanhDonHang();

    private CartManager() {
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public synchronized void addToCart(MonAnDeXuat dish) {
        if (dish == null) {
            return;
        }

        String key = buildDishKey(dish);
        CartItem existingItem = itemMap.get(key);
        if (existingItem == null) {
            itemMap.put(key, new CartItem(dish, 1));
        } else {
            existingItem.increaseQuantity();
        }

        notifyCartChanged();
    }

    public synchronized void themVaoGio(MonAnDeXuat dish) {
        addToCart(dish);
    }

    public synchronized void increaseQuantity(String dishKey) {
        CartItem item = itemMap.get(dishKey);
        if (item == null) {
            return;
        }

        item.increaseQuantity();
        notifyCartChanged();
    }

    public synchronized void tangSoLuong(String dishKey) {
        increaseQuantity(dishKey);
    }

    public synchronized void decreaseQuantity(String dishKey) {
        CartItem item = itemMap.get(dishKey);
        if (item == null) {
            return;
        }

        item.decreaseQuantity();
        if (item.laySoLuong() <= 0) {
            itemMap.remove(dishKey);
        }

        notifyCartChanged();
    }

    public synchronized void giamSoLuong(String dishKey) {
        decreaseQuantity(dishKey);
    }

    public synchronized void removeItem(String dishKey) {
        if (itemMap.remove(dishKey) != null) {
            notifyCartChanged();
        }
    }

    public synchronized void xoaMon(String dishKey) {
        removeItem(dishKey);
    }

    public synchronized void clearCart() {
        if (itemMap.isEmpty() && laNguCanhMacDinh()) {
            return;
        }

        itemMap.clear();
        nguCanhDonHang.datMacDinh();
        notifyCartChanged();
    }

    public synchronized void xoaToanBoGio() {
        clearCart();
    }

    public synchronized List<CartItem> getItems() {
        return new ArrayList<>(itemMap.values());
    }

    public synchronized List<CartItem> layDanhSachMon() {
        return getItems();
    }

    public synchronized int getTotalQuantity() {
        int total = 0;
        for (CartItem item : itemMap.values()) {
            total += item.laySoLuong();
        }
        return total;
    }

    public synchronized int layTongSoLuong() {
        return getTotalQuantity();
    }

    public synchronized boolean isEmpty() {
        return itemMap.isEmpty();
    }

    public synchronized boolean laGioHangRong() {
        return isEmpty();
    }

    public synchronized void addListener(CartListener listener) {
        if (listener == null || listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public synchronized void themLangNghe(CartListener listener) {
        addListener(listener);
    }

    public synchronized void removeListener(CartListener listener) {
        listeners.remove(listener);
    }

    public synchronized void xoaLangNghe(CartListener listener) {
        removeListener(listener);
    }

    public synchronized String getDishKey(CartItem item) {
        return buildDishKey(item.getDish());
    }

    public synchronized String layKhoaMon(CartItem item) {
        return getDishKey(item);
    }

    public synchronized void capNhatNguCanhDonHang(DonHang.HinhThucDon hinhThucDon, String soBan, String ghiChu) {
        nguCanhDonHang.capNhat(hinhThucDon, soBan, ghiChu);
        notifyCartChanged();
    }

    public synchronized NguCanhDonHang layNguCanhDonHang() {
        NguCanhDonHang banSao = new NguCanhDonHang();
        banSao.capNhat(nguCanhDonHang.layHinhThucDon(), nguCanhDonHang.laySoBan(), nguCanhDonHang.layGhiChu());
        return banSao;
    }

    public synchronized void datNguCanhMacDinh() {
        nguCanhDonHang.datMacDinh();
        notifyCartChanged();
    }

    private boolean laNguCanhMacDinh() {
        return nguCanhDonHang.layHinhThucDon() == DonHang.HinhThucDon.MANG_DI
                && nguCanhDonHang.laySoBan().isEmpty()
                && nguCanhDonHang.layGhiChu().isEmpty();
    }

    private String buildDishKey(MonAnDeXuat dish) {
        if (dish == null) {
            return "";
        }
        return dish.layImageResId()
                + "|" + (dish.layTenMon() == null ? "" : dish.layTenMon().trim())
                + "|" + (dish.layGiaBan() == null ? "" : dish.layGiaBan().trim())
                + "|" + (dish.layTenDanhMuc() == null ? "" : dish.layTenDanhMuc().trim());
    }

    private void notifyCartChanged() {
        List<CartListener> snapshot = new ArrayList<>(listeners);
        for (CartListener listener : snapshot) {
            listener.onCartChanged();
        }
    }
}
