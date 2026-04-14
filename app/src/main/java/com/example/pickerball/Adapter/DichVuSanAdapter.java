package com.example.pickerball.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.DichVuModel;
import com.example.pickerball.R;

import java.util.List;
import java.util.Locale;

public class DichVuSanAdapter extends RecyclerView.Adapter<DichVuSanAdapter.VH> {

    private final List<DichVuModel> list;

    public DichVuSanAdapter(List<DichVuModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dichvu_san_small, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DichVuModel m = list.get(position);
        h.ten.setText(m.getTen());
        h.meta.setText(String.format(Locale.getDefault(), "%,.0f đ / %s · %s",
                m.getGia(), m.getDonVi() != null ? m.getDonVi() : "—",
                m.getMoTa() != null ? m.getMoTa() : ""));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView ten, meta;

        VH(@NonNull View itemView) {
            super(itemView);
            ten = itemView.findViewById(R.id.tvDvTen);
            meta = itemView.findViewById(R.id.tvDvMeta);
        }
    }
}
