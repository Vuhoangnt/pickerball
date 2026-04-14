package com.example.pickerball.UI.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.AdminNvBaoCaoAdapter;
import com.example.pickerball.DAO.ThongKeDAO;
import com.example.pickerball.R;
import com.example.pickerball.ThemeHelper;
import com.example.pickerball.util.TrangThaiLabels;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminThongKeFragment extends Fragment {

    private String tuNgay;
    private String denNgay;
    private TextView tvTu, tvDen;
    private TextView tvTienVao, tvTienRa, tvDoanhSo, tvLoiNhuan;
    private TextView tvTienSan, tvTienDv;
    private TextView tvTongPhieu, tvTongDuKien, tvHdTt;
    private BarChart chartBar;
    private LineChart chartLine;
    private LineChart chartTien;
    private AdminNvBaoCaoAdapter nvBaoCaoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_thongke, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        Calendar c = Calendar.getInstance();
        denNgay = fmt(c);
        c.set(Calendar.DAY_OF_MONTH, 1);
        tuNgay = fmt(c);

        tvTu = v.findViewById(R.id.tvTkTu);
        tvDen = v.findViewById(R.id.tvTkDen);
        tvTienVao = v.findViewById(R.id.tvTkTienVao);
        tvTienRa = v.findViewById(R.id.tvTkTienRa);
        tvDoanhSo = v.findViewById(R.id.tvTkDoanhSo);
        tvLoiNhuan = v.findViewById(R.id.tvTkLoiNhuan);
        tvTienSan = v.findViewById(R.id.tvTkTienSan);
        tvTienDv = v.findViewById(R.id.tvTkTienDv);
        tvTongPhieu = v.findViewById(R.id.tvTkTongPhieu);
        tvTongDuKien = v.findViewById(R.id.tvTkTongDuKien);
        tvHdTt = v.findViewById(R.id.tvTkHdTt);
        chartBar = v.findViewById(R.id.chartTrangThai);
        chartLine = v.findViewById(R.id.chartTheoNgay);
        chartTien = v.findViewById(R.id.chartTienNgay);

        RecyclerView rvNv = v.findViewById(R.id.rvAdminNvBaoCao);
        rvNv.setLayoutManager(new LinearLayoutManager(requireContext()));
        nvBaoCaoAdapter = new AdminNvBaoCaoAdapter(requireContext());
        rvNv.setAdapter(nvBaoCaoAdapter);

        v.findViewById(R.id.btnTkTu).setOnClickListener(x -> pick(true));
        v.findViewById(R.id.btnTkDen).setOnClickListener(x -> pick(false));

        v.findViewById(R.id.chipToday).setOnClickListener(x -> setToday());
        v.findViewById(R.id.chipMonth).setOnClickListener(x -> setThisMonth());
        v.findViewById(R.id.chipYear).setOnClickListener(x -> setThisYear());

        styleChart(chartBar);
        styleLineChart(chartLine);
        styleMoneyLineChart(chartTien);
        updateDateLabels();
        reload();
    }

    private static String fmt(Calendar c) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    private void setToday() {
        Calendar c = Calendar.getInstance();
        tuNgay = fmt(c);
        denNgay = fmt(c);
        updateDateLabels();
        reload();
    }

    private void setThisMonth() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DAY_OF_MONTH, 1);
        tuNgay = fmt(a);
        denNgay = fmt(Calendar.getInstance());
        updateDateLabels();
        reload();
    }

    private void setThisYear() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.MONTH, Calendar.JANUARY);
        a.set(Calendar.DAY_OF_MONTH, 1);
        tuNgay = fmt(a);
        denNgay = fmt(Calendar.getInstance());
        updateDateLabels();
        reload();
    }

    private void pick(boolean isTu) {
        Calendar c = Calendar.getInstance();
        String[] p = (isTu ? tuNgay : denNgay).split("-");
        c.set(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            Calendar x = Calendar.getInstance();
            x.set(y, m, d);
            String s = fmt(x);
            if (isTu) tuNgay = s;
            else denNgay = s;
            updateDateLabels();
            reload();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabels() {
        tvTu.setText(getString(R.string.admin_stats_from) + ": " + tuNgay);
        tvDen.setText(getString(R.string.admin_stats_to) + ": " + denNgay);
    }

    private void reload() {
        ThongKeDAO dao = new ThongKeDAO(requireContext());
        ThongKeDAO.TomTat t = dao.tomTat(tuNgay, denNgay);
        Locale lc = Locale.getDefault();

        tvTienVao.setText(String.format(lc, "%,.0f đ", t.doanhThuHoaDon));
        tvTienRa.setText(String.format(lc, "%,.0f đ", t.tongChiPhi));
        tvDoanhSo.setText(String.format(lc, "%d đơn", t.soHoaDonDaTt));
        tvLoiNhuan.setText(String.format(lc, "%,.0f đ", t.loiNhuanGop));
        int profitColor = t.loiNhuanGop >= 0
                ? ContextCompat.getColor(requireContext(), R.color.stat_profit)
                : ContextCompat.getColor(requireContext(), R.color.stat_expense);
        tvLoiNhuan.setTextColor(profitColor);

        tvTienSan.setText(getString(R.string.admin_stat_tien_san_fmt, t.tienSanDaTt));
        tvTienDv.setText(getString(R.string.admin_stat_tien_dv_fmt, t.tienDichVuDaTt));

        tvTongPhieu.setText(getString(R.string.admin_dash_stat_phieu, t.tongPhieuDat));
        tvTongDuKien.setText(String.format(lc, getString(R.string.admin_dash_stat_du_kien_fmt), t.tongDuKienPhieu));
        tvHdTt.setText(getString(R.string.admin_dash_stat_hd, t.soHoaDonDaTt));

        int primary = ThemeHelper.resolvePrimaryColor(requireContext());

        List<ThongKeDAO.DemTrangThai> sts = dao.demTheoTrangThai(tuNgay, denNgay);
        List<BarEntry> be = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < sts.size(); i++) {
            be.add(new BarEntry(i, sts.get(i).soLuong));
            labels.add(TrangThaiLabels.vn(sts.get(i).trangThai));
        }
        if (be.isEmpty()) {
            be.add(new BarEntry(0, 0));
            labels.add("—");
        }
        BarDataSet dsBar = new BarDataSet(be, getString(R.string.admin_dash_chart_legend_phieu));
        dsBar.setColor(primary);
        dsBar.setValueTextSize(10f);
        BarData bd = new BarData(dsBar);
        bd.setBarWidth(0.5f);
        chartBar.setData(bd);
        chartBar.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartBar.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartBar.getXAxis().setGranularity(1f);
        chartBar.getDescription().setEnabled(false);
        chartBar.getLegend().setEnabled(false);
        chartBar.invalidate();

        List<ThongKeDAO.DemTheoNgay> days = dao.demPhieuTheoNgay(tuNgay, denNgay);
        List<Entry> le = new ArrayList<>();
        List<String> dayLabels = new ArrayList<>();
        for (int i = 0; i < days.size(); i++) {
            le.add(new Entry(i, days.get(i).soLuong));
            String ng = days.get(i).ngay;
            if (ng != null && ng.length() >= 10) {
                dayLabels.add(ng.substring(8, 10) + "/" + ng.substring(5, 7));
            } else {
                dayLabels.add(String.valueOf(i));
            }
        }
        if (le.isEmpty()) {
            le.add(new Entry(0, 0));
            dayLabels.add("—");
        }
        LineDataSet ls = new LineDataSet(le, getString(R.string.admin_dash_chart_legend_phieu));
        ls.setColor(ContextCompat.getColor(requireContext(), R.color.brand_orange));
        ls.setCircleColor(ContextCompat.getColor(requireContext(), R.color.brand_orange));
        ls.setLineWidth(2f);
        ls.setValueTextSize(9f);
        LineData ld = new LineData(ls);
        chartLine.setData(ld);
        chartLine.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dayLabels));
        chartLine.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartLine.getXAxis().setGranularity(1f);
        chartLine.getDescription().setEnabled(false);
        chartLine.invalidate();

        List<ThongKeDAO.TienTheoNgay> moneyDays = dao.tomTatTienTheoNgayLienTuc(tuNgay, denNgay);
        List<Entry> ev = new ArrayList<>();
        List<Entry> er = new ArrayList<>();
        List<String> moneyLabels = new ArrayList<>();
        for (int i = 0; i < moneyDays.size(); i++) {
            ThongKeDAO.TienTheoNgay x = moneyDays.get(i);
            ev.add(new Entry(i, (float) x.tienVao));
            er.add(new Entry(i, (float) x.tienRa));
            String ng = x.ngay;
            if (ng != null && ng.length() >= 10) {
                moneyLabels.add(ng.substring(8, 10) + "/" + ng.substring(5, 7));
            } else {
                moneyLabels.add("—");
            }
        }
        if (ev.isEmpty()) {
            ev.add(new Entry(0, 0));
            er.add(new Entry(0, 0));
            moneyLabels.add("—");
        }
        int cIn = ContextCompat.getColor(requireContext(), R.color.stat_revenue);
        int cOut = ContextCompat.getColor(requireContext(), R.color.stat_expense);
        LineDataSet dsIn = new LineDataSet(ev, getString(R.string.admin_stat_tien_vao));
        dsIn.setColor(cIn);
        dsIn.setCircleColor(cIn);
        dsIn.setLineWidth(2.2f);
        dsIn.setDrawValues(false);
        LineDataSet dsOut = new LineDataSet(er, getString(R.string.admin_stat_tien_ra));
        dsOut.setColor(cOut);
        dsOut.setCircleColor(cOut);
        dsOut.setLineWidth(2.2f);
        dsOut.setDrawValues(false);
        LineData ldMoney = new LineData(dsIn, dsOut);
        chartTien.setData(ldMoney);
        chartTien.getXAxis().setValueFormatter(new IndexAxisValueFormatter(moneyLabels));
        chartTien.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartTien.getXAxis().setGranularity(1f);
        chartTien.getDescription().setEnabled(false);
        Legend leg = chartTien.getLegend();
        leg.setEnabled(true);
        leg.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        leg.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        leg.setTextSize(11f);
        chartTien.invalidate();

        nvBaoCaoAdapter.setList(dao.listBaoCaoNhanVienTheoKy(tuNgay, denNgay));
    }

    private static void styleChart(BarChart c) {
        c.getAxisLeft().setAxisMinimum(0f);
        c.getAxisRight().setEnabled(false);
        c.getXAxis().setGranularity(1f);
    }

    private static void styleLineChart(LineChart c) {
        c.getAxisLeft().setAxisMinimum(0f);
        c.getAxisRight().setEnabled(false);
        c.getXAxis().setGranularity(1f);
    }

    private static void styleMoneyLineChart(LineChart c) {
        c.getAxisLeft().setAxisMinimum(0f);
        c.getAxisRight().setEnabled(false);
        c.getXAxis().setGranularity(1f);
        c.getXAxis().setLabelRotationAngle(-45f);
        c.setExtraBottomOffset(8f);
    }
}
