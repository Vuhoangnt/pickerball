package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/** Gán dịch vụ áp dụng cho từng sân (nếu không có bản ghi → coi như áp dụng tất cả). */
public class SanDichVuDAO {

    private final SQLiteDatabase db;

    public SanDichVuDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public boolean hasAnyForSan(int maSan) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM san_dich_vu WHERE ma_san=?", new String[]{String.valueOf(maSan)});
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n > 0;
    }

    public List<Integer> listMaDvForSan(int maSan) {
        List<Integer> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ma_dv FROM san_dich_vu WHERE ma_san=? ORDER BY ma_dv", new String[]{String.valueOf(maSan)});
        while (c.moveToNext()) list.add(c.getInt(0));
        c.close();
        return list;
    }

    public void replaceForSan(int maSan, List<Integer> maDvs) {
        db.delete("san_dich_vu", "ma_san=?", new String[]{String.valueOf(maSan)});
        for (Integer id : maDvs) {
            ContentValues v = new ContentValues();
            v.put("ma_san", maSan);
            v.put("ma_dv", id);
            db.insert("san_dich_vu", null, v);
        }
    }
}
