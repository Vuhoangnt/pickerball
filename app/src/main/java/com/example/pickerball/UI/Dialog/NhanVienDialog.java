package com.example.pickerball.UI.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pickerball.DAO.NhanVienDAO;
import com.example.pickerball.Model.NhanVienModel;
import com.example.pickerball.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class NhanVienDialog {

    private NhanVienDialog() {}

    public static void show(Context context, NhanVienModel existing, Runnable onDone) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_nhanvien, null);
        EditText edtTen = v.findViewById(R.id.edtNvTen);
        EditText edtCv = v.findViewById(R.id.edtNvCv);
        EditText edtSdt = v.findViewById(R.id.edtNvSdt);
        EditText edtNgay = v.findViewById(R.id.edtNvNgay);
        Spinner spTt = v.findViewById(R.id.spNvTt);

        String[] tt = {"DANG_LAM", "NGHI_VIEC"};
        spTt.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, tt));

        if (existing != null) {
            edtTen.setText(existing.getHoTen());
            edtCv.setText(existing.getChucVu());
            edtSdt.setText(existing.getSoDienThoai());
            edtNgay.setText(existing.getNgayVaoLam());
            for (int i = 0; i < tt.length; i++) {
                if (tt[i].equals(existing.getTrangThai())) {
                    spTt.setSelection(i);
                    break;
                }
            }
        } else {
            edtNgay.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            spTt.setSelection(0);
        }

        NhanVienDAO dao = new NhanVienDAO(context);

        new AlertDialog.Builder(context)
                .setTitle(existing == null ? "Thêm nhân viên" : "Sửa nhân viên")
                .setView(v)
                .setPositiveButton("Lưu", (d, w) -> {
                    String ten = String.valueOf(edtTen.getText()).trim();
                    if (ten.isEmpty()) {
                        Toast.makeText(context, "Nhập họ tên", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    NhanVienModel m = existing != null ? existing : new NhanVienModel();
                    m.setHoTen(ten);
                    m.setChucVu(String.valueOf(edtCv.getText()).trim());
                    m.setSoDienThoai(String.valueOf(edtSdt.getText()).trim());
                    m.setNgayVaoLam(String.valueOf(edtNgay.getText()).trim());
                    m.setTrangThai(tt[spTt.getSelectedItemPosition()]);
                    if (existing == null) dao.insert(m);
                    else dao.update(m);
                    Toast.makeText(context, "Đã lưu", Toast.LENGTH_SHORT).show();
                    if (onDone != null) onDone.run();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
