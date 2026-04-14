package com.example.pickerball.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class KhungGioDAO {

    private final SQLiteDatabase db;

    public KhungGioDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    /** Khung nằm trong giờ mở/đóng sân. */
    public List<KhungGioModel> getKhungForSan(SanModel san) {
        List<KhungGioModel> all = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ma_khung, gio_bat_dau, gio_ket_thuc FROM khung_gio ORDER BY ma_khung", null);
        while (c.moveToNext()) {
            KhungGioModel k = new KhungGioModel();
            k.maKhung = c.getInt(0);
            k.gioBatDau = c.getString(1);
            k.gioKetThuc = c.getString(2);
            all.add(k);
        }
        c.close();
        if (san == null) return all;
        String mo = san.gioMoCua != null ? san.gioMoCua : "06:00";
        String doc = san.gioDongCua != null ? san.gioDongCua : "22:00";
        List<KhungGioModel> out = new ArrayList<>();
        int o = DateUtils.toMinutes(mo);
        int cl = DateUtils.toMinutes(doc);
        for (KhungGioModel k : all) {
            int a = DateUtils.toMinutes(k.gioBatDau);
            int b = DateUtils.toMinutes(k.gioKetThuc);
            if (a >= o && b <= cl && b > a) out.add(k);
        }
        return out.isEmpty() ? all : out;
    }
}
