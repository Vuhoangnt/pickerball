package com.example.pickerball;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "pickerball_session";
    private static final String K_MA_TK = "ma_tk";
    private static final String K_USERNAME = "username";
    private static final String K_VAI_TRO = "vai_tro";
    private static final String K_MA_KH = "ma_kh";
    private static final String K_MA_NV = "ma_nv";
    private static final String K_HO_TEN = "ho_ten";
    private static final String K_ANH_DAI_DIEN = "anh_dai_dien";

    private final SharedPreferences sp;

    public SessionManager(Context ctx) {
        sp = ctx.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void login(int maTk, String username, String vaiTro, Integer maKh, Integer maNv, String hoTen, String anhDaiDien) {
        SharedPreferences.Editor e = sp.edit();
        e.putInt(K_MA_TK, maTk);
        e.putString(K_USERNAME, username);
        e.putString(K_VAI_TRO, vaiTro);
        if (maKh != null && maKh > 0) e.putInt(K_MA_KH, maKh);
        else e.remove(K_MA_KH);
        if (maNv != null && maNv > 0) e.putInt(K_MA_NV, maNv);
        else e.remove(K_MA_NV);
        e.putString(K_HO_TEN, hoTen != null ? hoTen : username);
        if (anhDaiDien != null && !anhDaiDien.isEmpty()) e.putString(K_ANH_DAI_DIEN, anhDaiDien);
        else e.remove(K_ANH_DAI_DIEN);
        // Commit synchronously to avoid role/session race when opening next Activity immediately.
        e.commit();
    }

    public void setAnhDaiDien(String filename) {
        if (filename != null && !filename.isEmpty()) sp.edit().putString(K_ANH_DAI_DIEN, filename).apply();
        else sp.edit().remove(K_ANH_DAI_DIEN).apply();
    }

    public String getAnhDaiDien() {
        return sp.getString(K_ANH_DAI_DIEN, "");
    }

    public void enterGuestMode() {
        SharedPreferences.Editor e = sp.edit();
        e.putInt(K_MA_TK, 0);
        e.putString(K_USERNAME, "");
        e.putString(K_VAI_TRO, AppConstants.ROLE_GUEST);
        e.remove(K_MA_KH);
        e.remove(K_MA_NV);
        e.putString(K_HO_TEN, "Khách");
        e.remove(K_ANH_DAI_DIEN);
        // Commit synchronously to avoid guest-mode read-before-write on first launch.
        e.commit();
    }

    public void logout() {
        // Commit synchronously to ensure logout state is applied before navigation.
        sp.edit().clear().commit();
    }

    public boolean isLoggedIn() {
        return sp.getInt(K_MA_TK, 0) > 0;
    }

    public boolean isGuest() {
        return AppConstants.ROLE_GUEST.equals(getVaiTro());
    }

    public int getMaTk() {
        return sp.getInt(K_MA_TK, 0);
    }

    public String getUsername() {
        return sp.getString(K_USERNAME, "");
    }

    public String getVaiTro() {
        return sp.getString(K_VAI_TRO, "");
    }

    public int getMaKh() {
        return sp.getInt(K_MA_KH, 0);
    }

    public int getMaNv() {
        return sp.getInt(K_MA_NV, 0);
    }

    public String getHoTen() {
        return sp.getString(K_HO_TEN, "");
    }
}
