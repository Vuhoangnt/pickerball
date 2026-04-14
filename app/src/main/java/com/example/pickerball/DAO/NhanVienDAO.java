package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.NhanVienModel;

import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    private final SQLiteDatabase db;

    public NhanVienDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public List<NhanVienModel> getAll() {
        List<NhanVienModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_nv, ho_ten, chuc_vu, so_dien_thoai, ngay_vao_lam, trang_thai FROM nhan_vien ORDER BY ma_nv",
                null);
        while (c.moveToNext()) {
            NhanVienModel m = new NhanVienModel();
            m.setMaNv(c.getInt(0));
            m.setHoTen(c.getString(1));
            m.setChucVu(c.getString(2));
            m.setSoDienThoai(c.getString(3));
            m.setNgayVaoLam(c.getString(4));
            m.setTrangThai(c.getString(5));
            list.add(m);
        }
        c.close();
        return list;
    }

    public long insert(NhanVienModel m) {
        ContentValues v = new ContentValues();
        v.put("ho_ten", m.getHoTen());
        v.put("chuc_vu", m.getChucVu());
        v.put("so_dien_thoai", m.getSoDienThoai());
        v.put("ngay_vao_lam", m.getNgayVaoLam());
        v.put("trang_thai", m.getTrangThai() != null ? m.getTrangThai() : "DANG_LAM");
        return db.insert("nhan_vien", null, v);
    }

    public void update(NhanVienModel m) {
        ContentValues v = new ContentValues();
        v.put("ho_ten", m.getHoTen());
        v.put("chuc_vu", m.getChucVu());
        v.put("so_dien_thoai", m.getSoDienThoai());
        v.put("ngay_vao_lam", m.getNgayVaoLam());
        v.put("trang_thai", m.getTrangThai());
        db.update("nhan_vien", v, "ma_nv=?", new String[]{String.valueOf(m.getMaNv())});
    }

    public void delete(int maNv) {
        db.delete("nhan_vien", "ma_nv=?", new String[]{String.valueOf(maNv)});
    }
}
