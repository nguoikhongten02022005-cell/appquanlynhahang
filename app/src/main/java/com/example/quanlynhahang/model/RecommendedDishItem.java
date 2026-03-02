package com.example.quanlynhahang.model;

public class RecommendedDishItem {
    private final int imageResId;
    private final String name;
    private final String price;
    private final boolean available;

    public RecommendedDishItem(int imageResId, String name, String price, boolean available) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }
}
