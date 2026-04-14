package com.example.pickerball.util;

import com.example.pickerball.AppConstants;

public final class TrangThaiLabels {

    private TrangThaiLabels() {}

    public static String vn(String trangThai) {
        if (trangThai == null) return "—";
        switch (trangThai) {
            case AppConstants.DS_CHO_DUYET:
                return "Chờ duyệt";
            case AppConstants.DS_DA_DUYET:
                return "Đã duyệt";
            case AppConstants.DS_HUY:
                return "Đã hủy";
            case AppConstants.DS_TU_CHOI:
                return "Từ chối";
            case AppConstants.DS_DA_XONG:
                return "Hoàn thành";
            default:
                return trangThai;
        }
    }
}
