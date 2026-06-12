package com.lumora.app.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityReaderBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReaderActivity extends AppCompatActivity {

    private ActivityReaderBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    private String bookKey;
    private String bookTitle;
    private String bookAuthor;
    private String bookCover;

    private int currentChapter = 1;
    private static final int TOTAL_CHAPTERS = 5;

    // Dummy Chapter Titles & Contents
    private final String[] chapterTitles = {
        "Prolog & Landasan Filosofis",
        "Pilar Utama & Struktur Teori",
        "Metodologi & Implementasi Praktis",
        "Studi Kasus & Analisis Empiris",
        "Epilog & Prospek Masa Depan"
    };

    private final String[] chapterContents = {
        "Dalam keheningan perpustakaan kuno, lembaran pertama naskah ini menyimpan pemikiran orisinil tentang landasan subjek ini. Di sini kita mempelajari fondasi filosofis dan sejarah perkembangan awal. Pengetahuan klasik membimbing kita untuk melihat melampaui apa yang tampak di permukaan, menyusun batu bata logika demi logika.",
        "Melangkah lebih dalam ke bab kedua, naskah kuno ini menjabarkan pilar-pilar konseptual yang saling bertautan. Setiap pilar saling menopang satu sama lain untuk membangun kerangka pemikiran yang kokoh. Memahami relasi internal antar konsep sangat penting sebelum melangkah ke implementasi yang lebih rumit.",
        "Bagian ini membahas penerapan praktis di dunia nyata. Dengan metodologi teruji, konsep-konsep abstrak diwujudkan dalam instansi konkret. Kita melihat bagaimana rancangan teoretis diuji secara langsung oleh tantangan praktis, membuktikan keandalan prinsip akademis yang telah dicanangkan.",
        "Arsip ini mencatat rangkaian eksperimen akademik yang pernah dijalankan demi membuktikan hipotesis awal. Data empiris menunjukkan konsistensi teori yang telah kita diskusikan sebelumnya. Rangkuman data disajikan secara analitis untuk mengidentifikasi margin kesalahan dan efisiensi sistem.",
        "Kita telah mencapai batas lembaran manuskrip klasik ini. Namun, pembelajaran sejati tidak pernah berakhir di sini. Bab penutup ini merangkum prospek masa depan, tantangan baru yang belum terpecahkan, serta mewariskan tongkat estafet pengetahuan kepada generasi akademisi berikutnya."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Extract intent extras
        if (getIntent() != null) {
            bookKey = getIntent().getStringExtra("extra_book_key");
            bookTitle = getIntent().getStringExtra("extra_book_title");
            bookAuthor = getIntent().getStringExtra("extra_book_author");
            bookCover = getIntent().getStringExtra("extra_book_cover");
        }

        binding.textBookTitle.setText(bookTitle != null ? bookTitle : "Buku Digital");
        binding.textBookAuthor.setText(bookAuthor != null ? bookAuthor : "Anonim");

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Load saved progress from db
        loadSavedProgress();

        // Setup Buttons
        binding.btnPrevChapter.setOnClickListener(v -> {
            if (currentChapter > 1) {
                currentChapter--;
                updateChapterUI();
            }
        });

        binding.btnNextChapter.setOnClickListener(v -> {
            if (currentChapter < TOTAL_CHAPTERS) {
                currentChapter++;
                updateChapterUI();
            }
        });

        binding.btnSaveProgress.setOnClickListener(v -> saveProgressToDb(true));
    }

    private void loadSavedProgress() {
        if (bookKey == null) {
            updateChapterUI();
            return;
        }

        executorService.execute(() -> {
            Cursor cursor = databaseHelper.getBookProgress(1, bookKey);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int chapIndex = cursor.getColumnIndex("current_chapter");
                        if (chapIndex != -1) {
                            currentChapter = cursor.getInt(chapIndex);
                            if (currentChapter < 1 || currentChapter > TOTAL_CHAPTERS) {
                                currentChapter = 1;
                            }
                        }
                    }
                } finally {
                    cursor.close();
                }
            }

            runOnUiThread(this::updateChapterUI);
        });
    }

    private void updateChapterUI() {
        binding.textChapterLabel.setText("BAB " + currentChapter);
        binding.textChapterTitle.setText(chapterTitles[currentChapter - 1]);
        binding.textChapterContent.setText(chapterContents[currentChapter - 1]);

        int progressPercent = (currentChapter * 100) / TOTAL_CHAPTERS;
        binding.progressReadingTop.setProgress(progressPercent);
        binding.textProgressPercentage.setText("Halaman " + currentChapter + " dari " + TOTAL_CHAPTERS + " • Progress: " + progressPercent + "%");

        binding.btnPrevChapter.setEnabled(currentChapter > 1);
        binding.btnNextChapter.setEnabled(currentChapter < TOTAL_CHAPTERS);

        // Auto-save silently in background on chapter change
        saveProgressToDb(false);
    }

    private void saveProgressToDb(boolean showToast) {
        if (bookKey == null) return;
        final int chapterToSave = currentChapter;
        final int progressToSave = (currentChapter * 100) / TOTAL_CHAPTERS;

        executorService.execute(() -> {
            databaseHelper.insertOrUpdateBookProgress(1, bookKey, bookTitle, bookAuthor, bookCover, chapterToSave, progressToSave);
            
            // Catat juga ke learning history
            databaseHelper.insertLearningHistory(1, bookTitle, "Membaca Buku", "BOOK");

            if (showToast) {
                runOnUiThread(() -> Toast.makeText(ReaderActivity.this, "Kemajuan membaca berhasil disimpan!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
