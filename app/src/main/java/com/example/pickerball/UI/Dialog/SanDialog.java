package com.example.pickerball.UI.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickerball.DAO.CauHinhGiaDAO;
import com.example.pickerball.DAO.KhungGioDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.DAO.SanAnhDAO;
import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.Model.SanAnhModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.util.DateUtils;
import com.example.pickerball.util.SanImageHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SanDialog {

    public interface SanDialogListener {
        void onSave();
    }

    /** Truyền từ {@code AdminSanFragment} để mở thư viện ảnh thiết bị. */
    public interface GalleryPickHost {
        void pickFromDevice(OnDeviceImage callback);

        interface OnDeviceImage {
            void onResult(String fileUrlOrNull);
        }
    }

    private static class GiaRowViews {
        int maKhung;
        EditText edtThuong;
        EditText edtCuoi;
    }

    public static void showDialog(Context context, SanModel san, SanDialogListener listener) {
        showDialog(context, san, listener, null);
    }

    public static void showDialog(Context context, SanModel san, SanDialogListener listener,
                                  GalleryPickHost galleryHost) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_san, null);

        TextInputEditText edtTen = view.findViewById(R.id.edtTen);
        TextInputEditText edtLoai = view.findViewById(R.id.edtLoai);
        TextInputEditText edtMoTa = view.findViewById(R.id.edtMoTa);
        Spinner spTrangThai = view.findViewById(R.id.spTrangThai);

        TextInputEditText edtGioMoCua = view.findViewById(R.id.edtGioMoCua);
        TextInputEditText edtGioDongCua = view.findViewById(R.id.edtGioDongCua);
        TextInputEditText edtGiaMoiGio = view.findViewById(R.id.edtGiaMoiGio);

        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        MaterialButton btnDelete = view.findViewById(R.id.btnDelete);
        MaterialButton btnAnhFromDevice = view.findViewById(R.id.btnAnhFromDevice);

        LinearLayout llSanAnhList = view.findViewById(R.id.llSanAnhList);
        Spinner spAnhPick = view.findViewById(R.id.spAnhPick);
        MaterialButton btnAnhAdd = view.findViewById(R.id.btnAnhAdd);
        LinearLayout llKhungGia = view.findViewById(R.id.llKhungGia);

        SanDAO sanDao = new SanDAO(context);
        SanAnhDAO anhDao = new SanAnhDAO(context);
        CauHinhGiaDAO giaDao = new CauHinhGiaDAO(context);
        KhungGioDAO kgDao = new KhungGioDAO(context);

        List<String> selectedImages = new ArrayList<>();
        List<GiaRowViews> giaRows = new ArrayList<>();

        // ======================
        // SPINNER DATA
        // ======================

        String[] trangThaiList = {"TRONG", "DA_DAT"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                trangThaiList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrangThai.setAdapter(adapter);

        // ======================
        // IMAGE PICK (demo)
        // ======================
        // Ở app hiện tại không có chọn ảnh từ máy, nên dùng danh sách drawable có sẵn (giống app cũ).
        String[] anhOptions = {"ic_ball", "ic_home", "ic_menu", "client", "ic_logout"};
        ArrayAdapter<String> imgAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                anhOptions
        );
        imgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAnhPick.setAdapter(imgAdapter);

        // ======================
        // SET DATA KHI UPDATE
        // ======================
        if (san != null) {
            edtTen.setText(san.tenSan);
            edtLoai.setText(san.loaiSan);
            edtMoTa.setText(san.moTa);
            edtGioMoCua.setText(san.gioMoCua != null ? san.gioMoCua : "06:00");
            edtGioDongCua.setText(san.gioDongCua != null ? san.gioDongCua : "22:00");
            edtGiaMoiGio.setText(String.valueOf((san.giaMoiGio > 0 ? san.giaMoiGio : 120000)));

            // set spinner đúng trạng thái
            for (int i = 0; i < spTrangThai.getCount(); i++) {
                if (spTrangThai.getItemAtPosition(i).toString().equals(san.trangThai)) {
                    spTrangThai.setSelection(i);
                    break;
                }
            }

            btnSave.setText("CẬP NHẬT");
            btnDelete.setVisibility(View.VISIBLE);

            selectedImages.clear();
            selectedImages.addAll(loadImagesForSan(anhDao, san.maSan));

        } else {
            btnSave.setText("THÊM SÂN");
            btnDelete.setVisibility(View.GONE); // ⚡ Ẩn khi thêm

            edtGioMoCua.setText("06:00");
            edtGioDongCua.setText("22:00");
            edtGiaMoiGio.setText("120000");
        }

        // ======================
        // DIALOG
        // ======================
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        // ======================
        // RENDER IMAGES + GIA THEO KHUNG
        // ======================
        class ImageRenderer {
            void render() {
                llSanAnhList.removeAllViews();
                for (int i = 0; i < selectedImages.size(); i++) {
                    final int idx = i;
                    View item = LayoutInflater.from(context).inflate(R.layout.item_san_anh_thumb, llSanAnhList, false);
                    ImageView iv = item.findViewById(R.id.ivThumb);
                    ImageButton del = item.findViewById(R.id.btnDelThumb);

                    String duongDan = selectedImages.get(i);
                    SanImageHelper.loadInto(context, iv, duongDan, 256);

                    del.setOnClickListener(v2 -> {
                        selectedImages.remove(idx);
                        render();
                    });

                    llSanAnhList.addView(item);
                }
            }
        }
        ImageRenderer renderer = new ImageRenderer();
        renderer.render();

        if (galleryHost != null) {
            btnAnhFromDevice.setVisibility(View.VISIBLE);
            btnAnhFromDevice.setOnClickListener(v -> galleryHost.pickFromDevice(path -> {
                if (path == null || path.isEmpty()) return;
                if (selectedImages.contains(path)) {
                    Toast.makeText(context, "Đã có ảnh này rồi", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedImages.add(path);
                renderer.render();
            }));
        } else {
            btnAnhFromDevice.setVisibility(View.GONE);
        }

        List<KhungGioModel> khungList = kgDao.getKhungForSan(san);
        giaRows.clear();
        llKhungGia.removeAllViews();
        for (KhungGioModel k : khungList) {
            View rowView = LayoutInflater.from(context).inflate(R.layout.item_khung_gia_row, llKhungGia, false);
            EditText edtThuong = rowView.findViewById(R.id.edtGiaThuong);
            EditText edtCuoi = rowView.findViewById(R.id.edtGiaCuoi);
            TextView tvLabel = rowView.findViewById(R.id.tvKhungLabel);

            tvLabel.setText(k.gioBatDau + "-" + k.gioKetThuc);

            if (san != null && san.maSan > 0) {
                double gThuong = giaDao.getGia(san.maSan, k.maKhung, "thuong");
                if (gThuong >= 0) edtThuong.setText(String.format(Locale.getDefault(), "%.0f", gThuong));
                double gCuoi = giaDao.getGia(san.maSan, k.maKhung, "cuoi_tuan");
                if (gCuoi >= 0) edtCuoi.setText(String.format(Locale.getDefault(), "%.0f", gCuoi));
            }

            GiaRowViews g = new GiaRowViews();
            g.maKhung = k.maKhung;
            g.edtThuong = edtThuong;
            g.edtCuoi = edtCuoi;
            giaRows.add(g);

            llKhungGia.addView(rowView);
        }

        btnAnhAdd.setOnClickListener(x -> {
            Object sel = spAnhPick.getSelectedItem();
            if (sel == null) return;
            String name = sel.toString();
            String duongDan = "drawable://" + name;
            if (selectedImages.contains(duongDan)) {
                Toast.makeText(context, "Đã có ảnh này rồi", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedImages.add(duongDan);
            renderer.render();
        });

        // ======================
        // SAVE (ADD / UPDATE)
        // ======================
        btnSave.setOnClickListener(v -> {

            String ten = edtTen.getText().toString().trim();

            if (ten.isEmpty()) {
                Toast.makeText(context, "Nhập tên sân!", Toast.LENGTH_SHORT).show();
                return;
            }

            SanModel s;

            if (san == null) {
                s = new SanModel(); // thêm mới
            } else {
                s = san; // update
            }

            s.tenSan = ten;
            s.loaiSan = edtLoai.getText().toString();
            s.moTa = edtMoTa.getText().toString();
            s.trangThai = spTrangThai.getSelectedItem().toString();

            String gioMo = edtGioMoCua.getText() != null ? edtGioMoCua.getText().toString().trim() : "";
            String gioDong = edtGioDongCua.getText() != null ? edtGioDongCua.getText().toString().trim() : "";
            int moMin = DateUtils.toMinutes(gioMo);
            int dongMin = DateUtils.toMinutes(gioDong);
            if (moMin < 0 || dongMin < 0 || dongMin <= moMin) {
                Toast.makeText(context, "Giờ mở/đóng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            s.gioMoCua = gioMo;
            s.gioDongCua = gioDong;

            String sGia = edtGiaMoiGio.getText() != null ? edtGiaMoiGio.getText().toString().trim() : "";
            double giaMoiGio;
            try {
                giaMoiGio = Double.parseDouble(sGia);
            } catch (Exception e) {
                giaMoiGio = 120000;
            }
            if (giaMoiGio <= 0) giaMoiGio = 120000;
            s.giaMoiGio = giaMoiGio;

            if (!selectedImages.isEmpty()) {
                s.hinhAnh = selectedImages.get(0);
            } else {
                s.hinhAnh = null;
            }

            if (s.maSan == 0) {
                long id = sanDao.insertAndGetId(s);
                s.maSan = (int) id;
                Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show();
            } else {
                sanDao.update(s);
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            }

            // Persist images (clear + reinsert theo thứ tự)
            anhDao.deleteByMaSan(s.maSan);
            for (int i = 0; i < selectedImages.size(); i++) {
                anhDao.insert(s.maSan, selectedImages.get(i), i);
            }

            // Persist prices by khung + loại ngày
            for (GiaRowViews row : giaRows) {
                String st = row.edtThuong.getText() != null ? row.edtThuong.getText().toString().trim() : "";
                if (st.isEmpty()) {
                    giaDao.deleteGia(s.maSan, row.maKhung, "thuong");
                } else {
                    double val = Double.parseDouble(st);
                    giaDao.upsertGia(s.maSan, row.maKhung, "thuong", val);
                }
                String sc = row.edtCuoi.getText() != null ? row.edtCuoi.getText().toString().trim() : "";
                if (sc.isEmpty()) {
                    giaDao.deleteGia(s.maSan, row.maKhung, "cuoi_tuan");
                } else {
                    double val = Double.parseDouble(sc);
                    giaDao.upsertGia(s.maSan, row.maKhung, "cuoi_tuan", val);
                }
            }

            dialog.dismiss();

            if (listener != null) {
                listener.onSave();
            }
        });

        // ======================
        // DELETE (CHỈ KHI UPDATE)
        // ======================
        btnDelete.setOnClickListener(v -> {

            if (san == null) return; // ⚡ tránh crash

            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn xóa sân này?")
                    .setPositiveButton("Xóa", (d, i) -> {
                        try {
                            new CauHinhGiaDAO(context).deleteByMaSan(san.maSan);
                            anhDao.deleteByMaSan(san.maSan);
                            sanDao.delete(san.maSan);
                            Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(context, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();

                        if (listener != null) {
                            listener.onSave();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        dialog.show();
    }

    private static List<String> loadImagesForSan(SanAnhDAO anhDao, int maSan) {
        List<String> out = new ArrayList<>();
        for (SanAnhModel m : anhDao.listByMaSan(maSan)) {
            if (m.duongDan != null && !m.duongDan.isEmpty()) out.add(m.duongDan);
        }
        return out;
    }
}