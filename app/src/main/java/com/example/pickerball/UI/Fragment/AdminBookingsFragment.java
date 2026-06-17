package com.example.pickerball.UI.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.AdminBookingAdapter;
import com.example.pickerball.AppConstants;
import com.example.pickerball.DAO.CauHinhGiaDAO;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.HoaDonDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.DAO.SuDungDvDAO;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.SessionManager;
import com.example.pickerball.util.DateUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminBookingsFragment extends Fragment {

    private static final int TAB_PENDING = 0;
    private static final int TAB_APPROVED = 1;
    private static final int TAB_ALL = 2;

    private DatSanDAO dao;
    private AdminBookingAdapter adapter;
    private final List<DatSanDAO.AdminBookingRow> list = new ArrayList<>();
    private TabLayout tabLayout;
    private TextView emptyView;
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        dao = new DatSanDAO(requireContext());
        tabLayout = v.findViewById(R.id.tabAdminBookings);
        rv = v.findViewById(R.id.rvAdminBookings);
        emptyView = v.findViewById(R.id.tvAdminBookingsEmpty);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdminBookingAdapter(requireContext(), new ArrayList<>(), new AdminBookingAdapter.ActionListener() {
            @Override
            public void onApprove(DatSanDAO.AdminBookingRow row) {
                SessionManager session = new SessionManager(requireContext());
                int maNv = session.getMaNv();

                HoaDonDAO hdDao = new HoaDonDAO(requireContext());
                if (hdDao.getMaHdByMaDatSan(row.maDatSan) <= 0) {
                    SanModel san = new SanDAO(requireContext()).getById(row.maSan);
                    if (san != null) {
                        Calendar day = Calendar.getInstance();
                        String[] p = row.ngayDat != null ? row.ngayDat.split("-") : new String[0];
                        if (p.length >= 3) {
                            day.set(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
                        }
                        String loaiNgay = DateUtils.loaiNgay(day);

                        double tienSan = new CauHinhGiaDAO(requireContext())
                                .getGiaOrTheoGio(san, 0, loaiNgay, row.gioBd, row.gioKt);
                        double tienDv = new SuDungDvDAO(requireContext()).sumGiaDvByMaDatSan(row.maDatSan);
                        double tongTien = tienSan + tienDv;

                        hdDao.createHoaDon(row.maDatSan, maNv > 0 ? maNv : 1, tienSan, tienDv, tongTien);
                    }
                }

                dao.updateTrangThai(row.maDatSan, AppConstants.DS_DA_DUYET, maNv > 0 ? maNv : 1);
                reloadCurrentTab();
            }

            @Override
            public void onReject(DatSanDAO.AdminBookingRow row) {
                SessionManager session = new SessionManager(requireContext());
                int maNv = session.getMaNv();
                dao.updateTrangThai(row.maDatSan, AppConstants.DS_TU_CHOI, maNv > 0 ? maNv : 1);
                reloadCurrentTab();
            }
        });
        rv.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Chờ duyệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã duyệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadDataForTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                loadDataForTab(tab.getPosition());
            }
        });

        loadDataForTab(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadCurrentTab();
    }

    private void reloadCurrentTab() {
        if (tabLayout != null) {
            loadDataForTab(tabLayout.getSelectedTabPosition());
        }
    }

    private void loadDataForTab(int tabPos) {
        List<DatSanDAO.AdminBookingRow> all = dao.listAllForAdmin();
        List<DatSanDAO.AdminBookingRow> filtered = new ArrayList<>();

        if (tabPos == TAB_PENDING) {
            for (DatSanDAO.AdminBookingRow r : all) {
                if (AppConstants.DS_CHO_DUYET.equalsIgnoreCase(r.trangThai)) {
                    filtered.add(r);
                }
            }
        } else if (tabPos == TAB_APPROVED) {
            for (DatSanDAO.AdminBookingRow r : all) {
                if (AppConstants.DS_DA_DUYET.equalsIgnoreCase(r.trangThai)) {
                    filtered.add(r);
                }
            }
        } else {
            filtered.addAll(all);
        }

        adapter.setList(filtered);
        emptyView.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
