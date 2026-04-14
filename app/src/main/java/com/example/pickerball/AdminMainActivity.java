package com.example.pickerball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pickerball.UI.Fragment.AdminDashboardFragment;
import com.example.pickerball.UI.Fragment.AdminDichVuFragment;
import com.example.pickerball.UI.Fragment.AdminKhFragment;
import com.example.pickerball.UI.Fragment.AdminKhTierFragment;
import com.example.pickerball.UI.Fragment.AdminSanFragment;
import com.example.pickerball.UI.Fragment.AdminThongKeFragment;
import com.example.pickerball.util.UiWindowHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> themeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        themeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        ThemeHelper.refreshChrome(this);
                    }
                });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        UiWindowHelper.install(this);
        UiWindowHelper.applyAppBarInsets(findViewById(R.id.appbar));
        ThemeHelper.applyAppBarGradient(findViewById(R.id.appbar));
        MaterialToolbar tb = findViewById(R.id.toolbar);
        ThemeHelper.tintToolbarIconsWhite(tb);
        setSupportActionBar(tb);
        tb.setNavigationIcon(null);
        SessionManager session = new SessionManager(this);
        String name = session.getHoTen();
        if (name != null && !name.isEmpty()) tb.setSubtitle(name);
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        ThemeHelper.styleBottomNav(nav, this);
        nav.setOnItemSelectedListener(this::onNav);
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_a_home);
            openFragment(new AdminDashboardFragment(), "Quản lý sân");
        }
    }

    private boolean onNav(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_a_home) {
            openFragment(new AdminDashboardFragment(), getString(R.string.admin_nav_home));
            return true;
        }
        if (id == R.id.nav_a_san) {
            openFragment(new AdminSanFragment(), "Quản lý sân");
            return true;
        }
        if (id == R.id.nav_a_kh) {
            openFragment(new AdminKhFragment(), "Khách hàng");
            return true;
        }
        if (id == R.id.nav_a_tier) {
            openFragment(new AdminKhTierFragment(), "Thứ hạng");
            return true;
        }
        if (id == R.id.nav_a_dv) {
            openFragment(new AdminDichVuFragment(), "Dịch vụ");
            return true;
        }
        if (id == R.id.nav_a_stats) {
            openFragment(new AdminThongKeFragment(), "Thống kê");
            return true;
        }
        return false;
    }

    private void openFragment(Fragment f, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_theme) {
            themeLauncher.launch(ThemeSettingsActivity.intentWithMarker(this));
            return true;
        }
        if (id == R.id.action_logout) {
            ThemeHelper.logoutAndGoLogin(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
