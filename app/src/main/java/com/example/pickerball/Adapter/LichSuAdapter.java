package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.LichSuModel;
import com.example.pickerball.R;

import java.util.List;

public class LichSuAdapter extends RecyclerView.Adapter<LichSuAdapter.VH> {

    private final Context context;
    private List<LichSuModel> list;

    public LichSuAdapter(Context context, List<LichSuModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<LichSuModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_lichsu_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        LichSuModel m = list.get(position);
        h.tvTitle.setText(m.hanhDong + " — " + m.doiTuong);
        h.tvTime.setText(m.thoiGian != null ? m.thoiGian : "");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvLsTitle);
            tvTime = itemView.findViewById(R.id.tvLsTime);
        }
    }
}
