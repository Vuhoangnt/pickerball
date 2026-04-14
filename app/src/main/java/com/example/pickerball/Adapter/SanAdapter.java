package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.SanDialog;
import com.example.pickerball.util.SanImageHelper;

import java.util.List;

public class SanAdapter extends RecyclerView.Adapter<SanAdapter.ViewHolder> {

    public interface OnSanClick {
        void onSan(SanModel s);
    }

    private final Context context;
    private final List<SanModel> list;
    private final OnSanClick customerClick;

    public SanAdapter(Context context, List<SanModel> list) {
        this(context, list, null);
    }

    public SanAdapter(Context context, List<SanModel> list, OnSanClick customerClick) {
        this.context = context;
        this.list = list;
        this.customerClick = customerClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_san, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSanThumb;
        TextView tvTen, tvLoai, tvTrangThai;

        public ViewHolder(View v) {
            super(v);
            imgSanThumb = v.findViewById(R.id.imgSanThumb);
            tvTen = v.findViewById(R.id.tvTenSan);
            tvLoai = v.findViewById(R.id.tvLoai);
            tvTrangThai = v.findViewById(R.id.tvTrangThai);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        SanModel s = list.get(i);
        h.tvTen.setText(s.tenSan);
        h.tvLoai.setText(s.loaiSan != null && !s.loaiSan.isEmpty() ? s.loaiSan : "Pickleball");
        h.tvTrangThai.setText(s.trangThai != null ? s.trangThai : "—");
        String thumb = s.hinhAnh;
        if (thumb == null || thumb.isEmpty()) {
            int p = (int) (10 * context.getResources().getDisplayMetrics().density);
            h.imgSanThumb.setPadding(p, p, p, p);
            h.imgSanThumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            h.imgSanThumb.setImageResource(R.drawable.ic_ball);
        } else {
            h.imgSanThumb.setPadding(0, 0, 0, 0);
            h.imgSanThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            SanImageHelper.loadInto(context, h.imgSanThumb, thumb, 480);
        }

        h.itemView.setOnClickListener(v -> {
            if (customerClick != null) {
                customerClick.onSan(s);
            } else {
                SanDialog.showDialog(context, s, () -> {
                    SanDAO dao = new SanDAO(context);
                    list.clear();
                    list.addAll(dao.getAll());
                    notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
