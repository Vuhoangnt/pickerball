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

import com.example.pickerball.Adapter.LeaderboardAdapter;
import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;

import java.util.ArrayList;
import java.util.List;

public class UserLeaderboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        RecyclerView rv = v.findViewById(R.id.rvLeaderboard);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<KhachHangModel> list = new KhachHangDAO(requireContext()).getLeaderboard(50);
        rv.setAdapter(new LeaderboardAdapter(new ArrayList<>(list)));
    }
}
