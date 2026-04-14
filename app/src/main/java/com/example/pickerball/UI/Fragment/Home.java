package com.example.pickerball.UI.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.R;

import java.util.Locale;

public class Home extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v == null) return;
        TextView tvSan = v.findViewById(R.id.tvDashSanCount);
        TextView tvBook = v.findViewById(R.id.tvDashBookCount);
        if (getContext() == null) return;
        int nSan = new SanDAO(requireContext()).count();
        int nBook = new DatSanDAO(requireContext()).count();
        tvSan.setText(String.format(Locale.getDefault(), "%d sân", nSan));
        tvBook.setText(String.format(Locale.getDefault(), "%d lượt", nBook));
    }
}
