package com.example.pickerball.Model;


public class KhachHangModel {
    private int maKh;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private int diem;
    private String ngayDangKy;
    private String hangHoiVien;

    public KhachHangModel() {}

    public int getMaKh() { return maKh; }
    public void setMaKh(int maKh) { this.maKh = maKh; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getDiem() { return diem; }
    public void setDiem(int diem) { this.diem = diem; }

    public String getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(String ngayDangKy) { this.ngayDangKy = ngayDangKy; }

    public String getHangHoiVien() { return hangHoiVien; }
    public void setHangHoiVien(String hangHoiVien) { this.hangHoiVien = hangHoiVien; }
}