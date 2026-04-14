package com.example.pickerball.UI.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.LoginActivity;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.R;
import com.example.pickerball.SessionManager;
import com.example.pickerball.UserMainActivity;
import com.example.pickerball.util.HoiVienHelper;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class UserProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        SessionManager s = ((UserMainActivity) requireActivity()).getSession();
        TextView tvName = v.findViewById(R.id.tvProfileName);
        TextView tvUser = v.findViewById(R.id.tvProfileUser);
        TextView tvRank = v.findViewById(R.id.tvProfileRank);
        TextView tvPoints = v.findViewById(R.id.tvProfilePoints);
        TextView tvHint = v.findViewById(R.id.tvProfileRankHint);
        TextView tvInfo = v.findViewById(R.id.tvProfileInfo);
        v.findViewById(R.id.btnTheme).setOnClickListener(x ->
                ((UserMainActivity) requireActivity()).openThemeSettings());
        MaterialButton btnLogout = v.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(x -> {
            s.logout();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.putExtra(LoginActivity.EXTRA_SHOW_LOGIN, true);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
        });

        if (s.isGuest()) {
            tvName.setText("Khách");
            tvUser.setVisibility(View.GONE);
            tvRank.setVisibility(View.GONE);
            tvPoints.setVisibility(View.GONE);
            tvHint.setVisibility(View.GONE);
            tvInfo.setText("Đăng nhập để xem lịch, thông báo và thứ hạng hội viên.");
            btnLogout.setText("Đăng nhập / Đăng ký");
            btnLogout.setOnClickListener(x -> {
                s.logout();
                Intent i = new Intent(requireContext(), LoginActivity.class);
                i.putExtra(LoginActivity.EXTRA_SHOW_LOGIN, true);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                requireActivity().finish();
            });
            return;
        }

        tvUser.setVisibility(View.VISIBLE);
        tvRank.setVisibility(View.VISIBLE);
        tvPoints.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        tvName.setText(s.getHoTen());
        tvUser.setText(s.getUsername());
        KhachHangModel kh = new KhachHangDAO(requireContext()).getByMaKh(s.getMaKh());
        if (kh != null) {
            tvRank.setText("Thứ hạng: " + HoiVienHelper.displayTitleHang(kh.getHangHoiVien()));
            tvPoints.setText(String.format(Locale.getDefault(), "Điểm tích lũy: %d", kh.getDiem()));
            tvHint.setText(HoiVienHelper.rankProgressHint(kh.getDiem()));
            double pct = HoiVienHelper.discountPercentHang(kh.getHangHoiVien()) * 100;
            String uu = pct > 0 ? String.format(Locale.getDefault(), "\nƯu đãi thanh toán tại sân: ~%.0f%%.", pct) : "";
            tvInfo.setText(String.format(Locale.getDefault(),
                    "Mã khách #%d · SĐT: %s%s", kh.getMaKh(),
                    kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "—", uu));
        } else {
            tvRank.setText("Thứ hạng: —");
            tvPoints.setText("Điểm: —");
            tvHint.setText("");
            tvInfo.setText("Mã khách #" + s.getMaKh());
        }
    }
}
