package com.lumora.app.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityAchievementGalleryBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AchievementGalleryActivity extends AppCompatActivity {

    private ActivityAchievementGalleryBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAchievementGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Check and update achievements status
        checkAchievements();
    }

    private void checkAchievements() {
        executorService.execute(() -> {
            // 1. Fetch metrics
            int completedTutorials = databaseHelper.countTutorialsCompleted(1);
            int completedPaths = databaseHelper.getLearningPathsCompleted(1);
            int booksRead = databaseHelper.countBooksRead(1);
            int completedQuizzes = databaseHelper.countQuizHistory();

            // Notes and journals count
            int notesCount = 0;
            Cursor notesCursor = null;
            try {
                notesCursor = databaseHelper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM notes WHERE user_id = 1", null);
                if (notesCursor != null && notesCursor.moveToFirst()) {
                    notesCount = notesCursor.getInt(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (notesCursor != null) notesCursor.close();
            }

                        int totalKeeperCount = notesCount;

            // Quiz Champion check
            boolean isQuizChampion = false;
            Cursor quizCursor = null;
            try {
                quizCursor = databaseHelper.getReadableDatabase().rawQuery("SELECT score, total_question FROM quiz_history", null);
                if (quizCursor != null) {
                    while (quizCursor.moveToNext()) {
                        int score = quizCursor.getInt(0);
                        int total = quizCursor.getInt(1);
                        if (total > 0) {
                            int pct = (score * 10) / total; // correct percentage
                            if (pct >= 80) {
                                isQuizChampion = true;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (quizCursor != null) quizCursor.close();
            }

            // Unlock flags
            boolean explorerUnlocked = completedTutorials >= 1;
            boolean scholarUnlocked = completedPaths >= 1;
            boolean readerUnlocked = booksRead >= 1;
            boolean keeperUnlocked = totalKeeperCount >= 3;
            boolean championUnlocked = isQuizChampion;

            // Calculate points to check Grand Archivist
            int earnedCount = 0;
            if (explorerUnlocked) earnedCount++;
            if (scholarUnlocked) earnedCount++;
            if (readerUnlocked) earnedCount++;
            if (keeperUnlocked) earnedCount++;
            if (championUnlocked) earnedCount++;

            int totalPoints = (booksRead * 20) + (completedTutorials * 15) + (completedQuizzes * 10) + (earnedCount * 30);
            boolean archivistUnlocked = totalPoints >= 1000;

            // Save unlocked achievements in SQLite
            if (explorerUnlocked) databaseHelper.insertAchievement(1, "Explorer", "Selesaikan minimal 1 tutorial akademik.");
            if (scholarUnlocked) databaseHelper.insertAchievement(1, "Scholar", "Selesaikan minimal 1 learning path modul.");
            if (readerUnlocked) databaseHelper.insertAchievement(1, "Master Reader", "Mulai membaca minimal 1 buku naskah.");
            if (keeperUnlocked) databaseHelper.insertAchievement(1, "Knowledge Keeper", "Simpan minimal 3 catatan atau jurnal.");
            if (championUnlocked) databaseHelper.insertAchievement(1, "Quiz Champion", "Dapatkan nilai di atas 80% pada kuis.");
            if (archivistUnlocked) databaseHelper.insertAchievement(1, "Grand Archivist", "Akumulasikan total poin scholar sebesar 1000.");

            // Update UI
            final boolean fExplorer = explorerUnlocked;
            final boolean fScholar = scholarUnlocked;
            final boolean fReader = readerUnlocked;
            final boolean fKeeper = keeperUnlocked;
            final boolean fChampion = championUnlocked;
            final boolean fArchivist = archivistUnlocked;

            runOnUiThread(() -> {
                updateBadgeUI(binding.imgAchExplorer, binding.textTitleExplorer, "Explorer", fExplorer);
                updateBadgeUI(binding.imgAchScholar, binding.textTitleScholar, "Scholar", fScholar);
                updateBadgeUI(binding.imgAchReader, binding.textTitleReader, "Master Reader", fReader);
                updateBadgeUI(binding.imgAchKeeper, binding.textTitleKeeper, "Knowledge Keeper", fKeeper);
                updateBadgeUI(binding.imgAchChampion, binding.textTitleChampion, "Quiz Champion", fChampion);
                updateBadgeUI(binding.imgAchArchivist, binding.textTitleArchivist, "Grand Archivist", fArchivist);
            });
        });
    }

    private void updateBadgeUI(ImageView image, TextView title, String badgeName, boolean isUnlocked) {
        if (isUnlocked) {
            image.setAlpha(1.0f);
            image.setColorFilter(Color.parseColor("#C8A165")); // Gold Active
            title.setText(badgeName + " (Aktif)");
            title.setTextColor(Color.parseColor("#3E2723"));
        } else {
            image.setAlpha(0.3f);
            image.setColorFilter(Color.parseColor("#888888")); // Lock gray
            title.setText(badgeName + " (Terkunci)");
            title.setTextColor(Color.parseColor("#8D6E63"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
