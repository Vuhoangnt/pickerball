package com.example.pickerball.Adapter;

import android.content.Context;
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

public class DichVuAdapter extends RecyclerView.Adapter<DichVuAdapter.VH> {

    public interface OnOpen {
        void onOpen(DichVuModel m);
    }

    private final Context context;
    private List<DichVuModel> list;
    private final OnOpen onOpen;

    public DichVuAdapter(Context context, List<DichVuModel> list, OnOpen onOpen) {
        this.context = context;
        this.list = list;
        this.onOpen = onOpen;
    }

    public void setList(List<DichVuModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_dichvu_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DichVuModel m = list.get(position);
        h.tvTen.setText(m.getTen());
        String dv = m.getDonVi() != null ? m.getDonVi() : "";
        h.tvGia.setText(String.format(Locale.getDefault(), "%,.0f đ / %s", m.getGia(), dv));
        h.itemView.setOnClickListener(v -> {
            if (onOpen != null) onOpen.onOpen(m);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTen, tvGia;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvDvTen);
            tvGia = itemView.findViewById(R.id.tvDvGia);
        }
    }
}
