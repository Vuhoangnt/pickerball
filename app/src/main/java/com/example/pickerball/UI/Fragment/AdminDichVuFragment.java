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

import com.example.pickerball.Adapter.DichVuAdapter;
import com.example.pickerball.DAO.DichVuDAO;
import com.example.pickerball.Model.DichVuModel;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.DichVuDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminDichVuFragment extends Fragment {

    private DichVuDAO dao;
    private DichVuAdapter adapter;
    private final List<DichVuModel> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dichvu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        dao = new DichVuDAO(requireContext());
        RecyclerView rv = v.findViewById(R.id.rvDichVu);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DichVuAdapter(requireContext(), list, m ->
                new AlertDialog.Builder(requireContext())
                        .setItems(new String[]{"Sửa", "Xóa"}, (d, which) -> {
                            if (which == 0) {
                                DichVuDialog.show(requireContext(), m, this::reload);
                            } else {
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Xóa dịch vụ?")
                                        .setPositiveButton("Xóa", (a, w) -> {
                                            dao.delete(m.getMaDv());
                                            reload();
                                        })
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .show();
                            }
                        })
                        .show());
        rv.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fabAddDv);
        fab.setOnClickListener(x -> DichVuDialog.show(requireContext(), null, this::reload));

        reload();
    }

    private void reload() {
        list.clear();
        list.addAll(dao.getAll());
        adapter.setList(new ArrayList<>(list));
    }
}
