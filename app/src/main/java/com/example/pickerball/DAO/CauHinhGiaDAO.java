package com.example.pickerball.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.util.BookingTimeHelper;

import android.content.ContentValues;
import java.util.ArrayList;
import java.util.List;

public class CauHinhGiaDAO {

    public static class GiaTheoKhungRow {
        public int maKhung;
        public String gioBatDau;
        public String gioKetThuc;
        public String loaiNgay;
        public double gia;
    }
    private final SQLiteDatabase db;

    public CauHinhGiaDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public double getGia(int maSan, int maKhung, String loaiNgay) {
        Cursor c = db.rawQuery(
                "SELECT gia_ap_dung FROM cau_hinh_gia WHERE ma_san=? AND ma_khung=? AND loai_ngay=? LIMIT 1",
                new String[]{String.valueOf(maSan), String.valueOf(maKhung), loaiNgay});
        double g = -1;
        if (c.moveToFirst()) g = c.getDouble(0);
        c.close();
        if (g >= 0) return g;
        Cursor c2 = db.rawQuery(
                "SELECT gia_ap_dung FROM cau_hinh_gia WHERE ma_san=? AND ma_khung=? LIMIT 1",
                new String[]{String.valueOf(maSan), String.valueOf(maKhung)});
        if (c2.moveToFirst()) g = c2.getDouble(0);
        c2.close();
        return g;
    }

    public double getGiaOrTheoGio(SanModel san, int maKhung, String loaiNgay, String gioBd, String gioKt) {
        double g = getGia(san.maSan, maKhung, loaiNgay);
        if (g > 0) return g;
        return BookingTimeHelper.tienSanTheoGio(san, gioBd, gioKt);
    }

    /** Bảng giá chi tiết: khung giờ × loại ngày (cho màn chi tiết sân / admin). */
    public List<GiaTheoKhungRow> listChiTietTheoSan(int maSan) {
        List<GiaTheoKhungRow> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT c.ma_khung, k.gio_bat_dau, k.gio_ket_thuc, c.loai_ngay, c.gia_ap_dung "
                        + "FROM cau_hinh_gia c JOIN khung_gio k ON k.ma_khung = c.ma_khung "
                        + "WHERE c.ma_san=? ORDER BY c.ma_khung, c.loai_ngay",
                new String[]{String.valueOf(maSan)});
        while (c.moveToNext()) {
            GiaTheoKhungRow r = new GiaTheoKhungRow();
            r.maKhung = c.getInt(0);
            r.gioBatDau = c.getString(1);
            r.gioKetThuc = c.getString(2);
            r.loaiNgay = c.getString(3);
            r.gia = c.getDouble(4);
            list.add(r);
        }
        c.close();
        return list;
    }

    public void deleteGia(int maSan, int maKhung, String loaiNgay) {
        db.delete(
                "cau_hinh_gia",
                "ma_san=? AND ma_khung=? AND loai_ngay=?",
                new String[]{String.valueOf(maSan), String.valueOf(maKhung), loaiNgay});
    }

    public void deleteGia(int maSan, int maKhung) {
        db.delete(
                "cau_hinh_gia",
                "ma_san=? AND ma_khung=?",
                new String[]{String.valueOf(maSan), String.valueOf(maKhung)});
    }

    public void deleteByMaSan(int maSan) {
        db.delete(
                "cau_hinh_gia",
                "ma_san=?",
                new String[]{String.valueOf(maSan)});
    }

    public void upsertGia(int maSan, int maKhung, String loaiNgay, double gia) {
        // cau_hinh_gia có khóa chính ma_gia auto-increment nên ta "clear theo khóa nghiệp vụ" rồi insert lại.
        deleteGia(maSan, maKhung, loaiNgay);
        ContentValues cv = new ContentValues();
        cv.put("ma_san", maSan);
        cv.put("ma_khung", maKhung);
        cv.put("loai_ngay", loaiNgay);
        cv.put("gia_ap_dung", gia);
        db.insert("cau_hinh_gia", null, cv);
    }
}
