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

import com.example.pickerball.DAO.ThongBaoDAO;
import com.example.pickerball.R;
import com.example.pickerball.SessionManager;

import java.util.List;

public class StaffNotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_notify, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        SessionManager s = new SessionManager(requireContext());
        RecyclerView rv = requireView().findViewById(R.id.rv_staff_notify);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (s.getMaTk() <= 0) {
            rv.setAdapter(new SimpleAdapter(java.util.Collections.emptyList()));
            return;
        }
        List<ThongBaoDAO.Row> rows = new ThongBaoDAO(requireContext()).listForMaTk(s.getMaTk());
        new ThongBaoDAO(requireContext()).markAllRead(s.getMaTk());
        rv.setAdapter(new SimpleAdapter(rows));
    }

    private static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VH> {
        private final List<ThongBaoDAO.Row> list;

        SimpleAdapter(List<ThongBaoDAO.Row> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ThongBaoDAO.Row r = list.get(position);
            h.t1.setText(r.tieuDe != null ? r.tieuDe : "");
            h.t2.setText(r.noiDung != null ? r.noiDung : "");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;

            VH(@NonNull View itemView) {
                super(itemView);
                t1 = itemView.findViewById(R.id.tvNTitle);
                t2 = itemView.findViewById(R.id.tvNBody);
            }
        }
    }
}
