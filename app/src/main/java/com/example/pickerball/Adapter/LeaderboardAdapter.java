package com.example.pickerball.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;
import com.example.pickerball.util.HoiVienHelper;

import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.VH> {

    private final List<KhachHangModel> list;

    public LeaderboardAdapter(List<KhachHangModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        KhachHangModel k = list.get(position);
        int rank = position + 1;
        if (rank == 1) h.rank.setText("🥇");
        else if (rank == 2) h.rank.setText("🥈");
        else if (rank == 3) h.rank.setText("🥉");
        else h.rank.setText(String.valueOf(rank));
        h.ten.setText(k.getHoTen());
        h.meta.setText(String.format(Locale.getDefault(), "%s · #%d",
                HoiVienHelper.displayTitleHang(k.getHangHoiVien()), k.getMaKh()));
        h.diem.setText(String.format(Locale.getDefault(), "%d", k.getDiem()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView rank, ten, meta, diem;

        VH(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.tvLbRank);
            ten = itemView.findViewById(R.id.tvLbTen);
            meta = itemView.findViewById(R.id.tvLbMeta);
            diem = itemView.findViewById(R.id.tvLbDiem);
        }
    }
}
