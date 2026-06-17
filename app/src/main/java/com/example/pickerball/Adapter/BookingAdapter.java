package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.AppConstants;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.Model.DatSanModel;
import com.example.pickerball.R;
import com.example.pickerball.util.TrangThaiLabels;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    public interface OnCancelListener {
        void onCancel(DatSanModel booking);
    }

    private final Context context;
    private final List<DatSanModel> list;
    private OnCancelListener cancelListener;

    public BookingAdapter(Context context, List<DatSanModel> list) {
        this(context, list, null);
    }

    public BookingAdapter(Context context, List<DatSanModel> list, OnCancelListener cancelListener) {
        this.context = context;
        this.list = list;
        this.cancelListener = cancelListener;
    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.cancelListener = listener;
    }

    @SuppressWarnings("unchecked")
    public void setList(List<DatSanModel> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
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
        String tenSan = com.example.pickerball.DAO.SanDAO.getTenSanStatic(context, d.maSan);
        if (tenSan == null) tenSan = "Sân #" + d.maSan;

        String trangThaiText = TrangThaiLabels.vn(d.trangThai);
        h.tvTitle.setText(String.format(Locale.getDefault(), "%s · %s", tenSan, d.ngayDat));
        h.tvMeta.setText(String.format(Locale.getDefault(),
                "%s — %s · %s · Dự kiến %,.0f đ",
                d.thoiGianBatDau, d.thoiGianKetThuc, trangThaiText, d.tongDuKien));

        boolean canCancel = AppConstants.DS_CHO_DUYET.equalsIgnoreCase(d.trangThai);
        h.btnCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);

        if (canCancel && cancelListener != null) {
            h.btnCancel.setOnClickListener(v -> cancelListener.onCancel(d));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta;
        MaterialButton btnCancel;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvMeta = itemView.findViewById(R.id.tvBookMeta);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
