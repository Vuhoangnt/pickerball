package com.example.pickerball.Model;

public class CauHinhGiaModel {

    private int maGia;
    private int maSan;
    private int maKhung;       // 🔥 khóa ngoại sang khung_gio
    private String loaiNgay;   // thuong / cuoi_tuan / le
    private double gia;        // giá áp dụng

    // =====================
    // CONSTRUCTOR
    // =====================
    public CauHinhGiaModel() {}

    public CauHinhGiaModel(int maSan, int maKhung, String loaiNgay, double gia) {
        this.maSan = maSan;
        this.maKhung = maKhung;
        this.loaiNgay = loaiNgay;
        this.gia = gia;
    }

    // =====================
    // GET - SET
    // =====================
    public int getMaGia() { return maGia; }
    public void setMaGia(int maGia) { this.maGia = maGia; }

    public int getMaSan() { return maSan; }
    public void setMaSan(int maSan) { this.maSan = maSan; }

    public int getMaKhung() { return maKhung; }
    public void setMaKhung(int maKhung) { this.maKhung = maKhung; }

    public String getLoaiNgay() { return loaiNgay; }
    public void setLoaiNgay(String loaiNgay) { this.loaiNgay = loaiNgay; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    // =====================
    // HELPER
    // =====================
    public String getDisplayText() {
        return "Khung " + maKhung + " | " + loaiNgay + " | " + gia + "k";
    }

    @Override
    public String toString() {
        return "Gia{" +
                "maSan=" + maSan +
                ", maKhung=" + maKhung +
                ", loaiNgay=" + loaiNgay +
                ", gia=" + gia +
                '}';
    }
}