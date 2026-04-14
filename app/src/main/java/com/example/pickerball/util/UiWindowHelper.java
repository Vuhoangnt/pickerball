package com.example.pickerball.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public final class UiWindowHelper {

    private UiWindowHelper() {}

    /**
     * Đẩy AppBar xuống dưới status bar / tai thỏ khi dùng edge-to-edge (decorFitsSystemWindows = false).
     */
    public static void applyAppBarInsets(View appBar) {
        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        ViewCompat.requestApplyInsets(appBar);
    }

    public static void install(Activity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController c = activity.getWindow().getInsetsController();
            if (c != null) {
                c.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        }
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
