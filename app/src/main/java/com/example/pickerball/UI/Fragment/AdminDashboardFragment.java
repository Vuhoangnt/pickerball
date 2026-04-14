package com.example.pickerball.UI.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickerball.DAO.HeThongDAO;
import com.example.pickerball.DAO.ThongKeDAO;
import com.example.pickerball.R;
import com.example.pickerball.util.TrangThaiLabels;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Trang chủ admin: cấu hình điểm khi đặt sân, tổng quan, biểu đồ cột theo ngày / tháng / năm.
 */
public class AdminDashboardFragment extends Fragment {

    private static final String K_DIEM_DAT = "DIEM_KHI_DAT_SAN";

    private int period; // 0 = 7 ngày, 1 = tháng hiện tại, 2 = năm hiện tại (chart 12 tháng hoặc gộp năm)

    private TextInputEditText edtPoints;
    private TextView tvRangeHint, tvTongPhieu, tvTongDuKien, tvDoanhThu, tvHdTt, tvSanKhach, tvChartCaption;
    private TextView tvDashTrangThai, tvDashHinhThuc;
    private BarChart chartBar;
    private Chip chipDay, chipMonth, chipYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        period = 0;

        edtPoints = v.findViewById(R.id.edtDashPoints);
        tvRangeHint = v.findViewById(R.id.tvDashRangeHint);
        tvTongPhieu = v.findViewById(R.id.tvDashTongPhieu);
        tvTongDuKien = v.findViewById(R.id.tvDashTongDuKien);
        tvDoanhThu = v.findViewById(R.id.tvDashDoanhThu);
        tvHdTt = v.findViewById(R.id.tvDashHdTt);
        tvSanKhach = v.findViewById(R.id.tvDashSanKhach);
        tvChartCaption = v.findViewById(R.id.tvDashChartCaption);
        tvDashTrangThai = v.findViewById(R.id.tvDashTrangThai);
        tvDashHinhThuc = v.findViewById(R.id.tvDashHinhThuc);
        chartBar = v.findViewById(R.id.chartDashBar);
        chipDay = v.findViewById(R.id.chipDashDay);
        chipMonth = v.findViewById(R.id.chipDashMonth);
        chipYear = v.findViewById(R.id.chipDashYear);
        MaterialButton btnSave = v.findViewById(R.id.btnDashSaveCfg);

        HeThongDAO he = new HeThongDAO(requireContext());
        edtPoints.setText(String.valueOf(he.getInt(K_DIEM_DAT, 5)));

        btnSave.setOnClickListener(x -> savePoints(he));

        View.OnClickListener chipListener = x -> {
            int id = x.getId();
            if (id == R.id.chipDashDay) period = 0;
            else if (id == R.id.chipDashMonth) period = 1;
            else period = 2;
            reload();
        };
        chipDay.setOnClickListener(chipListener);
        chipMonth.setOnClickListener(chipListener);
        chipYear.setOnClickListener(chipListener);

        styleChart(chartBar);
        reload();
    }

    private void savePoints(HeThongDAO he) {
        String s = edtPoints.getText() != null ? edtPoints.getText().toString().trim() : "";
        int val;
        try {
            val = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), R.string.admin_cfg_points_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (val < 0 || val > 999) {
            Toast.makeText(getContext(), R.string.admin_cfg_points_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        he.putInt(K_DIEM_DAT, val);
        Toast.makeText(getContext(), R.string.admin_cfg_saved, Toast.LENGTH_SHORT).show();
    }

    private void reload() {
        ThongKeDAO dao = new ThongKeDAO(requireContext());
        Calendar now = Calendar.getInstance();
        String tuNgay;
        String denNgay;
        String rangeLabel;

        if (period == 0) {
            Calendar start = Calendar.getInstance();
            start.add(Calendar.DAY_OF_MONTH, -6);
            tuNgay = fmt(start);
            denNgay = fmt(now);
            rangeLabel = getString(R.string.admin_dash_range_7d, tuNgay, denNgay);
        } else if (period == 1) {
            Calendar a = Calendar.getInstance();
            a.set(Calendar.DAY_OF_MONTH, 1);
            tuNgay = fmt(a);
            Calendar b = Calendar.getInstance();
            b.set(Calendar.DAY_OF_MONTH, b.getActualMaximum(Calendar.DAY_OF_MONTH));
            denNgay = fmt(b);
            rangeLabel = getString(R.string.admin_dash_range_month, tuNgay, denNgay);
        } else {
            int y = now.get(Calendar.YEAR);
            tuNgay = String.format(Locale.US, "%04d-01-01", y);
            denNgay = String.format(Locale.US, "%04d-12-31", y);
            rangeLabel = getString(R.string.admin_dash_range_year, y);
        }

        tvRangeHint.setText(rangeLabel);

        ThongKeDAO.TomTat t = dao.tomTat(tuNgay, denNgay);
        tvTongPhieu.setText(getString(R.string.admin_dash_stat_phieu, t.tongPhieuDat));
        tvTongDuKien.setText(String.format(Locale.getDefault(), getString(R.string.admin_dash_stat_du_kien_fmt), t.tongDuKienPhieu));
        tvDoanhThu.setText(String.format(Locale.getDefault(), getString(R.string.admin_dash_stat_dt_fmt), t.doanhThuHoaDon));
        tvHdTt.setText(getString(R.string.admin_dash_stat_hd, t.soHoaDonDaTt));
        tvSanKhach.setText(getString(R.string.admin_dash_stat_san_khach, dao.demTongSan(), dao.demTongKhachHang()));

        // Thống kê "kiểu" (trang_thai + hinh_thuc)
        List<ThongKeDAO.DemTrangThai> trangThais = dao.demTheoTrangThai(tuNgay, denNgay);
        if (trangThais == null || trangThais.isEmpty()) {
            tvDashTrangThai.setText("Trạng thái: —");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Trạng thái: ");
            for (int i = 0; i < trangThais.size(); i++) {
                ThongKeDAO.DemTrangThai d = trangThais.get(i);
                sb.append(TrangThaiLabels.vn(d.trangThai)).append(": ").append(d.soLuong);
                if (i < trangThais.size() - 1) sb.append(" · ");
            }
            tvDashTrangThai.setText(sb.toString());
        }

        List<ThongKeDAO.DemHinhThuc> kinds = dao.demTheoHinhThuc(tuNgay, denNgay);
        if (kinds == null || kinds.isEmpty()) {
            tvDashHinhThuc.setText("Kiểu: —");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Kiểu: ");
            for (int i = 0; i < kinds.size(); i++) {
                ThongKeDAO.DemHinhThuc d = kinds.get(i);
                sb.append(d.hinhThuc).append(": ").append(d.soLuong);
                if (i < kinds.size() - 1) sb.append(" · ");
            }
            tvDashHinhThuc.setText(sb.toString());
        }

        List<ThongKeDAO.DemTheoNgay> series;
        List<String> labels = new ArrayList<>();

        if (period == 2) {
            int y = now.get(Calendar.YEAR);
            series = dao.demPhieuTheoThangTrongNam(y);
            tvChartCaption.setText(getString(R.string.admin_dash_chart_months_of_year, y));
            for (ThongKeDAO.DemTheoNgay d : series) {
                if (d.ngay != null && d.ngay.length() >= 7) {
                    int m = Integer.parseInt(d.ngay.substring(5, 7));
                    labels.add("T" + m);
                } else {
                    labels.add("—");
                }
            }
        } else if (period == 1) {
            series = dao.demPhieuTheoNgayLienTuc(tuNgay, denNgay);
            tvChartCaption.setText(getString(R.string.admin_dash_chart_days_in_month));
            for (ThongKeDAO.DemTheoNgay d : series) {
                labels.add(shortDayLabel(d.ngay));
            }
        } else {
            series = dao.demPhieuTheoNgayLienTuc(tuNgay, denNgay);
            tvChartCaption.setText(getString(R.string.admin_dash_chart_7days));
            for (ThongKeDAO.DemTheoNgay d : series) {
                labels.add(shortDayLabel(d.ngay));
            }
        }

        List<BarEntry> be = new ArrayList<>();
        for (int i = 0; i < series.size(); i++) {
            be.add(new BarEntry(i, series.get(i).soLuong));
        }
        if (be.isEmpty()) {
            be.add(new BarEntry(0, 0));
            labels.clear();
            labels.add("—");
        }

        BarDataSet ds = new BarDataSet(be, getString(R.string.admin_dash_chart_legend_phieu));
        ds.setColor(Color.parseColor("#1877F2"));
        ds.setValueTextSize(9f);
        BarData bd = new BarData(ds);
        bd.setBarWidth(period == 2 ? 0.45f : 0.65f);
        chartBar.setData(bd);
        chartBar.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartBar.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartBar.getXAxis().setGranularity(1f);
        chartBar.getXAxis().setLabelRotationAngle(period == 1 ? -45f : 0f);
        chartBar.getDescription().setEnabled(false);
        chartBar.getLegend().setEnabled(false);
        chartBar.invalidate();
    }

    private static String shortDayLabel(String yyyyMmDd) {
        if (yyyyMmDd != null && yyyyMmDd.length() >= 10) {
            return yyyyMmDd.substring(8, 10) + "/" + yyyyMmDd.substring(5, 7);
        }
        return "—";
    }

    private static String fmt(Calendar c) {
        return String.format(Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    private static void styleChart(BarChart c) {
        c.getAxisLeft().setAxisMinimum(0f);
        c.getAxisRight().setEnabled(false);
        c.getXAxis().setGranularity(1f);
    }
}
