package com.example.pickerball.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.AppConstants;
import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.NhanVienModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Thống kê admin: theo khoảng ngày. */
public class ThongKeDAO {

    private final SQLiteDatabase db;
    private final HoaDonDAO hoaDonDAO;
    private final Context appContext;

    public ThongKeDAO(Context context) {
        appContext = context.getApplicationContext();
        db = new DatabaseHelper(context).getWritableDatabase();
        hoaDonDAO = new HoaDonDAO(context);
    }

    public static class TomTat {
        public int tongPhieuDat;
        public double tongDuKienPhieu;
        /** Doanh thu thực thu (HĐ đã TT) — tiền vào. */
        public double doanhThuHoaDon;
        public int soHoaDonDaTt;
        public double tienSanDaTt;
        public double tienDichVuDaTt;
        /** Chi phí vận hành trong kỳ — tiền ra. */
        public double tongChiPhi;
        /** Doanh thu − chi phí (gộp theo kỳ). */
        public double loiNhuanGop;
    }

    /** Tiền vào / ra theo từng ngày trong khoảng (đủ mốc, thiếu = 0). */
    public static class TienTheoNgay {
        public String ngay;
        public double tienVao;
        public double tienRa;
    }

    public static class DemTrangThai {
        public String trangThai;
        public int soLuong;
    }

    public static class DemHinhThuc {
        public String hinhThuc;
        public int soLuong;
    }

    public static class DemTheoNgay {
        public String ngay;
        public int soLuong;
    }

    public TomTat tomTat(String tuNgay, String denNgay) {
        TomTat t = new TomTat();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*), COALESCE(SUM(tong_du_kien),0) FROM dat_san WHERE date(ngay_dat) BETWEEN date(?) AND date(?)",
                new String[]{tuNgay, denNgay});
        if (c.moveToFirst()) {
            t.tongPhieuDat = c.getInt(0);
            t.tongDuKienPhieu = c.getDouble(1);
        }
        c.close();
        t.doanhThuHoaDon = hoaDonDAO.sumPaidBetween(tuNgay, denNgay);
        t.soHoaDonDaTt = hoaDonDAO.countPaidBetween(tuNgay, denNgay);
        t.tienSanDaTt = hoaDonDAO.sumPaidTienSanBetween(tuNgay, denNgay);
        t.tienDichVuDaTt = hoaDonDAO.sumPaidTienDvBetween(tuNgay, denNgay);
        t.tongChiPhi = sumChiPhiBetween(tuNgay, denNgay);
        t.loiNhuanGop = t.doanhThuHoaDon - t.tongChiPhi;
        return t;
    }

    public double sumChiPhiBetween(String tuNgay, String denNgay) {
        Cursor c = db.rawQuery(
                "SELECT COALESCE(SUM(so_tien),0) FROM chi_phi WHERE date(ngay) BETWEEN date(?) AND date(?)",
                new String[]{tuNgay, denNgay});
        double s = 0;
        if (c.moveToFirst()) s = c.getDouble(0);
        c.close();
        return s;
    }

    public List<TienTheoNgay> tomTatTienTheoNgayLienTuc(String tuNgay, String denNgay) {
        Map<String, Double> vao = new HashMap<>();
        Cursor c = db.rawQuery(
                "SELECT date(ngay_tt), SUM(tong_tien) FROM hoa_don WHERE trang_thai=1 AND ngay_tt IS NOT NULL AND ngay_tt != '' "
                        + "AND date(ngay_tt) BETWEEN date(?) AND date(?) GROUP BY date(ngay_tt) ORDER BY 1",
                new String[]{tuNgay, denNgay});
        while (c.moveToNext()) {
            String ng = c.getString(0);
            if (ng != null) vao.put(ng, c.getDouble(1));
        }
        c.close();
        Map<String, Double> ra = new HashMap<>();
        Cursor c2 = db.rawQuery(
                "SELECT date(ngay), SUM(so_tien) FROM chi_phi WHERE date(ngay) BETWEEN date(?) AND date(?) GROUP BY date(ngay) ORDER BY 1",
                new String[]{tuNgay, denNgay});
        while (c2.moveToNext()) {
            String ng = c2.getString(0);
            if (ng != null) ra.put(ng, c2.getDouble(1));
        }
        c2.close();
        List<TienTheoNgay> out = new ArrayList<>();
        String[] a = tuNgay.split("-");
        String[] b = denNgay.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(a[0]), Integer.parseInt(a[1]) - 1, Integer.parseInt(a[2]));
        Calendar end = Calendar.getInstance();
        end.set(Integer.parseInt(b[0]), Integer.parseInt(b[1]) - 1, Integer.parseInt(b[2]));
        while (!cal.after(end)) {
            String key = String.format(Locale.US, "%04d-%02d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            TienTheoNgay x = new TienTheoNgay();
            x.ngay = key;
            x.tienVao = vao.getOrDefault(key, 0d);
            x.tienRa = ra.getOrDefault(key, 0d);
            out.add(x);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return out;
    }

    public List<DemTrangThai> demTheoTrangThai(String tuNgay, String denNgay) {
        List<DemTrangThai> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT trang_thai, COUNT(*) FROM dat_san WHERE date(ngay_dat) BETWEEN date(?) AND date(?) GROUP BY trang_thai ORDER BY trang_thai",
                new String[]{tuNgay, denNgay});
        while (c.moveToNext()) {
            DemTrangThai d = new DemTrangThai();
            d.trangThai = c.getString(0);
            d.soLuong = c.getInt(1);
            list.add(d);
        }
        c.close();
        return list;
    }

    public List<DemHinhThuc> demTheoHinhThuc(String tuNgay, String denNgay) {
        List<DemHinhThuc> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT hinh_thuc, COUNT(*) FROM dat_san WHERE date(ngay_dat) BETWEEN date(?) AND date(?) GROUP BY hinh_thuc ORDER BY hinh_thuc",
                new String[]{tuNgay, denNgay});
        while (c.moveToNext()) {
            DemHinhThuc d = new DemHinhThuc();
            d.hinhThuc = c.getString(0);
            d.soLuong = c.getInt(1);
            list.add(d);
        }
        c.close();
        return list;
    }

    public List<DemTheoNgay> demPhieuTheoNgay(String tuNgay, String denNgay) {
        List<DemTheoNgay> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ngay_dat, COUNT(*) FROM dat_san WHERE date(ngay_dat) BETWEEN date(?) AND date(?) GROUP BY ngay_dat ORDER BY ngay_dat",
                new String[]{tuNgay, denNgay});
        while (c.moveToNext()) {
            DemTheoNgay d = new DemTheoNgay();
            d.ngay = c.getString(0);
            d.soLuong = c.getInt(1);
            list.add(d);
        }
        c.close();
        return list;
    }

    /** Điền đủ từng ngày trong khoảng (0 nếu không có phiếu). */
    public List<DemTheoNgay> demPhieuTheoNgayLienTuc(String tuNgay, String denNgay) {
        List<DemTheoNgay> raw = demPhieuTheoNgay(tuNgay, denNgay);
        Map<String, Integer> map = new HashMap<>();
        for (DemTheoNgay d : raw) {
            if (d.ngay != null) map.put(d.ngay, d.soLuong);
        }
        List<DemTheoNgay> out = new ArrayList<>();
        String[] a = tuNgay.split("-");
        String[] b = denNgay.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(a[0]), Integer.parseInt(a[1]) - 1, Integer.parseInt(a[2]));
        Calendar end = Calendar.getInstance();
        end.set(Integer.parseInt(b[0]), Integer.parseInt(b[1]) - 1, Integer.parseInt(b[2]));
        while (!cal.after(end)) {
            String key = String.format(Locale.US, "%04d-%02d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            DemTheoNgay x = new DemTheoNgay();
            x.ngay = key;
            x.soLuong = map.getOrDefault(key, 0);
            out.add(x);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return out;
    }

    /** 12 tháng trong năm (kể cả tháng 0 phiếu). */
    public List<DemTheoNgay> demPhieuTheoThangTrongNam(int year) {
        String y = String.valueOf(year);
        Map<String, Integer> map = new HashMap<>();
        Cursor c = db.rawQuery(
                "SELECT strftime('%m', ngay_dat), COUNT(*) FROM dat_san WHERE strftime('%Y', ngay_dat)=? "
                        + "GROUP BY strftime('%m', ngay_dat) ORDER BY 1",
                new String[]{y});
        while (c.moveToNext()) {
            map.put(c.getString(0), c.getInt(1));
        }
        c.close();
        List<DemTheoNgay> out = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            String mm = String.format(Locale.US, "%02d", m);
            DemTheoNgay d = new DemTheoNgay();
            d.ngay = y + "-" + mm;
            d.soLuong = map.getOrDefault(mm, 0);
            out.add(d);
        }
        return out;
    }

    /** Các năm có dữ liệu (tối đa 8 năm, tăng dần). */
    public List<DemTheoNgay> demPhieuGopTheoNam() {
        List<DemTheoNgay> list = new ArrayList<>();
        Cursor cur = db.rawQuery(
                "SELECT strftime('%Y', ngay_dat), COUNT(*) FROM dat_san GROUP BY strftime('%Y', ngay_dat) ORDER BY 1 ASC LIMIT 8",
                null);
        while (cur.moveToNext()) {
            DemTheoNgay d = new DemTheoNgay();
            d.ngay = cur.getString(0);
            d.soLuong = cur.getInt(1);
            list.add(d);
        }
        cur.close();
        return list;
    }

    public int demTongSan() {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM san", null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    public int demTongKhachHang() {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM khach_hang", null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    /** Báo cáo theo nhân viên trong khoảng ngày (admin). */
    public static class BaoCaoNhanVien {
        public int maNv;
        public String hoTen;
        /** Số HĐ (phiếu) do NV đó duyệt — theo ngày đặt sân trong kỳ. */
        public int soDonDuyetTrongKy;
        /** Số phiếu từ chối do NV đó xử lý — theo ngày đặt trong kỳ. */
        public int soLanTuChoiTrongKy;
        /** Số lần thu tiền (HĐ đã TT) trong kỳ. */
        public int soLanThuTienTrongKy;
        /** Tổng tiền NV đó đã thu (ngày TT trong kỳ). */
        public double doanhThuDaThuTrongKy;
    }

    public List<BaoCaoNhanVien> listBaoCaoNhanVienTheoKy(String tuNgay, String denNgay) {
        Map<Integer, Integer> duyet = new HashMap<>();
        Cursor c1 = db.rawQuery(
                "SELECT h.ma_nv, COUNT(*) FROM hoa_don h "
                        + "INNER JOIN dat_san d ON d.ma_dat_san = h.ma_dat_san "
                        + "WHERE date(d.ngay_dat) BETWEEN date(?) AND date(?) AND h.ma_nv IS NOT NULL "
                        + "GROUP BY h.ma_nv",
                new String[]{tuNgay, denNgay});
        while (c1.moveToNext()) {
            duyet.put(c1.getInt(0), c1.getInt(1));
        }
        c1.close();

        Map<Integer, Integer> tuChoi = new HashMap<>();
        Cursor c2 = db.rawQuery(
                "SELECT ma_nv_xu_ly, COUNT(*) FROM dat_san WHERE trang_thai=? "
                        + "AND date(ngay_dat) BETWEEN date(?) AND date(?) AND IFNULL(ma_nv_xu_ly,0) > 0 "
                        + "GROUP BY ma_nv_xu_ly",
                new String[]{AppConstants.DS_TU_CHOI, tuNgay, denNgay});
        while (c2.moveToNext()) {
            tuChoi.put(c2.getInt(0), c2.getInt(1));
        }
        c2.close();

        Map<Integer, Integer> lanThu = new HashMap<>();
        Map<Integer, Double> doanhThu = new HashMap<>();
        Cursor c3 = db.rawQuery(
                "SELECT ma_nv_thanh_toan, COUNT(*), COALESCE(SUM(tong_tien),0) FROM hoa_don WHERE trang_thai=1 "
                        + "AND IFNULL(ma_nv_thanh_toan,0) > 0 AND date(ngay_tt) BETWEEN date(?) AND date(?) "
                        + "GROUP BY ma_nv_thanh_toan",
                new String[]{tuNgay, denNgay});
        while (c3.moveToNext()) {
            int m = c3.getInt(0);
            lanThu.put(m, c3.getInt(1));
            doanhThu.put(m, c3.getDouble(2));
        }
        c3.close();

        List<BaoCaoNhanVien> out = new ArrayList<>();
        NhanVienDAO nvDao = new NhanVienDAO(appContext);
        for (NhanVienModel nv : nvDao.getAll()) {
            int id = nv.getMaNv();
            BaoCaoNhanVien b = new BaoCaoNhanVien();
            b.maNv = id;
            b.hoTen = nv.getHoTen() != null ? nv.getHoTen() : ("#" + id);
            b.soDonDuyetTrongKy = duyet.getOrDefault(id, 0);
            b.soLanTuChoiTrongKy = tuChoi.getOrDefault(id, 0);
            b.soLanThuTienTrongKy = lanThu.getOrDefault(id, 0);
            b.doanhThuDaThuTrongKy = doanhThu.getOrDefault(id, 0d);
            out.add(b);
        }
        return out;
    }
}
