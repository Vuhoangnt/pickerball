package com.example.pickerball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.pickerball.UI.Fragment.UserBookFragment;
import com.example.pickerball.UI.Fragment.UserBookingsFragment;
import com.example.pickerball.UI.Fragment.UserHomeFragment;
import com.example.pickerball.UI.Fragment.UserNotificationsFragment;
import com.example.pickerball.UI.Fragment.UserProfileFragment;
import com.example.pickerball.util.UiWindowHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class UserMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAV = "pickerball_nav";
    public static final String KEY_PRESELECT_SAN = "preselect_ma_san";
    public static final String EXTRA_OPEN_TAB_BOOK = "open_tab_book";

    private SessionManager session;
    private DrawerLayout drawerLayout;
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
        session = new SessionManager(this);
        boolean okGuest = session.isGuest();
        boolean okKhach = session.isLoggedIn() && AppConstants.ROLE_KHACH.equals(session.getVaiTro());
        if (!okGuest && !okKhach) {
            finish();
            return;
        }
        setContentView(R.layout.activity_user_main);
        UiWindowHelper.install(this);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        UiWindowHelper.applyAppBarInsets(tb);
        setSupportActionBar(tb);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, tb, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = findViewById(R.id.nav_drawer);
        nav.setNavigationItemSelectedListener(this);
        refreshHeader();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        ThemeHelper.styleBottomNav(bottomNav, this);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_u_home) {
                openFragment(new UserHomeFragment());
                setTitle("Trang chủ");
            } else if (id == R.id.nav_u_book) {
                openFragment(new UserBookFragment());
                setTitle("Đặt sân");
            } else if (id == R.id.nav_u_bookings) {
                openFragment(new UserBookingsFragment());
                setTitle("Lịch của tôi");
            } else if (id == R.id.nav_u_notify) {
                openFragment(new UserNotificationsFragment());
                setTitle("Thông báo");
            } else if (id == R.id.nav_u_profile) {
                openFragment(new UserProfileFragment());
                setTitle("Hồ sơ");
            } else {
                return false;
            }
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_u_home);
        }
        applyNavigationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        applyNavigationIntent(intent);
    }

    private void applyNavigationIntent(Intent intent) {
        if (intent == null) return;
        if (intent.getBooleanExtra(EXTRA_OPEN_TAB_BOOK, false)) {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            bottomNav.setSelectedItemId(R.id.nav_u_book);
        }
    }

    private void openFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.drawer_leaderboard) {
            openLeaderboard();
        } else if (id == R.id.drawer_theme) {
            themeLauncher.launch(ThemeSettingsActivity.intentWithMarker(this));
        } else if (id == R.id.drawer_logout) {
            ThemeHelper.logoutAndGoLogin(this);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public SessionManager getSession() {
        return session;
    }

    public void refreshHeader() {
        NavigationView nav = findViewById(R.id.nav_drawer);
        TextView tvName = nav.getHeaderView(0).findViewById(R.id.navHeaderName);
        TextView tvSub = nav.getHeaderView(0).findViewById(R.id.navHeaderSub);
        tvName.setText(session.getHoTen());
        tvSub.setText(session.isGuest() ? "Chế độ khách" : session.getUsername());
    }

    public void navigateToBookTab() {
        BottomNavigationView b = findViewById(R.id.bottom_nav);
        if (b != null) b.setSelectedItemId(R.id.nav_u_book);
    }

    public void openLeaderboard() {
        openFragment(new com.example.pickerball.UI.Fragment.UserLeaderboardFragment());
        setTitle("Bảng xếp hạng");
    }

    public void updateBadge() {}

    public void openThemeSettings() {
        themeLauncher.launch(ThemeSettingsActivity.intentWithMarker(this));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
