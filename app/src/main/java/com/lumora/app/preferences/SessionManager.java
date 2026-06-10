package com.lumora.app.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager - Mengelola sesi autentikasi pengguna menggunakan SharedPreferences.
 * Menyimpan status login, profil aktif, dan statistik kuis pengguna.
 */
public class SessionManager {

    private static final String PREF_NAME = "lumora_session_prefs";
    
    // Kunci preferensi (preference keys)
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    
    // Statistik Kuis
    private static final String KEY_QUIZ_COMPLETED = "quiz_completed";
    private static final String KEY_QUIZ_HIGH_SCORE = "quiz_high_score";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    
    // Instance Singleton
    private static SessionManager instance;

    /**
     * Mendapatkan instance singleton dari SessionManager.
     *
     * @param context Context aplikasi
     * @return Instance SessionManager
     */
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Menyimpan sesi login pengguna setelah autentikasi sukses.
     */
    public void login(int userId, String name, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    /**
     * Menghapus sesi login saat logout.
     */
    public void logout() {
        // Hapus detail login tapi tetap pertahankan statistik kuis lokal jika diinginkan,
        // namun untuk pembersihan total, kita bisa hapus semuanya atau pertahankan kuis.
        // Agar statistik kuis per-perangkat tetap tersimpan, kita bisa menghapus hanya detail user
        // atau menghapus seluruhnya. Kita hapus seluruh preferensi agar sesi bersih.
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    // ==========================================
    // MANAJEMEN STATISTIK KUIS
    // ==========================================

    /**
     * Menaikkan jumlah kuis yang diselesaikan sebanyak 1.
     */
    public void incrementQuizCompleted() {
        int current = getQuizCompletedCount();
        editor.putInt(KEY_QUIZ_COMPLETED, current + 1);
        editor.apply();
    }

    /**
     * Mendapatkan total kuis yang telah diselesaikan.
     */
    public int getQuizCompletedCount() {
        return sharedPreferences.getInt(KEY_QUIZ_COMPLETED, 0);
    }

    /**
     * Memperbarui skor kuis tertinggi jika skor baru lebih tinggi.
     *
     * @param score Skor baru yang didapatkan
     */
    public void updateHighScore(int score) {
        int currentHighScore = getHighScore();
        if (score > currentHighScore) {
            editor.putInt(KEY_QUIZ_HIGH_SCORE, score);
            editor.apply();
        }
    }

    /**
     * Mendapatkan skor kuis tertinggi yang pernah dicapai.
     */
    public int getHighScore() {
        return sharedPreferences.getInt(KEY_QUIZ_HIGH_SCORE, 0);
    }
}
