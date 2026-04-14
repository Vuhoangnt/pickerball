package com.example.pickerball.Model;

public class KhuyenMaiModel {
    private int maKm;
    private String ten, moTa, ngayBd, ngayKt, dieuKien;
    private double giam;

    public int getMaKm() { return maKm; }
    public void setMaKm(int maKm) { this.maKm = maKm; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public double getGiam() { return giam; }
    public void setGiam(double giam) { this.giam = giam; }

    public String getNgayBd() { return ngayBd; }
    public void setNgayBd(String ngayBd) { this.ngayBd = ngayBd; }

    public String getNgayKt() { return ngayKt; }
    public void setNgayKt(String ngayKt) { this.ngayKt = ngayKt; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getDieuKien() { return dieuKien; }
    public void setDieuKien(String dieuKien) { this.dieuKien = dieuKien; }
}