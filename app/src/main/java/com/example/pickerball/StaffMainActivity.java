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

import com.example.pickerball.UI.Fragment.StaffBookingsFragment;
import com.example.pickerball.UI.Fragment.StaffHomeFragment;
import com.example.pickerball.UI.Fragment.StaffInvoiceFragment;
import com.example.pickerball.UI.Fragment.StaffNotificationsFragment;
import com.example.pickerball.util.UiWindowHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StaffMainActivity extends AppCompatActivity {

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
        SessionManager s = new SessionManager(this);
        if (!s.isLoggedIn() || !AppConstants.ROLE_NHAN_VIEN.equals(s.getVaiTro())) {
            finish();
            return;
        }
        setContentView(R.layout.activity_staff_main);
        UiWindowHelper.install(this);
        UiWindowHelper.applyAppBarInsets(findViewById(R.id.appbar));
        ThemeHelper.applyStaffAppBarGradient(findViewById(R.id.appbar));
        MaterialToolbar tb = findViewById(R.id.toolbar);
        ThemeHelper.tintToolbarIconsWhite(tb);
        setSupportActionBar(tb);
        tb.setNavigationIcon(null);
        String name = s.getHoTen();
        if (name != null && !name.isEmpty()) tb.setSubtitle(name);
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        ThemeHelper.styleStaffBottomNav(nav, this);
        nav.setOnItemSelectedListener(this::onNav);
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_s_home);
            openFragment(new StaffHomeFragment(), "Trang chủ");
        }
    }

    private boolean onNav(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_s_home) {
            openFragment(new StaffHomeFragment(), "Trang chủ");
            return true;
        }
        if (id == R.id.nav_s_pending) {
            openFragment(new StaffBookingsFragment(), "Chờ duyệt");
            return true;
        }
        if (id == R.id.nav_s_hd) {
            openFragment(new StaffInvoiceFragment(), "Hóa đơn");
            return true;
        }
        if (id == R.id.nav_s_notify) {
            openFragment(new StaffNotificationsFragment(), "Thông báo");
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
        getMenuInflater().inflate(R.menu.menu_staff_appbar, menu);
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
