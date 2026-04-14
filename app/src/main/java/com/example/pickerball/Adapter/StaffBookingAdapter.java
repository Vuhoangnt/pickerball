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
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class StaffBookingAdapter extends RecyclerView.Adapter<StaffBookingAdapter.VH> {

    public interface ActionListener {
        void onApprove(DatSanDAO.ChoDuyetRow row);

        void onReject(DatSanDAO.ChoDuyetRow row);
    }

    private final Context context;
    private List<DatSanDAO.ChoDuyetRow> list;
    private final ActionListener listener;

    public StaffBookingAdapter(Context context, List<DatSanDAO.ChoDuyetRow> list, ActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void setList(List<DatSanDAO.ChoDuyetRow> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_staff_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DatSanDAO.ChoDuyetRow r = list.get(position);
        h.title.setText(String.format(Locale.getDefault(), "#%d · %s", r.maDatSan, r.tenSan));
        String sdt = r.sdtKh != null && !r.sdtKh.isEmpty() ? r.sdtKh : "—";
        String email = r.emailKh != null && !r.emailKh.isEmpty() ? r.emailKh : "—";
        h.meta.setText(String.format(Locale.getDefault(),
                "%s · %s – %s\n%s · SĐT: %s\nEmail: %s · Điểm: %d\n%,.0f đ",
                r.ngayDat, r.gioBd, r.gioKt,
                r.tenKh, sdt, email, r.diemKh, r.tongDuKien));
        h.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(r);
        });
        h.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(r);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, meta;
        MaterialButton btnApprove, btnReject;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvStaffBkTitle);
            meta = itemView.findViewById(R.id.tvStaffBkMeta);
            btnApprove = itemView.findViewById(R.id.btnStaffApprove);
            btnReject = itemView.findViewById(R.id.btnStaffReject);
        }
    }
}
