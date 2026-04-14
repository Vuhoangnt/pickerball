package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.SanModel;

import java.util.ArrayList;
import java.util.List;

public class SanDAO {

    private final SQLiteDatabase db;

    public SanDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    private static SanModel map(Cursor c) {
        SanModel s = new SanModel();
        s.maSan = c.getInt(0);
        s.tenSan = c.getString(1);
        s.loaiSan = c.getString(2);
        s.trangThai = c.getString(3);
        s.moTa = c.getString(4);
        s.hinhAnh = c.isNull(5) ? null : c.getString(5);
        s.gioMoCua = c.isNull(6) ? "06:00" : c.getString(6);
        s.gioDongCua = c.isNull(7) ? "22:00" : c.getString(7);
        s.giaMoiGio = c.isNull(8) ? 120000 : c.getDouble(8);
        return s;
    }

    public List<SanModel> getAll() {
        List<SanModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_san, ten_san, loai_san, trang_thai, mo_ta, hinh_anh, gio_mo_cua, gio_dong_cua, gia_moi_gio FROM san ORDER BY ma_san",
                null);
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public List<SanModel> getAllForCustomer() {
        return getAll();
    }

    public SanModel getById(int maSan) {
        Cursor c = db.rawQuery(
                "SELECT ma_san, ten_san, loai_san, trang_thai, mo_ta, hinh_anh, gio_mo_cua, gio_dong_cua, gia_moi_gio FROM san WHERE ma_san=?",
                new String[]{String.valueOf(maSan)});
        SanModel s = null;
        if (c.moveToFirst()) s = map(c);
        c.close();
        return s;
    }

    public String getTenSan(int maSan) {
        Cursor c = db.rawQuery("SELECT ten_san FROM san WHERE ma_san=?", new String[]{String.valueOf(maSan)});
        String t = null;
        if (c.moveToFirst()) t = c.getString(0);
        c.close();
        return t;
    }

    public void insert(SanModel s) {
        ContentValues v = new ContentValues();
        v.put("ten_san", s.tenSan);
        v.put("loai_san", s.loaiSan);
        v.put("trang_thai", s.trangThai);
        v.put("mo_ta", s.moTa);
        v.put("hinh_anh", s.hinhAnh);
        v.put("gio_mo_cua", s.gioMoCua != null ? s.gioMoCua : "06:00");
        v.put("gio_dong_cua", s.gioDongCua != null ? s.gioDongCua : "22:00");
        v.put("gia_moi_gio", s.giaMoiGio > 0 ? s.giaMoiGio : 120000);
        db.insert("san", null, v);
    }

    public long insertAndGetId(SanModel s) {
        ContentValues v = new ContentValues();
        v.put("ten_san", s.tenSan);
        v.put("loai_san", s.loaiSan);
        v.put("trang_thai", s.trangThai);
        v.put("mo_ta", s.moTa);
        v.put("hinh_anh", s.hinhAnh);
        v.put("gio_mo_cua", s.gioMoCua != null ? s.gioMoCua : "06:00");
        v.put("gio_dong_cua", s.gioDongCua != null ? s.gioDongCua : "22:00");
        v.put("gia_moi_gio", s.giaMoiGio > 0 ? s.giaMoiGio : 120000);
        return db.insert("san", null, v);
    }

    public void update(SanModel s) {
        ContentValues v = new ContentValues();
        v.put("ten_san", s.tenSan);
        v.put("loai_san", s.loaiSan);
        v.put("trang_thai", s.trangThai);
        v.put("mo_ta", s.moTa);
        v.put("hinh_anh", s.hinhAnh);
        v.put("gio_mo_cua", s.gioMoCua);
        v.put("gio_dong_cua", s.gioDongCua);
        v.put("gia_moi_gio", s.giaMoiGio);
        db.update("san", v, "ma_san=?", new String[]{String.valueOf(s.maSan)});
    }

    public void delete(int maSan) {
        db.delete("san", "ma_san=?", new String[]{String.valueOf(maSan)});
    }

    public int count() {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM san", null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }
}
