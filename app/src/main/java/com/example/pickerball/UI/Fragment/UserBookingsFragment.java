package com.example.pickerball.UI.Fragment;

import android.app.AlertDialog;
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

import com.example.pickerball.Adapter.BookingAdapter;
import com.example.pickerball.AppConstants;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.Model.DatSanModel;
import com.example.pickerball.R;
import com.example.pickerball.SessionManager;
import com.example.pickerball.UserMainActivity;

import java.util.ArrayList;
import java.util.List;

public class UserBookingsFragment extends Fragment implements BookingAdapter.OnCancelListener {

    private RecyclerView rv;
    private BookingAdapter adapter;
    private final List<DatSanModel> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        rv = v.findViewById(R.id.rvBookings);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new BookingAdapter(requireContext(), list, this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    private void reloadData() {
        list.clear();
        SessionManager s = ((UserMainActivity) requireActivity()).getSession();
        if (!s.isGuest() && s.getMaKh() > 0) {
            list.addAll(new DatSanDAO(requireContext()).listByMaKh(s.getMaKh()));
        }
        adapter.setList(new ArrayList<>(list));
    }

    @Override
    public void onCancel(DatSanModel booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hủy đặt sân")
                .setMessage("Bạn có chắc muốn hủy đặt sân này không?")
                .setPositiveButton("Hủy đặt", (dialog, which) -> {
                    new DatSanDAO(requireContext()).updateTrangThai(booking.maDatSan, AppConstants.DS_HUY);
                    Toast.makeText(requireContext(), "Đã hủy đặt sân", Toast.LENGTH_SHORT).show();
                    reloadData();
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
