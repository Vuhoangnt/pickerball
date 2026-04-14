package com.example.pickerball.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.AppConstants;
import com.example.pickerball.ThemeHelper;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

public class KhachHangAdapter extends RecyclerView.Adapter<KhachHangAdapter.VH> {

    public interface OnOpen {
        void onOpen(KhachHangModel k);
    }

    public interface OnLong {
        void onLong(KhachHangModel k);
    }

    private final Context context;
    private List<KhachHangModel> list;
    private final OnOpen onOpen;
    private final OnLong onLong;
    private boolean rankMode;

    public KhachHangAdapter(Context context, List<KhachHangModel> list, OnOpen onOpen, OnLong onLong) {
        this.context = context;
        this.list = list;
        this.onOpen = onOpen;
        this.onLong = onLong;
    }

    public void setList(List<KhachHangModel> list, boolean rankMode) {
        this.list = list;
        this.rankMode = rankMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_kh_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        KhachHangModel k = list.get(position);
        h.tvTen.setText(k.getHoTen());
        h.tvSdt.setText(k.getSoDienThoai() != null ? k.getSoDienThoai() : "—");

        String initial = "?";
        String name = k.getHoTen();
        if (name != null && !name.trim().isEmpty()) {
            initial = name.trim().substring(0, 1).toUpperCase(Locale.getDefault());
        }
        h.tvAvatarInitial.setText(initial);

        float density = context.getResources().getDisplayMetrics().density;

        if (rankMode) {
            h.flMedal.setVisibility(View.VISIBLE);
            h.flAvatarKh.setVisibility(View.GONE);
            h.llDiemBadge.setVisibility(View.VISIBLE);

            h.tvDiemSo.setText(String.format(Locale.getDefault(), "%d", k.getDiem()));

            if (position == 0) {
                h.tvMedal.setText("🥇");
                h.tvMedal.setTextSize(30);
                h.tvMedal.setTextColor(0xFF1E293B);
                h.flMedal.setBackgroundResource(R.drawable.bg_medal_gold);
            } else if (position == 1) {
                h.tvMedal.setText("🥈");
                h.tvMedal.setTextSize(30);
                h.tvMedal.setTextColor(0xFF1E293B);
                h.flMedal.setBackgroundResource(R.drawable.bg_medal_silver);
            } else if (position == 2) {
                h.tvMedal.setText("🥉");
                h.tvMedal.setTextSize(30);
                h.tvMedal.setTextColor(0xFF1E293B);
                h.flMedal.setBackgroundResource(R.drawable.bg_medal_bronze);
            } else {
                h.tvMedal.setText(String.valueOf(position + 1));
                h.tvMedal.setTextSize(20);
                h.tvMedal.setTextColor(ThemeHelper.resolvePrimaryColor(context));
                h.flMedal.setBackgroundResource(R.drawable.bg_rank_number);
            }

            String mail = k.getEmail() != null ? k.getEmail() : "—";
            h.tvKhMeta.setText(String.format(Locale.getDefault(),
                    "#%d · %s · %s",
                    k.getMaKh(), labelHangShort(k.getHangHoiVien()), mail));

            int strokePx = (int) (2 * density);
            if (position < 3) {
                int c = position == 0 ? 0xFFF59E0B : (position == 1 ? 0xFF94A3B8 : 0xFFC2410C);
                h.cardKh.setStrokeWidth(strokePx);
                h.cardKh.setStrokeColor(c);
            } else {
                h.cardKh.setStrokeWidth((int) (1 * density));
                h.cardKh.setStrokeColor(context.getColor(R.color.outline_soft));
            }
        } else {
            h.flMedal.setVisibility(View.GONE);
            h.flAvatarKh.setVisibility(View.VISIBLE);
            h.llDiemBadge.setVisibility(View.GONE);
            h.cardKh.setStrokeWidth((int) (1 * density));
            h.cardKh.setStrokeColor(context.getColor(R.color.outline_soft));

            String mail = k.getEmail() != null ? k.getEmail() : "—";
            h.tvKhMeta.setText(String.format(Locale.getDefault(),
                    "#%d · Điểm: %d · %s · Email: %s",
                    k.getMaKh(), k.getDiem(), labelHang(k.getHangHoiVien()), mail));
        }

        h.itemView.setOnClickListener(v -> {
            if (onOpen != null) onOpen.onOpen(k);
        });
        if (onLong != null) {
            h.itemView.setOnLongClickListener(v -> {
                onLong.onLong(k);
                return true;
            });
        }
    }

    private static String labelHang(String h) {
        if (h == null || AppConstants.HANG_THUONG.equals(h)) return "Hạng: Thường";
        if (AppConstants.HANG_VANG.equals(h)) return "Hạng: Vàng";
        if (AppConstants.HANG_BAC.equals(h)) return "Hạng: Bạc";
        return "Hạng: " + h;
    }

    private static String labelHangShort(String h) {
        if (h == null || AppConstants.HANG_THUONG.equals(h)) return "Thường";
        if (AppConstants.HANG_VANG.equals(h)) return "Vàng";
        if (AppConstants.HANG_BAC.equals(h)) return "Bạc";
        return h != null ? h : "—";
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final MaterialCardView cardKh;
        final FrameLayout flMedal;
        final FrameLayout flAvatarKh;
        final TextView tvMedal;
        final TextView tvAvatarInitial;
        final TextView tvTen, tvSdt, tvKhMeta;
        final LinearLayout llDiemBadge;
        final TextView tvDiemSo;

        VH(@NonNull View itemView) {
            super(itemView);
            cardKh = itemView.findViewById(R.id.cardKh);
            flMedal = itemView.findViewById(R.id.flMedal);
            flAvatarKh = itemView.findViewById(R.id.flAvatarKh);
            tvMedal = itemView.findViewById(R.id.tvMedal);
            tvAvatarInitial = itemView.findViewById(R.id.tvAvatarInitial);
            tvTen = itemView.findViewById(R.id.tvKhTen);
            tvSdt = itemView.findViewById(R.id.tvKhSdt);
            tvKhMeta = itemView.findViewById(R.id.tvKhMeta);
            llDiemBadge = itemView.findViewById(R.id.llDiemBadge);
            tvDiemSo = itemView.findViewById(R.id.tvDiemSo);
        }
    }
}
