package com.example.pickerball.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.DAO.SanAnhDAO;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.SanDialog;
import com.example.pickerball.util.SanImageHelper;

import java.util.List;
import java.util.Locale;

public class AdminSanAdapter extends RecyclerView.Adapter<AdminSanAdapter.VH> {

    private final Context context;
    private List<SanModel> list;
    private final Runnable onDataChanged;
    private final SanDialog.GalleryPickHost galleryHost;

    public AdminSanAdapter(Context context, List<SanModel> list,
                           SanDialog.GalleryPickHost galleryHost,
                           Runnable onDataChanged) {
        this.context = context;
        this.list = list;
        this.galleryHost = galleryHost;
        this.onDataChanged = onDataChanged;
    }

    public void setList(List<SanModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_san_admin, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SanModel s = list.get(position);
        SanAnhDAO anhDao = new SanAnhDAO(context);
        String path = anhDao.getFirstDuongDan(s.maSan);
        if (path == null || path.isEmpty()) {
            path = s.hinhAnh;
        }
        if (path == null || path.isEmpty()) {
            int p = (int) (12 * context.getResources().getDisplayMetrics().density);
            h.img.setPadding(p, p, p, p);
            h.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            h.img.setImageResource(R.drawable.ic_ball);
        } else {
            h.img.setPadding(0, 0, 0, 0);
            h.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            SanImageHelper.loadInto(context, h.img, path, 400);
        }

        h.tvTen.setText(s.tenSan);
        h.tvLoai.setText(s.loaiSan != null && !s.loaiSan.isEmpty() ? s.loaiSan : "Pickleball");

        String tt = s.trangThai != null ? s.trangThai : "";
        boolean trong = "TRONG".equalsIgnoreCase(tt);
        h.tvTrangThai.setText(trong ? "Trống" : ("DA_DAT".equalsIgnoreCase(tt) ? "Đã đặt" : tt));
        int bg = trong ? Color.parseColor("#DCFCE7") : Color.parseColor("#FEF3C7");
        int fg = trong ? Color.parseColor("#166534") : Color.parseColor("#B45309");
        float r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        GradientDrawable pill = new GradientDrawable();
        pill.setCornerRadius(r);
        pill.setColor(bg);
        h.tvTrangThai.setBackground(pill);
        h.tvTrangThai.setTextColor(fg);
        float d = context.getResources().getDisplayMetrics().density;
        int ph = (int) (10 * d);
        int pv = (int) (5 * d);
        h.tvTrangThai.setPadding(ph, pv, ph, pv);

        h.tvGia.setText(String.format(Locale.getDefault(), "%,.0f đ/giờ", s.giaMoiGio));

        h.itemView.setOnClickListener(v ->
                SanDialog.showDialog(context, s, () -> {
                    if (onDataChanged != null) onDataChanged.run();
                }, galleryHost));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView img;
        final TextView tvTen, tvLoai, tvTrangThai, tvGia;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgSanAdminThumb);
            tvTen = itemView.findViewById(R.id.tvSanAdminTen);
            tvLoai = itemView.findViewById(R.id.tvSanAdminLoai);
            tvTrangThai = itemView.findViewById(R.id.tvSanAdminTrangThai);
            tvGia = itemView.findViewById(R.id.tvSanAdminGia);
        }
    }
}
