package com.example.pickerball.UI.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.KhachHangAdapter;
import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminKhTierFragment extends Fragment {

    private KhachHangDAO dao;
    private KhachHangAdapter adapter;
    private final List<KhachHangModel> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_tier, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        dao = new KhachHangDAO(requireContext());
        RecyclerView rv = v.findViewById(R.id.rv_tier_kh);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new KhachHangAdapter(requireContext(), list, null, null);
        rv.setAdapter(adapter);

        MaterialButton btn = v.findViewById(R.id.btnSyncHang);
        btn.setOnClickListener(x -> {
            int n = dao.syncHangFromPointsForAll();
            Toast.makeText(requireContext(), "Đã cập nhật hạng cho " + n + " khách", Toast.LENGTH_SHORT).show();
            reload();
        });

        reload();
    }

    private void reload() {
        list.clear();
        list.addAll(dao.getAllForAdmin());
        adapter.setList(new ArrayList<>(list), false);
    }
}
