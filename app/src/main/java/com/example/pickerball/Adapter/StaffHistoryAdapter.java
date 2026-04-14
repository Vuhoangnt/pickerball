package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.R;
import com.example.pickerball.util.TrangThaiLabels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffHistoryAdapter extends RecyclerView.Adapter<StaffHistoryAdapter.VH> {

    private final Context context;
    private List<DatSanDAO.XuLyHistoryRow> list;

    public StaffHistoryAdapter(Context context, List<DatSanDAO.XuLyHistoryRow> list) {
        this.context = context;
        this.list = list != null ? list : new ArrayList<>();
    }

    public void setList(List<DatSanDAO.XuLyHistoryRow> list) {
        this.list = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_staff_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DatSanDAO.XuLyHistoryRow r = list.get(position);
        h.title.setText(String.format(Locale.getDefault(), "#%d · %s", r.maDatSan, r.tenSan));
        String nv = r.tenNvXuLy != null && !r.tenNvXuLy.isEmpty() ? r.tenNvXuLy : "—";
        h.meta.setText(String.format(Locale.getDefault(),
                "%s · %s – %s\n%s · %s\nNV xử lý: %s",
                r.ngayDat, r.gioBd, r.gioKt,
                r.tenKh, TrangThaiLabels.vn(r.trangThai), nv));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView meta;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvHistTitle);
            meta = itemView.findViewById(R.id.tvHistMeta);
        }
    }
}
