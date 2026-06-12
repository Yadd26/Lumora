package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lumora.app.R;
import com.lumora.app.adapters.BookAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityTutorialDetailBinding;
import com.lumora.app.models.Book;
import com.lumora.app.models.OpenLibraryResponse;
import com.lumora.app.models.QuizCategory;
import com.lumora.app.models.Tutorial;
import com.lumora.app.network.ApiClient;
import com.lumora.app.utils.QuizQuestionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TutorialDetailActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {

    private ActivityTutorialDetailBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private int tutorialId;
    private Tutorial tutorial;
    private BookAdapter bookAdapter;
    private final List<Book> bookList = new ArrayList<>();
    private Call<OpenLibraryResponse> searchCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTutorialDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        if (getIntent() != null) {
            tutorialId = getIntent().getIntExtra("tutorial_id", -1);
        }

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        setupBookRecyclerView();
        loadTutorialDetails();
    }

    private void setupBookRecyclerView() {
        binding.rvTutBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bookAdapter = new BookAdapter(bookList, this);
        binding.rvTutBooks.setAdapter(bookAdapter);
    }

    private void loadTutorialDetails() {
        if (tutorialId == -1) {
            Toast.makeText(this, "Tutorial tidak valid.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        executorService.execute(() -> {
            tutorial = databaseHelper.getTutorialById(1, tutorialId);
            if (tutorial != null) {
                runOnUiThread(() -> {
                    populateUI();
                    fetchRelatedBooks(tutorial.getRelatedBookQuery());
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(TutorialDetailActivity.this, "Gagal memuat tutorial dari database.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void populateUI() {
        binding.textTutTitle.setText(tutorial.getTitle());
        binding.textTutCategory.setText(tutorial.getCategory());
        binding.textTutDifficulty.setText("Tingkat: " + tutorial.getDifficulty());
        binding.textTutTime.setText("Estimasi: " + tutorial.getTimeEstimation());
        binding.textTutDesc.setText(tutorial.getDescription());
        binding.textTutConcepts.setText(tutorial.getConcepts());
        binding.textTutSteps.setText(tutorial.getSteps());
        binding.textTutCode.setText(tutorial.getCodeExample());
        binding.textTutOutput.setText(tutorial.getOutputExample());
        binding.textTutTips.setText(tutorial.getAcademicTips());

        binding.btnTestQuiz.setText("Kuis " + tutorial.getCategory());

        // Button state check
        if ("Selesai".equals(tutorial.getStatus())) {
            binding.btnCompleteTutorial.setText("Tutorial Selesai ✓");
            binding.btnCompleteTutorial.setEnabled(false);
        } else {
            binding.btnCompleteTutorial.setText("Tandai Selesai & Catat Kemajuan");
            binding.btnCompleteTutorial.setEnabled(true);
            binding.btnCompleteTutorial.setOnClickListener(v -> markTutorialAsComplete());
        }

        // Quiz trigger
        binding.btnTestQuiz.setOnClickListener(v -> {
            QuizCategory matchingCat = null;
            for (QuizCategory cat : QuizQuestionProvider.getCategories()) {
                if (cat.getName().equalsIgnoreCase(tutorial.getCategory()) || 
                    (tutorial.getCategory().contains("Mobile") && cat.getName().contains("Mobile"))) {
                    matchingCat = cat;
                    break;
                }
            }

            if (matchingCat != null) {
                Intent intent = new Intent(TutorialDetailActivity.this, QuizDetailActivity.class);
                intent.putExtra(QuizDetailActivity.EXTRA_CATEGORY, matchingCat);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Kuis kategori ini belum tersedia.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRelatedBooks(String query) {
        if (query == null || query.isEmpty()) {
            binding.textNoBooks.setVisibility(View.VISIBLE);
            return;
        }

        binding.progressTutBooks.setVisibility(View.VISIBLE);
        binding.textNoBooks.setVisibility(View.GONE);

        searchCall = ApiClient.getBookApiService().searchBooks(query);
        searchCall.enqueue(new Callback<OpenLibraryResponse>() {
            @Override
            public void onResponse(Call<OpenLibraryResponse> call, Response<OpenLibraryResponse> response) {
                binding.progressTutBooks.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<OpenLibraryResponse.BookDoc> docs = response.body().getDocs();
                    bookList.clear();
                    if (docs != null && !docs.isEmpty()) {
                        for (OpenLibraryResponse.BookDoc doc : docs) {
                            if (bookList.size() >= 5) break; // Limit to 5 related books
                            bookList.add(doc.toBook());
                        }
                        bookAdapter.updateData(bookList);
                        binding.rvTutBooks.setVisibility(View.VISIBLE);
                        binding.textNoBooks.setVisibility(View.GONE);
                    } else {
                        binding.rvTutBooks.setVisibility(View.GONE);
                        binding.textNoBooks.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.rvTutBooks.setVisibility(View.GONE);
                    binding.textNoBooks.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<OpenLibraryResponse> call, Throwable t) {
                // Handle offline or failure case smoothly (displaying nothing or showing cached offline text)
                binding.progressTutBooks.setVisibility(View.GONE);
                binding.rvTutBooks.setVisibility(View.GONE);
                binding.textNoBooks.setText("Koneksi internet lambat / Offline. Referensi tidak termuat.");
                binding.textNoBooks.setVisibility(View.VISIBLE);
            }
        });
    }

    private void markTutorialAsComplete() {
        executorService.execute(() -> {
            databaseHelper.insertOrUpdateTutorialProgress(1, tutorialId, "Selesai", 100);
            
            // Catat ke learning history
            databaseHelper.insertLearningHistory(1, tutorial.getTitle(), tutorial.getCategory(), "TUTORIAL");

            runOnUiThread(() -> {
                Toast.makeText(TutorialDetailActivity.this, "Progres tutorial disimpan!", Toast.LENGTH_SHORT).show();
                binding.btnCompleteTutorial.setText("Tutorial Selesai ✓");
                binding.btnCompleteTutorial.setEnabled(false);
            });
        });
    }

    @Override
    public void onBookClick(Book book) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BOOK_KEY, book.getKey());
        intent.putExtra(DetailActivity.EXTRA_BOOK_TITLE, book.getTitle());
        intent.putExtra(DetailActivity.EXTRA_BOOK_AUTHOR, book.getAuthor());
        intent.putExtra(DetailActivity.EXTRA_BOOK_YEAR, book.getFirstPublishYear());
        intent.putExtra(DetailActivity.EXTRA_BOOK_COVER, book.getCoverUrl());
        intent.putExtra(DetailActivity.EXTRA_BOOK_SUBJECT, book.getSubject());
        intent.putExtra(DetailActivity.EXTRA_BOOK_EDITION_COUNT, book.getEditionCount());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchCall != null) {
            searchCall.cancel();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
