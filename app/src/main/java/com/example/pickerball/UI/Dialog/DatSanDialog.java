package com.example.pickerball.UI.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pickerball.AppConstants;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.DatSanModel;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DatSanDialog {

    private DatSanDialog() {}

    public static void show(Context context, Runnable onDone) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_dat_san, null);
        Spinner spSan = v.findViewById(R.id.spDatSan);
        Spinner spKh = v.findViewById(R.id.spDatKh);
        EditText edtBd = v.findViewById(R.id.edtGioBd);
        EditText edtKt = v.findViewById(R.id.edtGioKt);
        Spinner spTt = v.findViewById(R.id.spDatTt);
        Spinner spHt = v.findViewById(R.id.spDatHt);

        SanDAO sanDao = new SanDAO(context);
        List<SanModel> sans = sanDao.getAll();
        if (sans.isEmpty()) {
            Toast.makeText(context, "Chưa có sân — thêm sân trước", Toast.LENGTH_LONG).show();
            return;
        }
        String[] sanLabels = new String[sans.size()];
        for (int i = 0; i < sans.size(); i++) sanLabels[i] = sans.get(i).tenSan;
        spSan.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, sanLabels));

        KhachHangDAO khDao = new KhachHangDAO(context);
        List<KhachHangModel> khs = khDao.getAll();
        if (khs.isEmpty()) {
            Toast.makeText(context, "Chưa có khách — thêm khách trước", Toast.LENGTH_LONG).show();
            return;
        }
        String[] khLabels = new String[khs.size()];
        for (int i = 0; i < khs.size(); i++) khLabels[i] = khs.get(i).getHoTen() + " (#" + khs.get(i).getMaKh() + ")";
        spKh.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, khLabels));

        final String[] ttLabels = {"Chờ duyệt", "Đã duyệt", "Hủy"};
        final String[] ttVal = {AppConstants.DS_CHO_DUYET, AppConstants.DS_DA_DUYET, AppConstants.DS_HUY};
        spTt.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, ttLabels));
        String[] ht = {"TRUC_TIEP", "APP"};
        spHt.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, ht));
        spTt.setSelection(1);
        spHt.setSelection(0);

        DatSanDAO dsDao = new DatSanDAO(context);

        new AlertDialog.Builder(context)
                .setTitle("Thêm phiếu đặt sân")
                .setView(v)
                .setPositiveButton("Lưu", (d, w) -> {
                    int iSan = spSan.getSelectedItemPosition();
                    int iKh = spKh.getSelectedItemPosition();
                    String bd = String.valueOf(edtBd.getText()).trim();
                    String kt = String.valueOf(edtKt.getText()).trim();
                    if (bd.isEmpty() || kt.isEmpty()) {
                        Toast.makeText(context, "Nhập giờ bắt đầu / kết thúc", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int maSan = sans.get(iSan).maSan;
                    int maKh = khs.get(iKh).getMaKh();
                    String tr = ttVal[spTt.getSelectedItemPosition()];
                    String h = ht[spHt.getSelectedItemPosition()];
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    DatSanModel row = new DatSanModel();
                    row.maSan = maSan;
                    row.maKh = maKh;
                    row.maKhung = null;
                    row.ngayDat = today;
                    row.thoiGianBatDau = bd;
                    row.thoiGianKetThuc = kt;
                    row.trangThai = tr;
                    row.hinhThuc = h;
                    row.ghiChu = null;
                    row.tongDuKien = 0;
                    row.createdAtMs = System.currentTimeMillis();
                    dsDao.insert(row);
                    Toast.makeText(context, "Đã thêm phiếu", Toast.LENGTH_SHORT).show();
                    if (onDone != null) onDone.run();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
