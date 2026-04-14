package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.NhanVienModel;
import com.example.pickerball.R;

import java.util.List;

public class NhanVienAdapter extends RecyclerView.Adapter<NhanVienAdapter.VH> {

    public interface OnOpen {
        void onOpen(NhanVienModel m);
    }

    private final Context context;
    private List<NhanVienModel> list;
    private final OnOpen onOpen;

    public NhanVienAdapter(Context context, List<NhanVienModel> list, OnOpen onOpen) {
        this.context = context;
        this.list = list;
        this.onOpen = onOpen;
    }

    public void setList(List<NhanVienModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_nv_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NhanVienModel m = list.get(position);
        h.tvTen.setText(m.getHoTen());
        h.tvMeta.setText((m.getChucVu() != null ? m.getChucVu() : "—") + " · " + m.getTrangThai()
                + " · " + (m.getSoDienThoai() != null ? m.getSoDienThoai() : ""));
        h.itemView.setOnClickListener(v -> {
            if (onOpen != null) onOpen.onOpen(m);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTen, tvMeta;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvNvTen);
            tvMeta = itemView.findViewById(R.id.tvNvMeta);
        }
    }
}
