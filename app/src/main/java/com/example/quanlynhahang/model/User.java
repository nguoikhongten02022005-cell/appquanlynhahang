package com.example.quanlynhahang.model;

public class User {

    private final long id;
    private final String name;
    private final String email;
    private final String phone;
    private final UserRole role;
    private final boolean isActive;

    public User(long id, String name, String email, String phone, UserRole role, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean laKhachHang() {
        return role == UserRole.KHACH_HANG;
    }

    public boolean laNhanVien() {
        return role == UserRole.NHAN_VIEN;
    }

    public boolean laAdmin() {
        return role == UserRole.ADMIN;
    }
}
