package com.example.pickerball.UI.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.KhachHangAdapter;
import com.example.pickerball.AppConstants;
import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.KhachHangDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminKhFragment extends Fragment {

    private KhachHangDAO dao;
    private KhachHangAdapter adapter;
    private final List<KhachHangModel> list = new ArrayList<>();
    private TabLayout tabs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_kh, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        dao = new KhachHangDAO(requireContext());
        tabs = v.findViewById(R.id.tabKhFilter);
        tabs.addTab(tabs.newTab().setText("Tất cả"));
        tabs.addTab(tabs.newTab().setText("Top điểm"));
        tabs.addTab(tabs.newTab().setText("Vàng"));
        tabs.addTab(tabs.newTab().setText("Bạc"));
        tabs.addTab(tabs.newTab().setText("Thường"));
        tabs.addTab(tabs.newTab().setText("Top đặt"));

        RecyclerView rv = v.findViewById(R.id.rv_admin_kh);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new KhachHangAdapter(requireContext(), list,
                k -> KhachHangDialog.show(requireContext(), k, this::reloadCurrentTab),
                k -> new AlertDialog.Builder(requireContext())
                        .setTitle("Xóa khách?")
                        .setMessage(k.getHoTen())
                        .setPositiveButton("Xóa", (d, w) -> {
                            dao.delete(k.getMaKh());
                            reloadCurrentTab();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show());
        rv.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab_add_kh_admin);
        fab.setOnClickListener(x -> KhachHangDialog.show(requireContext(), null, this::reloadCurrentTab));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                reloadForPosition(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                reloadForPosition(tab.getPosition());
            }
        });

        reloadForPosition(0);
    }

    private void reloadCurrentTab() {
        if (tabs != null) reloadForPosition(tabs.getSelectedTabPosition());
    }

    private void reloadForPosition(int pos) {
        list.clear();
        boolean rank = pos == 1;
        if (pos == 0) {
            list.addAll(dao.getAllForAdmin());
        } else if (pos == 1) {
            list.addAll(dao.getLeaderboard(500));
        } else if (pos == 2) {
            list.addAll(dao.getAllForAdminByHang(AppConstants.HANG_VANG));
        } else if (pos == 3) {
            list.addAll(dao.getAllForAdminByHang(AppConstants.HANG_BAC));
        } else if (pos == 4) {
            list.addAll(dao.getAllForAdminByHang(AppConstants.HANG_THUONG));
        } else {
            list.addAll(dao.getAllForAdminOrderByBookingsDesc());
        }
        adapter.setList(new ArrayList<>(list), rank);
    }
}
