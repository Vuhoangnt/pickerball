package com.example.pickerball.util;

import com.example.pickerball.Model.SanModel;

import java.util.Calendar;

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

    /**
     * So sánh {@code yyyy-MM-dd} với hôm nay. Trả về:
     * <ul>
     *     <li>-1 nếu ngày đã qua</li>
     *     <li>0 nếu là hôm nay</li>
     *     <li>1 nếu là tương lai</li>
     * </ul>
     */
    public static int compareToToday(String yyyyMmDd) {
        if (yyyyMmDd == null || yyyyMmDd.length() < 10) return 1;
        int y, m, d;
        try {
            y = Integer.parseInt(yyyyMmDd.substring(0, 4));
            m = Integer.parseInt(yyyyMmDd.substring(5, 7));
            d = Integer.parseInt(yyyyMmDd.substring(8, 10));
        } catch (NumberFormatException e) {
            return 1;
        }
        Calendar today = Calendar.getInstance();
        int ty = today.get(Calendar.YEAR);
        int tm = today.get(Calendar.MONTH) + 1;
        int td = today.get(Calendar.DAY_OF_MONTH);
        if (y < ty) return -1;
        if (y > ty) return 1;
        if (m < tm) return -1;
        if (m > tm) return 1;
        return Integer.compare(d, td);
    }

    public static boolean isPastDate(String yyyyMmDd) {
        return compareToToday(yyyyMmDd) < 0;
    }

    /**
     * Kiểm tra thời điểm bắt đầu đặt sân đã qua so với hiện tại hay chưa.
     * Nếu là ngày tương lai → false. Nếu là ngày quá khứ → true. Nếu là hôm nay
     * thì so sánh giờ bắt đầu với giờ hiện tại (bao gồm phút).
     */
    public static boolean isStartInPast(String yyyyMmDd, String hhMm) {
        if (yyyyMmDd == null || hhMm == null) return false;
        int cmp = compareToToday(yyyyMmDd);
        if (cmp < 0) return true;
        if (cmp > 0) return false;
        int startMin = DateUtils.toMinutes(hhMm.trim());
        if (startMin < 0) return false;
        Calendar now = Calendar.getInstance();
        int nowMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        return startMin <= nowMin;
    }

    /**
     * Trả về số phút tối thiểu mà giờ bắt đầu phải cách hiện tại
     * (mặc định 0 = cho phép đặt trong khung hiện tại).
     */
    public static final int MIN_LEAD_MINUTES = 0;
}
