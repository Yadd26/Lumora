package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityDetailBinding;
import com.lumora.app.models.Book;
import com.lumora.app.models.Bookmark;

import com.lumora.app.models.QuizCategory;
import com.lumora.app.utils.QuizQuestionProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DetailActivity - Menampilkan konten lengkap dari materi pembelajaran berupa Buku yang dipilih.
 * Mencatat riwayat belajar pengguna secara otomatis dan menghubungkan ke silabus belajar.
 */
public class DetailActivity extends AppCompatActivity {

    // Kunci untuk Intent extra
    public static final String EXTRA_BOOK_KEY = "extra_book_key";
    public static final String EXTRA_BOOK_TITLE = "extra_book_title";
    public static final String EXTRA_BOOK_AUTHOR = "extra_book_author";
    public static final String EXTRA_BOOK_YEAR = "extra_book_year";
    public static final String EXTRA_BOOK_COVER = "extra_book_cover";
    public static final String EXTRA_BOOK_SUBJECT = "extra_book_subject";
    public static final String EXTRA_BOOK_EDITION_COUNT = "extra_book_edition_count";

    private ActivityDetailBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    // Data buku yang diterima via Intent
    private String bookKey;
    private String bookTitle;
    private String bookAuthor;
    private String bookYear;
    private String bookCover;
    private String bookSubject;
    private int bookEditions;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menggelembungkan layout menggunakan ViewBinding
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi database dan thread pool
        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Mengekstrak data dari Intent
        extractIntentData();

        // Menyiapkan komponen UI
        setupToolbar();
        displayContent();
        checkBookmarkStatus();
        setupBookmarkButton();
        setupStartLearningButton();
        setupCurriculumInfo();

        // Simpan otomatis ke riwayat belajar
        saveToLearningHistory();
    }

    /**
     * Mengekstrak data materi yang dikirim dari aktivitas sebelumnya melalui Intent.
     */
    private void extractIntentData() {
        if (getIntent() != null) {
            bookKey = getIntent().getStringExtra(EXTRA_BOOK_KEY);
            bookTitle = getIntent().getStringExtra(EXTRA_BOOK_TITLE);
            bookAuthor = getIntent().getStringExtra(EXTRA_BOOK_AUTHOR);
            bookYear = getIntent().getStringExtra(EXTRA_BOOK_YEAR);
            bookCover = getIntent().getStringExtra(EXTRA_BOOK_COVER);
            bookSubject = getIntent().getStringExtra(EXTRA_BOOK_SUBJECT);
            bookEditions = getIntent().getIntExtra(EXTRA_BOOK_EDITION_COUNT, 1);
        }
    }

    /**
     * Menyiapkan toolbar dengan navigasi kembali.
     */
    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Menampilkan judul materi dan konten teks utama.
     */
    private void displayContent() {
        String title = bookTitle;
        if (title != null && !title.isEmpty()) {
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
        }
        binding.textDetailTitle.setText(title);
        binding.textDetailAuthor.setText("Pengarang: " + (bookAuthor != null ? bookAuthor : "Tidak diketahui"));
        binding.textDetailYear.setText("Tahun Terbit: " + (bookYear != null ? bookYear : "-"));
        binding.textDetailEditions.setText("Jumlah Edisi: " + bookEditions);
        binding.textDetailKey.setText("Kunci Buku: " + (bookKey != null ? bookKey : "-"));
        
        // Atur Chip kategori subjek
        binding.chipCategory.setText(bookSubject != null && !bookSubject.isEmpty() ? bookSubject : "Umum");

        // Memuat cover image menggunakan Glide
        if (bookCover != null && !bookCover.isEmpty()) {
            Glide.with(this)
                    .load(bookCover)
                    .placeholder(R.drawable.ic_bookmark_outline)
                    .error(R.drawable.ic_bookmark_outline)
                    .into(binding.imageDetailCover);
        } else {
            binding.imageDetailCover.setImageResource(R.drawable.ic_bookmark_outline);
        }
    }

    /**
     * Memeriksa apakah materi ini sudah ditandai (bookmarked).
     * Berjalan di background thread untuk menghindari pemblokiran UI.
     */
    private void checkBookmarkStatus() {
        if (bookKey == null) return;
        executorService.execute(() -> {
            isBookmarked = databaseHelper.isBookmarked(bookKey);

            // Perbarui UI di main thread
            runOnUiThread(this::updateBookmarkIcon);
        });
    }

    /**
     * Menyiapkan listener klik untuk tombol bookmark (FAB).
     * Beralih antara keadaan ditandai dan tidak ditandai.
     */
    private void setupBookmarkButton() {
        binding.fabBookmark.setOnClickListener(v -> {
            if (bookKey == null) return;
            if (isBookmarked) {
                // Hapus markah di background thread
                executorService.execute(() -> {
                    databaseHelper.deleteBookmark(bookKey);
                    isBookmarked = false;

                    runOnUiThread(() -> {
                        updateBookmarkIcon();
                        Toast.makeText(this,
                                R.string.bookmark_removed, Toast.LENGTH_SHORT).show();
                    });
                });
            } else {
                // Tambahkan ke markah di background thread
                executorService.execute(() -> {
                    Bookmark bookmark = new Bookmark(bookTitle, bookAuthor, bookYear, bookCover, bookKey, bookSubject, bookEditions);
                    databaseHelper.insertBookmark(bookmark);
                    isBookmarked = true;

                    runOnUiThread(() -> {
                        updateBookmarkIcon();
                        Toast.makeText(this,
                                R.string.bookmark_added, Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });

        // Hubungkan tombol baru ke tombol bookmark lama (FAB)
        binding.btnSaveToCollection.setOnClickListener(v -> binding.fabBookmark.performClick());
    }

    /**
     * Konfigurasi tombol Mulai Belajar menuju LearningInfoActivity.
     */
    private void setupStartLearningButton() {
        binding.btnStartLearning.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, LearningInfoActivity.class);
            intent.putExtra(LearningInfoActivity.EXTRA_BOOK_TITLE, bookTitle);
            intent.putExtra(LearningInfoActivity.EXTRA_BOOK_AUTHOR, bookAuthor);
            intent.putExtra(LearningInfoActivity.EXTRA_BOOK_COVER, bookCover);
            intent.putExtra(LearningInfoActivity.EXTRA_BOOK_SUBJECT, bookSubject);
            startActivity(intent);
        });
    }

    /**
     * Menyimpan informasi akses buku saat ini ke dalam tabel riwayat belajar di SQLite secara asinkron.
     */
    private void saveToLearningHistory() {
        if (bookKey == null) return;
        executorService.execute(() -> {
            Book book = new Book(bookTitle, bookAuthor, bookCover, bookYear, bookSubject, bookEditions, bookKey);
            databaseHelper.insertOrUpdateHistory(book);
        });
    }

    /**
     * Memperbarui ikon bookmark (FAB) berdasarkan status markah saat ini.
     */
    private void updateBookmarkIcon() {
        if (isBookmarked) {
            binding.fabBookmark.setImageResource(R.drawable.ic_bookmark_filled);
            binding.btnSaveToCollection.setText("Hapus dari Koleksi");
            binding.btnSaveToCollection.setIconResource(R.drawable.ic_bookmark_filled);
        } else {
            binding.fabBookmark.setImageResource(R.drawable.ic_bookmark_outline);
            binding.btnSaveToCollection.setText("Simpan ke Koleksi");
            binding.btnSaveToCollection.setIconResource(R.drawable.ic_bookmark_outline);
        }
    }

    /**
     * Konfigurasi informasi kurikulum akademis dan navigasi terkait.
     */
    private void setupCurriculumInfo() {
        TextView tvSubject = binding.textCurriculumSubject;
        TextView tvPrereq = binding.textCurriculumPrereq;
        TextView tvDuration = binding.textCurriculumDuration;

        final String subject = bookSubject != null ? bookSubject : "Umum";
        final int tutorialId;
        final int quizCategoryId;
        final String categoryName;

        String s = subject.toLowerCase();
        if (s.contains("programming") || s.contains("java") || s.contains("pemrograman") || s.contains("kotlin") || s.contains("software")) {
            tvSubject.setText("Subjek Belajar: Pemrograman");
            tvPrereq.setText("Prasyarat: Logika Algoritma");
            tvDuration.setText("Estimasi Durasi: 4 Minggu");
            tutorialId = 1;
            quizCategoryId = 1;
            categoryName = "Pemrograman";
        } else if (s.contains("database") || s.contains("sql") || s.contains("basis data")) {
            tvSubject.setText("Subjek Belajar: Basis Data");
            tvPrereq.setText("Prasyarat: Logika Dasar");
            tvDuration.setText("Estimasi Durasi: 3 Minggu");
            tutorialId = 2;
            quizCategoryId = 2;
            categoryName = "Basis Data";
        } else if (s.contains("network") || s.contains("jaringan") || s.contains("internet")) {
            tvSubject.setText("Subjek Belajar: Jaringan Komputer");
            tvPrereq.setText("Prasyarat: Jaringan Dasar");
            tvDuration.setText("Estimasi Durasi: 3 Minggu");
            tutorialId = 5;
            quizCategoryId = 3;
            categoryName = "Jaringan Komputer";
        } else if (s.contains("mobile") || s.contains("android") || s.contains("ios")) {
            tvSubject.setText("Subjek Belajar: Mobile Development");
            tvPrereq.setText("Prasyarat: Pemrograman Java/Kotlin");
            tvDuration.setText("Estimasi Durasi: 5 Minggu");
            tutorialId = 3;
            quizCategoryId = 4;
            categoryName = "Mobile Development";
        } else if (s.contains("artificial") || s.contains("intelligence") || s.contains("machine") || s.contains("ai")) {
            tvSubject.setText("Subjek Belajar: Artificial Intelligence");
            tvPrereq.setText("Prasyarat: Aljabar & Python");
            tvDuration.setText("Estimasi Durasi: 6 Minggu");
            tutorialId = 4;
            quizCategoryId = 5;
            categoryName = "Artificial Intelligence";
        } else if (s.contains("security") || s.contains("crypt") || s.contains("cyber") || s.contains("kripto")) {
            tvSubject.setText("Subjek Belajar: Cyber Security");
            tvPrereq.setText("Prasyarat: Jaringan Komputer");
            tvDuration.setText("Estimasi Durasi: 4 Minggu");
            tutorialId = 5;
            quizCategoryId = 6;
            categoryName = "Cyber Security";
        } else if (s.contains("science") || s.contains("data") || s.contains("pandas") || s.contains("python")) {
            tvSubject.setText("Subjek Belajar: Data Science");
            tvPrereq.setText("Prasyarat: Python & Statistika");
            tvDuration.setText("Estimasi Durasi: 4 Minggu");
            tutorialId = 6;
            quizCategoryId = 7;
            categoryName = "Data Science";
        } else {
            tvSubject.setText("Subjek Belajar: Umum");
            tvPrereq.setText("Prasyarat: Tidak ada");
            tvDuration.setText("Estimasi Durasi: 2 Minggu");
            tutorialId = 1;
            quizCategoryId = 1;
            categoryName = "Pemrograman";
        }

        binding.btnCurriculumTutorial.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, TutorialDetailActivity.class);
            intent.putExtra("tutorial_id", tutorialId);
            startActivity(intent);
        });

        binding.btnCurriculumQuiz.setOnClickListener(v -> {
            QuizCategory quizCat = QuizQuestionProvider.getCategoryById(quizCategoryId);
            if (quizCat != null) {
                Intent intent = new Intent(DetailActivity.this, QuizDetailActivity.class);
                intent.putExtra(QuizDetailActivity.EXTRA_CATEGORY, quizCat);
                startActivity(intent);
            } else {
                Toast.makeText(DetailActivity.this, "Kategori kuis tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnReadBook.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, ReaderActivity.class);
            intent.putExtra("extra_book_key", bookKey);
            intent.putExtra("extra_book_title", bookTitle);
            intent.putExtra("extra_book_author", bookAuthor);
            intent.putExtra("extra_book_cover", bookCover);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hentikan executor untuk mencegah kebocoran memori (memory leak)
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
