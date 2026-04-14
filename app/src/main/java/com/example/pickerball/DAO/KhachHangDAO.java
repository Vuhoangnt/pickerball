package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.AppConstants;
import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.util.HoiVienHelper;

import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    private final SQLiteDatabase db;

    public KhachHangDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    private static KhachHangModel map(Cursor c) {
        KhachHangModel k = new KhachHangModel();
        k.setMaKh(c.getInt(0));
        k.setHoTen(c.getString(1));
        k.setSoDienThoai(c.getString(2));
        k.setEmail(c.isNull(3) ? null : c.getString(3));
        k.setDiem(c.getInt(4));
        k.setNgayDangKy(c.isNull(5) ? null : c.getString(5));
        k.setHangHoiVien(c.isNull(6) ? AppConstants.HANG_THUONG : c.getString(6));
        return k;
    }

    /** Alias cho màn cũ / dialog dùng tên getAll(). */
    public List<KhachHangModel> getAll() {
        return getAllForAdmin();
    }

    /** Bảng xếp hạng theo điểm tích lũy. */
    public List<KhachHangModel> getLeaderboard(int limit) {
        List<KhachHangModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_kh, ho_ten, so_dien_thoai, email, diem_tich_luy, ngay_dang_ky, hang_hoi_vien FROM khach_hang "
                        + "ORDER BY diem_tich_luy DESC, ma_kh ASC LIMIT ?",
                new String[]{String.valueOf(limit)});
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public List<KhachHangModel> getAllForAdmin() {
        List<KhachHangModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_kh, ho_ten, so_dien_thoai, email, diem_tich_luy, ngay_dang_ky, hang_hoi_vien FROM khach_hang ORDER BY ma_kh DESC",
                null);
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public List<KhachHangModel> getAllForAdminByHang(String hang) {
        List<KhachHangModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_kh, ho_ten, so_dien_thoai, email, diem_tich_luy, ngay_dang_ky, hang_hoi_vien FROM khach_hang WHERE hang_hoi_vien=? ORDER BY ma_kh DESC",
                new String[]{hang});
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public List<KhachHangModel> getAllForAdminOrderByBookingsDesc() {
        List<KhachHangModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT k.ma_kh, k.ho_ten, k.so_dien_thoai, k.email, k.diem_tich_luy, k.ngay_dang_ky, k.hang_hoi_vien, "
                        + "(SELECT COUNT(*) FROM dat_san d WHERE d.ma_kh = k.ma_kh) AS cnt "
                        + "FROM khach_hang k ORDER BY cnt DESC, k.ma_kh DESC",
                null);
        while (c.moveToNext()) {
            KhachHangModel m = mapPartial(c);
            list.add(m);
        }
        c.close();
        return list;
    }

    private KhachHangModel mapPartial(Cursor c) {
        KhachHangModel k = new KhachHangModel();
        k.setMaKh(c.getInt(0));
        k.setHoTen(c.getString(1));
        k.setSoDienThoai(c.getString(2));
        k.setEmail(c.isNull(3) ? null : c.getString(3));
        k.setDiem(c.getInt(4));
        k.setNgayDangKy(c.isNull(5) ? null : c.getString(5));
        k.setHangHoiVien(c.isNull(6) ? AppConstants.HANG_THUONG : c.getString(6));
        return k;
    }

    public KhachHangModel getByMaKh(int maKh) {
        Cursor c = db.rawQuery(
                "SELECT ma_kh, ho_ten, so_dien_thoai, email, diem_tich_luy, ngay_dang_ky, hang_hoi_vien FROM khach_hang WHERE ma_kh=?",
                new String[]{String.valueOf(maKh)});
        KhachHangModel k = null;
        if (c.moveToFirst()) k = map(c);
        c.close();
        return k;
    }

    public KhachHangModel getByPhone(String sdt) {
        Cursor c = db.rawQuery(
                "SELECT ma_kh, ho_ten, so_dien_thoai, email, diem_tich_luy, ngay_dang_ky, hang_hoi_vien FROM khach_hang WHERE so_dien_thoai=? LIMIT 1",
                new String[]{sdt});
        KhachHangModel k = null;
        if (c.moveToFirst()) k = map(c);
        c.close();
        return k;
    }

    public void updateHangOnly(int maKh, String hang) {
        ContentValues v = new ContentValues();
        v.put("hang_hoi_vien", hang);
        db.update("khach_hang", v, "ma_kh=?", new String[]{String.valueOf(maKh)});
    }

    public long insert(KhachHangModel k) {
        ContentValues v = new ContentValues();
        v.put("ho_ten", k.getHoTen());
        v.put("so_dien_thoai", k.getSoDienThoai());
        v.put("email", k.getEmail());
        v.put("diem_tich_luy", k.getDiem());
        v.put("ngay_dang_ky", k.getNgayDangKy());
        String hang = HoiVienHelper.hangFromPoints(k.getDiem());
        v.put("hang_hoi_vien", hang);
        return db.insert("khach_hang", null, v);
    }

    public void update(KhachHangModel k) {
        ContentValues v = new ContentValues();
        v.put("ho_ten", k.getHoTen());
        v.put("so_dien_thoai", k.getSoDienThoai());
        v.put("email", k.getEmail());
        v.put("diem_tich_luy", k.getDiem());
        v.put("ngay_dang_ky", k.getNgayDangKy());
        v.put("hang_hoi_vien", HoiVienHelper.hangFromPoints(k.getDiem()));
        db.update("khach_hang", v, "ma_kh=?", new String[]{String.valueOf(k.getMaKh())});
    }

    public int syncHangFromPointsForAll() {
        Cursor c = db.rawQuery("SELECT ma_kh, diem_tich_luy FROM khach_hang", null);
        int n = 0;
        while (c.moveToNext()) {
            int ma = c.getInt(0);
            int diem = c.getInt(1);
            String h = HoiVienHelper.hangFromPoints(diem);
            updateHangOnly(ma, h);
            n++;
        }
        c.close();
        return n;
    }

    public void delete(int maKh) {
        Cursor c = db.rawQuery("SELECT ma_dat_san FROM dat_san WHERE ma_kh=?", new String[]{String.valueOf(maKh)});
        while (c.moveToNext()) {
            int maDat = c.getInt(0);
            db.delete("su_dung_dv", "ma_dat_san=?", new String[]{String.valueOf(maDat)});
            db.delete("hoa_don", "ma_dat_san=?", new String[]{String.valueOf(maDat)});
            db.delete("dat_san", "ma_dat_san=?", new String[]{String.valueOf(maDat)});
        }
        c.close();
        db.delete("danh_gia", "ma_kh=?", new String[]{String.valueOf(maKh)});
        db.delete("khach_hang", "ma_kh=?", new String[]{String.valueOf(maKh)});
    }
}
