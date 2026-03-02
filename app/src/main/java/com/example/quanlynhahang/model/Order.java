package com.example.quanlynhahang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    public enum Status {
        PENDING_CONFIRMATION,
        CONFIRMED,
        COMPLETED,
        CANCELED
    }

    private final String code;
    private final String time;
    private final String totalPrice;
    private final List<OrderDish> dishes;
    private Status status;
    private boolean expanded;

    public Order(String code,
                 String time,
                 String totalPrice,
                 Status status,
                 List<OrderDish> dishes) {
        this.code = code;
        this.time = time;
        this.totalPrice = totalPrice;
        this.status = status;
        this.dishes = new ArrayList<>(dishes);
        this.expanded = false;
    }

    public String getCode() {
        return code;
    }

    public String getTime() {
        return time;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public Status getStatus() {
        return status;
    }

    public List<OrderDish> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean canCancel() {
        return status == Status.PENDING_CONFIRMATION;
    }

    public void cancel() {
        if (canCancel()) {
            status = Status.CANCELED;
        }
    }

    public static class OrderDish {
        private final RecommendedDishItem dishItem;
        private final int quantity;

        public OrderDish(RecommendedDishItem dishItem, int quantity) {
            this.dishItem = dishItem;
            this.quantity = quantity;
        }

        public RecommendedDishItem getDishItem() {
            return dishItem;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
