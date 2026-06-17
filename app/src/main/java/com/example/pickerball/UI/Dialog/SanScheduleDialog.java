package com.example.pickerball.UI.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.AdminSlotAdapter;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.util.DateUtils;
import com.example.pickerball.util.GridSpacingItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SanScheduleDialog extends BottomSheetDialogFragment {

    private static final String ARG_MA_SAN = "maSan";
    private static final String ARG_TEN_SAN = "tenSan";
    private static final String ARG_NGAY = "ngay";

    public static SanScheduleDialog newInstance(SanModel san, String ngay) {
        SanScheduleDialog d = new SanScheduleDialog();
        Bundle b = new Bundle();
        b.putInt(ARG_MA_SAN, san != null ? san.maSan : 0);
        b.putString(ARG_TEN_SAN, san != null && san.tenSan != null ? san.tenSan : "Sân");
        b.putString(ARG_NGAY, ngay);
        d.setArguments(b);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_san_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b = getArguments();
        if (b == null) {
            dismiss();
            return;
        }
        int maSan = b.getInt(ARG_MA_SAN, 0);
        String tenSan = b.getString(ARG_TEN_SAN, "Sân");
        String ngay = b.getString(ARG_NGAY, "");

        TextView tvName = view.findViewById(R.id.tvScheduleSanName);
        TextView tvSub = view.findViewById(R.id.tvScheduleSub);
        TextView tvSummary = view.findViewById(R.id.tvScheduleSummary);
        ImageButton btnClose = view.findViewById(R.id.btnScheduleClose);
        RecyclerView rv = view.findViewById(R.id.rvScheduleSlots);

        tvName.setText(tenSan);

        SanDAO sanDAO = new SanDAO(requireContext());
        SanModel san = sanDAO.getById(maSan);
        String mo = san != null && san.gioMoCua != null ? san.gioMoCua : "06:00";
        String cl = san != null && san.gioDongCua != null ? san.gioDongCua : "22:00";
        tvSub.setText(String.format(Locale.getDefault(), "%s — %s · %s", mo, cl, ngay));

        AdminSlotAdapter adapter = new AdminSlotAdapter(this::onSlotClicked);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        if (rv.getItemDecorationCount() == 0) {
            int gap = (int) (6 * requireContext().getResources().getDisplayMetrics().density);
            rv.addItemDecoration(new GridSpacingItemDecoration(gap, false));
        }
        rv.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dismiss());

        int openMin = DateUtils.toMinutes(mo);
        int closeMin = DateUtils.toMinutes(cl);
        if (openMin < 0 || closeMin <= openMin) {
            tvSummary.setText("Chưa có khung giờ hợp lệ");
            return;
        }

        List<KhungGioModel> slotBlocks = new ArrayList<>();
        for (int t = openMin; t + 60 <= closeMin; t += 60) {
            KhungGioModel k = new KhungGioModel();
            k.gioBatDau = String.format(Locale.US, "%02d:%02d", t / 60, t % 60);
            k.gioKetThuc = String.format(Locale.US, "%02d:%02d", (t + 60) / 60, (t + 60) % 60);
            slotBlocks.add(k);
        }

        DatSanDAO datSanDAO = new DatSanDAO(requireContext());
        List<DatSanDAO.BookedRange> ranges = datSanDAO.listBookedRanges(maSan, ngay);

        List<AdminSlotAdapter.SlotCell> cells = new ArrayList<>();
        int bookedCount = 0;
        for (KhungGioModel slot : slotBlocks) {
            AdminSlotAdapter.SlotCell cell = new AdminSlotAdapter.SlotCell();
            cell.khung = slot;
            cell.maDatSan = 0;
            cell.trangThai = null;
            cell.tenKh = null;

            int b0 = DateUtils.toMinutes(slot.gioBatDau);
            int e0 = DateUtils.toMinutes(slot.gioKetThuc);
            for (DatSanDAO.BookedRange r : ranges) {
                int rb = DateUtils.toMinutes(r.gioBd);
                int rk = DateUtils.toMinutes(r.gioKt);
                if (rb < 0 || rk < 0 || rk <= rb) continue;
                if (b0 < rk && e0 > rb) {
                    cell.trangThai = r.trangThai;
                    cell.tenKh = r.tenKh;
                    cell.maDatSan = r.maDatSan;
                    if (r.trangThai != null
                            && !r.trangThai.equalsIgnoreCase("HUY")
                            && !r.trangThai.equalsIgnoreCase("TU_CHOI")) {
                        bookedCount++;
                    }
                    break;
                }
            }
            cells.add(cell);
        }

        adapter.setData(cells);
        tvSummary.setText(String.format(Locale.getDefault(),
                "%d/%d khung đã có lịch", bookedCount, slotBlocks.size()));
    }

    private void onSlotClicked(AdminSlotAdapter.SlotCell cell) {
        if (cell == null || cell.khung == null) return;
        if (cell.maDatSan <= 0 || cell.trangThai == null
                || cell.trangThai.equalsIgnoreCase("HUY")
                || cell.trangThai.equalsIgnoreCase("TU_CHOI")) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(String.format(Locale.getDefault(),
                            "Khung %s — %s",
                            cell.khung.gioBatDau, cell.khung.gioKetThuc))
                    .setMessage("Khung giờ này đang trống. Khách có thể đặt sân.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        Bundle b = getArguments();
        String ngay = b != null ? b.getString(ARG_NGAY, "") : "";
        String detail = String.format(Locale.US,
                "Ngày: %s\nKhung: %s — %s\nTrạng thái: %s\nKhách: %s",
                ngay,
                cell.khung.gioBatDau, cell.khung.gioKetThuc,
                cell.trangThai,
                cell.tenKh != null ? cell.tenKh : "—");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Chi tiết đặt sân")
                .setMessage(detail)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton("Đóng", null)
                .show();
    }
}
