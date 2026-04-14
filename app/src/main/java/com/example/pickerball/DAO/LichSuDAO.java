package com.example.pickerball.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.LichSuModel;

import java.util.ArrayList;
import java.util.List;

public class LichSuDAO {

    private final SQLiteDatabase db;

    public LichSuDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public List<LichSuModel> getAll() {
        List<LichSuModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT id, hanh_dong, doi_tuong, thoi_gian FROM lich_su ORDER BY id DESC LIMIT 200",
                null);
        while (c.moveToNext()) {
            LichSuModel m = new LichSuModel();
            m.id = c.getInt(0);
            m.hanhDong = c.getString(1);
            m.doiTuong = c.getString(2);
            m.thoiGian = c.getString(3);
            list.add(m);
        }
        c.close();
        return list;
    }
}
