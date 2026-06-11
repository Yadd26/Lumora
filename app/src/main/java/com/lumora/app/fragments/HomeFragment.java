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
import com.lumora.app.adapters.BookAdapter;
import com.lumora.app.databinding.FragmentHomeBinding;
import com.lumora.app.models.Book;
import com.lumora.app.models.OpenLibraryResponse;
import com.lumora.app.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HomeFragment - Layar utama pembelajaran yang menampilkan Rekomendasi Buku Hari Ini,
 * Kategori Pembelajaran Interaktif, dan 5 Section Buku Terkini dari Open Library API.
 */
public class HomeFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private FragmentHomeBinding binding;

    // Adapters
    private BookAdapter popularAdapter;
    private BookAdapter programmingAdapter;
    private BookAdapter databaseAdapter;
    private BookAdapter networkingAdapter;
    private BookAdapter aiAdapter;
    private BookAdapter searchAdapter;

    // Data lists
    private final List<Book> popularBooks = new ArrayList<>();
    private final List<Book> programmingBooks = new ArrayList<>();
    private final List<Book> databaseBooks = new ArrayList<>();
    private final List<Book> networkingBooks = new ArrayList<>();
    private final List<Book> aiBooks = new ArrayList<>();
    private final List<Book> searchBooks = new ArrayList<>();

    private Book recommendationBook;
    private boolean isSearching = false;
    private Call<OpenLibraryResponse> searchCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViews();
        setupSearchView();
        setupCategoryClickListeners();
        setupSwipeRefresh();
        setupCloseSearchButton();
        setupRecommendationClick();
        setupRetryButton();

        // Muat semua data beranda
        loadDashboardData();
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
    private void performSearch(String query) {
        isSearching = true;
        showSearchLoadingState();

        binding.textSearchHeader.setText("Hasil untuk \"" + query + "\"");

        if (searchCall != null && !searchCall.isCanceled()) {
            searchCall.cancel();
        }

        searchCall = ApiClient.getBookApiService().searchBooks(query);
        searchCall.enqueue(new Callback<OpenLibraryResponse>() {
            @Override
            public void onResponse(@NonNull Call<OpenLibraryResponse> call, @NonNull Response<OpenLibraryResponse> response) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<OpenLibraryResponse.BookDoc> docs = response.body().getDocs();
                    searchBooks.clear();
                    if (docs != null) {
                        for (OpenLibraryResponse.BookDoc doc : docs) {
                            searchBooks.add(doc.toBook());
                        }
                    }
                    searchAdapter.updateData(searchBooks);

                    if (searchBooks.isEmpty()) {
                        showSearchEmptyState();
                    } else {
                        showSearchContentState();
                    }
                } else {
                    showSearchErrorState("Kesalahan server saat melakukan pencarian.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OpenLibraryResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                if (call.isCanceled()) return;

                binding.swipeRefresh.setRefreshing(false);
                showSearchErrorState("Koneksi gagal. Silakan coba lagi.");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchCall != null) {
            searchCall.cancel();
        }
        binding = null;
    }
}
