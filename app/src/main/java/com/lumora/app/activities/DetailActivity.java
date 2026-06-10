package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityDetailBinding;
import com.lumora.app.models.Book;
import com.lumora.app.models.Bookmark;

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
        } else {
            binding.fabBookmark.setImageResource(R.drawable.ic_bookmark_outline);
        }
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
