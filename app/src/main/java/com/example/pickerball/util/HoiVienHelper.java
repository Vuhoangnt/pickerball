package com.example.pickerball.util;

import com.example.pickerball.AppConstants;

public final class HoiVienHelper {

    private HoiVienHelper() {}

    public static double discountPercentHang(String hang) {
        if (hang == null) return 0;
        if (AppConstants.HANG_VANG.equals(hang)) return 0.15;
        if (AppConstants.HANG_BAC.equals(hang)) return 0.08;
        return 0;
    }

    public static String labelHang(String hang) {
        if (AppConstants.HANG_VANG.equals(hang)) return "Vàng";
        if (AppConstants.HANG_BAC.equals(hang)) return "Bạc";
        return "Thường";
    }

    public static String displayTitleHang(String hang) {
        if (AppConstants.HANG_VANG.equals(hang)) return "VIP (Vàng)";
        if (AppConstants.HANG_BAC.equals(hang)) return "Hội viên (Bạc)";
        return "Thường";
    }

    public static String hangFromPoints(int diem) {
        if (diem >= 150) return AppConstants.HANG_VANG;
        if (diem >= 50) return AppConstants.HANG_BAC;
        return AppConstants.HANG_THUONG;
    }

    public static String rankProgressHint(int diem) {
        if (diem >= 150) return "Bạn đang ở hạng cao nhất theo điểm tích lũy.";
        if (diem >= 50) {
            return String.format(java.util.Locale.getDefault(), "Còn %d điểm nữa để đạt VIP (Vàng).", 150 - diem);
        }
        int toBac = 50 - diem;
        return String.format(java.util.Locale.getDefault(),
                "Còn %d điểm để lên Bạc; %d điểm để lên VIP (Vàng).",
                Math.max(0, toBac), Math.max(0, 150 - diem));
    }
}
