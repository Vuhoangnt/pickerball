package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ThongBaoDAO {

    public static class Row {
        public int maTb;
        public String tieuDe;
        public String noiDung;
        public long thoiGianMs;
        public int daDoc;
    }

    private final SQLiteDatabase db;

    public ThongBaoDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public List<Row> listForMaTk(int maTk) {
        List<Row> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_tb, tieu_de, noi_dung, thoi_gian_ms, da_doc FROM thong_bao WHERE ma_tk=? ORDER BY thoi_gian_ms DESC LIMIT 100",
                new String[]{String.valueOf(maTk)});
        while (c.moveToNext()) {
            Row r = new Row();
            r.maTb = c.getInt(0);
            r.tieuDe = c.getString(1);
            r.noiDung = c.getString(2);
            r.thoiGianMs = c.isNull(3) ? 0 : c.getLong(3);
            r.daDoc = c.getInt(4);
            list.add(r);
        }
        c.close();
        return list;
    }

    public void insert(int maTk, String tieuDe, String noiDung, String loai, long thoiGianMs, Integer maDatSan) {
        ContentValues v = new ContentValues();
        v.put("ma_tk", maTk);
        v.put("tieu_de", tieuDe);
        v.put("noi_dung", noiDung);
        v.put("loai", loai);
        v.put("da_doc", 0);
        v.put("thoi_gian_ms", thoiGianMs);
        if (maDatSan != null) v.put("ma_dat_san", maDatSan);
        db.insert("thong_bao", null, v);
    }

    public int countChuaDoc(int maTk) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM thong_bao WHERE ma_tk=? AND da_doc=0", new String[]{String.valueOf(maTk)});
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    public void markAllRead(int maTk) {
        ContentValues v = new ContentValues();
        v.put("da_doc", 1);
        db.update("thong_bao", v, "ma_tk=?", new String[]{String.valueOf(maTk)});
    }
}
