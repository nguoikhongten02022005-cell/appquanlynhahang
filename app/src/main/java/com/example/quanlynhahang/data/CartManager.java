package com.example.quanlynhahang.data;

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

    private static CartManager instance;

    private final Map<String, CartItem> itemMap = new LinkedHashMap<>();
    private final List<CartListener> listeners = new ArrayList<>();

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
        if (itemMap.isEmpty()) {
            return;
        }

        itemMap.clear();
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

    private String buildDishKey(MonAnDeXuat dish) {
        return dish.layTenMon() + "|" + dish.layGiaBan();
    }

    private void notifyCartChanged() {
        List<CartListener> snapshot = new ArrayList<>(listeners);
        for (CartListener listener : snapshot) {
            listener.onCartChanged();
        }
    }
}
