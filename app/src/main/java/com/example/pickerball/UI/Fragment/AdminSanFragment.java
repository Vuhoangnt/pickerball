package com.example.pickerball.UI.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.AdminSanAdapter;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.SanDialog;
import com.example.pickerball.UI.Dialog.SanScheduleDialog;
import com.example.pickerball.util.DateUtils;
import com.example.pickerball.util.GridSpacingItemDecoration;
import com.example.pickerball.util.SanMediaStorage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminSanFragment extends Fragment implements SanDialog.GalleryPickHost {

    private RecyclerView rv;
    private SanDAO sanDAO;
    private DatSanDAO datSanDAO;
    private AdminSanAdapter adapter;
    private final List<SanModel> list = new ArrayList<>();
    private TextView tvDate;
    private TextView tvTong, tvTrong, tvCoLich;
    private TextView tvEmpty;

    private final Calendar cal = Calendar.getInstance();
    private String ngayXem;

    private final Map<Integer, AdminSanAdapter.SlotSummary> summaryCache = new HashMap<>();

    private ActivityResultLauncher<String> pickImageLauncher;
    private SanDialog.GalleryPickHost.OnDeviceImage pendingGallery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (pendingGallery == null) return;
                    SanDialog.GalleryPickHost.OnDeviceImage cb = pendingGallery;
                    pendingGallery = null;
                    if (uri == null) {
                        cb.onResult(null);
                        return;
                    }
                    try {
                        cb.onResult(SanMediaStorage.copyFromUri(requireContext(), uri));
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Không lưu được ảnh", Toast.LENGTH_SHORT).show();
                        cb.onResult(null);
                    }
                });
    }

    @Override
    public void pickFromDevice(SanDialog.GalleryPickHost.OnDeviceImage callback) {
        pendingGallery = callback;
        pickImageLauncher.launch("image/*");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_san, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        try {
            rv = v.findViewById(R.id.recyclerSan);
            tvDate = v.findViewById(R.id.tvSanAdminDate);
            tvTong = v.findViewById(R.id.tvSanAdminTong);
            tvTrong = v.findViewById(R.id.tvSanAdminTrong);
            tvCoLich = v.findViewById(R.id.tvSanAdminCoLich);
            tvEmpty = v.findViewById(R.id.tvSanEmpty);

            sanDAO = new SanDAO(requireContext());
            datSanDAO = new DatSanDAO(requireContext());

            ngayXem = formatDate(cal);
            tvDate.setText(ngayXem);

            GridLayoutManager glm = new GridLayoutManager(requireContext(), 2);
            rv.setLayoutManager(glm);
            int gap = (int) (6 * requireContext().getResources().getDisplayMetrics().density);
            if (rv.getItemDecorationCount() == 0) {
                rv.addItemDecoration(new GridSpacingItemDecoration(gap, false));
            }
            adapter = new AdminSanAdapter(requireContext(), list,
                    maSan -> summaryCache.get(maSan),
                    this,
                    new AdminSanAdapter.Listener() {
                        @Override
                        public void onSanClicked(SanModel san) {
                            SanScheduleDialog d = SanScheduleDialog.newInstance(san, ngayXem);
                            d.show(getChildFragmentManager(), "schedule");
                        }

                        @Override
                        public void onEditClicked(SanModel san) {
                            reload();
                        }
                    });
            rv.setAdapter(adapter);

            FloatingActionButton fab = v.findViewById(R.id.fabAdd);
            fab.setOnClickListener(view ->
                    SanDialog.showDialog(requireContext(), null, this::reload, this));

            MaterialButton btnPick = v.findViewById(R.id.btnSanAdminPickDate);
            btnPick.setOnClickListener(x -> showDatePicker());
            MaterialButton btnToday = v.findViewById(R.id.btnSanAdminToday);
            btnToday.setOnClickListener(x -> {
                cal.setTimeInMillis(System.currentTimeMillis());
                ngayXem = formatDate(cal);
                tvDate.setText(ngayXem);
                reload();
            });

            reload();
        } catch (Exception ex) {
            android.util.Log.e("AdminSan", "init error", ex);
            Toast.makeText(requireContext(), "Lỗi tải sân: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void showDatePicker() {
        DatePickerDialog dp = new DatePickerDialog(requireContext(),
                (view, y, m, d) -> {
                    cal.set(y, m, d);
                    ngayXem = formatDate(cal);
                    tvDate.setText(ngayXem);
                    reload();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void reload() {
        list.clear();
        list.addAll(sanDAO.getAll());
        summaryCache.clear();
        computeSummaries();
        adapter.setList(list);

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }

        int total = list.size();
        int booked = 0;
        for (AdminSanAdapter.SlotSummary s : summaryCache.values()) {
            if (s.isBooked()) booked++;
        }
        tvTong.setText(String.valueOf(total));
        tvTrong.setText(String.valueOf(total - booked));
        tvCoLich.setText(String.valueOf(booked));
    }

    private void computeSummaries() {
        for (SanModel san : list) {
            int totalSlots = computeTotalSlots(san);
            List<DatSanDAO.BookedRange> ranges = datSanDAO.listBookedRanges(san.maSan, ngayXem);
            int booked = 0;
            boolean hasAny = false;
            for (DatSanDAO.BookedRange r : ranges) {
                if (r.trangThai != null
                        && !r.trangThai.equalsIgnoreCase("HUY")
                        && !r.trangThai.equalsIgnoreCase("TU_CHOI")) {
                    booked++;
                    hasAny = true;
                }
            }
            summaryCache.put(san.maSan,
                    new AdminSanAdapter.SlotSummary(booked, totalSlots, hasAny));
        }
    }

    private int computeTotalSlots(SanModel san) {
        String mo = san.gioMoCua != null ? san.gioMoCua : "06:00";
        String cl = san.gioDongCua != null ? san.gioDongCua : "22:00";
        int openMin = DateUtils.toMinutes(mo);
        int closeMin = DateUtils.toMinutes(cl);
        if (openMin < 0 || closeMin <= openMin) return 0;
        int n = 0;
        for (int t = openMin; t + 60 <= closeMin; t += 60) n++;
        return n;
    }

    private String formatDate(Calendar c) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }
}
