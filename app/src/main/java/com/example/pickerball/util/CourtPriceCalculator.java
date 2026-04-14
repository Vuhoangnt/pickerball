package com.example.pickerball.util;

import com.example.pickerball.DAO.CauHinhGiaDAO;
import com.example.pickerball.DAO.KhungGioDAO;
import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.Model.SanModel;

import java.util.List;
import java.util.Locale;

/**
 * Tính tiền sân theo thời lượng: (đ/giờ hiệu dụng) × (số giờ đặt).
 * Đ/giờ lấy từ cấu hình khung giờ (chia đều giá khung theo độ dài khung) hoặc {@link SanModel#giaMoiGio}.
 */
public final class CourtPriceCalculator {

    private CourtPriceCalculator() {}

    /** Khung chứa thời điểm bắt đầu (phút trong ngày). */
    public static KhungGioModel findKhungForStart(List<KhungGioModel> khungs, int startMin) {
        if (khungs == null || khungs.isEmpty()) return null;
        for (KhungGioModel k : khungs) {
            int a = DateUtils.toMinutes(k.gioBatDau);
            int b = DateUtils.toMinutes(k.gioKetThuc);
            if (a >= 0 && b >= 0 && startMin >= a && startMin < b) return k;
        }
        return khungs.get(0);
    }

    /**
     * Đơn giá một giờ (đ/giờ) để hiển thị và nhân với thời lượng.
     */
    public static double effectiveHourlyRate(SanModel san, String loaiNgay, String gioBatDau,
                                             CauHinhGiaDAO giaDao, KhungGioDAO kgDao) {
        if (san == null) return 120000;
        List<KhungGioModel> khungs = kgDao.getKhungForSan(san);
        int startMin = DateUtils.toMinutes(gioBatDau);
        KhungGioModel k = startMin >= 0 ? findKhungForStart(khungs, startMin) : null;
        double base = san.giaMoiGio > 0 ? san.giaMoiGio : 120000;
        if (k == null) return base;
        double g = giaDao.getGia(san.maSan, k.maKhung, loaiNgay);
        if (g > 0) {
            int slotMin = BookingTimeHelper.durationMinutes(k.gioBatDau, k.gioKetThuc);
            if (slotMin > 0) {
                return g / (slotMin / 60.0);
            }
        }
        return base;
    }

    /**
     * Tiền sân = đ/giờ × (phút đặt / 60), làm tròn đồng.
     */
    public static double computeCourtTotal(SanModel san, String loaiNgay, String gioBd, String gioKt,
                                           CauHinhGiaDAO giaDao, KhungGioDAO kgDao) {
        int minutes = BookingTimeHelper.durationMinutes(gioBd, gioKt);
        if (minutes <= 0) return 0;
        double hourly = effectiveHourlyRate(san, loaiNgay, gioBd, giaDao, kgDao);
        double raw = hourly * (minutes / 60.0);
        return Math.round(raw);
    }

    public static String formatDurationVi(int totalMinutes) {
        if (totalMinutes <= 0) return "—";
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        if (h > 0 && m > 0) {
            return String.format(Locale.getDefault(), "%d giờ %d phút", h, m);
        }
        if (h > 0) return String.format(Locale.getDefault(), "%d giờ", h);
        return String.format(Locale.getDefault(), "%d phút", m);
    }
}
