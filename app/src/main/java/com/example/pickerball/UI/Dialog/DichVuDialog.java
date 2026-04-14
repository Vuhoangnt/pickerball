package com.example.pickerball.UI.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pickerball.DAO.DichVuDAO;
import com.example.pickerball.Model.DichVuModel;
import com.example.pickerball.R;

public final class DichVuDialog {

    private DichVuDialog() {}

    public static void show(Context context, DichVuModel existing, Runnable onDone) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_dichvu, null);
        EditText edtTen = v.findViewById(R.id.edtDvTen);
        EditText edtGia = v.findViewById(R.id.edtDvGia);
        EditText edtDv = v.findViewById(R.id.edtDvDonVi);
        EditText edtMt = v.findViewById(R.id.edtDvMoTa);

        if (existing != null) {
            edtTen.setText(existing.getTen());
            edtGia.setText(String.valueOf((long) existing.getGia()));
            edtDv.setText(existing.getDonVi());
            edtMt.setText(existing.getMoTa());
        }

        DichVuDAO dao = new DichVuDAO(context);

        new AlertDialog.Builder(context)
                .setTitle(existing == null ? "Thêm dịch vụ" : "Sửa dịch vụ")
                .setView(v)
                .setPositiveButton("Lưu", (d, w) -> {
                    String ten = String.valueOf(edtTen.getText()).trim();
                    if (ten.isEmpty()) {
                        Toast.makeText(context, "Nhập tên dịch vụ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double gia = 0;
                    try {
                        gia = Double.parseDouble(String.valueOf(edtGia.getText()).trim().replace(",", "."));
                    } catch (NumberFormatException ignored) {}
                    DichVuModel m = existing != null ? existing : new DichVuModel();
                    m.setTen(ten);
                    m.setGia(gia);
                    m.setDonVi(String.valueOf(edtDv.getText()).trim());
                    m.setMoTa(String.valueOf(edtMt.getText()).trim());
                    if (existing == null) dao.insert(m);
                    else dao.update(m);
                    Toast.makeText(context, "Đã lưu", Toast.LENGTH_SHORT).show();
                    if (onDone != null) onDone.run();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
