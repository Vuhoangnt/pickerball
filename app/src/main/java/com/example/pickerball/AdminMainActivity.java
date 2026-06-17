package com.example.pickerball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pickerball.UI.Fragment.AdminBookingsFragment;
import com.example.pickerball.UI.Fragment.AdminDashboardFragment;
import com.example.pickerball.UI.Fragment.AdminDichVuFragment;
import com.example.pickerball.UI.Fragment.AdminKhFragment;
import com.example.pickerball.UI.Fragment.AdminKhTierFragment;
import com.example.pickerball.UI.Fragment.AdminSanFragment;
import com.example.pickerball.UI.Fragment.AdminThongKeFragment;
import com.example.pickerball.util.UiWindowHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> themeLauncher;
    private BottomNavigationView nav;
    private boolean suppressMenuSelection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
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
            BottomNavigationView navLocal = findViewById(R.id.bottom_nav);
            nav = navLocal;
            ThemeHelper.styleBottomNav(navLocal, this);
            navLocal.setOnItemSelectedListener(this::onNav);
            if (savedInstanceState == null) {
                navLocal.setSelectedItemId(R.id.nav_a_home);
                openFragment(new AdminDashboardFragment(), "Quản lý sân");
            }
        } catch (Exception e) {
            android.util.Log.e("AdminMain", "onCreate crash", e);
            android.widget.Toast.makeText(this, "Lỗi khởi động admin: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean onNav(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_a_home) {
            openFragment(new AdminDashboardFragment(), getString(R.string.admin_nav_home));
            return true;
        }
        if (id == R.id.nav_a_bookings) {
            openFragment(new AdminBookingsFragment(), "Đặt sân");
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
        if (id == R.id.nav_a_stats) {
            showExtraMenu();
            return true;
        }
        return false;
    }

    private void showExtraMenu() {
        final String[] items = new String[]{
                "Dịch vụ",
                "Hạng khách hàng",
                "Thống kê & Báo cáo"
        };
        final Runnable[] actions = new Runnable[]{
                () -> openFragment(new AdminDichVuFragment(), "Dịch vụ"),
                () -> openFragment(new AdminKhTierFragment(), "Hạng khách hàng"),
                () -> openFragment(new AdminThongKeFragment(), "Thống kê & Báo cáo")
        };
        new MaterialAlertDialogBuilder(this)
                .setTitle("Menu")
                .setItems(items, (dialog, which) -> {
                    try {
                        actions[which].run();
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi mở màn: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setOnDismissListener(d -> {
                    if (suppressMenuSelection) {
                        suppressMenuSelection = false;
                        return;
                    }
                    if (nav != null && nav.getSelectedItemId() == R.id.nav_a_stats) {
                        suppressMenuSelection = true;
                        nav.setSelectedItemId(R.id.nav_a_home);
                    }
                })
                .show();
    }

    private void openFragment(Fragment f, String title) {
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
        } catch (Exception e) {
            android.util.Log.e("AdminMain", "openFragment crash", e);
            Toast.makeText(this, "Lỗi mở màn: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
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
