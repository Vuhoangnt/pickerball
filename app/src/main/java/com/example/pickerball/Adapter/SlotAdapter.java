package com.example.pickerball.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.VH> {

    public interface OnSelectionChanged {
        void onChanged(List<KhungGioModel> selectedSlots);
    }

    private final List<KhungGioModel> list;
    private final boolean[] available;
    private final OnSelectionChanged onSelectionChanged;
    private final Set<Integer> selected = new LinkedHashSet<>();

    public SlotAdapter(List<KhungGioModel> list, boolean[] available, OnSelectionChanged onSelectionChanged) {
        this.list = list;
        this.available = available;
        this.onSelectionChanged = onSelectionChanged;
    }

    public void clearSelection() {
        selected.clear();
        notifyDataSetChanged();
        if (onSelectionChanged != null) onSelectionChanged.onChanged(new ArrayList<>());
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        KhungGioModel k = list.get(position);
        h.tvSlot.setText(k.gioBatDau + " — " + k.gioKetThuc);
        boolean ok = available[position];
        boolean isSelected = selected.contains(position);
        if (!ok) {
            h.tvStatus.setText("Đã có lịch");
            h.tvStatus.setTextColor(Color.parseColor("#B00020"));
            h.itemView.setAlpha(0.5f);
            ((CardView) h.itemView).setCardBackgroundColor(Color.WHITE);
        } else {
            h.tvStatus.setText(isSelected ? "Đang chọn" : "Còn trống");
            h.tvStatus.setTextColor(Color.parseColor("#0D9668"));
            h.itemView.setAlpha(1f);
            ((CardView) h.itemView).setCardBackgroundColor(
                    isSelected ? Color.parseColor("#E8F1FE") : Color.WHITE
            );
        }
        h.itemView.setOnClickListener(v -> {
            if (!ok) return;
            int idx = h.getBindingAdapterPosition();
            if (selected.contains(idx)) {
                selected.remove(idx);
            } else {
                if (!selected.isEmpty()) {
                    int min = Integer.MAX_VALUE;
                    int max = Integer.MIN_VALUE;
                    for (Integer s : selected) {
                        if (s < min) min = s;
                        if (s > max) max = s;
                    }
                    if (idx != min - 1 && idx != max + 1) {
                        Toast.makeText(v.getContext(), "Chỉ chọn khung giờ liền kề", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                selected.add(idx);
            }
            notifyDataSetChanged();
            if (onSelectionChanged != null) {
                List<KhungGioModel> out = new ArrayList<>();
                for (Integer i : selected) out.add(list.get(i));
                onSelectionChanged.onChanged(out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvSlot, tvStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            tvSlot = itemView.findViewById(R.id.tvSlot);
            tvStatus = itemView.findViewById(R.id.tvSlotStatus);
        }
    }
}
