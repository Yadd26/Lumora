package com.lumora.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.lumora.app.R;
import com.lumora.app.databinding.ActivityMainBinding;
import com.lumora.app.preferences.PreferenceManager;

/**
 * MainActivity - Aktivitas peluncur yang menampung Navigation Component
 * dan BottomNavigationView untuk navigasi berbasis fragment.
 *
 * Tanggung Jawab:
 * - Menampung NavHostFragment untuk navigasi fragment
 * - Mengelola BottomNavigationView dengan Navigation Component
 * - Menerapkan tema yang disimpan (terang/gelap) saat startup
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Terapkan tema yang disimpan sebelum super.onCreate untuk menghindari efek berkedip (flickering)
        applyTheme();

        super.onCreate(savedInstanceState);

        // Menggelembungkan (inflate) layout menggunakan ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Menyiapkan Navigation Component
        setupNavigation();
    }

    /**
     * Menerapkan preferensi tema yang disimpan (mode terang atau gelap).
     */
    private void applyTheme() {
        PreferenceManager prefManager = PreferenceManager.getInstance(this);
        int themeMode = prefManager.getTheme();

        if (themeMode == PreferenceManager.THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Menyiapkan Navigation Component dengan BottomNavigationView.
     * Menghubungkan NavController milik NavHostFragment ke navigasi bawah.
     */
    private void setupNavigation() {
        // Mendapatkan NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Menghubungkan BottomNavigationView dengan NavController
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
