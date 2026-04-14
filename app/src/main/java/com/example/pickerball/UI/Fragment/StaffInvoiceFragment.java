package com.example.pickerball.UI.Fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.Adapter.HoaDonAdapter;
import com.example.pickerball.DAO.HoaDonDAO;
import com.example.pickerball.Model.HoaDonListItem;
import com.example.pickerball.R;
import com.example.pickerball.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffInvoiceFragment extends Fragment {

    private HoaDonAdapter adapter;
    private final List<HoaDonListItem> list = new ArrayList<>();
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        RecyclerView rv = v.findViewById(R.id.rv_staff_hoadon);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        tvEmpty = v.findViewById(R.id.tvInvEmpty);
        adapter = new HoaDonAdapter(requireContext(), list, this::showInvoiceDetailDialog, this::showPayDialog);
        rv.setAdapter(adapter);
        reloadFromDb();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadFromDb();
    }

    private void reloadFromDb() {
        SessionManager session = new SessionManager(requireContext());
        int maNv = session.getMaNv();
        list.clear();
        if (maNv > 0) {
            list.addAll(new HoaDonDAO(requireContext()).getAllWithSanForStaffMe(maNv));
        }
        adapter.setList(new ArrayList<>(list));
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showInvoiceDetailDialog(HoaDonListItem item) {
        View detail = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_staff_invoice_detail, null, false);
        TextView tvTitle = detail.findViewById(R.id.tvDetailHdTitle);
        TextView tvSan = detail.findViewById(R.id.tvDetailSan);
        Chip chipStatus = detail.findViewById(R.id.chipDetailStatus);
        TextView tvMaDat = detail.findViewById(R.id.tvDetailMaDat);
        TextView tvTienSan = detail.findViewById(R.id.tvDetailTienSan);
        TextView tvTienDv = detail.findViewById(R.id.tvDetailTienDv);
        TextView tvTong = detail.findViewById(R.id.tvDetailTong);
        TextView tvNvDuyet = detail.findViewById(R.id.tvDetailNvDuyet);
        TextView tvNvThu = detail.findViewById(R.id.tvDetailNvThu);
        TextView tvPttt = detail.findViewById(R.id.tvDetailPttt);
        TextView tvNgayTt = detail.findViewById(R.id.tvDetailNgayTt);

        tvTitle.setText(String.format(Locale.getDefault(), getString(R.string.staff_invoice_row_title_fmt), item.maHd));
        tvSan.setText(item.tenSan != null && !item.tenSan.isEmpty() ? item.tenSan : "—");

        boolean paid = item.trangThai == 1;
        String status = paid ? getString(R.string.staff_invoice_status_paid) : getString(R.string.staff_invoice_status_unpaid);
        chipStatus.setText(status);
        int bg = paid ? R.color.theme_forest_container : R.color.brand_orange_soft;
        int fg = paid ? R.color.theme_forest_on_container : R.color.brand_orange_dark;
        chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bg)));
        chipStatus.setTextColor(ContextCompat.getColor(requireContext(), fg));

        tvMaDat.setText(String.valueOf(item.maDatSan));
        tvTienSan.setText(String.format(Locale.getDefault(), "%,.0f đ", item.tienSan));
        tvTienDv.setText(String.format(Locale.getDefault(), "%,.0f đ", item.tienDv));
        tvTong.setText(String.format(Locale.getDefault(), "%,.0f đ", item.tongTien));

        tvNvDuyet.setText(item.tenNvDuyet != null && !item.tenNvDuyet.isEmpty() ? item.tenNvDuyet : "—");
        if (paid) {
            tvNvThu.setText(item.tenNvThanhToan != null && !item.tenNvThanhToan.isEmpty() ? item.tenNvThanhToan : "—");
        } else {
            tvNvThu.setText(getString(R.string.staff_invoice_not_collected));
        }
        tvPttt.setText(item.phuongThucTt != null && !item.phuongThucTt.isEmpty() ? item.phuongThucTt : "—");
        tvNgayTt.setText(item.ngayTt != null && !item.ngayTt.isEmpty() ? item.ngayTt : "—");

        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.staff_invoice_detail_title)
                .setView(detail)
                .setNegativeButton(R.string.staff_invoice_close, null);
        if (!paid) {
            b.setPositiveButton(R.string.staff_invoice_pay, (d, w) -> showPayDialog(item));
        }
        b.show();
    }

    private void showPayDialog(HoaDonListItem item) {
        View form = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment, null);
        Spinner spPhuongThuc = form.findViewById(R.id.spPaymentMethod);

        String[] opts = new String[]{"Tiền mặt", "Chuyển khoản", "MOMO", "ZaloPay"};
        spPhuongThuc.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opts));
        spPhuongThuc.setSelection(0);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thanh toán hóa đơn")
                .setMessage(String.format(Locale.getDefault(), "HĐ #%d · Tổng: %,.0f đ", item.maHd, item.tongTien))
                .setView(form)
                .setPositiveButton("Xác nhận", (d, w) -> {
                    SessionManager session = new SessionManager(requireContext());
                    int maNvThu = session.getMaNv();
                    if (maNvThu <= 0) {
                        reloadFromDb();
                        return;
                    }
                    String ptt = String.valueOf(spPhuongThuc.getSelectedItem());
                    String ngay = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
                    new HoaDonDAO(requireContext()).markPaid(item.maHd, ptt, ngay, maNvThu);
                    reloadFromDb();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
