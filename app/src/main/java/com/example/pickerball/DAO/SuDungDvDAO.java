package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;

public class SuDungDvDAO {

    private final SQLiteDatabase db;

    public SuDungDvDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public void deleteByMaDatSan(int maDatSan) {
        db.delete("su_dung_dv", "ma_dat_san=?", new String[]{String.valueOf(maDatSan)});
    }

    public long insert(int maDatSan, int maDv, int soLuong) {
        ContentValues v = new ContentValues();
        v.put("ma_dat_san", maDatSan);
        v.put("ma_dv", maDv);
        v.put("so_luong", soLuong);
        return db.insert("su_dung_dv", null, v);
    }

    public double sumGiaDvByMaDatSan(int maDatSan) {
        Cursor c = db.rawQuery(
                "SELECT COALESCE(SUM(d.gia * sd.so_luong),0) "
                        + "FROM su_dung_dv sd "
                        + "JOIN dich_vu d ON d.ma_dv = sd.ma_dv "
                        + "WHERE sd.ma_dat_san=?",
                new String[]{String.valueOf(maDatSan)});
        double sum = 0;
        if (c.moveToFirst()) sum = c.getDouble(0);
        c.close();
        return sum;
    }
}

