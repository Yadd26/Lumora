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

import com.lumora.app.R;
import com.lumora.app.activities.DetailActivity;
import com.lumora.app.adapters.BookAdapter;
import com.lumora.app.adapters.ContinueReadingAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.FragmentHomeBinding;
import com.lumora.app.models.Book;
import com.lumora.app.models.BookProgress;
import com.lumora.app.models.OpenLibraryResponse;
import com.lumora.app.network.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private FragmentHomeBinding binding;

    private BookAdapter popularAdapter;
    private BookAdapter academicAdapter;
    private BookAdapter curatedAdapter;
    private BookAdapter recentAdapter;
    private ContinueReadingAdapter continueReadingAdapter;

    private final List<Book> popularBooks = new ArrayList<>();
    private final List<Book> academicBooks = new ArrayList<>();
    private final List<Book> curatedBooks = new ArrayList<>();
    private final List<Book> recentBooks = new ArrayList<>();
    private final List<BookProgress> continueReadingList = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private boolean isSearching = false;

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

        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerViews();
        setupSearchView();
        setupSwipeRefresh();

        loadDashboardData();
    }

    private void setupRecyclerViews() {
        popularAdapter = new BookAdapter(popularBooks, this);
        binding.rvPopularBooks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopularBooks.setAdapter(popularAdapter);

        academicAdapter = new BookAdapter(academicBooks, this);
        binding.rvAcademicReferences.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAcademicReferences.setAdapter(academicAdapter);

        curatedAdapter = new BookAdapter(curatedBooks, this);
        binding.rvCuratedCollections.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCuratedCollections.setAdapter(curatedAdapter);

        recentAdapter = new BookAdapter(recentBooks, this);
        binding.rvRecentlyAdded.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecentlyAdded.setAdapter(recentAdapter);

        continueReadingAdapter = new ContinueReadingAdapter(requireContext(), continueReadingList);
        binding.rvContinueReading.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvContinueReading.setAdapter(continueReadingAdapter);
    }

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
                return true;
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.tertiary);
        binding.swipeRefresh.setOnRefreshListener(this::loadDashboardData);
    }

    private void loadDashboardData() {
        binding.swipeRefresh.setRefreshing(true);
        fetchBooksForSection("popular", popularBooks, popularAdapter);
        fetchBooksForSection("science", academicBooks, academicAdapter);
        fetchBooksForSection("history", curatedBooks, curatedAdapter);
        fetchBooksForSection("programming", recentBooks, recentAdapter);
        
        loadProgress();
    }

    private void fetchBooksForSection(String query, List<Book> targetList, BookAdapter adapter) {
        ApiClient.getBookApiService().searchBooks(query).enqueue(new Callback<OpenLibraryResponse>() {
            @Override
            public void onResponse(@NonNull Call<OpenLibraryResponse> call, @NonNull Response<OpenLibraryResponse> response) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<OpenLibraryResponse.BookDoc> docs = response.body().getDocs();
                    targetList.clear();
                    if (docs != null) {
                        for (int i = 0; i < Math.min(8, docs.size()); i++) {
                            targetList.add(docs.get(i).toBook());
                        }
                    }
                    adapter.updateData(targetList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<OpenLibraryResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void performSearch(String query) {
        isSearching = true;
        binding.swipeRefresh.setRefreshing(true);
        fetchBooksForSection(query, popularBooks, popularAdapter);
        binding.rvAcademicReferences.setVisibility(View.GONE);
        binding.rvCuratedCollections.setVisibility(View.GONE);
        binding.rvRecentlyAdded.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProgress();
    }

    private void loadProgress() {
        if (executorService == null || databaseHelper == null) return;
        executorService.execute(() -> {
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

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding != null) {
                        continueReadingList.clear();
                        continueReadingList.addAll(tempProgress);
                        continueReadingAdapter.notifyDataSetChanged();

                        if (continueReadingList.isEmpty()) {
                            binding.layoutContinueReading.setVisibility(View.GONE);
                        } else {
                            binding.layoutContinueReading.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

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
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        binding = null;
    }
}
