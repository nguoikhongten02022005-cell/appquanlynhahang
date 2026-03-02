package com.example.quanlynhahang.model;

public class CategoryItem {
    private final int iconResId;
    private final String name;

    public CategoryItem(int iconResId, String name) {
        this.iconResId = iconResId;
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }
}
