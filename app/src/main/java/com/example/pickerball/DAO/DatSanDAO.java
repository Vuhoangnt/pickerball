package com.example.pickerball.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pickerball.AppConstants;
import com.example.pickerball.Database.DatabaseHelper;
import com.example.pickerball.Model.DatSanListItem;
import com.example.pickerball.Model.DatSanModel;

import java.util.ArrayList;
import java.util.List;

public class DatSanDAO {

    private final SQLiteDatabase db;

    public DatSanDAO(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public List<DatSanModel> listByMaKh(int maKh) {
        List<DatSanModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_dat_san, ma_san, ma_kh, ma_khung, ngay_dat, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, hinh_thuc, ghi_chu, tong_du_kien, created_at_ms "
                        + "FROM dat_san WHERE ma_kh=? ORDER BY ngay_dat DESC, ma_dat_san DESC",
                new String[]{String.valueOf(maKh)});
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public List<DatSanModel> listChoDuyet() {
        List<DatSanModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_dat_san, ma_san, ma_kh, ma_khung, ngay_dat, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, hinh_thuc, ghi_chu, tong_du_kien, created_at_ms "
                        + "FROM dat_san WHERE trang_thai=? ORDER BY created_at_ms DESC",
                new String[]{AppConstants.DS_CHO_DUYET});
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    /** Dòng chờ duyệt kèm tên sân & khách (màn nhân viên). */
    public static class ChoDuyetRow {
        public int maDatSan;
        public int maSan;
        public int maKh;
        public int maKhung;
        public String ngayDat;
        public String gioBd;
        public String gioKt;
        public String trangThai;
        public double tongDuKien;
        public String tenSan;
        public String tenKh;
        public String sdtKh;
        public String emailKh;
        public int diemKh;
    }

    public List<ChoDuyetRow> listChoDuyetWithNames() {
        List<ChoDuyetRow> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT d.ma_dat_san, d.ma_san, d.ma_kh, IFNULL(d.ma_khung,0), d.ngay_dat, d.thoi_gian_bat_dau, d.thoi_gian_ket_thuc, d.trang_thai, d.tong_du_kien, "
                        + "IFNULL(s.ten_san,''), IFNULL(k.ho_ten,''), IFNULL(k.so_dien_thoai,''), IFNULL(k.email,''), IFNULL(k.diem_tich_luy,0) "
                        + "FROM dat_san d "
                        + "LEFT JOIN san s ON s.ma_san = d.ma_san "
                        + "LEFT JOIN khach_hang k ON k.ma_kh = d.ma_kh "
                        + "WHERE d.trang_thai=? ORDER BY d.created_at_ms DESC",
                new String[]{AppConstants.DS_CHO_DUYET});
        while (c.moveToNext()) {
            ChoDuyetRow r = new ChoDuyetRow();
            r.maDatSan = c.getInt(0);
            r.maSan = c.getInt(1);
            r.maKh = c.getInt(2);
            r.maKhung = c.getInt(3);
            r.ngayDat = c.getString(4);
            r.gioBd = c.getString(5);
            r.gioKt = c.getString(6);
            r.trangThai = c.getString(7);
            r.tongDuKien = c.getDouble(8);
            r.tenSan = c.getString(9);
            r.tenKh = c.getString(10);
            r.sdtKh = c.getString(11);
            r.emailKh = c.getString(12);
            r.diemKh = c.getInt(13);
            list.add(r);
        }
        c.close();
        return list;
    }

    public List<DatSanModel> listAll() {
        List<DatSanModel> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT ma_dat_san, ma_san, ma_kh, ma_khung, ngay_dat, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, hinh_thuc, ghi_chu, tong_du_kien, created_at_ms "
                        + "FROM dat_san ORDER BY ma_dat_san DESC",
                null);
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public static class BookedRange {
        public String gioBd;
        public String gioKt;
        public String trangThai;
    }

    /** Các khung giờ đã có lịch (trừ Hủy/Từ chối) theo sân + ngày. */
    public List<BookedRange> listBookedRanges(int maSan, String ngayDat) {
        List<BookedRange> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai "
                        + "FROM dat_san WHERE ma_san=? AND ngay_dat=? AND trang_thai NOT IN (?, ?) "
                        + "ORDER BY thoi_gian_bat_dau ASC",
                new String[]{String.valueOf(maSan), ngayDat, AppConstants.DS_HUY, AppConstants.DS_TU_CHOI});
        while (c.moveToNext()) {
            BookedRange row = new BookedRange();
            row.gioBd = c.getString(0);
            row.gioKt = c.getString(1);
            row.trangThai = c.getString(2);
            list.add(row);
        }
        c.close();
        return list;
    }

    private static DatSanModel map(Cursor c) {
        DatSanModel d = new DatSanModel();
        d.maDatSan = c.getInt(0);
        d.maSan = c.getInt(1);
        d.maKh = c.getInt(2);
        if (!c.isNull(3)) d.maKhung = c.getInt(3);
        d.ngayDat = c.getString(4);
        d.thoiGianBatDau = c.getString(5);
        d.thoiGianKetThuc = c.getString(6);
        d.trangThai = c.getString(7);
        d.hinhThuc = c.getString(8);
        d.ghiChu = c.isNull(9) ? null : c.getString(9);
        d.tongDuKien = c.getDouble(10);
        d.createdAtMs = c.isNull(11) ? 0L : c.getLong(11);
        return d;
    }

    public long insert(DatSanModel d) {
        ContentValues v = new ContentValues();
        v.put("ma_san", d.maSan);
        v.put("ma_kh", d.maKh);
        if (d.maKhung != null) v.put("ma_khung", d.maKhung);
        v.put("ngay_dat", d.ngayDat);
        v.put("thoi_gian_bat_dau", d.thoiGianBatDau);
        v.put("thoi_gian_ket_thuc", d.thoiGianKetThuc);
        v.put("trang_thai", d.trangThai);
        v.put("hinh_thuc", d.hinhThuc);
        v.put("ghi_chu", d.ghiChu);
        v.put("tong_du_kien", d.tongDuKien);
        v.put("created_at_ms", d.createdAtMs > 0 ? d.createdAtMs : System.currentTimeMillis());
        return db.insert("dat_san", null, v);
    }

    public void updateTrangThai(int maDatSan, String trangThai) {
        updateTrangThai(maDatSan, trangThai, null);
    }

    /**
     * Cập nhật trạng thái; ghi {@code ma_nv_xu_ly} khi duyệt hoặc từ chối (lễ tân).
     */
    public void updateTrangThai(int maDatSan, String trangThai, Integer maNvXuLy) {
        ContentValues v = new ContentValues();
        v.put("trang_thai", trangThai);
        if (maNvXuLy != null && maNvXuLy > 0) {
            if (AppConstants.DS_DA_DUYET.equals(trangThai) || AppConstants.DS_TU_CHOI.equals(trangThai)) {
                v.put("ma_nv_xu_ly", maNvXuLy);
            }
        }
        db.update("dat_san", v, "ma_dat_san=?", new String[]{String.valueOf(maDatSan)});
    }

    /** Phiếu đã duyệt / từ chối (có NV xử lý), mới nhất trước. */
    public static class XuLyHistoryRow {
        public int maDatSan;
        public String ngayDat;
        public String gioBd;
        public String gioKt;
        public String trangThai;
        public String tenSan;
        public String tenKh;
        public int maNvXuLy;
        public String tenNvXuLy;
    }

    public List<XuLyHistoryRow> listLichSuXuLyByMaNv(int maNvFilter) {
        List<XuLyHistoryRow> list = new ArrayList<>();
        String sql = "SELECT d.ma_dat_san, d.ngay_dat, d.thoi_gian_bat_dau, d.thoi_gian_ket_thuc, d.trang_thai, "
                + "IFNULL(s.ten_san,''), IFNULL(k.ho_ten,''), IFNULL(d.ma_nv_xu_ly,0), IFNULL(nv.ho_ten,'') "
                + "FROM dat_san d "
                + "LEFT JOIN san s ON s.ma_san = d.ma_san "
                + "LEFT JOIN khach_hang k ON k.ma_kh = d.ma_kh "
                + "LEFT JOIN nhan_vien nv ON nv.ma_nv = d.ma_nv_xu_ly "
                + "WHERE d.trang_thai IN (?, ?) ";
        String[] args;
        if (maNvFilter > 0) {
            sql += "AND d.ma_nv_xu_ly=? ";
            args = new String[]{AppConstants.DS_DA_DUYET, AppConstants.DS_TU_CHOI, String.valueOf(maNvFilter)};
        } else {
            args = new String[]{AppConstants.DS_DA_DUYET, AppConstants.DS_TU_CHOI};
        }
        sql += "ORDER BY d.created_at_ms DESC LIMIT 200";
        Cursor c = db.rawQuery(sql, args);
        while (c.moveToNext()) {
            XuLyHistoryRow r = new XuLyHistoryRow();
            r.maDatSan = c.getInt(0);
            r.ngayDat = c.getString(1);
            r.gioBd = c.getString(2);
            r.gioKt = c.getString(3);
            r.trangThai = c.getString(4);
            r.tenSan = c.getString(5);
            r.tenKh = c.getString(6);
            r.maNvXuLy = c.getInt(7);
            r.tenNvXuLy = c.getString(8);
            list.add(r);
        }
        c.close();
        return list;
    }

    /**
     * Trùng lịch: cùng sân, cùng ngày, khoảng giờ giao (loại trừ ma_dat_san khi sửa).
     */
    public boolean hasConflict(int maSan, String ngayDat, String gioBd, String gioKt, Integer excludeMaDat) {
        Cursor c = db.rawQuery(
                "SELECT ma_dat_san, thoi_gian_bat_dau, thoi_gian_ket_thuc FROM dat_san WHERE ma_san=? AND ngay_dat=? "
                        + "AND trang_thai NOT IN (?, ?)",
                new String[]{String.valueOf(maSan), ngayDat, AppConstants.DS_HUY, AppConstants.DS_TU_CHOI});
        int b = com.example.pickerball.util.DateUtils.toMinutes(gioBd);
        int k = com.example.pickerball.util.DateUtils.toMinutes(gioKt);
        boolean conflict = false;
        while (c.moveToNext()) {
            int mid = c.getInt(0);
            if (excludeMaDat != null && mid == excludeMaDat) continue;
            int ob = com.example.pickerball.util.DateUtils.toMinutes(c.getString(1));
            int ok = com.example.pickerball.util.DateUtils.toMinutes(c.getString(2));
            if (ob < 0 || ok < 0) continue;
            if (b < ok && k > ob) {
                conflict = true;
                break;
            }
        }
        c.close();
        return conflict;
    }

    public int count() {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM dat_san", null);
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    /** Số phiếu đặt trong ngày (bỏ hủy / từ chối) — dashboard khách. */
    public int countBookingsOnDate(String yyyyMmDd) {
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM dat_san WHERE ngay_dat=? AND trang_thai NOT IN (?, ?)",
                new String[]{yyyyMmDd, AppConstants.DS_HUY, AppConstants.DS_TU_CHOI});
        int n = 0;
        if (c.moveToFirst()) n = c.getInt(0);
        c.close();
        return n;
    }

    public List<DatSanListItem> getAllWithNames() {
        List<DatSanListItem> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT d.ma_dat_san, d.ma_san, d.ma_kh, IFNULL(s.ten_san,''), IFNULL(k.ho_ten,''), "
                        + "d.thoi_gian_bat_dau, d.thoi_gian_ket_thuc, d.trang_thai, d.hinh_thuc "
                        + "FROM dat_san d "
                        + "LEFT JOIN san s ON s.ma_san = d.ma_san "
                        + "LEFT JOIN khach_hang k ON k.ma_kh = d.ma_kh "
                        + "ORDER BY d.ma_dat_san DESC",
                null);
        while (c.moveToNext()) {
            DatSanListItem row = new DatSanListItem();
            row.maDatSan = c.getInt(0);
            row.maSan = c.getInt(1);
            row.maKh = c.getInt(2);
            row.tenSan = c.getString(3);
            row.tenKh = c.getString(4);
            row.batDau = c.getString(5);
            row.ketThuc = c.getString(6);
            row.trangThai = c.getString(7);
            row.hinhThuc = c.getString(8);
            list.add(row);
        }
        c.close();
        return list;
    }

    public void delete(int maDatSan) {
        db.delete("su_dung_dv", "ma_dat_san=?", new String[]{String.valueOf(maDatSan)});
        db.delete("hoa_don", "ma_dat_san=?", new String[]{String.valueOf(maDatSan)});
        db.delete("dat_san", "ma_dat_san=?", new String[]{String.valueOf(maDatSan)});
    }
}
