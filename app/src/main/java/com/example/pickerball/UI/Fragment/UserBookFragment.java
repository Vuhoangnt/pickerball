package com.example.pickerball.UI.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickerball.AppConstants;
import com.example.pickerball.Adapter.SlotAdapter;
import com.example.pickerball.DAO.CauHinhGiaDAO;
import com.example.pickerball.DAO.DatSanDAO;
import com.example.pickerball.DAO.DichVuDAO;
import com.example.pickerball.DAO.HeThongDAO;
import com.example.pickerball.DAO.KhungGioDAO;
import com.example.pickerball.DAO.KhachHangDAO;
import com.example.pickerball.DAO.SanDAO;
import com.example.pickerball.DAO.SuDungDvDAO;
import com.example.pickerball.DAO.TaiKhoanDAO;
import com.example.pickerball.DAO.ThongBaoDAO;
import com.example.pickerball.Model.DichVuModel;
import com.example.pickerball.Model.DatSanModel;
import com.example.pickerball.Model.KhungGioModel;
import com.example.pickerball.Model.KhachHangModel;
import com.example.pickerball.Model.TaiKhoanModel;
import com.example.pickerball.Model.SanModel;
import com.example.pickerball.R;
import com.example.pickerball.SessionManager;
import com.example.pickerball.UserMainActivity;
import com.example.pickerball.Adapter.DichVuPickAdapter;
import com.example.pickerball.util.BookingTimeHelper;
import com.example.pickerball.util.CourtPriceCalculator;
import com.example.pickerball.util.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UserBookFragment extends Fragment {

    private Spinner spinnerSan;
    private TextView tvDate;
    private TextView tvPrice;
    private TextView tvSanOnly;
    private TextView tvDvOnly;
    private TextView tvBookingDuration;
    private TextView tvGiaGioRef;
    private TextView tvSanOpenHint;
    private TextView tvBookedSlots;
    private TextView tvSelectedSlotSummary;
    private RecyclerView rvSlotTable;
    private MaterialButton btnToggleSlotTable;
    private MaterialButton btnGioBatDau;
    private MaterialButton btnGioKetThuc;
    private TextInputEditText edtNote;
    private MaterialButton btnBook;
    private Calendar cal = Calendar.getInstance();
    private String ngayDat;
    private List<SanModel> sanList = new ArrayList<>();
    /** Giờ đặt do khách chọn (HH:mm). */
    private String gioBatDau;
    private String gioKetThuc;

    private RecyclerView rvDvPick;
    private List<DichVuModel> dvList = new ArrayList<>();
    private final Set<Integer> selectedDvMaIds = new HashSet<>();
    private DichVuPickAdapter dvAdapter;

    private KhungGioDAO kgDao;
    private DatSanDAO dsDao;
    private CauHinhGiaDAO giaDao;
    private final List<DatSanDAO.BookedRange> bookedRanges = new ArrayList<>();
    private final List<KhungGioModel> slotBlocks = new ArrayList<>();
    private SlotAdapter slotAdapter;
    private boolean slotTableExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        ngayDat = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        spinnerSan = v.findViewById(R.id.spinnerSan);
        tvDate = v.findViewById(R.id.tvDate);
        tvPrice = v.findViewById(R.id.tvPrice);
        tvSanOnly = v.findViewById(R.id.tvSanOnly);
        tvDvOnly = v.findViewById(R.id.tvDvOnly);
        tvBookingDuration = v.findViewById(R.id.tvBookingDuration);
        tvGiaGioRef = v.findViewById(R.id.tvGiaGioRef);
        tvSanOpenHint = v.findViewById(R.id.tvSanOpenHint);
        tvBookedSlots = v.findViewById(R.id.tvBookedSlots);
        tvSelectedSlotSummary = v.findViewById(R.id.tvSelectedSlotSummary);
        rvSlotTable = v.findViewById(R.id.rvSlotTable);
        btnToggleSlotTable = v.findViewById(R.id.btnToggleSlotTable);
        btnGioBatDau = v.findViewById(R.id.btnGioBatDau);
        btnGioKetThuc = v.findViewById(R.id.btnGioKetThuc);
        edtNote = v.findViewById(R.id.edtNote);
        btnBook = v.findViewById(R.id.btnBook);
        MaterialButton btnPickDate = v.findViewById(R.id.btnPickDate);
        rvDvPick = v.findViewById(R.id.rvDvPick);

        tvDate.setText("Ngày: " + ngayDat);
        kgDao = new KhungGioDAO(requireContext());
        dsDao = new DatSanDAO(requireContext());
        giaDao = new CauHinhGiaDAO(requireContext());

        SanDAO sanDAO = new SanDAO(requireContext());
        sanList = sanDAO.getAllForCustomer();
        List<String> labels = new ArrayList<>();
        for (SanModel s : sanList) labels.add(s.tenSan);
        spinnerSan.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, labels));
        spinnerSan.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                onSanChanged();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnPickDate.setOnClickListener(x -> {
            DatePickerDialog dp = new DatePickerDialog(requireContext(), (view, y, m, d) -> {
                cal.set(y, m, d);
                ngayDat = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                tvDate.setText("Ngày: " + ngayDat);
                refreshBookedSlots();
                refreshSlotTable();
                updatePrice();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        btnGioBatDau.setOnClickListener(x -> showTimePicker(true));
        btnGioKetThuc.setOnClickListener(x -> showTimePicker(false));
        btnToggleSlotTable.setOnClickListener(x -> toggleSlotTable());

        btnBook.setOnClickListener(x -> submit());

        loadAllDichVuPickList();

        if (!sanList.isEmpty()) {
            onSanChanged();
        }
    }

    private void onSanChanged() {
        if (sanList.isEmpty() || spinnerSan == null) return;
        int pos = spinnerSan.getSelectedItemPosition();
        if (pos < 0) return;
        SanModel san = sanList.get(pos);
        resetTimesForSan(san);
        refreshBookedSlots();
        refreshSlotTable();
        updateTimeLabels();
        updatePrice();
    }

    /** Gợi ý 1 giờ đầu trong giờ mở cửa. */
    private void resetTimesForSan(SanModel san) {
        gioBatDau = null;
        gioKetThuc = null;
        if (san == null) return;
        String open = san.gioMoCua != null ? san.gioMoCua : "06:00";
        int o = DateUtils.toMinutes(open);
        int c = DateUtils.toMinutes(san.gioDongCua != null ? san.gioDongCua : "22:00");
        if (o < 0 || c <= o) return;
        int end = Math.min(o + 60, c);
        gioBatDau = BookingTimeHelper.normalizeHhMm(open);
        gioKetThuc = BookingTimeHelper.normalizeHhMm(String.format(Locale.US, "%02d:%02d", end / 60, end % 60));
    }

    private void showTimePicker(boolean start) {
        int h = 8;
        int mi = 0;
        String cur = start ? gioBatDau : gioKetThuc;
        if (cur != null) {
            int t = DateUtils.toMinutes(cur);
            if (t >= 0) {
                h = t / 60;
                mi = t % 60;
            }
        }
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(h)
                .setMinute(mi)
                .setTitleText(start ? "Giờ bắt đầu" : "Giờ kết thúc")
                .build();
        picker.show(getParentFragmentManager(), start ? "t_bd" : "t_kt");
        picker.addOnPositiveButtonClickListener(v -> {
            String normalized = String.format(Locale.US, "%02d:%02d", picker.getHour(), picker.getMinute());
            normalized = BookingTimeHelper.normalizeHhMm(normalized);
            if (normalized == null) return;
            if (start) {
                if (isSelectionBlocked(normalized, gioKetThuc)) {
                    Toast.makeText(getContext(), "Khung giờ này đã có lịch, vui lòng chọn giờ khác", Toast.LENGTH_SHORT).show();
                    return;
                }
                gioBatDau = normalized;
            } else {
                if (isSelectionBlocked(gioBatDau, normalized)) {
                    Toast.makeText(getContext(), "Khung giờ này đã có lịch, vui lòng chọn giờ khác", Toast.LENGTH_SHORT).show();
                    return;
                }
                gioKetThuc = normalized;
            }
            updateTimeLabels();
            updatePrice();
        });
    }

    private void refreshBookedSlots() {
        if (tvBookedSlots == null || spinnerSan == null || sanList == null || sanList.isEmpty()) return;
        int pos = spinnerSan.getSelectedItemPosition();
        if (pos < 0 || pos >= sanList.size()) return;
        bookedRanges.clear();
        bookedRanges.addAll(dsDao.listBookedRanges(sanList.get(pos).maSan, ngayDat));
        if (bookedRanges.isEmpty()) {
            tvBookedSlots.setText("Chưa có lịch đặt trong ngày này.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (DatSanDAO.BookedRange r : bookedRanges) {
            sb.append("- ")
                    .append(r.gioBd != null ? r.gioBd : "--:--")
                    .append(" - ")
                    .append(r.gioKt != null ? r.gioKt : "--:--");
            if (r.trangThai != null && !r.trangThai.isEmpty()) {
                sb.append(" (").append(r.trangThai).append(")");
            }
            sb.append('\n');
        }
        tvBookedSlots.setText(sb.toString().trim());
    }

    private void toggleSlotTable() {
        slotTableExpanded = !slotTableExpanded;
        rvSlotTable.setVisibility(slotTableExpanded ? View.VISIBLE : View.GONE);
        if (tvSelectedSlotSummary != null) {
            tvSelectedSlotSummary.setVisibility(slotTableExpanded ? View.VISIBLE : View.GONE);
        }
        btnToggleSlotTable.setText(slotTableExpanded ? "Ẩn bảng khung giờ" : "Xem bảng khung giờ");
        btnToggleSlotTable.setIconResource(
                slotTableExpanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float
        );
    }

    private void refreshSlotTable() {
        if (spinnerSan == null || sanList == null || sanList.isEmpty() || rvSlotTable == null) return;
        int pos = spinnerSan.getSelectedItemPosition();
        if (pos < 0 || pos >= sanList.size()) return;
        SanModel san = sanList.get(pos);

        slotBlocks.clear();
        int openMin = DateUtils.toMinutes(san.gioMoCua != null ? san.gioMoCua : "06:00");
        int closeMin = DateUtils.toMinutes(san.gioDongCua != null ? san.gioDongCua : "22:00");
        if (openMin < 0 || closeMin <= openMin) {
            rvSlotTable.setAdapter(null);
            return;
        }

        for (int t = openMin; t + 60 <= closeMin; t += 60) {
            KhungGioModel k = new KhungGioModel();
            int end = t + 60;
            k.gioBatDau = String.format(Locale.US, "%02d:%02d", t / 60, t % 60);
            k.gioKetThuc = String.format(Locale.US, "%02d:%02d", end / 60, end % 60);
            slotBlocks.add(k);
        }

        boolean[] available = new boolean[slotBlocks.size()];
        for (int i = 0; i < slotBlocks.size(); i++) {
            KhungGioModel slot = slotBlocks.get(i);
            available[i] = !isSelectionBlocked(slot.gioBatDau, slot.gioKetThuc);
        }

        slotAdapter = new SlotAdapter(slotBlocks, available, selectedSlots -> {
            if (selectedSlots == null || selectedSlots.isEmpty()) {
                gioBatDau = null;
                gioKetThuc = null;
                if (tvSelectedSlotSummary != null) {
                    tvSelectedSlotSummary.setText("Đang chọn: chưa có");
                }
                updateTimeLabels();
                updatePrice();
                return;
            }
            int min = Integer.MAX_VALUE;
            int max = -1;
            for (KhungGioModel slot : selectedSlots) {
                int s = DateUtils.toMinutes(slot.gioBatDau);
                int e = DateUtils.toMinutes(slot.gioKetThuc);
                if (s >= 0 && s < min) min = s;
                if (e >= 0 && e > max) max = e;
            }
            if (min >= 0 && max > min) {
                gioBatDau = String.format(Locale.US, "%02d:%02d", min / 60, min % 60);
                gioKetThuc = String.format(Locale.US, "%02d:%02d", max / 60, max % 60);
                if (tvSelectedSlotSummary != null) {
                    tvSelectedSlotSummary.setText(String.format(
                            Locale.getDefault(),
                            "Đang chọn: %s - %s (%d khung)",
                            gioBatDau, gioKetThuc, selectedSlots.size()
                    ));
                }
                updateTimeLabels();
                updatePrice();
            }
        });
        rvSlotTable.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSlotTable.setAdapter(slotAdapter);
        if (tvSelectedSlotSummary != null) {
            tvSelectedSlotSummary.setText("Đang chọn: chưa có");
            tvSelectedSlotSummary.setVisibility(slotTableExpanded ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isSelectionBlocked(String start, String end) {
        int b = DateUtils.toMinutes(start);
        int k = DateUtils.toMinutes(end);
        if (b < 0 || k < 0 || k <= b || bookedRanges.isEmpty()) return false;
        for (DatSanDAO.BookedRange r : bookedRanges) {
            int rb = DateUtils.toMinutes(r.gioBd);
            int rk = DateUtils.toMinutes(r.gioKt);
            if (rb < 0 || rk < 0 || rk <= rb) continue;
            if (b < rk && k > rb) return true;
        }
        return false;
    }

    private void updateTimeLabels() {
        btnGioBatDau.setText(gioBatDau != null ? gioBatDau : "Giờ bắt đầu");
        btnGioKetThuc.setText(gioKetThuc != null ? gioKetThuc : "Giờ kết thúc");
        int mins = BookingTimeHelper.durationMinutes(gioBatDau, gioKetThuc);
        if (mins > 0) {
            tvBookingDuration.setText("Thời lượng: " + CourtPriceCalculator.formatDurationVi(mins));
        } else {
            tvBookingDuration.setText("Chọn giờ bắt đầu và kết thúc (kết thúc sau khi bắt đầu)");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() == null) return;
        SharedPreferences sp = requireActivity().getSharedPreferences(UserMainActivity.PREFS_NAV, Context.MODE_PRIVATE);
        int pre = sp.getInt(UserMainActivity.KEY_PRESELECT_SAN, -1);
        if (pre > 0 && sanList != null && !sanList.isEmpty() && spinnerSan != null) {
            for (int i = 0; i < sanList.size(); i++) {
                if (sanList.get(i).maSan == pre) {
                    spinnerSan.setSelection(i);
                    sp.edit().remove(UserMainActivity.KEY_PRESELECT_SAN).apply();
                    onSanChanged();
                    break;
                }
            }
        }
    }

    private void loadAllDichVuPickList() {
        if (rvDvPick == null) return;
        dvList.clear();
        selectedDvMaIds.clear();
        dvList.addAll(new DichVuDAO(requireContext()).getAll());
        dvAdapter = new DichVuPickAdapter(requireContext(), dvList, selected -> {
            selectedDvMaIds.clear();
            selectedDvMaIds.addAll(selected);
            updatePrice();
        });
        rvDvPick.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDvPick.setAdapter(dvAdapter);
        updatePrice();
    }

    private void updatePrice() {
        if (tvPrice == null || spinnerSan == null) return;
        int pos = spinnerSan.getSelectedItemPosition();
        if (pos < 0) {
            tvPrice.setText("Chọn sân");
            return;
        }
        SanModel san = sanList.get(pos);
        String mo = san.gioMoCua != null ? san.gioMoCua : "06:00";
        String cl = san.gioDongCua != null ? san.gioDongCua : "22:00";
        tvSanOpenHint.setText(String.format(Locale.getDefault(),
                "Giờ hoạt động sân: %s – %s", mo, cl));

        Calendar day = Calendar.getInstance();
        String[] p = ngayDat.split("-");
        day.set(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
        String loaiNgay = DateUtils.loaiNgay(day);

        if (gioBatDau == null || gioKetThuc == null) {
            tvPrice.setText("Chọn giờ bắt đầu và giờ kết thúc");
            tvSanOnly.setText("Tiền sân: —");
            tvDvOnly.setText("Tiền dịch vụ: —");
            tvGiaGioRef.setText("Đơn giá tham chiếu: —");
            return;
        }

        double hourly = CourtPriceCalculator.effectiveHourlyRate(san, loaiNgay, gioBatDau, giaDao, kgDao);
        tvGiaGioRef.setText(String.format(Locale.getDefault(),
                "Đơn giá tham chiếu: %,.0f đ/giờ · loại ngày: %s", hourly, loaiNgay));

        int durMin = BookingTimeHelper.durationMinutes(gioBatDau, gioKetThuc);
        if (durMin <= 0) {
            tvPrice.setText("Giờ kết thúc phải sau giờ bắt đầu");
            tvSanOnly.setText("Tiền sân: —");
            tvDvOnly.setText(String.format(Locale.getDefault(), "Tiền dịch vụ: %,.0f đ", calcDvTotal()));
            return;
        }
        if (isSelectionBlocked(gioBatDau, gioKetThuc)) {
            tvPrice.setText("Khung giờ đã có lịch đặt, vui lòng chọn khung khác");
            tvSanOnly.setText("Tiền sân: —");
            tvDvOnly.setText(String.format(Locale.getDefault(), "Tiền dịch vụ: %,.0f đ", calcDvTotal()));
            return;
        }

        double giaSan = CourtPriceCalculator.computeCourtTotal(san, loaiNgay, gioBatDau, gioKetThuc, giaDao, kgDao);
        double tienDv = calcDvTotal();
        double tong = giaSan + tienDv;

        tvSanOnly.setText(String.format(Locale.getDefault(),
                "Tiền sân: %,.0f đ (%s × %,.0f đ/giờ)",
                giaSan, CourtPriceCalculator.formatDurationVi(durMin), hourly));
        tvDvOnly.setText(String.format(Locale.getDefault(),
                "Tiền dịch vụ: %,.0f đ", tienDv));
        tvPrice.setText(String.format(Locale.getDefault(),
                "Tổng tiền dự kiến: %,.0f đ", tong));
    }

    private double calcDvTotal() {
        double sum = 0;
        if (dvList == null || dvList.isEmpty()) return 0;
        for (DichVuModel dv : dvList) {
            if (selectedDvMaIds.contains(dv.getMaDv())) {
                sum += dv.getGia();
            }
        }
        return sum;
    }

    private void submit() {
        SessionManager session = ((UserMainActivity) requireActivity()).getSession();
        if (sanList.isEmpty()) {
            Toast.makeText(getContext(), "Chưa có sân", Toast.LENGTH_SHORT).show();
            return;
        }
        int pos = spinnerSan.getSelectedItemPosition();
        if (pos < 0) return;
        SanModel san = sanList.get(pos);

        if (gioBatDau == null || gioKetThuc == null) {
            Toast.makeText(getContext(), "Chọn giờ bắt đầu và giờ kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }

        int durMin = BookingTimeHelper.durationMinutes(gioBatDau, gioKetThuc);
        if (durMin <= 0) {
            Toast.makeText(getContext(), "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (durMin < BookingTimeHelper.MIN_BOOKING_MINUTES) {
            Toast.makeText(getContext(),
                    "Thời lượng tối thiểu " + BookingTimeHelper.MIN_BOOKING_MINUTES + " phút",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!BookingTimeHelper.isWithinSanHours(gioBatDau, gioKetThuc, san)) {
            Toast.makeText(getContext(),
                    "Khung giờ phải nằm trong giờ mở cửa sân", Toast.LENGTH_LONG).show();
            return;
        }

        Calendar day = Calendar.getInstance();
        String[] pa = ngayDat.split("-");
        day.set(Integer.parseInt(pa[0]), Integer.parseInt(pa[1]) - 1, Integer.parseInt(pa[2]));
        String loaiNgay = DateUtils.loaiNgay(day);
        double giaSan = CourtPriceCalculator.computeCourtTotal(san, loaiNgay, gioBatDau, gioKetThuc, giaDao, kgDao);

        if (dsDao.hasConflict(san.maSan, ngayDat, gioBatDau, gioKetThuc, null)) {
            Toast.makeText(getContext(), "Khung giờ vừa bị đặt, chọn khác", Toast.LENGTH_SHORT).show();
            return;
        }

        double tienDv = calcDvTotal();
        double tongTien = giaSan + tienDv;

        if (session.isGuest() || session.getMaKh() <= 0) {
            showGuestCheckoutAndBook(session, san, giaSan, tienDv, tongTien);
            return;
        }

        bookForSession(session, san, giaSan, tienDv, tongTien);
    }

    private void showGuestCheckoutAndBook(SessionManager session,
                                          SanModel san,
                                          double giaSan,
                                          double tienDv,
                                          double tongTien) {
        View form = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_guest_checkout, null);
        TextInputEditText edtGuestName = form.findViewById(R.id.edtGuestName);
        TextInputEditText edtGuestPhone = form.findViewById(R.id.edtGuestPhone);
        TextInputEditText edtGuestEmail = form.findViewById(R.id.edtGuestEmail);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thông tin khách")
                .setMessage("Nhập thông tin để tạo tài khoản và đặt sân ngay.")
                .setView(form)
                .setPositiveButton("Đặt ngay", (d, w) -> {
                    String hoTen = edtGuestName.getText() != null ? edtGuestName.getText().toString().trim() : "";
                    String sdt = edtGuestPhone.getText() != null ? edtGuestPhone.getText().toString().trim() : "";
                    String email = edtGuestEmail.getText() != null ? edtGuestEmail.getText().toString().trim() : "";

                    if (hoTen.isEmpty() || sdt.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập họ tên và số điện thoại", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String username = "kh_" + sdt.replaceAll("\\s+", "");
                    String password = "123456";

                    KhachHangDAO khDao = new KhachHangDAO(requireContext());
                    KhachHangModel kh = khDao.getByPhone(sdt);
                    TaiKhoanDAO tkDao = new TaiKhoanDAO(requireContext());
                    TaiKhoanModel tk;

                    try {
                        if (tkDao.usernameExists(username)) {
                            tk = tkDao.getByUsername(username);
                        } else {
                            if (kh == null) {
                                long maTk = tkDao.registerKhach(username, password, hoTen, sdt, email.isEmpty() ? null : email);
                                if (maTk <= 0) {
                                    Toast.makeText(getContext(), "Không tạo được tài khoản khách (trùng?)", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                tk = tkDao.getByMaTk((int) maTk);
                            } else {
                                tk = tkDao.getByMaKh(kh.getMaKh());
                                if (tk == null) {
                                    long maTk = tkDao.createTaiKhoanKhachForMaKh(kh.getMaKh(), username, password, hoTen);
                                    if (maTk <= 0) {
                                        Toast.makeText(getContext(), "Không tạo được tài khoản khách", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    tk = tkDao.getByMaTk((int) maTk);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), "Lỗi tạo tài khoản: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (tk == null || tk.getMaTk() <= 0 || tk.getMaKh() <= 0) {
                        Toast.makeText(getContext(), "Tạo tài khoản thất bại", Toast.LENGTH_LONG).show();
                        return;
                    }

                    session.login(
                            tk.getMaTk(),
                            tk.getTenDangNhap(),
                            AppConstants.ROLE_KHACH,
                            tk.getMaKh() > 0 ? tk.getMaKh() : null,
                            null,
                            tk.getHoTenHienThi() != null ? tk.getHoTenHienThi() : hoTen,
                            tk.getAnhDaiDien()
                    );

                    bookForSession(session, san, giaSan, tienDv, tongTien);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void bookForSession(SessionManager session,
                                  SanModel san,
                                  double giaSan,
                                  double tienDv,
                                  double tongTien) {
        DatSanModel d = new DatSanModel();
        d.maSan = san.maSan;
        d.maKh = session.getMaKh();
        KhungGioModel k = CourtPriceCalculator.findKhungForStart(
                kgDao.getKhungForSan(san), DateUtils.toMinutes(gioBatDau));
        d.maKhung = k != null ? k.maKhung : null;
        d.ngayDat = ngayDat;
        d.thoiGianBatDau = gioBatDau;
        d.thoiGianKetThuc = gioKetThuc;
        d.trangThai = AppConstants.DS_CHO_DUYET;
        d.hinhThuc = "APP";
        d.ghiChu = String.valueOf(edtNote.getText());
        d.tongDuKien = tongTien;
        d.createdAtMs = System.currentTimeMillis();

        long id = dsDao.insert(d);
        int maDat = (int) id;

        SuDungDvDAO sdDao = new SuDungDvDAO(requireContext());
        for (Integer maDv : selectedDvMaIds) {
            sdDao.insert(maDat, maDv, 1);
        }

        int bonus = new HeThongDAO(requireContext()).getInt("DIEM_KHI_DAT_SAN", 5);
        ThongBaoDAO tbDao = new ThongBaoDAO(requireContext());
        tbDao.insert(session.getMaTk(), "Đặt sân", "Yêu cầu đang chờ duyệt.", AppConstants.TB_DAT_SAN, System.currentTimeMillis(), maDat);

        Toast.makeText(getContext(), "Đã gửi yêu cầu đặt sân" + (bonus > 0 ? " (+" + bonus + " điểm khi duyệt)" : ""), Toast.LENGTH_SHORT).show();

        selectedDvMaIds.clear();
        if (dvAdapter != null) dvAdapter.clearSelection();
        edtNote.setText("");
        resetTimesForSan(san);
        refreshBookedSlots();
        refreshSlotTable();
        updateTimeLabels();
        updatePrice();
        ((UserMainActivity) requireActivity()).updateBadge();
    }
}
