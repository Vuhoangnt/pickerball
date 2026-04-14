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
import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.KhachHangDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class KhachHangFragment extends Fragment {

    private RecyclerView rv;
    private KhachHangDAO dao;
    private KhachHangAdapter adapter;
    private final List<KhachHangModel> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_khachhang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        dao = new KhachHangDAO(requireContext());
        rv = v.findViewById(R.id.rv_khachhang);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new KhachHangAdapter(requireContext(), list,
                k -> KhachHangDialog.show(requireContext(), k, this::reload),
                k -> new AlertDialog.Builder(requireContext())
                        .setTitle("Xóa khách?")
                        .setMessage(k.getHoTen())
                        .setPositiveButton("Xóa", (d, w) -> {
                            dao.delete(k.getMaKh());
                            reload();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show());
        rv.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab_add_kh);
        fab.setImageResource(android.R.drawable.ic_input_add);
        fab.setOnClickListener(x -> KhachHangDialog.show(requireContext(), null, this::reload));

        reload();
    }

    private void reload() {
        list.clear();
        list.addAll(dao.getAll());
        adapter.setList(new ArrayList<>(list), false);
    }
}
