package com.example.pickerball;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.ThemePickerAdapter;
import com.example.pickerball.util.UiWindowHelper;
import com.google.android.material.appbar.MaterialToolbar;

public class ThemeSettingsActivity extends AppCompatActivity {

    public static final String EXTRA_INITIAL_THEME = "initial_theme_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);
        UiWindowHelper.install(this);

        MaterialToolbar tb = findViewById(R.id.toolbarTheme);
        findViewById(R.id.rootThemeSettings).setBackgroundColor(ThemeHelper.resolveBackgroundColor(this));
        ThemeHelper.applyAppBarGradient(findViewById(R.id.appbarTheme));
        ThemeHelper.tintToolbarIconsWhite(tb);
        UiWindowHelper.applyAppBarInsets(tb);
        tb.setNavigationOnClickListener(v -> finishWithResult());
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RecyclerView rv = findViewById(R.id.rvThemes);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        String current = ThemeHelper.getThemeId(this);
        ThemePickerAdapter adapter = new ThemePickerAdapter(this, current, id -> {
            if (id.equals(ThemeHelper.getThemeId(this))) return;
            ThemeHelper.setThemeId(this, id);
            ThemePickerAdapter ad = (ThemePickerAdapter) rv.getAdapter();
            if (ad != null) ad.setSelectedId(id);
            setResult(RESULT_OK);
            ThemeHelper.refreshThemePickerScreen(this);
        });
        rv.setAdapter(adapter);
    }

    private void finishWithResult() {
        String before = getIntent().getStringExtra(EXTRA_INITIAL_THEME);
        if (before == null) before = ThemeHelper.THEME_DEFAULT;
        boolean changed = !ThemeHelper.getThemeId(this).equals(before);
        setResult(changed ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishWithResult();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    /** Gọi từ màn khác khi mở màn chọn theme. */
    public static Intent intentWithMarker(android.content.Context ctx) {
        return new Intent(ctx, ThemeSettingsActivity.class)
                .putExtra(EXTRA_INITIAL_THEME, ThemeHelper.getThemeId(ctx));
    }
}
