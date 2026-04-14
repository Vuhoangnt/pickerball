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

import com.example.pickerball.Adapter.StaffBookingAdapter;
import com.example.pickerball.Adapter.StaffHistoryAdapter;
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

public class StaffBookingsFragment extends Fragment {

    private static final int TAB_PENDING = 0;
    private static final int TAB_HISTORY = 1;

    private DatSanDAO dao;
    private StaffBookingAdapter pendingAdapter;
    private StaffHistoryAdapter historyAdapter;
    private final List<DatSanDAO.ChoDuyetRow> pendingList = new ArrayList<>();
    private final List<DatSanDAO.XuLyHistoryRow> historyList = new ArrayList<>();

    private TextView empty;
    private RecyclerView rv;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        dao = new DatSanDAO(requireContext());
        empty = v.findViewById(R.id.tvStaffEmpty);
        rv = v.findViewById(R.id.rv_staff_pending);
        tabLayout = v.findViewById(R.id.tabStaffBookings);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        pendingAdapter = new StaffBookingAdapter(requireContext(), pendingList, new StaffBookingAdapter.ActionListener() {
            @Override
            public void onApprove(DatSanDAO.ChoDuyetRow row) {
                SessionManager session = new SessionManager(requireContext());
                int maNv = session.getMaNv();
                if (maNv <= 0) {
                    reloadPending();
                    return;
                }

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
                                .getGiaOrTheoGio(san, row.maKhung, loaiNgay, row.gioBd, row.gioKt);
                        double tienDv = new SuDungDvDAO(requireContext()).sumGiaDvByMaDatSan(row.maDatSan);
                        double tongTien = tienSan + tienDv;

                        hdDao.createHoaDon(row.maDatSan, maNv, tienSan, tienDv, tongTien);
                    }
                }

                dao.updateTrangThai(row.maDatSan, AppConstants.DS_DA_DUYET, maNv);
                reloadPending();
            }

            @Override
            public void onReject(DatSanDAO.ChoDuyetRow row) {
                SessionManager session = new SessionManager(requireContext());
                int maNv = session.getMaNv();
                if (maNv > 0) {
                    dao.updateTrangThai(row.maDatSan, AppConstants.DS_TU_CHOI, maNv);
                } else {
                    dao.updateTrangThai(row.maDatSan, AppConstants.DS_TU_CHOI);
                }
                reloadPending();
            }
        });
        historyAdapter = new StaffHistoryAdapter(requireContext(), historyList);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.staff_tab_pending)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.staff_tab_history_mine)));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                applyMainTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        applyMainTab(TAB_PENDING);
    }

    private void applyMainTab(int position) {
        if (position == TAB_PENDING) {
            rv.setAdapter(pendingAdapter);
            reloadPending();
        } else {
            rv.setAdapter(historyAdapter);
            reloadHistory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tabLayout.getSelectedTabPosition() == TAB_PENDING) {
            reloadPending();
        } else {
            reloadHistory();
        }
    }

    private void reloadPending() {
        pendingList.clear();
        pendingList.addAll(dao.listChoDuyetWithNames());
        pendingAdapter.setList(new ArrayList<>(pendingList));
        if (tabLayout.getSelectedTabPosition() == TAB_PENDING) {
            empty.setText(R.string.staff_bookings_empty_pending);
            empty.setVisibility(pendingList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void reloadHistory() {
        SessionManager s = new SessionManager(requireContext());
        int maNv = s.getMaNv();
        historyList.clear();
        if (maNv > 0) {
            historyList.addAll(dao.listLichSuXuLyByMaNv(maNv));
        }
        historyAdapter.setList(new ArrayList<>(historyList));
        if (tabLayout.getSelectedTabPosition() == TAB_HISTORY) {
            empty.setText(R.string.staff_bookings_empty_history_mine);
            empty.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
