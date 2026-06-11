package com.lumora.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.activities.DetailActivity;
import com.lumora.app.activities.LearningPathDetailActivity;
import com.lumora.app.activities.TutorialDetailActivity;
import com.lumora.app.adapters.BookAdapter;
import com.lumora.app.adapters.ContinueReadingAdapter;
import com.lumora.app.adapters.LearningHistoryAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.FragmentArchiveBinding;
import com.lumora.app.models.Book;
import com.lumora.app.models.BookProgress;
import com.lumora.app.models.LearningHistoryItem;
import com.lumora.app.models.LearningPath;
import com.lumora.app.models.OpenLibraryResponse;
import com.lumora.app.models.Tutorial;
import com.lumora.app.network.ApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HomeFragment - Layar utama pembelajaran yang menampilkan Rekomendasi Buku Hari Ini,
 * Kategori Pembelajaran Interaktif, dan 5 Section Buku Terkini dari Open Library API.
 */
public class ArchiveFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private FragmentArchiveBinding binding;

    // Adapters
    private BookAdapter popularAdapter;
    private BookAdapter programmingAdapter;
    private BookAdapter databaseAdapter;
    private BookAdapter networkingAdapter;
    private BookAdapter aiAdapter;
    private BookAdapter searchAdapter;
    private ContinueReadingAdapter continueReadingAdapter;
    private LearningHistoryAdapter learningHistoryAdapter;

    // Data lists
    private final List<Book> popularBooks = new ArrayList<>();
    private final List<Book> programmingBooks = new ArrayList<>();
    private final List<Book> databaseBooks = new ArrayList<>();
    private final List<Book> networkingBooks = new ArrayList<>();
    private final List<Book> aiBooks = new ArrayList<>();
    private final List<Book> searchBooks = new ArrayList<>();
    private final List<BookProgress> continueReadingList = new ArrayList<>();
    private final List<LearningHistoryItem> learningHistoryList = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    private Book recommendationBook;
    private boolean isSearching = false;
    private Call<OpenLibraryResponse> searchCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentArchiveBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerViews();
        setupSearchView();
        setupCategoryClickListeners();
        setupSwipeRefresh();
        setupCloseSearchButton();
        setupRecommendationClick();
        setupRetryButton();
        setupQuickAccessButtons();

        // Muat semua data beranda
        loadDashboardData();
    }

    /**
     * Bind click listeners for quick access menu buttons.
     */
    private void setupQuickAccessButtons() {
        binding.btnQuickQuiz.setOnClickListener(v -> {
            try {
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.quizFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.btnQuickLearningPath.setOnClickListener(v -> {
            try {
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.learningPathFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.btnQuickTutorials.setOnClickListener(v -> {
            try {
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.tutorialFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.btnQuickResources.setOnClickListener(v -> {
            try {
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.learningResourcesFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Konfigurasi seluruh RecyclerView (horizontal dan vertikal hasil cari).
     */
    private void setupRecyclerViews() {
        // Horizontal list setup helper
        LinearLayoutManager popularLayout = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager progLayout = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager dbLayout = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager netLayout = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager aiLayout = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        popularAdapter = new BookAdapter(popularBooks, this);
        programmingAdapter = new BookAdapter(programmingBooks, this);
        databaseAdapter = new BookAdapter(databaseBooks, this);
        networkingAdapter = new BookAdapter(networkingBooks, this);
        aiAdapter = new BookAdapter(aiBooks, this);

        binding.recyclerPopular.setLayoutManager(popularLayout);
        binding.recyclerPopular.setAdapter(popularAdapter);

        binding.recyclerProgramming.setLayoutManager(progLayout);
        binding.recyclerProgramming.setAdapter(programmingAdapter);

        binding.recyclerDatabase.setLayoutManager(dbLayout);
        binding.recyclerDatabase.setAdapter(databaseAdapter);

        binding.recyclerNetworking.setLayoutManager(netLayout);
        binding.recyclerNetworking.setAdapter(networkingAdapter);

        binding.recyclerAi.setLayoutManager(aiLayout);
        binding.recyclerAi.setAdapter(aiAdapter);

        // Continue Reading setup
        binding.rvContinueReading.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        continueReadingAdapter = new ContinueReadingAdapter(requireContext(), continueReadingList);
        binding.rvContinueReading.setAdapter(continueReadingAdapter);

        // Learning History setup
        binding.rvLearningHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        learningHistoryAdapter = new LearningHistoryAdapter(requireContext(), learningHistoryList);
        binding.rvLearningHistory.setAdapter(learningHistoryAdapter);

        // Vertical Search list setup
        searchAdapter = new BookAdapter(searchBooks, this);
        binding.recyclerSearchList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSearchList.setAdapter(searchAdapter);
    }

    /**
     * Setup Klik Kategori Pembelajaran.
     */
    private void setupCategoryClickListeners() {
        binding.catPemrograman.setOnClickListener(v -> triggerCategorySearch("Programming"));
        binding.catBasisData.setOnClickListener(v -> triggerCategorySearch("Database"));
        binding.catJaringan.setOnClickListener(v -> triggerCategorySearch("Computer Networking"));
        binding.catSistemInformasi.setOnClickListener(v -> triggerCategorySearch("Information Systems"));
        binding.catAi.setOnClickListener(v -> triggerCategorySearch("Artificial Intelligence"));
        binding.catDataScience.setOnClickListener(v -> triggerCategorySearch("Data Science"));
        binding.catSecurity.setOnClickListener(v -> triggerCategorySearch("Cyber Security"));
        binding.catMobile.setOnClickListener(v -> triggerCategorySearch("Mobile Development"));
    }

    private void triggerCategorySearch(String category) {
        binding.searchView.setQuery(category, true);
    }

    /**
     * Setup pencarian SearchView.
     */
    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    performSearch(query.trim());
                }
                binding.searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.trim().length() >= 3) {
                    performSearch(newText.trim());
                } else if (newText == null || newText.trim().isEmpty()) {
                    closeSearchState();
                }
                return true;
            }
        });
    }

    /**
     * Close Search button.
     */
    private void setupCloseSearchButton() {
        binding.btnCloseSearch.setOnClickListener(v -> {
            binding.searchView.setQuery("", false);
            closeSearchState();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (isSearching) {
                String query = binding.searchView.getQuery().toString();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    binding.swipeRefresh.setRefreshing(false);
                }
            } else {
                loadDashboardData();
            }
        });
    }

    private void setupRecommendationClick() {
        binding.cardRecommendation.setOnClickListener(v -> {
            if (recommendationBook != null) {
                onBookClick(recommendationBook);
            }
        });
    }

    private void setupRetryButton() {
        binding.btnRetry.setOnClickListener(v -> loadDashboardData());
    }

    /**
     * Memuat 5 kategori buku untuk dashboard.
     */
    private void loadDashboardData() {
        showLoadingState();

        final int[] loadedCount = {0};
        final boolean[] hasError = {false};

        fetchBooksForSection("popular", popularBooks, popularAdapter, loadedCount, hasError, true);
        fetchBooksForSection("programming", programmingBooks, programmingAdapter, loadedCount, hasError, false);
        fetchBooksForSection("database", databaseBooks, databaseAdapter, loadedCount, hasError, false);
        fetchBooksForSection("networking", networkingBooks, networkingAdapter, loadedCount, hasError, false);
        fetchBooksForSection("artificial intelligence", aiBooks, aiAdapter, loadedCount, hasError, false);
    }

    /**
     * Mengambil data untuk kategori buku tertentu dari Open Library.
     */
    private void fetchBooksForSection(String query, List<Book> targetList, BookAdapter adapter, int[] loadedCount, boolean[] hasError, boolean updateRecommendation) {
        ApiClient.getBookApiService().searchBooks(query).enqueue(new Callback<OpenLibraryResponse>() {
            @Override
            public void onResponse(@NonNull Call<OpenLibraryResponse> call, @NonNull Response<OpenLibraryResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<OpenLibraryResponse.BookDoc> docs = response.body().getDocs();
                    targetList.clear();
                    if (docs != null) {
                        for (int i = 0; i < Math.min(8, docs.size()); i++) {
                            targetList.add(docs.get(i).toBook());
                        }
                    }
                    adapter.updateData(targetList);

                    if (updateRecommendation && !targetList.isEmpty()) {
                        // Ambil buku pertama sebagai rekomendasi hari ini
                        setRecommendation(targetList.get(0));
                    }
                } else {
                    hasError[0] = true;
                }

                checkDashboardLoadProgress(loadedCount, hasError);
            }

            @Override
            public void onFailure(@NonNull Call<OpenLibraryResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                hasError[0] = true;
                checkDashboardLoadProgress(loadedCount, hasError);
            }
        });
    }

    private void checkDashboardLoadProgress(int[] loadedCount, boolean[] hasError) {
        loadedCount[0]++;
        if (loadedCount[0] >= 5) {
            binding.swipeRefresh.setRefreshing(false);
            if (hasError[0] && popularBooks.isEmpty()) {
                showErrorState("Gagal memuat materi belajar. Periksa koneksi Anda.");
            } else {
                showContentState();
            }
        }
    }

    /**
     * Mengatur UI Rekomendasi Hari Ini.
     */
    private void setRecommendation(Book book) {
        recommendationBook = book;
        binding.textRecommendTitle.setText(book.getTitle());
        binding.textRecommendAuthor.setText("Pengarang: " + book.getAuthor());

        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(this)
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.ic_bookmark_outline)
                    .error(R.drawable.ic_bookmark_outline)
                    .into(binding.imageRecommendCover);
        } else {
            binding.imageRecommendCover.setImageResource(R.drawable.ic_bookmark_outline);
        }
    }

    /**
     * Melakukan pencarian buku cerdas menggunakan Open Library.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadProgressAndHistory();
    }

    private void loadProgressAndHistory() {
        if (executorService == null || databaseHelper == null) return;
        executorService.execute(() -> {
            // 1. Load Continue Reading
            android.database.Cursor cursor = databaseHelper.getRecentBookProgress(1);
            List<BookProgress> tempProgress = new ArrayList<>();
            if (cursor != null) {
                try {
                    int keyIdx = cursor.getColumnIndex("book_key");
                    int titleIdx = cursor.getColumnIndex("book_title");
                    int authorIdx = cursor.getColumnIndex("book_author");
                    int coverIdx = cursor.getColumnIndex("book_cover");
                    int chapIdx = cursor.getColumnIndex("current_chapter");
                    int progIdx = cursor.getColumnIndex("progress");

                    while (cursor.moveToNext()) {
                        String key = keyIdx != -1 ? cursor.getString(keyIdx) : "";
                        String title = titleIdx != -1 ? cursor.getString(titleIdx) : "";
                        String author = authorIdx != -1 ? cursor.getString(authorIdx) : "";
                        String cover = coverIdx != -1 ? cursor.getString(coverIdx) : "";
                        int chap = chapIdx != -1 ? cursor.getInt(chapIdx) : 1;
                        int prog = progIdx != -1 ? cursor.getInt(progIdx) : 0;
                        tempProgress.add(new BookProgress(key, title, author, cover, chap, prog));
                    }
                } finally {
                    cursor.close();
                }
            }

            // 2. Load History
            List<LearningHistoryItem> tempHistory = databaseHelper.getLearningHistoryList(1, 5);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    continueReadingList.clear();
                    continueReadingList.addAll(tempProgress);
                    continueReadingAdapter.notifyDataSetChanged();

                    if (continueReadingList.isEmpty()) {
                        binding.layoutContinueReading.setVisibility(View.GONE);
                    } else {
                        binding.layoutContinueReading.setVisibility(View.VISIBLE);
                    }

                    learningHistoryList.clear();
                    learningHistoryList.addAll(tempHistory);
                    learningHistoryAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void searchLocalItems(String query, List<Book> resultsList) {
        // Query local tutorials
        List<Tutorial> tutorials = databaseHelper.getTutorials(1);
        if (tutorials != null) {
            for (Tutorial tut : tutorials) {
                if (tut.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                    tut.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                    tut.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    
                    Book dummy = new Book(
                        "PANDUAN: " + tut.getTitle(),
                        tut.getCategory() + " • " + tut.getDifficulty(),
                        "",
                        tut.getTimeEstimation(),
                        tut.getCategory(),
                        1,
                        "TUTORIAL_" + tut.getId()
                    );
                    resultsList.add(dummy);
                }
            }
        }

        // Query predefined learning paths
        String[] paths = {
            "Pemrograman", "Basis Data", "Software Engineering", 
            "Mobile Development", "Artificial Intelligence", "Cyber Security", "Data Science"
        };
        String[] descriptions = {
            "Kembangkan fondasi pemrograman kuat menggunakan Java, logika algoritma, pilar OOP, dan struktur data.",
            "Pelajari perancangan database relasional, SQL Query, normalisasi skema 1NF/2NF/3NF, indeks, dan ACID.",
            "Pelajari siklus pengembangan perangkat lunak (SDLC), metodologi Agile/Scrum, perancangan arsitektur, clean code, dan testing.",
            "Kuasai Android SDK secara mendalam menggunakan Kotlin, UI, navigation, API Retrofit, local Room, MVVM, dan Compose.",
            "Bangun pemahaman dasar Machine Learning, regresi linear, neural networks, NLP, CV, dan LLM.",
            "Lindungi sistem menggunakan CIA Triad, enkripsi asimetris RSA, malware analysis, firewall, dan pentest.",
            "Eksplorasi statistika deskriptif, analisis data tabular Pandas, manipulasi array NumPy, visualisasi Seaborn."
        };

        for (int i = 0; i < paths.length; i++) {
            String name = paths[i];
            if (name.toLowerCase().contains(query.toLowerCase()) || descriptions[i].toLowerCase().contains(query.toLowerCase())) {
                Book dummy = new Book(
                    "JALUR BELAJAR: " + name,
                    descriptions[i],
                    "",
                    "Kurikulum",
                    name,
                    1,
                    "PATH_" + name
                );
                resultsList.add(dummy);
            }
        }
    }

    /**
     * Melakukan pencarian buku cerdas menggunakan Open Library.
     */
    private void performSearch(String query) {
        isSearching = true;
        showSearchLoadingState();

        binding.textSearchHeader.setText("Hasil untuk \"" + query + "\"");

        if (searchCall != null && !searchCall.isCanceled()) {
            searchCall.cancel();
        }

        // 1. Load local matches immediately
        searchBooks.clear();
        searchLocalItems(query, searchBooks);
        searchAdapter.updateData(searchBooks);
        if (!searchBooks.isEmpty()) {
            showSearchContentState();
        }

        searchCall = ApiClient.getBookApiService().searchBooks(query);
        searchCall.enqueue(new Callback<OpenLibraryResponse>() {
            @Override
            public void onResponse(@NonNull Call<OpenLibraryResponse> call, @NonNull Response<OpenLibraryResponse> response) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<OpenLibraryResponse.BookDoc> docs = response.body().getDocs();
                    if (docs != null) {
                        for (OpenLibraryResponse.BookDoc doc : docs) {
                            Book b = doc.toBook();
                            boolean isDuplicate = false;
                            for (Book existing : searchBooks) {
                                if (existing.getKey() != null && existing.getKey().equals(b.getKey())) {
                                    isDuplicate = true;
                                    break;
                                }
                            }
                            if (!isDuplicate) {
                                searchBooks.add(b);
                            }
                        }
                    }
                    searchAdapter.updateData(searchBooks);

                    if (searchBooks.isEmpty()) {
                        showSearchEmptyState();
                    } else {
                        showSearchContentState();
                    }
                } else {
                    if (searchBooks.isEmpty()) {
                        showSearchErrorState("Kesalahan server saat melakukan pencarian.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<OpenLibraryResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                if (call.isCanceled()) return;

                binding.swipeRefresh.setRefreshing(false);
                if (searchBooks.isEmpty()) {
                    showSearchErrorState("Koneksi gagal. Silakan coba lagi.");
                }
            }
        });
    }

    private void closeSearchState() {
        isSearching = false;
        binding.layoutDashboard.setVisibility(View.VISIBLE);
        binding.layoutSearchResults.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    // ==========================================
    // MANAJEMEN STATE TAMPILAN
    // ==========================================

    private void showLoadingState() {
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutDashboard.setVisibility(View.GONE);
        binding.layoutSearchResults.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showContentState() {
        binding.layoutDashboard.setVisibility(View.VISIBLE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutSearchResults.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showErrorState(String message) {
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setText(message);
        binding.layoutDashboard.setVisibility(View.GONE);
        binding.layoutSearchResults.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    // Search results sub-states
    private void showSearchLoadingState() {
        binding.layoutDashboard.setVisibility(View.GONE);
        binding.layoutSearchResults.setVisibility(View.VISIBLE);
        binding.recyclerSearchList.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showSearchContentState() {
        binding.layoutDashboard.setVisibility(View.GONE);
        binding.layoutSearchResults.setVisibility(View.VISIBLE);
        binding.recyclerSearchList.setVisibility(View.VISIBLE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showSearchEmptyState() {
        binding.layoutDashboard.setVisibility(View.GONE);
        binding.layoutSearchResults.setVisibility(View.VISIBLE);
        binding.recyclerSearchList.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showSearchErrorState(String message) {
        binding.layoutDashboard.setVisibility(View.GONE);
        binding.layoutSearchResults.setVisibility(View.VISIBLE);
        binding.recyclerSearchList.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setText(message);
    }

    // ==========================================
    // PENANGAN KLIK BUKU
    // ==========================================

    @Override
    public void onBookClick(Book book) {
        if (book.getKey() != null && book.getKey().startsWith("TUTORIAL_")) {
            int tutId = Integer.parseInt(book.getKey().substring("TUTORIAL_".length()));
            Intent intent = new Intent(requireContext(), TutorialDetailActivity.class);
            intent.putExtra("tutorial_id", tutId);
            startActivity(intent);
            return;
        }
        if (book.getKey() != null && book.getKey().startsWith("PATH_")) {
            String pathName = book.getKey().substring("PATH_".length());
            LearningPath targetPath = getLearningPathByName(pathName);
            if (targetPath != null) {
                Intent intent = new Intent(requireContext(), LearningPathDetailActivity.class);
                intent.putExtra("learning_path", targetPath);
                startActivity(intent);
            }
            return;
        }

        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BOOK_KEY, book.getKey());
        intent.putExtra(DetailActivity.EXTRA_BOOK_TITLE, book.getTitle());
        intent.putExtra(DetailActivity.EXTRA_BOOK_AUTHOR, book.getAuthor());
        intent.putExtra(DetailActivity.EXTRA_BOOK_YEAR, book.getFirstPublishYear());
        intent.putExtra(DetailActivity.EXTRA_BOOK_COVER, book.getCoverUrl());
        intent.putExtra(DetailActivity.EXTRA_BOOK_SUBJECT, book.getSubject());
        intent.putExtra(DetailActivity.EXTRA_BOOK_EDITION_COUNT, book.getEditionCount());
        startActivity(intent);
    }

    private LearningPath getLearningPathByName(String name) {
        String desc = "";
        List<String> modules = new ArrayList<>();
        if ("Pemrograman".equals(name)) {
            desc = "Kembangkan fondasi pemrograman kuat menggunakan Java, logika algoritma, pilar OOP, dan struktur data.";
            modules = Arrays.asList("Java Dasar & Algoritma", "OOP Java & Pewarisan", "Exception & File Handling", "Collections Framework", "Concurrency & Threading");
        } else if ("Basis Data".equals(name)) {
            desc = "Pelajari perancangan database relasional, SQL Query, normalisasi skema 1NF/2NF/3NF, indeks, dan ACID.";
            modules = Arrays.asList("Skema Relasional & ERD", "Perintah SQL Dasar (DDL/DML)", "Normalisasi Database", "Indexing & Optimasi Query", "Transaksi ACID");
        } else if ("Software Engineering".equals(name)) {
            desc = "Pelajari siklus pengembangan perangkat lunak (SDLC), metodologi Agile/Scrum, perancangan arsitektur, clean code, dan testing.";
            modules = Arrays.asList("Pengenalan SDLC & Agile", "Analisis Kebutuhan", "Perancangan UML & Arsitektur", "Clean Code & Refactoring", "Software Testing & QA", "CI/CD & Deployment");
        } else if ("Mobile Development".equals(name)) {
            desc = "Kuasai Android SDK secara mendalam menggunakan Kotlin, UI, navigation, API Retrofit, local Room, MVVM, dan Compose.";
            modules = Arrays.asList("Kotlin Dasar", "Android Lifecycle", "ViewBinding & XML Layout", "Navigation Component", "Retrofit API Integration", "Local SQLite & Room", "Jetpack Compose", "Arsitektur MVVM", "Testing Aplikasi Mobile");
        } else if ("Artificial Intelligence".equals(name)) {
            desc = "Bangun pemahaman dasar Machine Learning, regresi linear, neural networks, NLP, CV, dan LLM.";
            modules = Arrays.asList("Matematika untuk AI", "Regresi & Klasifikasi", "Jaringan Saraf Tiruan", "Natural Language Processing", "Computer Vision", "Generative AI & LLM");
        } else if ("Cyber Security".equals(name)) {
            desc = "Lindungi sistem menggunakan CIA Triad, enkripsi asimetris RSA, malware analysis, firewall, dan pentest.";
            modules = Arrays.asList("Konsep CIA Triad", "Kriptografi & Enkripsi", "Malware & Trojan Analysis", "Social Engineering", "Network Security & Firewall", "Penetration Testing");
        } else if ("Data Science".equals(name)) {
            desc = "Eksplorasi statistika deskriptif, analisis data tabular Pandas, manipulasi array NumPy, visualisasi Seaborn.";
            modules = Arrays.asList("Statistika Deskriptif", "Python Data Stack", "Data Cleaning Pandas", "Manipulasi Array NumPy", "Visualisasi Data", "Model Prediksi Sederhana");
        }
        if (!desc.isEmpty()) {
            return new LearningPath(name, desc, name, "4 Minggu", modules);
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchCall != null) {
            searchCall.cancel();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        binding = null;
    }
}

