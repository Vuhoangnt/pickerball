package com.example.pickerball.UI.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.AdminSanAdapter;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.SanDialog;
import com.example.pickerball.util.SanMediaStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminSanFragment extends Fragment implements SanDialog.GalleryPickHost {

    private RecyclerView rv;
    private SanDAO dao;
    private AdminSanAdapter adapter;
    private final List<SanModel> list = new ArrayList<>();
    private TabLayout tabs;

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
        rv = v.findViewById(R.id.recyclerSan);
        dao = new SanDAO(requireContext());
        tabs = v.findViewById(R.id.tabSanStatus);
        tabs.addTab(tabs.newTab().setText("Tất cả"));
        tabs.addTab(tabs.newTab().setText("Trống"));
        tabs.addTab(tabs.newTab().setText("Đã đặt"));

        adapter = new AdminSanAdapter(requireContext(), list, this, this::reloadCurrentTab);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fabAdd);
        fab.setOnClickListener(view -> SanDialog.showDialog(requireContext(), null, this::reloadCurrentTab, this));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void reloadCurrentTab() {
        if (tabs != null) loadDataForTab(tabs.getSelectedTabPosition());
    }

    private void loadDataForTab(int tabPos) {
        List<SanModel> all = dao.getAll();
        List<SanModel> filtered = new ArrayList<>();
        if (tabPos == 0) {
            filtered.addAll(all);
        } else if (tabPos == 1) {
            for (SanModel s : all) {
                if (s.trangThai != null && "TRONG".equalsIgnoreCase(s.trangThai.trim())) {
                    filtered.add(s);
                }
            }
        } else {
            for (SanModel s : all) {
                if (s.trangThai != null && "DA_DAT".equalsIgnoreCase(s.trangThai.trim())) {
                    filtered.add(s);
                }
            }
        }
        adapter.setList(filtered);
    }
}
