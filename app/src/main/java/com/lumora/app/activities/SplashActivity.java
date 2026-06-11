package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.lumora.app.databinding.ActivitySplashBinding;
import com.lumora.app.preferences.PreferenceManager;
import com.lumora.app.preferences.SessionManager;

/**
 * SplashActivity - Aktivitas layar pembuka (splash screen) aplikasi Lumora.
 * Menampilkan logo, nama aplikasi, dan tagline dengan animasi fade-in.
 * Memeriksa status login lokal untuk mengarahkan pengguna ke alur yang tepat.
 */
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private static final int SPLASH_DURATION = 2000; // Durasi splash screen (2 detik)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Terapkan preferensi tema sebelum memuat layout
        applySavedTheme();

        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Jalankan animasi fade-in modern pada container logo & teks
        startFadeInAnimation();

        // Alihkan layar setelah durasi yang ditentukan
        new Handler(Looper.getMainLooper()).postDelayed(this::checkSessionAndRedirect, SPLASH_DURATION);
    }

    /**
     * Menerapkan tema yang disimpan pengguna (terang/gelap) saat startup.
     */
    private void applySavedTheme() {
        PreferenceManager prefManager = PreferenceManager.getInstance(this);
        int themeMode = prefManager.getTheme();

        if (themeMode == PreferenceManager.THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Memulai animasi fade-in asinkron untuk mempercantik transisi masuk logo.
     */
    private void startFadeInAnimation() {
        binding.layoutLogoContainer.setAlpha(0f);
        binding.layoutLogoContainer.animate()
                .alpha(1f)
                .setDuration(1200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Memeriksa sesi login lokal dan mengarahkan pengguna ke aktivitas berikutnya.
     */
    private void checkSessionAndRedirect() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        Intent intent;

        if (sessionManager.isLoggedIn()) {
            // Jika sudah masuk, arahkan langsung ke halaman utama (MainActivity)
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // Jika belum masuk, arahkan ke halaman login (LoginActivity)
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Tutup SplashActivity agar tidak bisa kembali dengan tombol back
    }
}
