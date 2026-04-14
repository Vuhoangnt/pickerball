package com.example.pickerball.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Model.SanAnhModel;
import com.example.pickerball.R;
import com.example.pickerball.util.SanImageHelper;

import java.util.List;

public class SanImagePagerAdapter extends RecyclerView.Adapter<SanImagePagerAdapter.VH> {

    private final List<SanAnhModel> list;

    public SanImagePagerAdapter(List<SanAnhModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_san_image_page, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SanAnhModel m = list.get(position);
        SanImageHelper.loadInto(h.itemView.getContext(), h.iv, m.duongDan, 0);
        h.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView iv;

        VH(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.ivSanHero);
        }
    }
}
