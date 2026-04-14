package com.example.pickerball;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickerball.DAO.TaiKhoanDAO;
import com.example.pickerball.Model.TaiKhoanModel;
import com.example.pickerball.util.UiWindowHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_LOGIN = "extra_show_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        SessionManager sm = new SessionManager(this);
        if (sm.isGuest()) {
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
            return;
        }
        if (sm.isLoggedIn()) {
            goByRole(sm.getVaiTro());
            finish();
            return;
        }
        if (!getIntent().getBooleanExtra(EXTRA_SHOW_LOGIN, false)) {
            sm.enterGuestMode();
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        UiWindowHelper.install(this);
        View loginHero = findViewById(R.id.loginHeroBg);
        if (loginHero != null) {
            ThemeHelper.applyAppBarGradient(loginHero);
        }

        TextInputEditText edtUser = findViewById(R.id.edtUser);
        TextInputEditText edtPass = findViewById(R.id.edtPass);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        MaterialButton btnGuest = findViewById(R.id.btnGuest);

        if (btnGuest != null) {
            btnGuest.setOnClickListener(v -> {
                sm.enterGuestMode();
                startActivity(new Intent(this, UserMainActivity.class));
                finish();
            });
        }

        btnLogin.setOnClickListener(v -> {
            String u = String.valueOf(edtUser.getText()).trim();
            String p = String.valueOf(edtPass.getText());
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Nhập đủ tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            TaiKhoanModel m = new TaiKhoanDAO(this).login(u, p);
            if (m == null) {
                Toast.makeText(this, "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }
            sm.login(
                    m.getMaTk(),
                    m.getTenDangNhap(),
                    m.getVaiTro(),
                    m.getMaKh() > 0 ? m.getMaKh() : null,
                    m.getMaNv() > 0 ? m.getMaNv() : null,
                    m.getHoTenHienThi() != null ? m.getHoTenHienThi() : m.getTenDangNhap(),
                    m.getAnhDaiDien());
            goByRole(m.getVaiTro());
            finish();
        });

        btnRegister.setOnClickListener(v -> showRegisterDialog());
    }

    private void goByRole(String vaiTro) {
        Intent i;
        if (AppConstants.ROLE_ADMIN.equals(vaiTro)) {
            i = new Intent(this, AdminMainActivity.class);
        } else if (AppConstants.ROLE_NHAN_VIEN.equals(vaiTro)) {
            i = new Intent(this, StaffMainActivity.class);
        } else {
            i = new Intent(this, UserMainActivity.class);
        }
        startActivity(i);
    }

    private void showRegisterDialog() {
        View form = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        TextInputEditText edtU = form.findViewById(R.id.reg_user);
        TextInputEditText edtP = form.findViewById(R.id.reg_pass);
        TextInputEditText edtTen = form.findViewById(R.id.reg_name);
        TextInputEditText edtSdt = form.findViewById(R.id.reg_sdt);
        TextInputEditText edtMail = form.findViewById(R.id.reg_email);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Đăng ký khách hàng")
                .setMessage("Tạo tài khoản để đặt sân và tích điểm.")
                .setView(form)
                .setPositiveButton("Đăng ký", (d, w) -> {
                    String u = String.valueOf(edtU.getText()).trim();
                    String p = String.valueOf(edtP.getText());
                    String ten = String.valueOf(edtTen.getText()).trim();
                    String sdt = String.valueOf(edtSdt.getText()).trim();
                    String mail = String.valueOf(edtMail.getText()).trim();
                    if (u.isEmpty() || p.isEmpty() || ten.isEmpty() || sdt.isEmpty()) {
                        Toast.makeText(this, "Điền đủ các trường bắt buộc", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TaiKhoanDAO dao = new TaiKhoanDAO(this);
                    if (dao.usernameExists(u)) {
                        Toast.makeText(this, "Username đã tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        dao.registerKhach(u, p, ten, sdt, mail.isEmpty() ? null : mail);
                        TaiKhoanModel m = new TaiKhoanDAO(this).login(u, p);
                        if (m != null) {
                            new SessionManager(this).login(
                                    m.getMaTk(), m.getTenDangNhap(), m.getVaiTro(),
                                    m.getMaKh() > 0 ? m.getMaKh() : null,
                                    null,
                                    m.getHoTenHienThi() != null ? m.getHoTenHienThi() : ten,
                                    null);
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, UserMainActivity.class));
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
