package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.HoaDonListItem;

import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    private static final String SQL_FROM =
            "FROM hoa_don h "
                    + "LEFT JOIN dat_san d ON d.ma_dat_san = h.ma_dat_san "
                    + "LEFT JOIN san s ON s.ma_san = d.ma_san "
                    + "LEFT JOIN nhan_vien nv1 ON nv1.ma_nv = h.ma_nv "
                    + "LEFT JOIN nhan_vien nv2 ON nv2.ma_nv = h.ma_nv_thanh_toan ";

    private static final String SQL_SELECT =
            "SELECT h.ma_hd, h.ma_dat_san, h.tong_tien, h.trang_thai, IFNULL(s.ten_san,''), IFNULL(h.ngay_tt,''), IFNULL(h.phuong_thuc_tt,''), "
                    + "IFNULL(h.ma_nv,0), IFNULL(nv1.ho_ten,''), IFNULL(h.ma_nv_thanh_toan,0), IFNULL(nv2.ho_ten,''), "
                    + "IFNULL(h.tien_san,0), IFNULL(h.tien_dv,0) ";

    private final SQLiteDatabase db;

    public HoaDonDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    private static HoaDonListItem mapRow(Cursor c) {
        HoaDonListItem row = new HoaDonListItem();
        row.maHd = c.getInt(0);
        row.maDatSan = c.getInt(1);
        row.tongTien = c.getDouble(2);
        row.trangThai = c.getInt(3);
        row.tenSan = c.getString(4);
        row.ngayTt = c.getString(5);
        row.phuongThucTt = c.getString(6);
        row.maNvDuyet = c.getInt(7);
        row.tenNvDuyet = c.getString(8);
        row.maNvThanhToan = c.getInt(9);
        row.tenNvThanhToan = c.getString(10);
        row.tienSan = c.getDouble(11);
        row.tienDv = c.getDouble(12);
        return row;
    }

    private List<HoaDonListItem> queryFull(String where, String[] args) {
        List<HoaDonListItem> list = new ArrayList<>();
        String sql = SQL_SELECT + SQL_FROM + where;
        Cursor c = db.rawQuery(sql, args);
        while (c.moveToNext()) {
            list.add(mapRow(c));
        }
        c.close();
        return list;
    }

    public List<HoaDonListItem> getAllWithSan() {
        return queryFull("ORDER BY h.ma_hd DESC", null);
    }

    public List<HoaDonListItem> getAllWithSanByMaNv(int maNv) {
        return queryFull("WHERE h.ma_nv=? ORDER BY h.ma_hd DESC", new String[]{String.valueOf(maNv)});
    }

    /**
     * Lễ tân: chỉ HĐ do mình duyệt (ma_nv) hoặc do mình thu tiền (ma_nv_thanh_toan).
     */
    public List<HoaDonListItem> getAllWithSanForStaffMe(int maNv) {
        if (maNv <= 0) return new ArrayList<>();
        return queryFull(
                "WHERE (h.ma_nv=? OR h.ma_nv_thanh_toan=?) ORDER BY h.ma_hd DESC",
                new String[]{String.valueOf(maNv), String.valueOf(maNv)});
    }

    public int getMaHdByMaDatSan(int maDatSan) {
        Cursor c = db.rawQuery(
                "SELECT ma_hd FROM hoa_don WHERE ma_dat_san=? LIMIT 1",
                new String[]{String.valueOf(maDatSan)});
        Integer id = null;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();
        return id == null ? -1 : id;
    }

    public long createHoaDon(int maDatSan, int maNv, double tienSan, double tienDv, double tongTien) {
        ContentValues v = new ContentValues();
        v.put("ma_dat_san", maDatSan);
        v.put("ma_nv", maNv);
        v.put("tien_san", tienSan);
        v.put("tien_dv", tienDv);
        v.put("tong_tien", tongTien);
        return db.insert("hoa_don", null, v);
    }

    public void markPaid(int maHd, String phuongThuc, String ngayTt, int maNvThanhToan) {
        ContentValues v = new ContentValues();
        v.put("trang_thai", 1);
        v.put("ngay_tt", ngayTt);
        v.put("phuong_thuc_tt", phuongThuc);
        v.put("ma_nv_thanh_toan", maNvThanhToan);
        db.update("hoa_don", v, "ma_hd=?", new String[]{String.valueOf(maHd)});
    }

    public int count() {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM hoa_don", null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    public double sumPaidBetween(String tuNgay, String denNgay) {
        Cursor c = db.rawQuery(
                "SELECT COALESCE(SUM(tong_tien),0) FROM hoa_don WHERE trang_thai=1 "
                        + "AND ngay_tt IS NOT NULL AND ngay_tt != '' AND date(ngay_tt) BETWEEN date(?) AND date(?)",
                new String[]{tuNgay, denNgay});
        double s = 0;
        if (c.moveToFirst()) s = c.getDouble(0);
        c.close();
        return s;
    }

    public int countPaidBetween(String tuNgay, String denNgay) {
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM hoa_don WHERE trang_thai=1 AND ngay_tt IS NOT NULL AND date(ngay_tt) BETWEEN date(?) AND date(?)",
                new String[]{tuNgay, denNgay});
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    public double sumPaidTienSanBetween(String tuNgay, String denNgay) {
        Cursor c = db.rawQuery(
                "SELECT COALESCE(SUM(tien_san),0) FROM hoa_don WHERE trang_thai=1 "
                        + "AND ngay_tt IS NOT NULL AND ngay_tt != '' AND date(ngay_tt) BETWEEN date(?) AND date(?)",
                new String[]{tuNgay, denNgay});
        double s = 0;
        if (c.moveToFirst()) s = c.getDouble(0);
        c.close();
        return s;
    }

    public double sumPaidTienDvBetween(String tuNgay, String denNgay) {
        Cursor c = db.rawQuery(
                "SELECT COALESCE(SUM(tien_dv),0) FROM hoa_don WHERE trang_thai=1 "
                        + "AND ngay_tt IS NOT NULL AND ngay_tt != '' AND date(ngay_tt) BETWEEN date(?) AND date(?)",
                new String[]{tuNgay, denNgay});
        double s = 0;
        if (c.moveToFirst()) s = c.getDouble(0);
        c.close();
        return s;
    }
}
