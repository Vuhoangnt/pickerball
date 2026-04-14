package com.example.pickerball.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.HoaDonListItem;
import com.example.pickerball.R;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class HoaDonAdapter extends RecyclerView.Adapter<HoaDonAdapter.VH> {

    private final Context context;
    private List<HoaDonListItem> list;
    @Nullable
    private final Consumer<HoaDonListItem> onOpenDetail;
    @Nullable
    private final Consumer<HoaDonListItem> onPay;

    public HoaDonAdapter(
            Context context,
            List<HoaDonListItem> list,
            @Nullable Consumer<HoaDonListItem> onOpenDetail,
            @Nullable Consumer<HoaDonListItem> onPay) {
        this.context = context;
        this.list = list;
        this.onOpenDetail = onOpenDetail;
        this.onPay = onPay;
    }

    public void setList(List<HoaDonListItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_hoadon_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        HoaDonListItem r = list.get(position);
        boolean paid = r.trangThai == 1;
        String statusLabel = paid
                ? context.getString(R.string.staff_invoice_status_paid)
                : context.getString(R.string.staff_invoice_status_unpaid);

        h.tvTitle.setText(String.format(Locale.getDefault(), context.getString(R.string.staff_invoice_row_title_fmt), r.maHd));

        h.tvSan.setText(r.tenSan != null && !r.tenSan.isEmpty() ? r.tenSan : "—");
        h.tvAmount.setText(String.format(Locale.getDefault(), "%,.0f đ", r.tongTien));

        h.chipStatus.setText(statusLabel);
        int bg = paid ? R.color.theme_forest_container : R.color.brand_orange_soft;
        int fg = paid ? R.color.theme_forest_on_container : R.color.brand_orange_dark;
        h.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, bg)));
        h.chipStatus.setTextColor(ContextCompat.getColor(context, fg));

        String nvDuyet = r.tenNvDuyet != null && !r.tenNvDuyet.isEmpty() ? r.tenNvDuyet : "—";
        String nvThu;
        if (paid) {
            nvThu = r.tenNvThanhToan != null && !r.tenNvThanhToan.isEmpty() ? r.tenNvThanhToan : "—";
        } else {
            nvThu = context.getString(R.string.staff_invoice_not_collected);
        }
        h.tvMeta.setText(String.format(Locale.getDefault(),
                "%s\n%s",
                String.format(Locale.getDefault(), "Duyệt: %s · Thu: %s", nvDuyet, nvThu),
                context.getString(R.string.staff_invoice_tap_hint)));

        boolean showPay = !paid && onPay != null;
        h.btnPay.setVisibility(showPay ? View.VISIBLE : View.GONE);
        h.btnPay.setOnClickListener(x -> {
            if (onPay != null) onPay.accept(r);
        });

        boolean openable = onOpenDetail != null;
        h.itemView.setClickable(openable);
        h.itemView.setFocusable(openable);
        h.itemView.setOnClickListener(openable ? v -> onOpenDetail.accept(r) : null);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSan, tvAmount, tvMeta;
        Chip chipStatus;
        com.google.android.material.button.MaterialButton btnPay;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHdTitle);
            tvSan = itemView.findViewById(R.id.tvHdSan);
            tvAmount = itemView.findViewById(R.id.tvHdAmount);
            tvMeta = itemView.findViewById(R.id.tvHdMeta);
            chipStatus = itemView.findViewById(R.id.chipHdStatus);
            btnPay = itemView.findViewById(R.id.btnHdPay);
        }
    }
}
