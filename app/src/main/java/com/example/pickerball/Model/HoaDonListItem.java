package com.example.pickerball.Model;

public class HoaDonListItem {
    public int maHd;
    public int maDatSan;
    /** Chi tiết hóa đơn (từ bảng hoa_don). */
    public double tienSan;
    public double tienDv;
    public double tongTien;
    public int trangThai;
    public String tenSan;
    public String ngayTt;
    public String phuongThucTt;
    /** NV tạo HĐ khi duyệt đơn. */
    public int maNvDuyet;
    public String tenNvDuyet;
    /** NV xác nhận thanh toán (sau khi đã TT). */
    public int maNvThanhToan;
    public String tenNvThanhToan;
}
