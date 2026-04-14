package com.example.pickerball.UI.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.LichSuAdapter;
import com.example.pickerball.DAO.LichSuDAO;
import com.example.pickerball.Model.LichSuModel;
import com.example.pickerball.R;

import java.util.ArrayList;
import java.util.List;

public class LichSuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lichsu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        LichSuDAO dao = new LichSuDAO(requireContext());
        List<LichSuModel> list = new ArrayList<>(dao.getAll());
        RecyclerView rv = v.findViewById(R.id.rvLichSu);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(new LichSuAdapter(requireContext(), list));
    }
}
