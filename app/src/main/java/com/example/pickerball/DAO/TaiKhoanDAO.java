package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.TaiKhoanModel;

public class TaiKhoanDAO {

    private final SQLiteDatabase db;

    public TaiKhoanDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public TaiKhoanModel login(String username, String password) {
        Cursor c = db.rawQuery(
                "SELECT ma_tk, username, password, vai_tro, ma_kh, ma_nv, ho_ten_hien_thi, anh_dai_dien, trang_thai "
                        + "FROM tai_khoan WHERE username=? AND password=? AND (trang_thai IS NULL OR trang_thai='HOAT_DONG')",
                new String[]{username, password});
        TaiKhoanModel m = null;
        if (c.moveToFirst()) {
            m = map(c);
        }
        c.close();
        return m;
    }

    public boolean usernameExists(String username) {
        Cursor c = db.rawQuery("SELECT 1 FROM tai_khoan WHERE username=? LIMIT 1", new String[]{username});
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    public TaiKhoanModel getByUsername(String username) {
        Cursor c = db.rawQuery(
                "SELECT ma_tk, username, password, vai_tro, ma_kh, ma_nv, ho_ten_hien_thi, anh_dai_dien, trang_thai "
                        + "FROM tai_khoan WHERE username=? LIMIT 1",
                new String[]{username});
        TaiKhoanModel m = null;
        if (c.moveToFirst()) m = map(c);
        c.close();
        return m;
    }

    public long registerKhach(String username, String password, String hoTen, String sdt, String email) {
        ContentValues kh = new ContentValues();
        kh.put("ho_ten", hoTen);
        kh.put("so_dien_thoai", sdt);
        kh.put("email", email);
        kh.put("diem_tich_luy", 0);
        kh.put("ngay_dang_ky", new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date()));
        long maKh = db.insert("khach_hang", null, kh);

        ContentValues tk = new ContentValues();
        tk.put("username", username);
        tk.put("password", password);
        tk.put("vai_tro", com.example.pickerball.AppConstants.ROLE_KHACH);
        tk.put("ma_kh", (int) maKh);
        tk.put("ho_ten_hien_thi", hoTen);
        tk.put("trang_thai", "HOAT_DONG");
        return db.insert("tai_khoan", null, tk);
    }

    public TaiKhoanModel getByMaTk(int maTk) {
        Cursor c = db.rawQuery(
                "SELECT ma_tk, username, password, vai_tro, ma_kh, ma_nv, ho_ten_hien_thi, anh_dai_dien, trang_thai FROM tai_khoan WHERE ma_tk=?",
                new String[]{String.valueOf(maTk)});
        TaiKhoanModel m = null;
        if (c.moveToFirst()) m = map(c);
        c.close();
        return m;
    }

    public TaiKhoanModel getByMaKh(int maKh) {
        Cursor c = db.rawQuery(
                "SELECT ma_tk, username, password, vai_tro, ma_kh, ma_nv, ho_ten_hien_thi, anh_dai_dien, trang_thai FROM tai_khoan WHERE ma_kh=? LIMIT 1",
                new String[]{String.valueOf(maKh)});
        TaiKhoanModel m = null;
        if (c.moveToFirst()) m = map(c);
        c.close();
        return m;
    }

    public long createTaiKhoanKhachForMaKh(int maKh, String username, String password, String hoTen) {
        ContentValues tk = new ContentValues();
        tk.put("username", username);
        tk.put("password", password);
        tk.put("vai_tro", com.example.pickerball.AppConstants.ROLE_KHACH);
        tk.put("ma_kh", maKh);
        tk.put("ho_ten_hien_thi", hoTen);
        tk.put("trang_thai", "HOAT_DONG");
        tk.putNull("ma_nv");
        return db.insert("tai_khoan", null, tk);
    }

    public void updateAvatar(int maTk, String filename) {
        ContentValues v = new ContentValues();
        v.put("anh_dai_dien", filename);
        db.update("tai_khoan", v, "ma_tk=?", new String[]{String.valueOf(maTk)});
    }

    private static TaiKhoanModel map(Cursor c) {
        TaiKhoanModel m = new TaiKhoanModel();
        m.setMaTk(c.getInt(0));
        m.setTenDangNhap(c.getString(1));
        m.setMatKhau(c.getString(2));
        m.setVaiTro(c.getString(3));
        if (!c.isNull(4)) m.setMaKh(c.getInt(4));
        if (!c.isNull(5)) m.setMaNv(c.getInt(5));
        m.setHoTenHienThi(c.getString(6));
        m.setAnhDaiDien(c.isNull(7) ? null : c.getString(7));
        m.setTrangThai(c.getString(8));
        return m;
    }
}
