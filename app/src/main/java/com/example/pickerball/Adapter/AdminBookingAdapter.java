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
import com.example.pickerball.R;
import com.example.pickerball.util.TrangThaiLabels;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.VH> {

    public interface ActionListener {
        void onApprove(DatSanDAO.AdminBookingRow row);
        void onReject(DatSanDAO.AdminBookingRow row);
    }

    private final Context context;
    private List<DatSanDAO.AdminBookingRow> list;
    private final ActionListener listener;

    public AdminBookingAdapter(Context context, List<DatSanDAO.AdminBookingRow> list, ActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void setList(List<DatSanDAO.AdminBookingRow> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DatSanDAO.AdminBookingRow r = list.get(position);
        h.title.setText(String.format(Locale.getDefault(), "#%d · %s", r.maDatSan, r.tenSan));
        String sdt = r.sdtKh != null && !r.sdtKh.isEmpty() ? r.sdtKh : "—";
        String email = r.emailKh != null && !r.emailKh.isEmpty() ? r.emailKh : "—";
        String hinhThuc = r.hinhThuc != null && !r.hinhThuc.isEmpty() ? r.hinhThuc : "—";
        String ghiChu = r.ghiChu != null && !r.ghiChu.isEmpty() ? r.ghiChu : "";
        String trangThaiText = TrangThaiLabels.vn(r.trangThai);

        h.meta.setText(String.format(Locale.getDefault(),
                "%s · %s – %s\n%s · SĐT: %s\nEmail: %s\nHình thức: %s\nTổng dự kiến: %,.0f đ\nTrạng thái: %s%s",
                r.ngayDat, r.gioBd, r.gioKt,
                r.tenKh, sdt, email,
                hinhThuc, r.tongDuKien,
                trangThaiText, ghiChu.isEmpty() ? "" : "\nGhi chú: " + ghiChu));

        boolean isPending = AppConstants.DS_CHO_DUYET.equalsIgnoreCase(r.trangThai);
        h.btnApprove.setVisibility(isPending ? View.VISIBLE : View.GONE);
        h.btnReject.setVisibility(isPending ? View.VISIBLE : View.GONE);

        if (isPending) {
            h.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(r);
            });
            h.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(r);
            });
        }
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
            title = itemView.findViewById(R.id.tvAdminBkTitle);
            meta = itemView.findViewById(R.id.tvAdminBkMeta);
            btnApprove = itemView.findViewById(R.id.btnAdminApprove);
            btnReject = itemView.findViewById(R.id.btnAdminReject);
        }
    }
}
