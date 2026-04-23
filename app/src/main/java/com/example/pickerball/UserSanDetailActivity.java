package com.example.pickerball;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pickerball.Adapter.GiaBangAdapter;
import com.example.pickerball.Adapter.SanImagePagerAdapter;
import com.example.pickerball.DAO.CauHinhGiaDAO;
import com.example.pickerball.DAO.SanAnhDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.SanAnhModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.util.GridSpacingItemDecoration;
import com.example.pickerball.util.UiWindowHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class UserSanDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MA_SAN = "ma_san";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_san_detail);
        UiWindowHelper.install(this);

        int maSan = getIntent().getIntExtra(EXTRA_MA_SAN, -1);
        if (maSan <= 0) {
            finish();
            return;
        }

        SanModel san = new SanDAO(this).getById(maSan);
        if (san == null) {
            finish();
            return;
        }

        MaterialToolbar tb = findViewById(R.id.toolbarSanDetail);
        UiWindowHelper.applyAppBarInsets(tb);
        tb.setTitle(san.tenSan);
        tb.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        TextView title = findViewById(R.id.tvSanDetailTitle);
        TextView sub = findViewById(R.id.tvSanDetailSub);
        title.setText(san.tenSan);
        sub.setText(String.format(Locale.getDefault(), "%s · %s",
                san.loaiSan != null ? san.loaiSan : "Pickleball",
                san.moTa != null ? san.moTa : ""));

        ((TextView) findViewById(R.id.tvSanHours)).setText(String.format(Locale.getDefault(),
                "Mở cửa %s – Đóng cửa %s",
                san.gioMoCua != null ? san.gioMoCua : "—",
                san.gioDongCua != null ? san.gioDongCua : "—"));
        ((TextView) findViewById(R.id.tvSanGiaGoc)).setText(String.format(Locale.getDefault(),
                "Giá theo giờ (mặc định): %,.0f đ/giờ", san.giaMoiGio));

        List<SanAnhModel> imgs = new SanAnhDAO(this).listByMaSan(maSan);
        if (imgs.isEmpty()) {
            SanAnhModel ph = new SanAnhModel();
            ph.duongDan = "drawable://ic_ball";
            imgs.add(ph);
        }
        ViewPager2 vp = findViewById(R.id.vpSanImages);
        SanImagePagerAdapter pAdapter = new SanImagePagerAdapter(imgs);
        vp.setAdapter(pAdapter);
        TextView dots = findViewById(R.id.tvSanPageDots);
        dots.setText("1 / " + imgs.size());
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                dots.setText((position + 1) + " / " + imgs.size());
            }
        });

        List<CauHinhGiaDAO.GiaTheoKhungRow> bangGia = new CauHinhGiaDAO(this).listChiTietTheoSan(maSan);
        RecyclerView rvGia = findViewById(R.id.rvBangGia);
        rvGia.setLayoutManager(new GridLayoutManager(this, 2));
        if (rvGia.getItemDecorationCount() == 0) {
            int gap = (int) (10 * getResources().getDisplayMetrics().density);
            rvGia.addItemDecoration(new GridSpacingItemDecoration(gap, true));
        }
        rvGia.setAdapter(new GiaBangAdapter(bangGia));

        MaterialButton btn = findViewById(R.id.btnBookThisSan);
        btn.setOnClickListener(v -> {
            getSharedPreferences(UserMainActivity.PREFS_NAV, Context.MODE_PRIVATE).edit()
                    .putInt(UserMainActivity.KEY_PRESELECT_SAN, maSan)
                    .apply();
            Intent i = new Intent(this, UserMainActivity.class);
            i.putExtra(UserMainActivity.EXTRA_OPEN_TAB_BOOK, true);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });
    }

    public static void start(Context ctx, int maSan) {
        ctx.startActivity(new Intent(ctx, UserSanDetailActivity.class).putExtra(EXTRA_MA_SAN, maSan));
    }
}
