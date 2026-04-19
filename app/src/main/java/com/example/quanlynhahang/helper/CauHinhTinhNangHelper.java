package com.example.quanlynhahang.helper;

public final class CauHinhTinhNangHelper {

    private static boolean choPhepNoiBoShellMoi;

    private CauHinhTinhNangHelper() {
    }

    public static boolean coNoiBoShellMoi() {
        return choPhepNoiBoShellMoi;
    }

    public static void setChoPhepNoiBoShellMoi(boolean duocBat) {
        choPhepNoiBoShellMoi = duocBat;
    }
}
