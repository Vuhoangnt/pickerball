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

import com.example.pickerball.Adapter.DatSanAdapter;
import com.example.pickerball.Adapter.HoaDonAdapter;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.HoaDonDAO;
import com.example.pickerball.Model.DatSanListItem;
import com.example.pickerball.Model.HoaDonListItem;
import com.example.pickerball.R;
import com.example.pickerball.UI.Dialog.DatSanDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DatSanHoaDonFragment extends Fragment {

    private DatSanDAO datSanDAO;
    private HoaDonDAO hoaDonDAO;
    private DatSanAdapter datAdapter;
    private HoaDonAdapter hdAdapter;
    private final List<DatSanListItem> datList = new ArrayList<>();
    private final List<HoaDonListItem> hdList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_datsan_hoadon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        datSanDAO = new DatSanDAO(requireContext());
        hoaDonDAO = new HoaDonDAO(requireContext());

        RecyclerView rvDat = v.findViewById(R.id.rvDatSan);
        rvDat.setLayoutManager(new LinearLayoutManager(requireContext()));
        datAdapter = new DatSanAdapter(requireContext(), datList, row ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xóa phiếu đặt?")
                        .setMessage("#" + row.maDatSan)
                        .setPositiveButton("Xóa", (d, w) -> {
                            datSanDAO.delete(row.maDatSan);
                            reload();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show());
        rvDat.setAdapter(datAdapter);

        RecyclerView rvHd = v.findViewById(R.id.rvHoaDon);
        rvHd.setLayoutManager(new LinearLayoutManager(requireContext()));
        hdAdapter = new HoaDonAdapter(requireContext(), hdList, null, null);
        rvHd.setAdapter(hdAdapter);

        FloatingActionButton fab = v.findViewById(R.id.fabAddDatSan);
        fab.setOnClickListener(x -> DatSanDialog.show(requireContext(), this::reload));

        reload();
    }

    private void reload() {
        datList.clear();
        datList.addAll(datSanDAO.getAllWithNames());
        datAdapter.setList(new ArrayList<>(datList));
        hdList.clear();
        hdList.addAll(hoaDonDAO.getAllWithSan());
        hdAdapter.setList(new ArrayList<>(hdList));
    }
}
