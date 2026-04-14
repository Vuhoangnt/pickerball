package com.example.pickerball.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pickerball.AppConstants;

/**
 * CSDL đa vai trò. v15: ghi nhận NV duyệt/từ chối phiếu & NV thu tiền.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "PickleballManager.db";
    private static final int DB_VERSION = 15;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSchema(db);
        seed(db);
    }

    private static void createSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE san (" +
                "ma_san INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten_san TEXT NOT NULL," +
                "loai_san TEXT," +
                "trang_thai TEXT DEFAULT 'TRONG'," +
                "mo_ta TEXT," +
                "hinh_anh TEXT," +
                "gio_mo_cua TEXT DEFAULT '06:00'," +
                "gio_dong_cua TEXT DEFAULT '22:00'," +
                "gia_moi_gio REAL DEFAULT 120000)");

        db.execSQL("CREATE TABLE khung_gio (" +
                "ma_khung INTEGER PRIMARY KEY AUTOINCREMENT," +
                "gio_bat_dau TEXT NOT NULL," +
                "gio_ket_thuc TEXT NOT NULL)");

        db.execSQL("CREATE TABLE cau_hinh_gia (" +
                "ma_gia INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_san INTEGER NOT NULL," +
                "ma_khung INTEGER NOT NULL," +
                "loai_ngay TEXT NOT NULL," +
                "gia_ap_dung REAL NOT NULL," +
                "FOREIGN KEY(ma_san) REFERENCES san(ma_san)," +
                "FOREIGN KEY(ma_khung) REFERENCES khung_gio(ma_khung))");

        db.execSQL("CREATE TABLE khach_hang (" +
                "ma_kh INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ho_ten TEXT NOT NULL," +
                "so_dien_thoai TEXT UNIQUE," +
                "email TEXT," +
                "diem_tich_luy INTEGER DEFAULT 0," +
                "ngay_dang_ky TEXT," +
                "hang_hoi_vien TEXT DEFAULT '" + AppConstants.HANG_THUONG + "')");

        db.execSQL("CREATE TABLE nhan_vien (" +
                "ma_nv INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ho_ten TEXT NOT NULL," +
                "chuc_vu TEXT," +
                "so_dien_thoai TEXT," +
                "ngay_vao_lam TEXT," +
                "trang_thai TEXT DEFAULT 'DANG_LAM')");

        db.execSQL("CREATE TABLE dich_vu (" +
                "ma_dv INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten_dv TEXT," +
                "gia REAL," +
                "don_vi TEXT," +
                "mo_ta TEXT)");

        db.execSQL("CREATE TABLE tai_khoan (" +
                "ma_tk INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "vai_tro TEXT NOT NULL," +
                "ma_kh INTEGER," +
                "ma_nv INTEGER," +
                "ho_ten_hien_thi TEXT," +
                "anh_dai_dien TEXT," +
                "trang_thai TEXT DEFAULT 'HOAT_DONG'," +
                "FOREIGN KEY(ma_kh) REFERENCES khach_hang(ma_kh)," +
                "FOREIGN KEY(ma_nv) REFERENCES nhan_vien(ma_nv))");

        db.execSQL("CREATE TABLE dat_san (" +
                "ma_dat_san INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_san INTEGER NOT NULL," +
                "ma_kh INTEGER NOT NULL," +
                "ma_khung INTEGER," +
                "ngay_dat TEXT NOT NULL," +
                "thoi_gian_bat_dau TEXT NOT NULL," +
                "thoi_gian_ket_thuc TEXT NOT NULL," +
                "trang_thai TEXT DEFAULT '" + AppConstants.DS_CHO_DUYET + "'," +
                "hinh_thuc TEXT DEFAULT 'APP'," +
                "ghi_chu TEXT," +
                "tong_du_kien REAL DEFAULT 0," +
                "created_at_ms INTEGER," +
                "ma_nv_xu_ly INTEGER," +
                "FOREIGN KEY(ma_san) REFERENCES san(ma_san)," +
                "FOREIGN KEY(ma_kh) REFERENCES khach_hang(ma_kh)," +
                "FOREIGN KEY(ma_khung) REFERENCES khung_gio(ma_khung)," +
                "FOREIGN KEY(ma_nv_xu_ly) REFERENCES nhan_vien(ma_nv))");

        db.execSQL("CREATE TABLE su_dung_dv (" +
                "ma_sd INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_dat_san INTEGER," +
                "ma_dv INTEGER," +
                "so_luong INTEGER," +
                "FOREIGN KEY(ma_dat_san) REFERENCES dat_san(ma_dat_san)," +
                "FOREIGN KEY(ma_dv) REFERENCES dich_vu(ma_dv))");

        db.execSQL("CREATE TABLE khuyen_mai (" +
                "ma_km INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten_km TEXT," +
                "phan_tram REAL," +
                "ngay_bat_dau TEXT," +
                "ngay_ket_thuc TEXT)");

        db.execSQL("CREATE TABLE hoa_don (" +
                "ma_hd INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_dat_san INTEGER UNIQUE," +
                "ma_nv INTEGER," +
                "ma_km INTEGER," +
                "tien_san REAL," +
                "tien_dv REAL," +
                "tong_tien REAL," +
                "giam_tich_diem REAL DEFAULT 0," +
                "giam_hoi_vien REAL DEFAULT 0," +
                "trang_thai INTEGER DEFAULT 0," +
                "ngay_tt TEXT," +
                "phuong_thuc_tt TEXT," +
                "ma_nv_thanh_toan INTEGER," +
                "FOREIGN KEY(ma_dat_san) REFERENCES dat_san(ma_dat_san)," +
                "FOREIGN KEY(ma_nv) REFERENCES nhan_vien(ma_nv)," +
                "FOREIGN KEY(ma_nv_thanh_toan) REFERENCES nhan_vien(ma_nv)," +
                "FOREIGN KEY(ma_km) REFERENCES khuyen_mai(ma_km))");

        db.execSQL("CREATE TABLE danh_gia (" +
                "ma_dg INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_kh INTEGER," +
                "ma_san INTEGER," +
                "so_sao INTEGER," +
                "noi_dung TEXT," +
                "ngay TEXT," +
                "FOREIGN KEY(ma_kh) REFERENCES khach_hang(ma_kh)," +
                "FOREIGN KEY(ma_san) REFERENCES san(ma_san))");

        db.execSQL("CREATE TABLE thong_bao (" +
                "ma_tb INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_tk INTEGER NOT NULL," +
                "tieu_de TEXT," +
                "noi_dung TEXT," +
                "loai TEXT," +
                "da_doc INTEGER DEFAULT 0," +
                "thoi_gian_ms INTEGER," +
                "ma_dat_san INTEGER," +
                "FOREIGN KEY(ma_tk) REFERENCES tai_khoan(ma_tk))");

        db.execSQL("CREATE TABLE he_thong (" +
                "k TEXT PRIMARY KEY," +
                "gia_tri_so INTEGER NOT NULL DEFAULT 0)");

        db.execSQL("CREATE TABLE lich_su (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "hanh_dong TEXT," +
                "doi_tuong TEXT," +
                "thoi_gian TEXT)");

        db.execSQL("CREATE TABLE chi_phi (" +
                "ma_cp INTEGER PRIMARY KEY AUTOINCREMENT," +
                "noi_dung TEXT NOT NULL," +
                "so_tien REAL NOT NULL," +
                "ngay TEXT NOT NULL," +
                "danh_muc TEXT)");

        db.execSQL("CREATE TABLE san_anh (" +
                "ma_anh INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ma_san INTEGER NOT NULL," +
                "duong_dan TEXT NOT NULL," +
                "thu_tu INTEGER DEFAULT 0," +
                "FOREIGN KEY(ma_san) REFERENCES san(ma_san) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE san_dich_vu (" +
                "ma_san INTEGER NOT NULL," +
                "ma_dv INTEGER NOT NULL," +
                "PRIMARY KEY (ma_san, ma_dv)," +
                "FOREIGN KEY(ma_san) REFERENCES san(ma_san) ON DELETE CASCADE," +
                "FOREIGN KEY(ma_dv) REFERENCES dich_vu(ma_dv) ON DELETE CASCADE)");

        db.execSQL("CREATE INDEX idx_dat_san_san ON dat_san(ma_san)");
        db.execSQL("CREATE INDEX idx_dat_san_kh ON dat_san(ma_kh)");
        db.execSQL("CREATE INDEX idx_dat_san_ngay ON dat_san(ngay_dat)");
        db.execSQL("CREATE INDEX idx_chi_phi_ngay ON chi_phi(ngay)");
    }

    private static void seed(SQLiteDatabase db) {
        db.execSQL("INSERT INTO san (ten_san, loai_san, trang_thai, mo_ta, hinh_anh, gio_mo_cua, gio_dong_cua, gia_moi_gio) VALUES " +
                "('Sân PB A1 — Ngoài trời có mái che', 'Pickleball', 'TRONG', 'Mặt acrylic, phù hợp giải phong trào', null, '06:00', '22:00', 140000)," +
                "('Sân PB A2 — Trong nhà điều hòa', 'Pickleball', 'TRONG', 'Sàn thảm chuyên dụng, đèn LED', null, '06:00', '23:00', 165000)," +
                "('Sân VIP Sky — View thành phố', 'Pickleball', 'TRONG', 'Ghế nghỉ, tủ đồ riêng', null, '07:00', '23:00', 290000)," +
                "('Sân Tennis / PB đa năng B1', 'Tennis', 'TRONG', 'Có thể kẻ vạch pickleball', null, '06:00', '21:00', 185000)," +
                "('Sân mini C1 — Tập kỹ thuật', 'Pickleball', 'TRONG', '2–4 người, giá mềm', null, '06:00', '22:00', 115000)," +
                "('Sân đôi D1 — Khung giờ vàng', 'Pickleball', 'TRONG', 'Ưu tiên 18h–21h', null, '06:00', '22:00', 195000)");

        db.execSQL("INSERT INTO khung_gio (gio_bat_dau, gio_ket_thuc) VALUES " +
                "('06:00','07:00'),('07:00','08:00'),('08:00','09:00'),('09:00','10:00')," +
                "('10:00','11:00'),('11:00','12:00'),('17:00','18:00'),('18:00','19:00'),('19:00','20:00')");

        db.execSQL("INSERT INTO cau_hinh_gia (ma_san, ma_khung, loai_ngay, gia_ap_dung) VALUES " +
                "(1,7,'thuong',150000),(1,8,'thuong',170000),(2,8,'thuong',180000),(2,9,'cuoi_tuan',200000)," +
                "(3,9,'thuong',320000),(4,6,'thuong',160000),(5,5,'thuong',95000),(6,8,'le',220000)");

        db.execSQL("INSERT INTO khach_hang (ho_ten, so_dien_thoai, email, diem_tich_luy, ngay_dang_ky, hang_hoi_vien) VALUES " +
                "('Nguyễn Minh Anh', '0901000001', 'minhanh@gmail.com', 128, date('now','-90 days'), '" + AppConstants.HANG_BAC + "')," +
                "('Trần Quốc Huy', '0901000002', 'qhuy.work@outlook.com', 256, date('now','-120 days'), '" + AppConstants.HANG_VANG + "')," +
                "('Lê Thu Hà', '0901000003', 'thuha.le@yahoo.com', 45, date('now','-30 days'), '" + AppConstants.HANG_THUONG + "')," +
                "('Phạm Đức Thịnh', '0901000004', 'ducthinh.pb@gmail.com', 88, date('now','-45 days'), '" + AppConstants.HANG_BAC + "')," +
                "('Hoàng Mai Chi', '0901000005', 'maichi.h@gmail.com', 312, date('now','-200 days'), '" + AppConstants.HANG_VANG + "')," +
                "('Vũ Hoài Nam', '0901000006', 'hoainam.vu@gmail.com', 12, date('now','-7 days'), '" + AppConstants.HANG_THUONG + "')," +
                "('Đỗ Bảo Ngọc', '0901000007', 'baongoc.do@gmail.com', 67, date('now','-60 days'), '" + AppConstants.HANG_BAC + "')," +
                "('Bùi Gia Kiệt', '0901000008', 'giakiet.bui@gmail.com', 154, date('now','-100 days'), '" + AppConstants.HANG_BAC + "')," +
                "('Khách demo', '0901000099', 'khach@test.com', 120, date('now'), '" + AppConstants.HANG_THUONG + "')," +
                "('Công ty TNHH Thể thao Xanh', '02873001234', 'lienhe@thethaoxanh.vn', 0, date('now','-14 days'), '" + AppConstants.HANG_THUONG + "')");

        db.execSQL("INSERT INTO nhan_vien (ho_ten, chuc_vu, so_dien_thoai, ngay_vao_lam, trang_thai) VALUES " +
                "('Nguyễn Thị Lan', 'Lễ tân', '0902000001', date('now','-400 days'), 'DANG_LAM')," +
                "('Trần Văn Hùng', 'Quản sân', '0902000002', date('now','-200 days'), 'DANG_LAM')," +
                "('Lê Minh Tuấn', 'Thu ngân', '0902000003', date('now','-90 days'), 'DANG_LAM')");

        db.execSQL("INSERT INTO dich_vu (ten_dv, gia, don_vi, mo_ta) VALUES " +
                "('Nước suối Lavie', 12000, 'chai', '500ml')," +
                "('Thuê vợt Pickleball', 55000, 'giờ', 'Head / Joola')," +
                "('Khăn lạnh', 5000, 'cái', 'Dùng một lần')," +
                "('Cà phê phin', 25000, 'ly', 'Tại quầy')," +
                "('Thuê bóng mới', 80000, 'hộp', '3 quả')," +
                "('Bảo quản túi đồ', 15000, 'phiên', 'Tủ khóa')");

        db.execSQL("INSERT INTO tai_khoan (username, password, vai_tro, ma_kh, ma_nv, ho_ten_hien_thi, trang_thai) VALUES " +
                "('admin','admin123','" + AppConstants.ROLE_ADMIN + "',null,null,'Quản trị','HOAT_DONG')," +
                "('nv','nv123','" + AppConstants.ROLE_NHAN_VIEN + "',null,1,'Nguyễn Thị Lan','HOAT_DONG')," +
                "('khach','kh123','" + AppConstants.ROLE_KHACH + "',1,null,'Nguyễn Minh Anh','HOAT_DONG')");

        db.execSQL("INSERT OR IGNORE INTO he_thong (k, gia_tri_so) VALUES ('DIEM_KHI_DAT_SAN', 5)");

        long ts = System.currentTimeMillis();
        seedChiPhiMau(db);
        seedDatVaHoaDonMau(db, ts);

        db.execSQL("INSERT INTO lich_su (hanh_dong, doi_tuong, thoi_gian) VALUES ('Khởi tạo DB v14 (dữ liệu mẫu)','Hệ thống', datetime('now'))");

        seedSanAnhVaDichVu(db);
    }

    private static void seedChiPhiMau(SQLiteDatabase db) {
        insertChiPhi(db, "Tiền điện khu sân (ước tính tháng)", 5200000, 3, "VAN_HANH");
        insertChiPhi(db, "Nước sinh hoạt + vệ sinh", 890000, 5, "VAN_HANH");
        insertChiPhi(db, "Đổ xăng máy kéo lưới / xe đẩy", 420000, 8, "VAN_HANH");
        insertChiPhi(db, "Thay lưới sân A1", 1800000, 12, "BAO_TRI");
        insertChiPhi(db, "Sơn vá vạch sân B1", 950000, 18, "BAO_TRI");
        insertChiPhi(db, "Mua bóng pickleball (lô)", 3600000, 22, "VAT_TU");
        insertChiPhi(db, "Bảo hiểm công cộng (quý)", 2400000, 25, "VAN_HANH");
        insertChiPhi(db, "Lương part-time cuối tuần", 6800000, 28, "NHAN_SU");
        insertChiPhi(db, "Marketing Facebook / Google", 2100000, 35, "MARKETING");
        insertChiPhi(db, "Thuê kế toán dịch vụ", 1500000, 40, "VAN_HANH");
        insertChiPhi(db, "Sửa điều hòa phòng A2", 3200000, 42, "BAO_TRI");
        insertChiPhi(db, "Chi phí sự kiện giải nội bộ", 4500000, 48, "MARKETING");
        insertChiPhi(db, "Gửi xe khách (hỗ trợ)", 280000, 1, "VAN_HANH");
        insertChiPhi(db, "Mua dây cuốn lưới dự phòng", 640000, 15, "VAT_TU");
    }

    private static void insertChiPhi(SQLiteDatabase db, String noiDung, double soTien, int daysAgo, String danhMuc) {
        String esc = noiDung.replace("'", "''");
        String d = daysAgo <= 0 ? "date('now')" : "date('now','-" + daysAgo + " days')";
        db.execSQL("INSERT INTO chi_phi (noi_dung, so_tien, ngay, danh_muc) VALUES ('"
                + esc + "'," + soTien + "," + d + ",'" + danhMuc + "')");
    }

    private static void seedDatVaHoaDonMau(SQLiteDatabase db, long ts) {
        // Đặt chờ duyệt (không hóa đơn)
        insertDatSan(db, ts, 1, 9, 7, 0, "18:00", "19:30", AppConstants.DS_CHO_DUYET, 240000, "");
        insertDatSan(db, ts, 2, 3, 8, 1, "19:00", "20:00", AppConstants.DS_CHO_DUYET, 260000, "Gọi trước 30p");
        insertDatSan(db, ts, 3, 5, 9, 2, "18:30", "20:00", AppConstants.DS_DA_DUYET, 310000, "");
        // Đã xong + đã thanh toán
        insertDatHoaDonPaid(db, ts, 1, 2, 6, 52, "17:00", "18:30", 220000, 24000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 2, 4, 7, 48, "18:00", "19:30", 250000, 12000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 3, 1, 8, 45, "19:00", "20:30", 280000, 50000, "VI");
        insertDatHoaDonPaid(db, ts, 4, 6, 5, 41, "10:00", "11:30", 160000, 10000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 5, 7, 4, 38, "08:00", "09:00", 110000, 5000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 6, 8, 9, 35, "19:30", "21:00", 320000, 25000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 1, 3, 8, 32, "18:00", "19:00", 200000, 12000, "VI");
        insertDatHoaDonPaid(db, ts, 2, 5, 7, 30, "17:30", "19:00", 230000, 24000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 3, 2, 6, 28, "20:00", "21:30", 270000, 37000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 4, 10, 3, 25, "15:00", "16:30", 140000, 0, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 5, 1, 8, 22, "18:00", "19:30", 210000, 50000, "VI");
        insertDatHoaDonPaid(db, ts, 6, 4, 9, 20, "19:00", "20:30", 300000, 12000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 1, 6, 7, 18, "18:30", "20:00", 240000, 25000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 2, 7, 6, 15, "09:00", "10:30", 150000, 12000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 3, 8, 8, 12, "19:00", "20:00", 260000, 5000, "VI");
        insertDatHoaDonPaid(db, ts, 4, 3, 5, 10, "11:00", "12:30", 170000, 37000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 5, 5, 9, 8, "20:00", "21:30", 290000, 24000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 6, 2, 7, 6, "17:00", "18:30", 225000, 12000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 1, 4, 8, 5, "18:00", "19:30", 235000, 50000, "VI");
        insertDatHoaDonPaid(db, ts, 2, 1, 9, 4, "19:30", "21:00", 305000, 80000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 3, 9, 6, 3, "08:30", "10:00", 145000, 5000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 4, 6, 7, 2, "18:00", "19:30", 248000, 12000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 5, 3, 8, 1, "19:00", "20:30", 255000, 25000, "VI");
        // Hủy / từ chối
        insertDatSan(db, ts, 2, 2, 7, 14, "18:00", "19:00", AppConstants.DS_HUY, 200000, "Khách đổi lịch");
        insertDatSan(db, ts, 4, 5, 6, 16, "17:00", "18:00", AppConstants.DS_TU_CHOI, 180000, "Trùng giờ bảo trì");
    }

    private static void insertDatSan(SQLiteDatabase db, long ts, int maSan, int maKh, int maKhung,
                                     int daysAgo, String bd, String kt, String trangThai, double tongDuKien, String ghiChu) {
        String d = daysAgo <= 0 ? "date('now')" : "date('now','-" + daysAgo + " days')";
        String gc = ghiChu.replace("'", "''");
        db.execSQL("INSERT INTO dat_san (ma_san, ma_kh, ma_khung, ngay_dat, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, hinh_thuc, ghi_chu, tong_du_kien, created_at_ms) VALUES ("
                + maSan + "," + maKh + "," + maKhung + "," + d + ",'" + bd + "','" + kt + "','"
                + trangThai + "','APP','" + gc + "'," + tongDuKien + "," + ts + ")");
    }

    private static void insertDatHoaDonPaid(SQLiteDatabase db, long ts, int maSan, int maKh, int maKhung,
                                            int daysAgo, String bd, String kt,
                                            double tienSan, double tienDv, String pt) {
        String d = daysAgo <= 0 ? "date('now')" : "date('now','-" + daysAgo + " days')";
        double tong = tienSan + tienDv;
        db.execSQL("INSERT INTO dat_san (ma_san, ma_kh, ma_khung, ngay_dat, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, hinh_thuc, ghi_chu, tong_du_kien, created_at_ms) VALUES ("
                + maSan + "," + maKh + "," + maKhung + "," + d + ",'" + bd + "','" + kt + "','"
                + AppConstants.DS_DA_XONG + "','APP',''," + tong + "," + ts + ")");
        Cursor c = db.rawQuery("SELECT last_insert_rowid()", null);
        int mid = 0;
        if (c.moveToFirst()) mid = (int) c.getLong(0);
        c.close();
        String escPt = pt.replace("'", "''");
        db.execSQL("INSERT INTO hoa_don (ma_dat_san, ma_nv, tien_san, tien_dv, tong_tien, trang_thai, ngay_tt, phuong_thuc_tt) VALUES ("
                + mid + ",1," + tienSan + "," + tienDv + "," + tong + ",1," + d + ",'" + escPt + "')");
    }

    private static void seedSanAnhVaDichVu(SQLiteDatabase db) {
        db.execSQL("INSERT INTO san_anh (ma_san, duong_dan, thu_tu) VALUES " +
                "(1,'drawable://ic_ball',0),(1,'drawable://ic_home',1),(2,'drawable://ic_ball',0),(2,'drawable://ic_menu',1)," +
                "(3,'drawable://ic_ball',0),(4,'drawable://ic_menu',0),(5,'drawable://ic_ball',0),(6,'drawable://ic_ball',0)");
        db.execSQL("INSERT INTO san_dich_vu (ma_san, ma_dv) VALUES " +
                "(1,1),(1,2),(1,3),(2,1),(2,2),(2,4),(3,1),(3,5),(4,1),(4,2),(4,6),(5,1),(6,1),(6,2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 12) {
            db.execSQL("CREATE TABLE IF NOT EXISTS san_anh (" +
                    "ma_anh INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ma_san INTEGER NOT NULL," +
                    "duong_dan TEXT NOT NULL," +
                    "thu_tu INTEGER DEFAULT 0," +
                    "FOREIGN KEY(ma_san) REFERENCES san(ma_san) ON DELETE CASCADE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS san_dich_vu (" +
                    "ma_san INTEGER NOT NULL," +
                    "ma_dv INTEGER NOT NULL," +
                    "PRIMARY KEY (ma_san, ma_dv)," +
                    "FOREIGN KEY(ma_san) REFERENCES san(ma_san) ON DELETE CASCADE," +
                    "FOREIGN KEY(ma_dv) REFERENCES dich_vu(ma_dv) ON DELETE CASCADE)");
            Cursor c = db.rawQuery("SELECT COUNT(*) FROM san_anh", null);
            int n = 0;
            if (c.moveToFirst()) n = c.getInt(0);
            c.close();
            if (n == 0) {
                seedSanAnhVaDichVu(db);
            }
        }

        if (oldVersion < 13) {
            // Thêm trường lưu hình thức thanh toán hóa đơn.
            // Dùng ALTER TABLE để tránh phá dữ liệu cũ.
            db.execSQL("ALTER TABLE hoa_don ADD COLUMN phuong_thuc_tt TEXT");
        }

        if (oldVersion < 14) {
            db.execSQL("CREATE TABLE IF NOT EXISTS chi_phi (" +
                    "ma_cp INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "noi_dung TEXT NOT NULL," +
                    "so_tien REAL NOT NULL," +
                    "ngay TEXT NOT NULL," +
                    "danh_muc TEXT)");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_chi_phi_ngay ON chi_phi(ngay)");
            migrateV14ExtraDemo(db);
        }

        if (oldVersion < 15) {
            db.execSQL("ALTER TABLE dat_san ADD COLUMN ma_nv_xu_ly INTEGER");
            db.execSQL("ALTER TABLE hoa_don ADD COLUMN ma_nv_thanh_toan INTEGER");
            db.execSQL("UPDATE hoa_don SET ma_nv_thanh_toan = ma_nv WHERE trang_thai = 1 AND ma_nv_thanh_toan IS NULL");
        }
    }

    /** Bổ sung chi phí + vài phiếu/hóa đơn mẫu cho DB đã tồn tại (một lần). */
    private static void migrateV14ExtraDemo(SQLiteDatabase db) {
        Cursor f = db.rawQuery("SELECT gia_tri_so FROM he_thong WHERE k='MIGRATION_V14'", null);
        if (f.moveToFirst()) {
            f.close();
            return;
        }
        f.close();

        Cursor cnt = db.rawQuery("SELECT COUNT(*) FROM chi_phi", null);
        int nChi = 0;
        if (cnt.moveToFirst()) nChi = cnt.getInt(0);
        cnt.close();
        if (nChi == 0) {
            seedChiPhiMau(db);
        }

        long ts = System.currentTimeMillis();
        insertDatHoaDonPaid(db, ts, 1, 1, 8, 11, "18:00", "19:30", 220000, 12000, "TIEN_MAT");
        insertDatHoaDonPaid(db, ts, 2, 2, 7, 9, "19:00", "20:30", 250000, 24000, "CHUYEN_KHOAN");
        insertDatHoaDonPaid(db, ts, 3, 1, 9, 13, "17:30", "19:00", 270000, 50000, "VI");

        ContentValues cv = new ContentValues();
        cv.put("k", "MIGRATION_V14");
        cv.put("gia_tri_so", 1);
        db.insert("he_thong", null, cv);
    }
}
