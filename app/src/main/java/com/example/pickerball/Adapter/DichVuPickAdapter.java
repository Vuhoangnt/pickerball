package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.DichVuModel;
import com.example.pickerball.R;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DichVuPickAdapter extends RecyclerView.Adapter<DichVuPickAdapter.VH> {

    public interface OnSelectionChange {
        void onSelectionChanged(Set<Integer> selectedMaDv);
    }

    private final Context context;
    private final List<DichVuModel> list;
    private final Set<Integer> selected = new HashSet<>();
    private final OnSelectionChange listener;

    public DichVuPickAdapter(Context context, List<DichVuModel> list, OnSelectionChange listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public Set<Integer> getSelectedMaDv() {
        return new HashSet<>(selected);
    }

    public void clearSelection() {
        selected.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dichvu_pick, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DichVuModel m = list.get(position);
        int maDv = m.getMaDv();
        h.cb.setChecked(selected.contains(maDv));
        h.tvTen.setText(m.getTen());
        h.tvMeta.setText(String.format(Locale.getDefault(), "%,.0f đ / %s · %s",
                m.getGia(),
                m.getDonVi() != null ? m.getDonVi() : "—",
                m.getMoTa() != null ? m.getMoTa() : ""));

        h.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selected.add(maDv);
            else selected.remove(maDv);
            if (listener != null) listener.onSelectionChanged(getSelectedMaDv());
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final CheckBox cb;
        final TextView tvTen, tvMeta;

        VH(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cbDv);
            tvTen = itemView.findViewById(R.id.tvDvTen);
            tvMeta = itemView.findViewById(R.id.tvDvMeta);
        }
    }
}

