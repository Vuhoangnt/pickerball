package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;

public class HeThongDAO {

    private final SQLiteDatabase db;

    public HeThongDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public int getInt(String key, int def) {
        Cursor c = db.rawQuery("SELECT gia_tri_so FROM he_thong WHERE k=? LIMIT 1", new String[]{key});
        int v = def;
        if (c.moveToFirst()) v = c.getInt(0);
        c.close();
        return v;
    }

    /** Lưu cấu hình số nguyên (vd: DIEM_KHI_DAT_SAN). */
    public void putInt(String key, int value) {
        ContentValues cv = new ContentValues();
        cv.put("k", key);
        cv.put("gia_tri_so", value);
        db.insertWithOnConflict("he_thong", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
