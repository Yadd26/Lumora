package com.lumora.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityOfflineLibraryBinding;
import com.lumora.app.models.Tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OfflineLibraryActivity extends AppCompatActivity {

    private ActivityOfflineLibraryBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private OfflineAdapter adapter;

    private int currentTab = 0; // 0: Books, 1: Tutorials, 2: Completed Tutorials

    private final List<OfflineBook> booksList = new ArrayList<>();
    private final List<Tutorial> tutorialsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfflineLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Setup Recycler View
        binding.rvOfflineItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OfflineAdapter();
        binding.rvOfflineItems.setAdapter(adapter);

        // Tab click listeners
        binding.btnTabBooks.setOnClickListener(v -> switchTab(0));
        binding.btnTabTutorials.setOnClickListener(v -> switchTab(1));
        binding.btnTabCompleted.setOnClickListener(v -> switchTab(2));

        // Switch to default tab (Books)
        switchTab(0);
    }

    private void switchTab(int tabIndex) {
        currentTab = tabIndex;

        // Update Tab visual state
        updateTabVisuals();

        // Load data in background
        loadTabData();
    }

    private void updateTabVisuals() {
        setTabActive(binding.btnTabBooks, currentTab == 0);
        setTabActive(binding.btnTabTutorials, currentTab == 1);
        setTabActive(binding.btnTabCompleted, currentTab == 2);
    }

    private void setTabActive(com.google.android.material.button.MaterialButton button, boolean isActive) {
        if (isActive) {
            button.setBackgroundColor(Color.parseColor("#5C3D2E"));
            button.setTextColor(Color.parseColor("#FAF4E8"));
        } else {
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setTextColor(Color.parseColor("#5C3D2E"));
        }
    }

    private void loadTabData() {
        executorService.execute(() -> {
            if (currentTab == 0) {
                // Fetch books from book_progress table
                loadBooksFromDb();
            } else if (currentTab == 1) {
                // Fetch all local tutorials
                loadTutorialsFromDb(false);
            } else {
                // Fetch completed tutorials
                loadTutorialsFromDb(true);
            }
        });
    }

    private void loadBooksFromDb() {
        booksList.clear();
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(
                    "book_progress",
                    null,
                    "user_id = 1",
                    null, null, null,
                    "last_read DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                int keyIdx = cursor.getColumnIndexOrThrow("book_key");
                int titleIdx = cursor.getColumnIndexOrThrow("book_title");
                int authorIdx = cursor.getColumnIndexOrThrow("book_author");
                int coverIdx = cursor.getColumnIndexOrThrow("book_cover");
                int chapIdx = cursor.getColumnIndexOrThrow("current_chapter");
                int progIdx = cursor.getColumnIndexOrThrow("progress");

                do {
                    OfflineBook book = new OfflineBook();
                    book.key = cursor.getString(keyIdx);
                    book.title = cursor.getString(titleIdx);
                    book.author = cursor.getString(authorIdx);
                    book.cover = cursor.getString(coverIdx);
                    book.chapter = cursor.getInt(chapIdx);
                    book.progress = cursor.getInt(progIdx);
                    booksList.add(book);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            checkEmptyState(booksList.isEmpty());
        });
    }

    private void loadTutorialsFromDb(boolean completedOnly) {
        tutorialsList.clear();
        try {
            List<Tutorial> all = databaseHelper.getTutorials(1);
            if (all != null) {
                for (Tutorial tut : all) {
                    if (completedOnly) {
                        if ("Selesai".equalsIgnoreCase(tut.getStatus())) {
                            tutorialsList.add(tut);
                        }
                    } else {
                        tutorialsList.add(tut);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            checkEmptyState(tutorialsList.isEmpty());
        });
    }

    private void checkEmptyState(boolean isEmpty) {
        if (isEmpty) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvOfflineItems.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvOfflineItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data to reflect reading or tutorial changes
        loadTabData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // ==========================================
    // RECYCLER VIEW ADAPTER
    // ==========================================

    private class OfflineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_BOOK = 0;
        private static final int TYPE_TUTORIAL = 1;

        @Override
        public int getItemViewType(int position) {
            return (currentTab == 0) ? TYPE_BOOK : TYPE_TUTORIAL;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_BOOK) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offline_book, parent, false);
                return new BookViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offline_tutorial, parent, false);
                return new TutorialViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_BOOK) {
                OfflineBook book = booksList.get(position);
                BookViewHolder h = (BookViewHolder) holder;

                h.textTitle.setText(book.title);
                h.textAuthor.setText(book.author != null ? book.author : "Anonim");
                h.progressBar.setProgress(book.progress);
                h.textPercent.setText(book.progress + "%");

                if (book.cover != null && !book.cover.isEmpty()) {
                    Glide.with(OfflineLibraryActivity.this)
                            .load(book.cover)
                            .placeholder(R.drawable.ic_open_book)
                            .into(h.imgCover);
                } else {
                    h.imgCover.setImageResource(R.drawable.ic_open_book);
                }

                h.btnOpen.setOnClickListener(v -> {
                    Intent intent = new Intent(OfflineLibraryActivity.this, ReaderActivity.class);
                    intent.putExtra("extra_book_key", book.key);
                    intent.putExtra("extra_book_title", book.title);
                    intent.putExtra("extra_book_author", book.author);
                    intent.putExtra("extra_book_cover", book.cover);
                    startActivity(intent);
                });
            } else {
                Tutorial tut = tutorialsList.get(position);
                TutorialViewHolder h = (TutorialViewHolder) holder;

                h.textTitle.setText(tut.getTitle());
                h.textDesc.setText(tut.getDescription());
                h.textStatus.setText(tut.getStatus());

                // Style badge status
                if ("Selesai".equalsIgnoreCase(tut.getStatus())) {
                    h.textStatus.setBackgroundColor(Color.parseColor("#C8A165"));
                    h.textStatus.setTextColor(Color.parseColor("#FAF4E8"));
                } else if ("Sedang Dipelajari".equalsIgnoreCase(tut.getStatus())) {
                    h.textStatus.setBackgroundColor(Color.parseColor("#FFE082"));
                    h.textStatus.setTextColor(Color.parseColor("#5C3D2E"));
                } else {
                    h.textStatus.setBackgroundColor(Color.parseColor("#EFE5D3"));
                    h.textStatus.setTextColor(Color.parseColor("#8D6E63"));
                }

                String meta = tut.getCategory() + " • " + tut.getDifficulty() + " • " + tut.getTimeEstimation();
                h.textMeta.setText(meta);

                h.btnStudy.setOnClickListener(v -> {
                    // Navigate to TutorialDetailActivity (Juga merekam riwayat & progress)
                    Intent intent = new Intent(OfflineLibraryActivity.this, TutorialDetailActivity.class);
                    intent.putExtra("tutorial_id", tut.getId());
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return (currentTab == 0) ? booksList.size() : tutorialsList.size();
        }

        // ViewHolders
        class BookViewHolder extends RecyclerView.ViewHolder {
            ImageView imgCover;
            TextView textTitle, textAuthor, textPercent;
            ProgressBar progressBar;
            View btnOpen;

            public BookViewHolder(@NonNull View itemView) {
                super(itemView);
                imgCover = itemView.findViewById(R.id.img_book_cover);
                textTitle = itemView.findViewById(R.id.text_book_title);
                textAuthor = itemView.findViewById(R.id.text_book_author);
                textPercent = itemView.findViewById(R.id.text_progress_percent);
                progressBar = itemView.findViewById(R.id.progress_reading);
                btnOpen = itemView.findViewById(R.id.btn_open_book);
            }
        }

        class TutorialViewHolder extends RecyclerView.ViewHolder {
            TextView textTitle, textDesc, textStatus, textMeta;
            View btnStudy;

            public TutorialViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.text_tut_title);
                textDesc = itemView.findViewById(R.id.text_tut_desc);
                textStatus = itemView.findViewById(R.id.text_tut_status);
                textMeta = itemView.findViewById(R.id.text_tut_meta);
                btnStudy = itemView.findViewById(R.id.btn_study_tutorial);
            }
        }
    }

    // Book Progress Data Holder
    private static class OfflineBook {
        String key;
        String title;
        String author;
        String cover;
        int chapter;
        int progress;
    }
}
