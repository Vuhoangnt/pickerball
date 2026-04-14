package com.example.pickerball.util;

import com.example.pickerball.Model.SanModel;

public final class BookingTimeHelper {

    public static final int MIN_BOOKING_MINUTES = 31;

    private BookingTimeHelper() {}

    public static int durationMinutes(String gioBatDau, String gioKetThuc) {
        if (gioBatDau == null || gioKetThuc == null) return 0;
        int s = DateUtils.toMinutes(gioBatDau.trim());
        int e = DateUtils.toMinutes(gioKetThuc.trim());
        if (s < 0 || e < 0 || e <= s) return 0;
        return e - s;
    }

    public static boolean isWithinSanHours(String gioBatDau, String gioKetThuc, SanModel san) {
        if (san == null) return false;
        String open = san.gioMoCua != null && !san.gioMoCua.isEmpty() ? san.gioMoCua : "06:00";
        String close = san.gioDongCua != null && !san.gioDongCua.isEmpty() ? san.gioDongCua : "22:00";
        int b = DateUtils.toMinutes(gioBatDau.trim());
        int k = DateUtils.toMinutes(gioKetThuc.trim());
        int o = DateUtils.toMinutes(open);
        int c = DateUtils.toMinutes(close);
        if (b < 0 || k < 0 || o < 0 || c < 0) return false;
        return b >= o && k <= c && k > b;
    }

    public static double tienSanTheoGio(SanModel san, String gioBatDau, String gioKetThuc) {
        if (san == null) return 0;
        int m = durationMinutes(gioBatDau, gioKetThuc);
        if (m <= 0) return 0;
        double gia = san.giaMoiGio > 0 ? san.giaMoiGio : 120000;
        return (m / 60.0) * gia;
    }

    public static String normalizeHhMm(String raw) {
        if (raw == null) return null;
        String t = raw.trim();
        if (t.isEmpty()) return null;
        String[] p = t.split(":");
        if (p.length < 2) return null;
        try {
            int h = Integer.parseInt(p[0].trim());
            int mi = Integer.parseInt(p[1].trim());
            if (h < 0 || h > 23 || mi < 0 || mi > 59) return null;
            return String.format(java.util.Locale.US, "%02d:%02d", h, mi);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
