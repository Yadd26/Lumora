package com.lumora.app.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PreferenceManager - Mengelola preferensi aplikasi menggunakan SharedPreferences.
 * Menangani pengaturan pengguna seperti nama pengguna (username) dan mode tema.
 */
public class PreferenceManager {

    // Nama file SharedPreferences
    private static final String PREF_NAME = "lumora_prefs";

    // Kunci preferensi (preference keys)
    private static final String KEY_USERNAME = "username";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";

    // Konstanta mode tema
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;

    // Nilai default
    private static final String DEFAULT_USERNAME = "Siswa";
    private static final int DEFAULT_THEME = THEME_LIGHT;

    private final SharedPreferences sharedPreferences;

    // Instance Singleton
    private static PreferenceManager instance;

    /**
     * Mengembalikan instance singleton PreferenceManager.
     *
     * @param context Context aplikasi
     * @return Instance PreferenceManager
     */
    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
        return instance;
    }

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Menyimpan status penyelesaian onboarding.
     *
     * @param completed true jika onboarding selesai, false jika belum
     */
    public void saveOnboardingCompleted(boolean completed) {
        sharedPreferences.edit()
                .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
                .apply();
    }

    /**
     * Mengambil status penyelesaian onboarding.
     *
     * @return true jika onboarding selesai, false jika belum
     */
    public boolean isOnboardingCompleted() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    /**
     * Menyimpan nama pengguna ke SharedPreferences.
     *
     * @param username Nama pengguna yang akan disimpan
     */
    public void saveUsername(String username) {
        sharedPreferences.edit()
                .putString(KEY_USERNAME, username)
                .apply();
    }

    /**
     * Mengambil nama pengguna yang disimpan.
     *
     * @return Nama pengguna yang disimpan, atau "Siswa" sebagai default
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, DEFAULT_USERNAME);
    }

    /**
     * Menyimpan mode tema ke SharedPreferences.
     *
     * @param themeMode THEME_LIGHT (0) atau THEME_DARK (1)
     */
    public void saveTheme(int themeMode) {
        sharedPreferences.edit()
                .putInt(KEY_THEME_MODE, themeMode)
                .apply();
    }

    /**
     * Mengambil mode tema yang disimpan.
     *
     * @return THEME_LIGHT (0) atau THEME_DARK (1)
     */
    public int getTheme() {
        return sharedPreferences.getInt(KEY_THEME_MODE, DEFAULT_THEME);
    }
}
