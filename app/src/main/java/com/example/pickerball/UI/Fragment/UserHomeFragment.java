package com.example.pickerball.UI.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.SanAdapter;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.UserSanDetailActivity;
import com.example.pickerball.SessionManager;
import com.example.pickerball.UserMainActivity;
import com.example.pickerball.util.HoiVienHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        v.findViewById(R.id.cardLeaderboard).setOnClickListener(x ->
                ((UserMainActivity) requireActivity()).openLeaderboard());
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v == null) return;
        TextView w = v.findViewById(R.id.tvWelcome);
        TextView tvRank = v.findViewById(R.id.tvHomeRank);
        TextView tvStatSan = v.findViewById(R.id.tv_user_stat_san);
        TextView tvStatBook = v.findViewById(R.id.tv_user_stat_book);
        SessionManager s = ((UserMainActivity) requireActivity()).getSession();
        w.setText("Chào " + s.getHoTen() + ",");
        tvStatSan.setText(String.valueOf(new SanDAO(requireContext()).count()));
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvStatBook.setText(String.valueOf(new DatSanDAO(requireContext()).countBookingsOnDate(today)));
        if (s.isGuest() || s.getMaKh() <= 0) {
            tvRank.setVisibility(View.GONE);
        } else {
            KhachHangModel kh = new KhachHangDAO(requireContext()).getByMaKh(s.getMaKh());
            if (kh != null) {
                tvRank.setVisibility(View.VISIBLE);
                tvRank.setText(String.format(Locale.getDefault(), "Thứ hạng: %s · %d điểm",
                        HoiVienHelper.displayTitleHang(kh.getHangHoiVien()), kh.getDiem()));
            } else {
                tvRank.setVisibility(View.GONE);
            }
        }

        RecyclerView rv = v.findViewById(R.id.rvSanBrowse);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<SanModel> sanList = new SanDAO(requireContext()).getAllForCustomer();
        rv.setAdapter(new SanAdapter(requireContext(), sanList,
                s1 -> UserSanDetailActivity.start(requireContext(), s1.maSan)));
    }
}
