package com.example.pickerball.UI.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.AdminSlotAdapter;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.util.DateUtils;
import com.example.pickerball.util.GridSpacingItemDecoration;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminLichSanFragment extends Fragment {

    private Spinner spinnerSan;
    private TextView tvDate;
    private TextView tvOpenHours;
    private TextView tvSummary;
    private TextView tvEmpty;
    private MaterialButton btnPickDate;
    private MaterialButton btnRefresh;
    private RecyclerView rvSlots;

    private SanDAO sanDAO;
    private DatSanDAO datSanDAO;
    private AdminSlotAdapter adapter;

    private final List<SanModel> sanList = new ArrayList<>();
    private final List<KhungGioModel> slotBlocks = new ArrayList<>();
    private Calendar cal = Calendar.getInstance();
    private String ngayDat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_lich_san, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        try {
            spinnerSan = v.findViewById(R.id.spinnerSanLich);
            tvDate = v.findViewById(R.id.tvLichDate);
            tvOpenHours = v.findViewById(R.id.tvLichOpenHours);
            tvSummary = v.findViewById(R.id.tvLichSummary);
            tvEmpty = v.findViewById(R.id.tvLichEmpty);
            btnPickDate = v.findViewById(R.id.btnLichPickDate);
            btnRefresh = v.findViewById(R.id.btnLichRefresh);
            rvSlots = v.findViewById(R.id.rvAdminSlot);

            sanDAO = new SanDAO(requireContext());
            datSanDAO = new DatSanDAO(requireContext());

            ngayDat = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            tvDate.setText(ngayDat);

            sanList.clear();
            sanList.addAll(sanDAO.getAll());

            if (sanList.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Chưa có sân nào. Vui lòng tạo sân trước.");
                rvSlots.setVisibility(View.GONE);
                tvSummary.setText("");
                return;
            }

            List<String> labels = new ArrayList<>();
            for (SanModel s : sanList) {
                labels.add(s.tenSan != null ? s.tenSan : "Sân " + s.maSan);
            }
            spinnerSan.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, labels));
            spinnerSan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadSchedule();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            btnPickDate.setOnClickListener(x -> {
                DatePickerDialog dp = new DatePickerDialog(requireContext(),
                        (view, y, m, d) -> {
                            cal.set(y, m, d);
                            ngayDat = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                            tvDate.setText(ngayDat);
                            loadSchedule();
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            });
            btnRefresh.setOnClickListener(x -> loadSchedule());

            adapter = new AdminSlotAdapter(this::onSlotClicked);
            rvSlots.setLayoutManager(new GridLayoutManager(requireContext(), 3));
            if (rvSlots.getItemDecorationCount() == 0) {
                int gap = (int) (8 * requireContext().getResources().getDisplayMetrics().density);
                rvSlots.addItemDecoration(new GridSpacingItemDecoration(gap, false));
            }
            rvSlots.setAdapter(adapter);

            loadSchedule();
        } catch (Exception ex) {
            android.util.Log.e("AdminLichSan", "init error", ex);
            Toast.makeText(requireContext(), "Lỗi tải lịch sân: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSchedule();
    }

    private void loadSchedule() {
        if (spinnerSan == null || sanList.isEmpty() || adapter == null) return;
        int pos = spinnerSan.getSelectedItemPosition();
        if (pos < 0 || pos >= sanList.size()) return;

        SanModel san = sanList.get(pos);
        String mo = san.gioMoCua != null ? san.gioMoCua : "06:00";
        String cl = san.gioDongCua != null ? san.gioDongCua : "22:00";
        tvOpenHours.setText(String.format(Locale.getDefault(), "%s — %s", mo, cl));

        int openMin = DateUtils.toMinutes(mo);
        int closeMin = DateUtils.toMinutes(cl);
        slotBlocks.clear();
        if (openMin < 0 || closeMin <= openMin) {
            adapter.setData(new ArrayList<>());
            tvSummary.setText(String.format(Locale.getDefault(),
                    "%s — %s — chưa có khung giờ hợp lệ", san.tenSan, ngayDat));
            return;
        }
        for (int t = openMin; t + 60 <= closeMin; t += 60) {
            KhungGioModel k = new KhungGioModel();
            k.gioBatDau = String.format(Locale.US, "%02d:%02d", t / 60, t % 60);
            k.gioKetThuc = String.format(Locale.US, "%02d:%02d", (t + 60) / 60, (t + 60) % 60);
            slotBlocks.add(k);
        }

        List<DatSanDAO.BookedRange> ranges = datSanDAO.listBookedRanges(san.maSan, ngayDat);

        List<AdminSlotAdapter.SlotCell> cells = new ArrayList<>();
        int bookedCount = 0;
        for (KhungGioModel slot : slotBlocks) {
            AdminSlotAdapter.SlotCell cell = new AdminSlotAdapter.SlotCell();
            cell.khung = slot;
            cell.maDatSan = 0;
            cell.trangThai = null;
            cell.tenKh = null;

            int b = DateUtils.toMinutes(slot.gioBatDau);
            int e = DateUtils.toMinutes(slot.gioKetThuc);
            for (DatSanDAO.BookedRange r : ranges) {
                int rb = DateUtils.toMinutes(r.gioBd);
                int rk = DateUtils.toMinutes(r.gioKt);
                if (rb < 0 || rk < 0 || rk <= rb) continue;
                if (b < rk && e > rb) {
                    cell.trangThai = r.trangThai;
                    cell.tenKh = r.tenKh;
                    cell.maDatSan = r.maDatSan;
                    if (r.trangThai != null &&
                            !r.trangThai.equalsIgnoreCase("HUY") &&
                            !r.trangThai.equalsIgnoreCase("TU_CHOI")) {
                        bookedCount++;
                    }
                    break;
                }
            }
            cells.add(cell);
        }

        adapter.setData(cells);
        tvSummary.setText(String.format(Locale.getDefault(),
                "%s — %s — %d/%d khung đã có lịch",
                san.tenSan != null ? san.tenSan : "Sân " + san.maSan,
                ngayDat, bookedCount, slotBlocks.size()));
    }

    private void onSlotClicked(AdminSlotAdapter.SlotCell cell) {
        if (cell == null || cell.khung == null) return;
        if (cell.maDatSan <= 0 || cell.trangThai == null
                || cell.trangThai.equalsIgnoreCase("HUY")
                || cell.trangThai.equalsIgnoreCase("TU_CHOI")) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(String.format(Locale.getDefault(),
                            "Khung %s — %s",
                            cell.khung.gioBatDau, cell.khung.gioKetThuc))
                    .setMessage("Khung giờ này đang trống. Khách có thể đặt sân.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        String detail = String.format(Locale.US,
                "Sân: %s\nNgày: %s\nKhung: %s — %s\nTrạng thái: %s\nKhách: %s",
                sanList.get(spinnerSan.getSelectedItemPosition()).tenSan,
                ngayDat,
                cell.khung.gioBatDau, cell.khung.gioKetThuc,
                cell.trangThai,
                cell.tenKh != null ? cell.tenKh : "—");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Chi tiết đặt sân")
                .setMessage(detail)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton("Đóng", null)
                .show();
    }
}