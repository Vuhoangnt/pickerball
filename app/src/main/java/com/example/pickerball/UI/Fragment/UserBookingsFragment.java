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

import com.example.pickerball.Adapter.BookingAdapter;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.Model.DatSanModel;
import com.example.pickerball.R;
import com.example.pickerball.SessionManager;
import com.example.pickerball.UserMainActivity;

import java.util.ArrayList;
import java.util.List;

public class UserBookingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_bookings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v == null) return;
        SessionManager s = ((UserMainActivity) requireActivity()).getSession();
        RecyclerView rv = v.findViewById(R.id.rvBookings);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<DatSanModel> list = new ArrayList<>();
        if (!s.isGuest() && s.getMaKh() > 0) {
            list.addAll(new DatSanDAO(requireContext()).listByMaKh(s.getMaKh()));
        }
        rv.setAdapter(new BookingAdapter(requireContext(), list));
    }
}
