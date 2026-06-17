package com.example.pickerball.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.AppConstants;
import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminSlotAdapter extends RecyclerView.Adapter<AdminSlotAdapter.VH> {

    /** Một ô khung giờ trong bảng lịch sân (admin). */
    public static class SlotCell {
        public KhungGioModel khung; // giờ bắt đầu / kết thúc
        public int maDatSan;       // 0 nếu trống
        public String trangThai;    // AppConstants.DS_*
        public String tenKh;        // null nếu trống
    }

    public interface OnSlotClick {
        void onClick(SlotCell cell);
    }

    private final List<SlotCell> data = new ArrayList<>();
    private final OnSlotClick onSlotClick;

    public AdminSlotAdapter(OnSlotClick onSlotClick) {
        this.onSlotClick = onSlotClick;
    }

    public void setData(List<SlotCell> newData) {
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_slot, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SlotCell cell = data.get(position);
        KhungGioModel k = cell.khung;
        if (k == null) return;

        h.tvTime.setText(String.format(Locale.US, "%s — %s",
                k.gioBatDau != null ? k.gioBatDau : "--:--",
                k.gioKetThuc != null ? k.gioKetThuc : "--:--"));

        boolean free = cell.maDatSan <= 0 || cell.trangThai == null
                || AppConstants.DS_HUY.equalsIgnoreCase(cell.trangThai)
                || AppConstants.DS_TU_CHOI.equalsIgnoreCase(cell.trangThai);

        int bgColor;
        String statusText;
        int statusColor;

        if (free) {
            bgColor = Color.WHITE;
            statusText = "Còn trống";
            statusColor = Color.parseColor("#0D9668");
            h.tvDetail.setVisibility(View.GONE);
        } else {
            String tt = cell.trangThai != null ? cell.trangThai.toUpperCase() : "";
            if (AppConstants.DS_CHO_DUYET.equals(tt)) {
                bgColor = Color.parseColor("#F8BBD0");
                statusText = "Chờ duyệt";
                statusColor = Color.parseColor("#AD1457");
            } else if (AppConstants.DS_DA_DUYET.equals(tt)) {
                bgColor = Color.parseColor("#FFE0B2");
                statusText = "Đã duyệt";
                statusColor = Color.parseColor("#E65100");
            } else if (AppConstants.DS_DA_XONG.equals(tt)) {
                bgColor = Color.parseColor("#E0E0E0");
                statusText = "Hoàn tất";
                statusColor = Color.parseColor("#424242");
            } else {
                bgColor = Color.parseColor("#FFE0B2");
                statusText = "Đã đặt";
                statusColor = Color.parseColor("#E65100");
            }
            if (cell.tenKh != null && !cell.tenKh.isEmpty()) {
                h.tvDetail.setVisibility(View.VISIBLE);
                h.tvDetail.setText(cell.tenKh);
            } else {
                h.tvDetail.setVisibility(View.GONE);
            }
        }

        ((CardView) h.itemView).setCardBackgroundColor(bgColor);
        h.tvStatus.setText(statusText);
        h.tvStatus.setTextColor(statusColor);
        h.itemView.setAlpha(1f);

        h.itemView.setOnClickListener(v -> {
            if (onSlotClick != null) onSlotClick.onClick(cell);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvTime, tvStatus, tvDetail;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvAdminSlotTime);
            tvStatus = itemView.findViewById(R.id.tvAdminSlotStatus);
            tvDetail = itemView.findViewById(R.id.tvAdminSlotDetail);
        }
    }
}