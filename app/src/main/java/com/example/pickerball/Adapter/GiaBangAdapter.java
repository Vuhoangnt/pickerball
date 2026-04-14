package com.example.pickerball.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.DAO.CauHinhGiaDAO;
import com.example.pickerball.R;
import com.example.pickerball.util.LoaiNgayLabels;

import java.util.List;
import java.util.Locale;

public class GiaBangAdapter extends RecyclerView.Adapter<GiaBangAdapter.VH> {

    private final List<CauHinhGiaDAO.GiaTheoKhungRow> list;

    public GiaBangAdapter(List<CauHinhGiaDAO.GiaTheoKhungRow> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gia_khung_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CauHinhGiaDAO.GiaTheoKhungRow r = list.get(position);
        h.khung.setText(String.format(Locale.getDefault(), "%s – %s", r.gioBatDau, r.gioKetThuc));
        h.loai.setText(LoaiNgayLabels.label(r.loaiNgay));
        h.gia.setText(String.format(Locale.getDefault(), "%,.0f đ / khung", r.gia));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView khung, loai, gia;

        VH(@NonNull View itemView) {
            super(itemView);
            khung = itemView.findViewById(R.id.tvGiaKhung);
            loai = itemView.findViewById(R.id.tvGiaLoaiNgay);
            gia = itemView.findViewById(R.id.tvGiaTien);
        }
    }
}
