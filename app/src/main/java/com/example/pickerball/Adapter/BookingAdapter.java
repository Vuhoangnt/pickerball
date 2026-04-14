package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.DatSanModel;
import com.example.pickerball.R;

import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    private final Context context;
    private final List<DatSanModel> list;

    public BookingAdapter(Context context, List<DatSanModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DatSanModel d = list.get(position);
        String tenSan = new SanDAO(context).getTenSan(d.maSan);
        if (tenSan == null) tenSan = "Sân #" + d.maSan;
        h.tvTitle.setText(tenSan + " · " + d.ngayDat);
        h.tvMeta.setText(String.format(Locale.getDefault(),
                "%s — %s · %s · Dự kiến %,.0f đ",
                d.thoiGianBatDau, d.thoiGianKetThuc, d.trangThai, d.tongDuKien));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvMeta = itemView.findViewById(R.id.tvBookMeta);
        }
    }
}
