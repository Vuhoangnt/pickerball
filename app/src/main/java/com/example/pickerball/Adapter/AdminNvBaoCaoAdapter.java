package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.DAO.ThongKeDAO;
import com.example.pickerball.R;

import java.util.ArrayList;
import java.util.List;
public class AdminNvBaoCaoAdapter extends RecyclerView.Adapter<AdminNvBaoCaoAdapter.VH> {

    private final Context context;
    private List<ThongKeDAO.BaoCaoNhanVien> list;

    public AdminNvBaoCaoAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setList(List<ThongKeDAO.BaoCaoNhanVien> list) {
        this.list = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_nv_bao_cao, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ThongKeDAO.BaoCaoNhanVien r = list.get(position);
        h.name.setText(r.hoTen);
        h.meta.setText(context.getString(
                R.string.admin_nv_bao_cao_meta_fmt,
                r.soDonDuyetTrongKy,
                r.soLanTuChoiTrongKy,
                r.soLanThuTienTrongKy,
                r.doanhThuDaThuTrongKy));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView meta;

        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvNvBaoCaoName);
            meta = itemView.findViewById(R.id.tvNvBaoCaoMeta);
        }
    }
}
