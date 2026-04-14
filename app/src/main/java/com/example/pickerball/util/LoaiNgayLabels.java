package com.example.pickerball.util;

import java.util.Locale;

public final class LoaiNgayLabels {

    private LoaiNgayLabels() {}

    public static String label(String loaiNgay) {
        if (loaiNgay == null) return "—";
        switch (loaiNgay.toLowerCase(Locale.ROOT)) {
            case "thuong":
                return "Ngày thường";
            case "cuoi_tuan":
                return "Cuối tuần";
            case "le":
                return "Ngày lễ";
            default:
                return loaiNgay;
        }
    }
}
