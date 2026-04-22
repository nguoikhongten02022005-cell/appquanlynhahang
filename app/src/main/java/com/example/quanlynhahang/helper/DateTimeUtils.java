package com.example.quanlynhahang.helper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtils {

    private static final String DINH_DANG_THOI_GIAN_DON_HANG = "dd/MM/yyyy HH:mm";

    private DateTimeUtils() {
    }

    public static String layThoiGianHienTai() {
        return new SimpleDateFormat(DINH_DANG_THOI_GIAN_DON_HANG, Locale.getDefault()).format(new Date());
    }

    public static long parseDonHangTimeToMillis(@Nullable String timeRaw) {
        if (timeRaw == null || timeRaw.isEmpty()) {
            return 0L;
        }

        try {
            Date parsedDate = new SimpleDateFormat(DINH_DANG_THOI_GIAN_DON_HANG, Locale.getDefault()).parse(timeRaw);
            return parsedDate == null ? 0L : parsedDate.getTime();
        } catch (ParseException ex) {
            return 0L;
        }
    }

    public static String formatCalendar(@Nullable Calendar calendar) {
        if (calendar == null) {
            return "";
        }
        return new SimpleDateFormat(DINH_DANG_THOI_GIAN_DON_HANG, Locale.getDefault()).format(calendar.getTime());
    }
}
