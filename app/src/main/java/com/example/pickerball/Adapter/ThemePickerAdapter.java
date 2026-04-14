package com.example.pickerball.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.R;
import com.example.pickerball.ThemeHelper;
import com.google.android.material.card.MaterialCardView;

public class ThemePickerAdapter extends RecyclerView.Adapter<ThemePickerAdapter.VH> {

    public interface OnPickTheme {
        void onPick(String themeId);
    }

    private final Context context;
    private final OnPickTheme listener;
    private String selectedId;

    public ThemePickerAdapter(Context context, String selectedId, OnPickTheme listener) {
        this.context = context;
        this.selectedId = selectedId != null ? selectedId : ThemeHelper.THEME_DEFAULT;
        this.listener = listener;
    }

    public void setSelectedId(String id) {
        this.selectedId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_theme_pick, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        String id = ThemeHelper.THEME_IDS_ORDERED[position];
        int color = ContextCompat.getColor(context, ThemeHelper.previewColorRes(id));
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(color);
        gd.setStroke((int) (2 * context.getResources().getDisplayMetrics().density), Color.argb(80, 0, 0, 0));
        h.swatch.setBackground(gd);

        h.name.setText(themeName(id));
        boolean sel = id.equals(selectedId);
        h.check.setVisibility(sel ? View.VISIBLE : View.GONE);
        h.hint.setVisibility(sel ? View.VISIBLE : View.GONE);
        h.hint.setText(sel ? "Đang dùng" : "");
        float dp = context.getResources().getDisplayMetrics().density;
        MaterialCardView card = (MaterialCardView) h.itemView;
        card.setStrokeWidth((int) ((sel ? 2.5f : 1f) * dp));
        card.setStrokeColor(sel ? color : ContextCompat.getColor(context, R.color.outline_soft));

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPick(id);
        });
    }

    private String themeName(String id) {
        if (ThemeHelper.THEME_DEFAULT.equals(id)) return context.getString(R.string.theme_name_default);
        if (ThemeHelper.THEME_OCEAN.equals(id)) return context.getString(R.string.theme_name_ocean);
        if (ThemeHelper.THEME_FOREST.equals(id)) return context.getString(R.string.theme_name_forest);
        if (ThemeHelper.THEME_SUNSET.equals(id)) return context.getString(R.string.theme_name_sunset);
        if (ThemeHelper.THEME_VIOLET.equals(id)) return context.getString(R.string.theme_name_violet);
        if (ThemeHelper.THEME_ROSE.equals(id)) return context.getString(R.string.theme_name_rose);
        if (ThemeHelper.THEME_SLATE.equals(id)) return context.getString(R.string.theme_name_slate);
        if (ThemeHelper.THEME_AMBER.equals(id)) return context.getString(R.string.theme_name_amber);
        return id;
    }

    @Override
    public int getItemCount() {
        return ThemeHelper.THEME_IDS_ORDERED.length;
    }

    static class VH extends RecyclerView.ViewHolder {
        final View swatch;
        final TextView name;
        final TextView hint;
        final TextView check;

        VH(@NonNull View itemView) {
            super(itemView);
            swatch = itemView.findViewById(R.id.viewThemeSwatch);
            name = itemView.findViewById(R.id.tvThemeName);
            hint = itemView.findViewById(R.id.tvThemeHint);
            check = itemView.findViewById(R.id.tvThemeCheck);
        }
    }
}
