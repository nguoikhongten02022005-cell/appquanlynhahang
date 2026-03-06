package com.example.quanlynhahang.data;

import com.example.quanlynhahang.model.RecommendedDishItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    public interface CartListener {
        void onCartChanged();
    }

    public static class CartItem {
        private final RecommendedDishItem dish;
        private int quantity;

        public CartItem(RecommendedDishItem dish, int quantity) {
            this.dish = dish;
            this.quantity = quantity;
        }

        public RecommendedDishItem getDish() {
            return dish;
        }

        public int getQuantity() {
            return quantity;
        }

        public void increaseQuantity() {
            quantity++;
        }

        public void decreaseQuantity() {
            if (quantity > 0) {
                quantity--;
            }
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

    public synchronized void addToCart(RecommendedDishItem dish) {
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

    public synchronized void increaseQuantity(String dishKey) {
        CartItem item = itemMap.get(dishKey);
        if (item == null) {
            return;
        }

        item.increaseQuantity();
        notifyCartChanged();
    }

    public synchronized void decreaseQuantity(String dishKey) {
        CartItem item = itemMap.get(dishKey);
        if (item == null) {
            return;
        }

        item.decreaseQuantity();
        if (item.getQuantity() <= 0) {
            itemMap.remove(dishKey);
        }

        notifyCartChanged();
    }

    public synchronized void removeItem(String dishKey) {
        if (itemMap.remove(dishKey) != null) {
            notifyCartChanged();
        }
    }

    public synchronized void clearCart() {
        if (itemMap.isEmpty()) {
            return;
        }

        itemMap.clear();
        notifyCartChanged();
    }

    public synchronized List<CartItem> getItems() {
        return new ArrayList<>(itemMap.values());
    }

    public synchronized int getTotalQuantity() {
        int total = 0;
        for (CartItem item : itemMap.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    public synchronized boolean isEmpty() {
        return itemMap.isEmpty();
    }

    public synchronized void addListener(CartListener listener) {
        if (listener == null || listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public synchronized void removeListener(CartListener listener) {
        listeners.remove(listener);
    }

    public synchronized String getDishKey(CartItem item) {
        return buildDishKey(item.getDish());
    }

    private String buildDishKey(RecommendedDishItem dish) {
        return dish.getName() + "|" + dish.getPrice();
    }

    private void notifyCartChanged() {
        List<CartListener> snapshot = new ArrayList<>(listeners);
        for (CartListener listener : snapshot) {
            listener.onCartChanged();
        }
    }
}
