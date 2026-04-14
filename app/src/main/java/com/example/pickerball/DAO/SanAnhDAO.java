package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.SanAnhModel;

import java.util.ArrayList;
import java.util.List;

public class SanAnhDAO {

    private final SQLiteDatabase db;

    public SanAnhDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public List<SanAnhModel> listByMaSan(int maSan) {
        List<SanAnhModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_anh, ma_san, duong_dan, thu_tu FROM san_anh WHERE ma_san=? ORDER BY thu_tu, ma_anh",
                new String[]{String.valueOf(maSan)});
        while (c.moveToNext()) {
            SanAnhModel m = new SanAnhModel();
            m.maAnh = c.getInt(0);
            m.maSan = c.getInt(1);
            m.duongDan = c.getString(2);
            m.thuTu = c.getInt(3);
            list.add(m);
        }
        c.close();
        return list;
    }

    public long insert(int maSan, String duongDan, int thuTu) {
        ContentValues v = new ContentValues();
        v.put("ma_san", maSan);
        v.put("duong_dan", duongDan);
        v.put("thu_tu", thuTu);
        return db.insert("san_anh", null, v);
    }

    public void delete(int maAnh) {
        db.delete("san_anh", "ma_anh=?", new String[]{String.valueOf(maAnh)});
    }

    public void deleteByMaSan(int maSan) {
        db.delete("san_anh", "ma_san=?", new String[]{String.valueOf(maSan)});
    }

    /** Ảnh đầu tiên (theo thứ tự) để hiển thị thumbnail danh sách. */
    public String getFirstDuongDan(int maSan) {
        Cursor c = db.rawQuery(
                "SELECT duong_dan FROM san_anh WHERE ma_san=? ORDER BY thu_tu, ma_anh LIMIT 1",
                new String[]{String.valueOf(maSan)});
        String out = null;
        if (c.moveToFirst()) out = c.getString(0);
        c.close();
        return out;
    }
}
