package com.example.pickerball.util;

import java.util.Calendar;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {}

    /** thuong | cuoi_tuan | le — đơn giản: T7/CN = cuoi_tuan. */
    public static String loaiNgay(Calendar day) {
        int dow = day.get(Calendar.DAY_OF_WEEK);
        if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) return "cuoi_tuan";
        return "thuong";
    }

    public static int toMinutes(String hhMm) {
        if (hhMm == null) return -1;
        String t = hhMm.trim();
        String[] p = t.split(":");
        if (p.length < 2) return -1;
        try {
            int h = Integer.parseInt(p[0].trim());
            int m = Integer.parseInt(p[1].trim());
            if (h < 0 || h > 23 || m < 0 || m > 59) return -1;
            return h * 60 + m;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
