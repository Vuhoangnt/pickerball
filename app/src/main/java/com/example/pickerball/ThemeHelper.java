package com.example.pickerball;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class ThemeHelper {

    private static final String PREF = "pickerball_prefs";
    private static final String KEY_THEME = "theme_id";

    public static final String THEME_DEFAULT = "default";
    public static final String THEME_OCEAN = "ocean";
    public static final String THEME_FOREST = "forest";
    public static final String THEME_SUNSET = "sunset";
    public static final String THEME_VIOLET = "violet";
    public static final String THEME_ROSE = "rose";
    public static final String THEME_SLATE = "slate";
    public static final String THEME_AMBER = "amber";

    /** Thứ tự hiển thị trong màn chọn giao diện. */
    public static final String[] THEME_IDS_ORDERED = {
            THEME_DEFAULT, THEME_OCEAN, THEME_FOREST, THEME_SUNSET,
            THEME_VIOLET, THEME_ROSE, THEME_SLATE, THEME_AMBER
    };

    private ThemeHelper() {}

    @NonNull
    public static String getThemeId(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(KEY_THEME, THEME_DEFAULT);
    }

    public static void setThemeId(Context ctx, String id) {
        if (id == null) id = THEME_DEFAULT;
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY_THEME, id).apply();
    }

    public static void applyTheme(Activity activity) {
        activity.setTheme(themeResId(activity));
    }

    /** Style theo id chủ đề (không đọc SharedPreferences). */
    @StyleRes
    public static int themeResIdForThemeId(@Nullable String themeId) {
        if (themeId == null) themeId = THEME_DEFAULT;
        switch (themeId) {
            case THEME_OCEAN:
                return R.style.Theme_PickerBall_Ocean;
            case THEME_FOREST:
                return R.style.Theme_PickerBall_Forest;
            case THEME_SUNSET:
                return R.style.Theme_PickerBall_Sunset;
            case THEME_VIOLET:
                return R.style.Theme_PickerBall_Violet;
            case THEME_ROSE:
                return R.style.Theme_PickerBall_Rose;
            case THEME_SLATE:
                return R.style.Theme_PickerBall_Slate;
            case THEME_AMBER:
                return R.style.Theme_PickerBall_Amber;
            default:
                return R.style.Theme_PickerBall;
        }
    }

    @StyleRes
    public static int themeResId(Context ctx) {
        return themeResIdForThemeId(getThemeId(ctx));
    }

    /** Màu primary theo chủ đề đã lưu (không phụ thuộc theme Activity cũ). */
    public static int resolvePrimaryColor(Context ctx) {
        Context app = ctx.getApplicationContext();
        Context w = new ContextThemeWrapper(app, themeResId(ctx));
        TypedValue tv = new TypedValue();
        if (w.getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, tv, true)) {
            return tv.data;
        }
        return ContextCompat.getColor(ctx, R.color.brand_blue);
    }

    public static int resolveBackgroundColor(Context ctx) {
        Context w = new ContextThemeWrapper(ctx.getApplicationContext(), themeResId(ctx));
        TypedValue tv = new TypedValue();
        if (w.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true)) {
            return tv.data;
        }
        return ContextCompat.getColor(ctx, R.color.surface);
    }

    /** Làm mới thanh app, toolbar, bottom nav theo chủ đề đã lưu — không cần recreate Activity. */
    public static void refreshChrome(AppCompatActivity activity) {
        if (activity == null) return;
        View appbar = activity.findViewById(R.id.appbar);
        if (appbar != null) {
            if (activity instanceof StaffMainActivity) {
                applyStaffAppBarGradient(appbar);
            } else {
                applyAppBarGradient(appbar);
            }
        }
        MaterialToolbar tb = activity.findViewById(R.id.toolbar);
        if (tb != null) {
            if (appbar == null) {
                applyAppBarGradient(tb);
            }
            tintToolbarIconsWhite(tb);
        }
        BottomNavigationView nav = activity.findViewById(R.id.bottom_nav);
        if (nav != null) {
            if (activity instanceof StaffMainActivity) {
                styleStaffBottomNav(nav, activity);
            } else {
                styleBottomNav(nav, activity);
            }
        }
    }

    /** Màn chọn giao diện: áp dụng nền + appbar ngay khi chọn. */
    public static void refreshThemePickerScreen(AppCompatActivity activity) {
        if (activity == null) return;
        View root = activity.findViewById(R.id.rootThemeSettings);
        if (root != null) {
            root.setBackgroundColor(resolveBackgroundColor(activity));
        }
        View appbar = activity.findViewById(R.id.appbarTheme);
        applyAppBarGradient(appbar);
        MaterialToolbar tb = activity.findViewById(R.id.toolbarTheme);
        tintToolbarIconsWhite(tb);
    }

    /** Màu mẫu (ô preview) theo id chủ đề. */
    public static int previewColorRes(String themeId) {
        if (themeId == null) return R.color.brand_blue;
        switch (themeId) {
            case THEME_OCEAN:
                return R.color.theme_ocean_primary;
            case THEME_FOREST:
                return R.color.theme_forest_primary;
            case THEME_SUNSET:
                return R.color.theme_sunset_primary;
            case THEME_VIOLET:
                return R.color.theme_violet_primary;
            case THEME_ROSE:
                return R.color.theme_rose_primary;
            case THEME_SLATE:
                return R.color.theme_slate_primary;
            case THEME_AMBER:
                return R.color.theme_amber_primary;
            default:
                return R.color.brand_blue;
        }
    }

    /** Gradient thanh AppBar / hero theo màu chủ đề hiện tại. */
    public static void applyAppBarGradient(View appBarOrHero) {
        if (appBarOrHero == null) return;
        Context ctx = appBarOrHero.getContext();
        int primary = resolvePrimaryColor(ctx);
        int top = darken(primary, 0.78f);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{top, primary});
        appBarOrHero.setBackground(gd);
    }

    /**
     * App bar lễ tân: gradient + bo góc dưới, hơi nổi — gọi từ {@link StaffMainActivity}.
     */
    public static void applyStaffAppBarGradient(View appBar) {
        if (appBar == null) return;
        Context ctx = appBar.getContext();
        float density = ctx.getResources().getDisplayMetrics().density;
        float r = 22f * density;
        int primary = resolvePrimaryColor(ctx);
        int top = darken(primary, 0.72f);
        int bottom = darken(primary, 0.95f);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{top, bottom});
        gd.setCornerRadii(new float[]{0f, 0f, 0f, 0f, r, r, r, r});
        appBar.setBackground(gd);
        appBar.setElevation(8f * density);
    }

    /** Bottom nav lễ tân: nền bo góc + màu chọn theo chủ đề. */
    public static void styleStaffBottomNav(BottomNavigationView nav, Context ctx) {
        if (nav == null) return;
        nav.setBackgroundResource(R.drawable.bg_staff_bottom_nav);
        styleBottomNav(nav, ctx);
    }

    public static void tintToolbarIconsWhite(MaterialToolbar tb) {
        if (tb == null) return;
        tb.setTitleTextColor(Color.WHITE);
        tb.setSubtitleTextColor(0xCCFFFFFF);
        tb.setNavigationIconTint(Color.WHITE);
        if (tb.getOverflowIcon() != null) {
            tb.getOverflowIcon().setTint(Color.WHITE);
        }
    }

    private static int darken(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.min(255, Math.round(Color.red(color) * factor));
        int g = Math.min(255, Math.round(Color.green(color) * factor));
        int b = Math.min(255, Math.round(Color.blue(color) * factor));
        return Color.argb(a, r, g, b);
    }

    public static void styleBottomNav(BottomNavigationView nav, Context ctx) {
        int primary = resolvePrimaryColor(ctx);
        int muted = ContextCompat.getColor(ctx, R.color.on_surface_muted);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{}
        };
        int[] colors = new int[]{primary, muted};
        ColorStateList csl = new ColorStateList(states, colors);
        nav.setItemIconTintList(csl);
        nav.setItemTextColor(csl);
    }

    public static void logoutAndGoLogin(AppCompatActivity activity) {
        new SessionManager(activity).logout();
        android.content.Intent i = new android.content.Intent(activity, LoginActivity.class);
        i.putExtra(LoginActivity.EXTRA_SHOW_LOGIN, true);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
        activity.finish();
    }
}
