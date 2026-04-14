package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.DichVuModel;

import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {

    private final SQLiteDatabase db;
    private final Context appContext;

    public DichVuDAO(Context context) {
        appContext = context.getApplicationContext();
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    /** Dịch vụ áp dụng cho sân: nếu chưa cấu hình san_dich_vu thì trả toàn bộ. */
    public List<DichVuModel> getForSan(int maSan) {
        SanDichVuDAO sd = new SanDichVuDAO(appContext);
        if (!sd.hasAnyForSan(maSan)) {
            return getAll();
        }
        List<Integer> ids = sd.listMaDvForSan(maSan);
        List<DichVuModel> out = new ArrayList<>();
        for (int id : ids) {
            DichVuModel m = getById(id);
            if (m != null) out.add(m);
        }
        return out;
    }

    private DichVuModel getById(int maDv) {
        Cursor c = db.rawQuery(
                "SELECT ma_dv, ten_dv, gia, don_vi, mo_ta FROM dich_vu WHERE ma_dv=?",
                new String[]{String.valueOf(maDv)});
        DichVuModel m = null;
        if (c.moveToFirst()) {
            m = new DichVuModel();
            m.setMaDv(c.getInt(0));
            m.setTen(c.getString(1));
            m.setGia(c.getDouble(2));
            m.setDonVi(c.getString(3));
            m.setMoTa(c.getString(4));
        }
        c.close();
        return m;
    }

    public List<DichVuModel> getAll() {
        List<DichVuModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_dv, ten_dv, gia, don_vi, mo_ta FROM dich_vu ORDER BY ma_dv", null);
        while (c.moveToNext()) {
            DichVuModel m = new DichVuModel();
            m.setMaDv(c.getInt(0));
            m.setTen(c.getString(1));
            m.setGia(c.getDouble(2));
            m.setDonVi(c.getString(3));
            m.setMoTa(c.getString(4));
            list.add(m);
        }
        c.close();
        return list;
    }

    public long insert(DichVuModel m) {
        ContentValues v = new ContentValues();
        v.put("ten_dv", m.getTen());
        v.put("gia", m.getGia());
        v.put("don_vi", m.getDonVi());
        v.put("mo_ta", m.getMoTa());
        return db.insert("dich_vu", null, v);
    }

    public void update(DichVuModel m) {
        ContentValues v = new ContentValues();
        v.put("ten_dv", m.getTen());
        v.put("gia", m.getGia());
        v.put("don_vi", m.getDonVi());
        v.put("mo_ta", m.getMoTa());
        db.update("dich_vu", v, "ma_dv=?", new String[]{String.valueOf(m.getMaDv())});
    }

    public void delete(int maDv) {
        db.delete("dich_vu", "ma_dv=?", new String[]{String.valueOf(maDv)});
    }
}
