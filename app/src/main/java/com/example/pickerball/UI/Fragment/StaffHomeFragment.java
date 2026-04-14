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
import com.example.pickerball.DAO.HoaDonDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.R;

import java.util.Locale;

public class StaffHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v == null) return;
        DatSanDAO dat = new DatSanDAO(requireContext());
        int pending = dat.listChoDuyet().size();
        int hd = new HoaDonDAO(requireContext()).count();
        int san = new SanDAO(requireContext()).count();
        TextView t1 = v.findViewById(R.id.tvStaffPending);
        TextView t2 = v.findViewById(R.id.tvStaffHoaDon);
        TextView t3 = v.findViewById(R.id.tvStaffSan);
        t1.setText(String.format(Locale.getDefault(), "Đặt sân chờ duyệt: %d", pending));
        t2.setText(String.format(Locale.getDefault(), "Hóa đơn (tổng): %d", hd));
        t3.setText(String.format(Locale.getDefault(), "Sân: %d", san));
    }
}
