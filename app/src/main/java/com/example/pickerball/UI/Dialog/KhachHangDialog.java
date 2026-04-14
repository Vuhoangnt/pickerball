package com.example.pickerball.UI.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class KhachHangDialog {

    private KhachHangDialog() {}

    public static void show(Context context, KhachHangModel existing, Runnable onDone) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_khachhang, null);
        EditText edtTen = v.findViewById(R.id.edtKhTen);
        EditText edtSdt = v.findViewById(R.id.edtKhSdt);
        EditText edtMail = v.findViewById(R.id.edtKhEmail);
        EditText edtDiem = v.findViewById(R.id.edtKhDiem);

        if (existing != null) {
            edtTen.setText(existing.getHoTen());
            edtSdt.setText(existing.getSoDienThoai());
            edtMail.setText(existing.getEmail());
            edtDiem.setText(String.valueOf(existing.getDiem()));
        }

        KhachHangDAO dao = new KhachHangDAO(context);

        new AlertDialog.Builder(context)
                .setTitle(existing == null ? "Thêm khách hàng" : "Sửa khách hàng")
                .setView(v)
                .setPositiveButton("Lưu", (d, w) -> {
                    String ten = String.valueOf(edtTen.getText()).trim();
                    String sdt = String.valueOf(edtSdt.getText()).trim();
                    if (ten.isEmpty() || sdt.isEmpty()) {
                        Toast.makeText(context, "Nhập họ tên và SĐT", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int diem = 0;
                    try {
                        diem = Integer.parseInt(String.valueOf(edtDiem.getText()).trim());
                    } catch (NumberFormatException ignored) {}
                    String mail = String.valueOf(edtMail.getText()).trim();
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    KhachHangModel m = existing != null ? existing : new KhachHangModel();
                    m.setHoTen(ten);
                    m.setSoDienThoai(sdt);
                    m.setEmail(mail.isEmpty() ? null : mail);
                    m.setDiem(diem);
                    if (existing == null) {
                        m.setNgayDangKy(today);
                        dao.insert(m);
                        Toast.makeText(context, "Đã thêm", Toast.LENGTH_SHORT).show();
                    } else {
                        if (m.getNgayDangKy() == null || m.getNgayDangKy().isEmpty()) {
                            m.setNgayDangKy(today);
                        }
                        dao.update(m);
                        Toast.makeText(context, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    }
                    if (onDone != null) onDone.run();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
