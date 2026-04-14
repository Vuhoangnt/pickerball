package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.DatSanListItem;
import com.example.pickerball.R;

import java.util.List;

public class DatSanAdapter extends RecyclerView.Adapter<DatSanAdapter.VH> {

    public interface OnPick {
        void onLongPick(DatSanListItem row);
    }

    private final Context context;
    private List<DatSanListItem> list;
    private final OnPick onPick;

    public DatSanAdapter(Context context, List<DatSanListItem> list, OnPick onPick) {
        this.context = context;
        this.list = list;
        this.onPick = onPick;
    }

    public void setList(List<DatSanListItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_dat_san_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DatSanListItem r = list.get(position);
        h.tvTitle.setText(r.tenSan + " · " + r.tenKh);
        h.tvTime.setText(r.batDau + " — " + r.ketThuc);
        h.tvMeta.setText("Trạng thái: " + r.trangThai + " · Hình thức: " + r.hinhThuc);
        h.itemView.setOnLongClickListener(v -> {
            if (onPick != null) onPick.onLongPick(r);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvMeta;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDsTitle);
            tvTime = itemView.findViewById(R.id.tvDsTime);
            tvMeta = itemView.findViewById(R.id.tvDsMeta);
        }
    }
}
