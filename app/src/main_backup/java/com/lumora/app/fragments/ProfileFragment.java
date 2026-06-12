package com.lumora.app.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Intent;

import java.util.List;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.lumora.app.R;
import com.lumora.app.activities.LoginActivity;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.FragmentProfileBinding;
import com.lumora.app.models.User;
import com.lumora.app.preferences.PreferenceManager;
import com.lumora.app.preferences.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ProfileFragment - Menampilkan profil pengguna, statistik data SQLite, dan pengaturan aplikasi.
 *
 * Fitur:
 * - Menampilkan nama dan email pengguna aktif dari SessionManager
 * - Menampilkan statistik jumlah markah, diskusi, dan total akun terdaftar
 * - Pengaturan mode gelap (SharedPreferences)
 * - Dialog ubah nama pengguna dengan pembaruan database
 * - Dialog interaktif "Tentang Lumora"
 * - Tombol keluar (logout) dengan pembersihan sesi
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PreferenceManager preferenceManager;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi helper dan pengelola preferensi/sesi
        preferenceManager = PreferenceManager.getInstance(requireContext());
        sessionManager = SessionManager.getInstance(requireContext());
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupProfile();
        setupDarkModeSwitch();
        setupEditUsername();
        setupAboutSection();
        setupLogoutButton();
        setupBookmarksNavigation();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Segarkan data statistik dan informasi pengguna saat halaman kembali aktif
        loadStatistics();
        updateProfileDisplay();
    }

    /**
     * Menyiapkan tampilan profil pengguna.
     */
    private void setupProfile() {
        updateProfileDisplay();
    }

    /**
     * Memperbarui tampilan profil (Nama, Email, dan inisial avatar) berdasarkan sesi aktif.
     */
    private void updateProfileDisplay() {
        String name = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();

        if (name == null || name.isEmpty()) {
            name = getString(R.string.default_username);
        }

        binding.textUsername.setText(name);
        binding.textCurrentUsername.setText(name);
        binding.textEmail.setText(email != null ? email : "");

        // Atur inisial avatar berdasarkan huruf pertama nama pengguna
        binding.textAvatarInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
    }

    private void setupBookmarksNavigation() {
        binding.cardTotalBookmarks.setOnClickListener(v -> {
            try {
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.bookmarkFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Memuat jumlah markah, riwayat dibuka, diskusi, minat utama, dan data kuis selesai dari SQLite secara asinkron.
     */
    private void loadStatistics() {
        executorService.execute(() -> {
            int totalBookmarks = databaseHelper.countBookmarks();
            int totalHistory = databaseHelper.countHistory();
            int totalDiscussions = databaseHelper.countDiscussions();
            String favoriteCategory = databaseHelper.getFavoriteCategory();
            
            // Statistik Kuis dari SQLite
            int totalQuizCompleted = databaseHelper.countQuizHistory();
            int maxQuizScore = databaseHelper.getMaxQuizScore();
            String favoriteQuizCategory = databaseHelper.getFavoriteQuizCategory();

            // Statistik Akademik Baru
            int booksRead = databaseHelper.countBooksRead(1);
            int tutorialsCompleted = databaseHelper.countTutorialsCompleted(1);
            int pathsCompleted = databaseHelper.getLearningPathsCompleted(1);
            int studyStreak = databaseHelper.getStudyStreak(1);

            // Sync and evaluate achievements into SQLite achievements table
            if (booksRead >= 5 && !databaseHelper.hasAchievement(1, "Explorer")) {
                databaseHelper.insertAchievement(1, "Explorer", "Membuka 5 Buku");
            }
            if (tutorialsCompleted >= 5 && !databaseHelper.hasAchievement(1, "Scholar")) {
                databaseHelper.insertAchievement(1, "Scholar", "Menyelesaikan 5 Tutorial");
            }
            if (booksRead >= 10 && !databaseHelper.hasAchievement(1, "Master Reader")) {
                databaseHelper.insertAchievement(1, "Master Reader", "Membaca 10 Buku");
            }
            if (pathsCompleted >= 1 && !databaseHelper.hasAchievement(1, "Knowledge Keeper")) {
                databaseHelper.insertAchievement(1, "Knowledge Keeper", "Menyelesaikan Learning Path");
            }
            if (maxQuizScore >= 90 && !databaseHelper.hasAchievement(1, "Quiz Champion")) {
                databaseHelper.insertAchievement(1, "Quiz Champion", "Mendapat skor kuis 90+");
            }

            // Retrieve list of earned achievements from database
            List<String> earnedAchievements = databaseHelper.getEarnedAchievements(1);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    binding.textTotalBookmarks.setText(String.valueOf(totalBookmarks));
                    binding.textTotalHistory.setText(String.valueOf(totalHistory));
                    binding.textTotalDiscussions.setText(String.valueOf(totalDiscussions));
                    binding.textFavoriteCategory.setText(favoriteCategory);
                    binding.textQuizCompleted.setText(String.valueOf(totalQuizCompleted));
                    binding.textQuizHighScore.setText(String.valueOf(maxQuizScore));
                    binding.textFavoriteQuizCategory.setText(favoriteQuizCategory);

                    binding.textBooksRead.setText(String.valueOf(booksRead));
                    binding.textTutorialsCompleted.setText(String.valueOf(tutorialsCompleted));
                    binding.textPathsCompleted.setText(String.valueOf(pathsCompleted));
                    binding.textStudyStreak.setText(studyStreak + " Hari");

                    updateAchievements(earnedAchievements);
                });
            }
        });
    }

    private void updateAchievements(List<String> earned) {
        int goldColor = Color.parseColor("#C8A165");
        int grayColor = Color.parseColor("#CCCCCC");

        // 1. Explorer (5 Books)
        if (earned.contains("Explorer")) {
            binding.imgAchievementExplorer.setBackgroundTintList(ColorStateList.valueOf(goldColor));
            binding.textStatusExplorer.setText("Terbuka ✓");
            binding.textStatusExplorer.setTextColor(goldColor);
        } else {
            binding.imgAchievementExplorer.setBackgroundTintList(ColorStateList.valueOf(grayColor));
            binding.textStatusExplorer.setText("Terkunci");
            binding.textStatusExplorer.setTextColor(grayColor);
        }

        // 2. Scholar (5 Tutorials)
        if (earned.contains("Scholar")) {
            binding.imgAchievementScholar.setBackgroundTintList(ColorStateList.valueOf(goldColor));
            binding.textStatusScholar.setText("Terbuka ✓");
            binding.textStatusScholar.setTextColor(goldColor);
        } else {
            binding.imgAchievementScholar.setBackgroundTintList(ColorStateList.valueOf(grayColor));
            binding.textStatusScholar.setText("Terkunci");
            binding.textStatusScholar.setTextColor(grayColor);
        }

        // 3. Master Reader (10 Books)
        if (earned.contains("Master Reader")) {
            binding.imgAchievementMaster.setBackgroundTintList(ColorStateList.valueOf(goldColor));
            binding.textStatusMaster.setText("Terbuka ✓");
            binding.textStatusMaster.setTextColor(goldColor);
        } else {
            binding.imgAchievementMaster.setBackgroundTintList(ColorStateList.valueOf(grayColor));
            binding.textStatusMaster.setText("Terkunci");
            binding.textStatusMaster.setTextColor(grayColor);
        }

        // 4. Knowledge Keeper (1 Path)
        if (earned.contains("Knowledge Keeper")) {
            binding.imgAchievementKeeper.setBackgroundTintList(ColorStateList.valueOf(goldColor));
            binding.textStatusKeeper.setText("Terbuka ✓");
            binding.textStatusKeeper.setTextColor(goldColor);
        } else {
            binding.imgAchievementKeeper.setBackgroundTintList(ColorStateList.valueOf(grayColor));
            binding.textStatusKeeper.setText("Terkunci");
            binding.textStatusKeeper.setTextColor(grayColor);
        }

        // 5. Quiz Champion (Skor 90+)
        if (earned.contains("Quiz Champion")) {
            binding.imgAchievementChampion.setBackgroundTintList(ColorStateList.valueOf(goldColor));
            binding.textStatusChampion.setText("Terbuka ✓");
            binding.textStatusChampion.setTextColor(goldColor);
        } else {
            binding.imgAchievementChampion.setBackgroundTintList(ColorStateList.valueOf(grayColor));
            binding.textStatusChampion.setText("Terkunci");
            binding.textStatusChampion.setTextColor(grayColor);
        }
    }

    /**
     * Menyiapkan switch mode gelap menggunakan SharedPreferences.
     */
    private void setupDarkModeSwitch() {
        boolean isDarkMode = preferenceManager.getTheme() == PreferenceManager.THEME_DARK;
        binding.switchDarkMode.setChecked(isDarkMode);

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                preferenceManager.saveTheme(PreferenceManager.THEME_DARK);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                preferenceManager.saveTheme(PreferenceManager.THEME_LIGHT);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    /**
     * Menyiapkan aksi edit nama pengguna.
     */
    private void setupEditUsername() {
        binding.layoutEditUsername.setOnClickListener(v -> showEditUsernameDialog());
    }

    /**
     * Menampilkan dialog untuk mengubah nama pengguna.
     * Mengupdate nama di SharedPreferences (SessionManager) dan tabel users di SQLite.
     */
    private void showEditUsernameDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_username, null);

        TextInputEditText editText = dialogView.findViewById(R.id.edit_username);
        editText.setText(sessionManager.getUserName());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.edit_username)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, (d, which) -> {
                    String newUsername = editText.getText() != null
                            ? editText.getText().toString().trim() : "";

                    if (!newUsername.isEmpty()) {
                        String email = sessionManager.getUserEmail();
                        
                        // Perbarui nama di SharedPreferences (sesi)
                        sessionManager.login(sessionManager.getUserId(), newUsername, email);
                        updateProfileDisplay();

                        // Perbarui nama di SQLite database secara asinkron
                        executorService.execute(() -> {
                            User user = databaseHelper.getUserByEmail(email);
                            if (user != null) {
                                user.setName(newUsername);
                                databaseHelper.updateUser(user);
                            }
                            
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(),
                                            R.string.username_saved, Toast.LENGTH_SHORT).show();
                                    loadStatistics(); // Segarkan data
                                });
                            }
                        });
                    }
                })
                .create();

        dialog.show();
    }

    /**
     * Menyiapkan row "Tentang Lumora" untuk menampilkan dialog informasi aplikasi.
     */
    private void setupAboutSection() {
        binding.layoutAboutLumora.setOnClickListener(v -> showAboutDialog());
    }

    /**
     * Menampilkan Material Design Alert Dialog yang berisi rincian deskripsi aplikasi.
     */
    private void showAboutDialog() {
        String developerName = getString(R.string.developer_name);
        String developerLabel = getString(R.string.developer_label, developerName);
        String appName = getString(R.string.app_name);
        String version = getString(R.string.version);
        String description = getString(R.string.about_description);

        StringBuilder message = new StringBuilder();
        message.append("Nama Aplikasi:\n").append(appName).append("\n\n");
        message.append("Versi:\n").append(version).append("\n\n");
        message.append(developerLabel).append("\n\n");
        message.append("Deskripsi:\n").append(description);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.about_lumora)
                .setMessage(message.toString())
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * Menyiapkan tombol keluar (logout) dan dialog konfirmasinya.
     */
    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Keluar?")
                    .setMessage("Apakah Anda yakin ingin keluar dari akun Anda?")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton("Keluar", (dialog, which) -> performLogout())
                    .show();
        });
    }

    /**
     * Membersihkan sesi pengguna dan mengalihkan kembali ke LoginActivity.
     */
    private void performLogout() {
        sessionManager.logout();
        
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Mencegah kebocoran memori (memory leak)
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
