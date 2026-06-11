package com.lumora.app.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityReaderBinding;
import com.lumora.app.models.Bookmark;
import com.lumora.app.models.Highlight;
import com.lumora.app.models.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    // Time tracking variables
    private long startTime = 0;

    // List of active highlights to draw on screen
    private final List<Highlight> activeHighlights = new ArrayList<>();

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

    private float currentTextSizeSp = 18f;
    private boolean isBookmarked = false;

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

        if (bookKey == null) {
            bookKey = "default_key";
        }

        binding.textBookTitle.setText(bookTitle != null ? bookTitle : "Buku Digital");
        binding.textBookAuthor.setText(bookAuthor != null ? bookAuthor : "Anonim");

        binding.btnBack.setOnClickListener(v -> onBackPressed());

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

        binding.btnAcademicTools.setOnClickListener(v -> showAcademicToolsBottomSheet());

        // Settings Panel Toggle
        binding.btnSettings.setOnClickListener(v -> {
            if (binding.layoutSettingsPanel.getVisibility() == View.VISIBLE) {
                binding.layoutSettingsPanel.setVisibility(View.GONE);
            } else {
                binding.layoutSettingsPanel.setVisibility(View.VISIBLE);
            }
        });

        // Bookmark Toggle
        binding.btnBookmark.setOnClickListener(v -> toggleBookmark());

        // Font Size Adjustments
        binding.btnFontDecrease.setOnClickListener(v -> {
            currentTextSizeSp = Math.max(12f, currentTextSizeSp - 2f);
            binding.textChapterContent.setTextSize(currentTextSizeSp);
        });

        binding.btnFontIncrease.setOnClickListener(v -> {
            currentTextSizeSp = Math.min(30f, currentTextSizeSp + 2f);
            binding.textChapterContent.setTextSize(currentTextSizeSp);
        });

        // Theme Selections
        binding.btnThemeLight.setOnClickListener(v -> applyReaderTheme(0));
        binding.btnThemeSepia.setOnClickListener(v -> applyReaderTheme(1));
        binding.btnThemeDark.setOnClickListener(v -> applyReaderTheme(2));

        // Focus Mode Actions
        binding.btnFocusMode.setOnClickListener(v -> toggleFocusMode(true));
        binding.btnExitFocus.setOnClickListener(v -> toggleFocusMode(false));

        // Load saved progress from db
        loadSavedProgress();
        checkBookmarkStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveReadingTime();
    }

    private void saveReadingTime() {
        if (startTime > 0) {
            long elapsedMs = System.currentTimeMillis() - startTime;
            startTime = 0; // reset
            int minutes = (int) (elapsedMs / 60000);
            if (minutes == 0 && elapsedMs > 5000) {
                minutes = 1; // Minimal 1 menit jika > 5 detik untuk pengujian instan
            }
            if (minutes > 0) {
                final int finalMinutes = minutes;
                executorService.execute(() -> {
                    databaseHelper.addStudyDuration(1, finalMinutes);
                });
            }
        }
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

            // Load highlights from database for this book
            loadHighlightsFromDb();
        });
    }

    private void loadHighlightsFromDb() {
        executorService.execute(() -> {
            List<Highlight> list = databaseHelper.getHighlightsByBook(1, bookKey);
            synchronized (activeHighlights) {
                activeHighlights.clear();
                if (list != null) {
                    activeHighlights.addAll(list);
                }
            }
            runOnUiThread(this::updateChapterUI);
        });
    }

    private void updateChapterUI() {
        binding.textChapterLabel.setText("BAB " + currentChapter);
        binding.textChapterTitle.setText(chapterTitles[currentChapter - 1]);

        String rawContent = chapterContents[currentChapter - 1];
        SpannableStringBuilder ssb = new SpannableStringBuilder(rawContent);

        // Apply highlights
        synchronized (activeHighlights) {
            for (Highlight hl : activeHighlights) {
                String phrase = hl.getSelectedText();
                if (phrase == null || phrase.trim().isEmpty()) continue;

                String lowerContent = rawContent.toLowerCase(Locale.getDefault());
                String lowerPhrase = phrase.toLowerCase(Locale.getDefault());

                int index = lowerContent.indexOf(lowerPhrase);
                while (index >= 0) {
                    int color = Color.parseColor("#FFF2B2"); // Default Yellow
                    if ("Emas".equalsIgnoreCase(hl.getColor())) {
                        color = Color.parseColor("#FFE082");
                    } else if ("Coklat".equalsIgnoreCase(hl.getColor())) {
                        color = Color.parseColor("#D7CCC8");
                    }

                    ssb.setSpan(new BackgroundColorSpan(color), index, index + phrase.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index = lowerContent.indexOf(lowerPhrase, index + 1);
                }
            }
        }

        binding.textChapterContent.setText(ssb);

        int progressPercent = (currentChapter * 100) / TOTAL_CHAPTERS;
        binding.progressReadingTop.setProgress(progressPercent);

        // Calculate estimated reading time
        int wordCount = rawContent.split("\\s+").length;
        double minEst = (double) wordCount / 200.0;
        String estText = minEst < 1 ? "< 1 m" : Math.round(minEst) + " m";

        binding.textProgressPercentage.setText("Halaman " + currentChapter + " dari " + TOTAL_CHAPTERS + " • Progress: " + progressPercent + "% • Est: " + estText);

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
            databaseHelper.insertLearningHistory(1, bookTitle != null ? bookTitle : "Buku Digital", "Membaca Buku", "BOOK");

            if (showToast) {
                runOnUiThread(() -> Toast.makeText(ReaderActivity.this, "Kemajuan membaca berhasil disimpan!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // ==========================================
    // ACADEMIC TOOLS: BOTTOM SHEET & DIALOGS
    // ==========================================

    private void showAcademicToolsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_academic_tools, null);
        bottomSheetDialog.setContentView(sheetView);

        // Find views in bottom sheet
        View btnTabNotes = sheetView.findViewById(R.id.btn_tab_notes);
        View btnTabHighlights = sheetView.findViewById(R.id.btn_tab_highlights);
        View layoutNotesTab = sheetView.findViewById(R.id.layout_notes_tab);
        View layoutHighlightsTab = sheetView.findViewById(R.id.layout_highlights_tab);

        LinearLayout containerNotes = sheetView.findViewById(R.id.container_notes);
        LinearLayout containerHighlights = sheetView.findViewById(R.id.container_highlights);

        EditText editHighlightPhrase = sheetView.findViewById(R.id.edit_highlight_phrase);
        RadioGroup rgHighlightColor = sheetView.findViewById(R.id.rg_highlight_color);
        View btnSaveHighlight = sheetView.findViewById(R.id.btn_save_highlight);
        View btnAddNote = sheetView.findViewById(R.id.btn_add_note);

        // Prefill currently selected text from chapter content if any
        int start = binding.textChapterContent.getSelectionStart();
        int end = binding.textChapterContent.getSelectionEnd();
        if (start != -1 && end != -1 && start != end) {
            CharSequence selectedText = binding.textChapterContent.getText().subSequence(start, end);
            editHighlightPhrase.setText(selectedText.toString().trim());
        }

        // Tab setup
        btnTabNotes.setOnClickListener(v -> {
            layoutNotesTab.setVisibility(View.VISIBLE);
            layoutHighlightsTab.setVisibility(View.GONE);
            btnTabNotes.setBackgroundColor(Color.parseColor("#5C3D2E"));
            ((com.google.android.material.button.MaterialButton) btnTabNotes).setTextColor(Color.parseColor("#FAF4E8"));
            btnTabHighlights.setBackgroundColor(Color.TRANSPARENT);
            ((com.google.android.material.button.MaterialButton) btnTabHighlights).setTextColor(Color.parseColor("#5C3D2E"));
        });

        btnTabHighlights.setOnClickListener(v -> {
            layoutNotesTab.setVisibility(View.GONE);
            layoutHighlightsTab.setVisibility(View.VISIBLE);
            btnTabHighlights.setBackgroundColor(Color.parseColor("#5C3D2E"));
            ((com.google.android.material.button.MaterialButton) btnTabHighlights).setTextColor(Color.parseColor("#FAF4E8"));
            btnTabNotes.setBackgroundColor(Color.TRANSPARENT);
            ((com.google.android.material.button.MaterialButton) btnTabNotes).setTextColor(Color.parseColor("#5C3D2E"));
        });

        // Load Notes & Highlights inside sheet
        refreshNotesInSheet(containerNotes);
        refreshHighlightsInSheet(containerHighlights);

        // Add Note action
        btnAddNote.setOnClickListener(v -> {
            showAddEditNoteDialog(null, () -> refreshNotesInSheet(containerNotes));
        });

        // Save Highlight action
        btnSaveHighlight.setOnClickListener(v -> {
            String phrase = editHighlightPhrase.getText().toString().trim();
            if (phrase.isEmpty()) {
                Toast.makeText(this, "Teks sorotan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = rgHighlightColor.getCheckedRadioButtonId();
            String colorName = "Kuning";
            if (checkedId == R.id.rb_color_gold) {
                colorName = "Emas";
            } else if (checkedId == R.id.rb_color_brown) {
                colorName = "Coklat";
            }

            final String finalColor = colorName;
            executorService.execute(() -> {
                databaseHelper.insertHighlight(1, bookKey, phrase, finalColor);
                // Reload in activity as well
                List<Highlight> list = databaseHelper.getHighlightsByBook(1, bookKey);
                synchronized (activeHighlights) {
                    activeHighlights.clear();
                    activeHighlights.addAll(list);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Sorotan berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    editHighlightPhrase.setText("");
                    refreshHighlightsInSheet(containerHighlights);
                    updateChapterUI(); // Re-apply highlight to text content
                });
            });
        });

        bottomSheetDialog.show();
    }

    private void refreshNotesInSheet(LinearLayout container) {
        container.removeAllViews();
        executorService.execute(() -> {
            List<Note> notes = databaseHelper.getNotesByBook(1, bookKey);
            runOnUiThread(() -> {
                if (notes.isEmpty()) {
                    TextView tv = new TextView(this);
                    tv.setText("Belum ada catatan akademik untuk buku ini.");
                    tv.setTextColor(Color.parseColor("#8D6E63"));
                    tv.setTextSize(13);
                    tv.setPadding(0, 16, 0, 0);
                    container.addView(tv);
                } else {
                    for (Note note : notes) {
                        View item = LayoutInflater.from(this).inflate(R.layout.item_bottom_sheet_note, container, false);
                        TextView textTitle = item.findViewById(R.id.text_note_title);
                        TextView textContent = item.findViewById(R.id.text_note_content);
                        TextView textDate = item.findViewById(R.id.text_note_date);
                        View btnEdit = item.findViewById(R.id.btn_edit_note);
                        View btnDelete = item.findViewById(R.id.btn_delete_note);

                        textTitle.setText(note.getTitle());
                        textContent.setText(note.getContent());
                        textDate.setText(note.getUpdatedAt());

                        btnEdit.setOnClickListener(v -> {
                            showAddEditNoteDialog(note, () -> refreshNotesInSheet(container));
                        });

                        btnDelete.setOnClickListener(v -> {
                            new AlertDialog.Builder(this)
                                .setTitle("Hapus Catatan")
                                .setMessage("Apakah Anda yakin ingin menghapus catatan akademik ini?")
                                .setPositiveButton("Hapus", (d, w) -> {
                                    executorService.execute(() -> {
                                        databaseHelper.deleteNote(note.getId());
                                        runOnUiThread(() -> {
                                            Toast.makeText(this, "Catatan berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                            refreshNotesInSheet(container);
                                        });
                                    });
                                })
                                .setNegativeButton("Batal", null)
                                .show();
                        });

                        container.addView(item);
                    }
                }
            });
        });
    }

    private void refreshHighlightsInSheet(LinearLayout container) {
        container.removeAllViews();
        executorService.execute(() -> {
            List<Highlight> highlights = databaseHelper.getHighlightsByBook(1, bookKey);
            runOnUiThread(() -> {
                if (highlights.isEmpty()) {
                    TextView tv = new TextView(this);
                    tv.setText("Belum ada sorotan teks untuk buku ini.");
                    tv.setTextColor(Color.parseColor("#8D6E63"));
                    tv.setTextSize(13);
                    tv.setPadding(0, 16, 0, 0);
                    container.addView(tv);
                } else {
                    for (Highlight hl : highlights) {
                        View item = LayoutInflater.from(this).inflate(R.layout.item_bottom_sheet_highlight, container, false);
                        View colorIndicator = item.findViewById(R.id.view_color_indicator);
                        TextView textQuote = item.findViewById(R.id.text_highlight_quote);
                        TextView textDate = item.findViewById(R.id.text_highlight_date);
                        View btnDelete = item.findViewById(R.id.btn_delete_highlight);

                        int color = Color.parseColor("#FFF2B2");
                        if ("Emas".equalsIgnoreCase(hl.getColor())) {
                            color = Color.parseColor("#FFE082");
                        } else if ("Coklat".equalsIgnoreCase(hl.getColor())) {
                            color = Color.parseColor("#D7CCC8");
                        }
                        colorIndicator.setBackgroundColor(color);

                        textQuote.setText("\"" + hl.getSelectedText() + "\"");
                        textDate.setText(hl.getCreatedAt() + " • " + hl.getColor());

                        btnDelete.setOnClickListener(v -> {
                            new AlertDialog.Builder(this)
                                .setTitle("Hapus Sorotan")
                                .setMessage("Apakah Anda yakin ingin menghapus sorotan ini?")
                                .setPositiveButton("Hapus", (d, w) -> {
                                    executorService.execute(() -> {
                                        databaseHelper.deleteHighlight(hl.getId());
                                        // Reload in activity as well
                                        List<Highlight> list = databaseHelper.getHighlightsByBook(1, bookKey);
                                        synchronized (activeHighlights) {
                                            activeHighlights.clear();
                                            activeHighlights.addAll(list);
                                        }

                                        runOnUiThread(() -> {
                                            Toast.makeText(this, "Sorotan berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                            refreshHighlightsInSheet(container);
                                            updateChapterUI(); // Re-apply highlight to text content
                                        });
                                    });
                                })
                                .setNegativeButton("Batal", null)
                                .show();
                        });

                        container.addView(item);
                    }
                }
            });
        });
    }

    private void showAddEditNoteDialog(Note existingNote, Runnable onComplete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        builder.setView(dialogView);

        EditText editTitle = dialogView.findViewById(R.id.edit_note_title);
        EditText editContent = dialogView.findViewById(R.id.edit_note_content);
        TextView titleLabel = dialogView.findViewById(R.id.text_dialog_title);

        if (existingNote != null) {
            titleLabel.setText("Edit Catatan Akademik");
            editTitle.setText(existingNote.getTitle());
            editContent.setText(existingNote.getContent());
        } else {
            titleLabel.setText("Tambah Catatan Akademik");
        }

        builder.setPositiveButton("Simpan", (d, w) -> {
            String title = editTitle.getText().toString().trim();
            String content = editContent.getText().toString().trim();
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Judul dan konten tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                if (existingNote != null) {
                    databaseHelper.updateNote(existingNote.getId(), title, content);
                } else {
                    databaseHelper.insertNote(1, bookKey, title, content);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    onComplete.run();
                });
            });
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void checkBookmarkStatus() {
        executorService.execute(() -> {
            isBookmarked = databaseHelper.isBookmarked(bookKey);
            runOnUiThread(() -> {
                if (isBookmarked) {
                    binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                } else {
                    binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_outline);
                }
            });
        });
    }

    private void toggleBookmark() {
        executorService.execute(() -> {
            if (isBookmarked) {
                databaseHelper.deleteBookmark(bookKey);
                isBookmarked = false;
                runOnUiThread(() -> {
                    binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_outline);
                    Toast.makeText(this, "Buku dihapus dari bookmark", Toast.LENGTH_SHORT).show();
                });
            } else {
                Bookmark b = new Bookmark(
                        bookTitle != null ? bookTitle : "Buku Digital",
                        bookAuthor != null ? bookAuthor : "Anonim",
                        "", "", bookKey, "", 0
                );
                databaseHelper.insertBookmark(b);
                isBookmarked = true;
                runOnUiThread(() -> {
                    binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    Toast.makeText(this, "Buku disimpan ke bookmark", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void applyReaderTheme(int themeCode) {
        int pageBg, textCol, headerBg;
        if (themeCode == 1) { // Sepia
            pageBg = Color.parseColor("#F4ECD8");
            textCol = Color.parseColor("#5C4033");
            headerBg = Color.parseColor("#E8DCB8");
        } else if (themeCode == 2) { // Dark
            pageBg = Color.parseColor("#1B1A17");
            textCol = Color.parseColor("#F5EFE6");
            headerBg = Color.parseColor("#2C241C");
        } else { // Light (0)
            pageBg = Color.parseColor("#FAF4E8");
            textCol = Color.parseColor("#3E2723");
            headerBg = Color.parseColor("#EFE5D3");
        }

        binding.layoutReadingArea.setBackgroundColor(pageBg);
        binding.textChapterContent.setTextColor(textCol);
        binding.textChapterTitle.setTextColor(textCol);
        binding.textDividerOrnament.setTextColor(textCol);
        binding.textChapterLabel.setTextColor(textCol);

        binding.layoutHeader.setBackgroundColor(headerBg);
        binding.layoutControlPanel.setBackgroundColor(headerBg);
        binding.layoutSettingsPanel.setBackgroundColor(headerBg);
        binding.textProgressPercentage.setTextColor(textCol);
    }

    private void toggleFocusMode(boolean active) {
        if (active) {
            binding.layoutHeader.setVisibility(View.GONE);
            binding.layoutControlPanel.setVisibility(View.GONE);
            binding.layoutSettingsPanel.setVisibility(View.GONE);
            binding.progressReadingTop.setVisibility(View.GONE);
            binding.btnExitFocus.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHeader.setVisibility(View.VISIBLE);
            binding.layoutControlPanel.setVisibility(View.VISIBLE);
            binding.progressReadingTop.setVisibility(View.VISIBLE);
            binding.btnExitFocus.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void showAcademicNotesDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        
        EditText editTitle = view.findViewById(R.id.edit_note_title);
        EditText editContent = view.findViewById(R.id.edit_note_content);
        View btnSave = view.findViewById(R.id.btn_save_note);
        
        // Load existing note if any
        executorService.execute(() -> {
            List<Note> notes = databaseHelper.getNotesByBook(1, bookKey); // User ID 1 for now
            if (!notes.isEmpty()) {
                Note existingNote = notes.get(0);
                runOnUiThread(() -> {
                    editTitle.setText(existingNote.getTitle());
                    editContent.setText(existingNote.getContent());
                });
            }
        });

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String noteContent = editContent.getText().toString().trim();
            
            if (title.isEmpty()) title = "Catatan Bab " + currentChapter;
            
            final String finalTitle = title;
            executorService.execute(() -> {
                Note note = new Note();
                note.setUserId(1);
                note.setBookKey(bookKey);
                note.setTitle(finalTitle);
                note.setContent(noteContent);
                
                long result = databaseHelper.insertNote(1, bookKey, note.getTitle(), note.getContent());
                runOnUiThread(() -> {
                    if (result != -1) {
                        Toast.makeText(this, "Catatan Akademik disimpan", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
        
        dialog.setContentView(view);
        dialog.show();
    }

}
