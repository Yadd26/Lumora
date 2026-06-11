package com.lumora.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lumora.app.R;
import com.lumora.app.activities.DetailActivity;
import com.lumora.app.adapters.BookmarkAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.FragmentBookmarkBinding;
import com.lumora.app.models.Bookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BookmarkFragment - Menampilkan semua materi pembelajaran yang disimpan/ditandai.
 *
 * Fitur:
 * - RecyclerView dengan BookmarkAdapter
 * - Klik untuk membuka detail materi
 * - Menghapus markah dengan konfirmasi dialog
 * - Bekerja sepenuhnya offline (SQLite)
 * - Semua operasi database dilakukan di background thread
 */
public class BookmarkFragment extends Fragment
        implements BookmarkAdapter.OnBookmarkActionListener {

    private FragmentBookmarkBinding binding;
    private BookmarkAdapter bookmarkAdapter;
    private List<Bookmark> bookmarks = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi database dan executor
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Memuat ulang markah setiap kali fragment terlihat
        // untuk merefleksikan perubahan yang dilakukan di DetailActivity
        loadBookmarks();
    }

    /**
     * Inisialisasi RecyclerView dengan LinearLayoutManager dan adapter.
     */
    private void setupRecyclerView() {
        bookmarkAdapter = new BookmarkAdapter(bookmarks, this);
        binding.recyclerBookmarks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBookmarks.setAdapter(bookmarkAdapter);
    }

    /**
     * Memuat semua markah dari SQLite di background thread.
     */
    private void loadBookmarks() {
        executorService.execute(() -> {
            List<Bookmark> loadedBookmarks = databaseHelper.getBookmarks();

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    bookmarks = loadedBookmarks;
                    bookmarkAdapter.updateData(bookmarks);
                    updateEmptyState();
                });
            }
        });
    }

    // ==========================================
    // PENANGAN AKSI MARKAH (BOOKMARK)
    // ==========================================

    @Override
    public void onBookmarkClick(Bookmark bookmark) {
        // Navigasi ke DetailActivity dengan data markah via Intent
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BOOK_KEY, bookmark.getBookKey());
        intent.putExtra(DetailActivity.EXTRA_BOOK_TITLE, bookmark.getTitle());
        intent.putExtra(DetailActivity.EXTRA_BOOK_AUTHOR, bookmark.getAuthor());
        intent.putExtra(DetailActivity.EXTRA_BOOK_YEAR, bookmark.getYear());
        intent.putExtra(DetailActivity.EXTRA_BOOK_COVER, bookmark.getCoverUrl());
        intent.putExtra(DetailActivity.EXTRA_BOOK_SUBJECT, bookmark.getSubject());
        intent.putExtra(DetailActivity.EXTRA_BOOK_EDITION_COUNT, bookmark.getEditionCount());
        startActivity(intent);
    }

    @Override
    public void onDeleteBookmark(Bookmark bookmark, int position) {
        // Tampilkan dialog konfirmasi sebelum menghapus
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_bookmark_title)
                .setMessage(R.string.delete_bookmark_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    deleteBookmark(bookmark, position);
                })
                .show();
    }

    /**
     * Menghapus markah dari database di background thread.
     *
     * @param bookmark Markah yang akan dihapus
     * @param position Posisi adapter untuk animasi
     */
    private void deleteBookmark(Bookmark bookmark, int position) {
        executorService.execute(() -> {
            databaseHelper.deleteBookmark(bookmark.getBookKey());

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    bookmarkAdapter.removeItem(position);
                    Toast.makeText(requireContext(),
                            R.string.bookmark_removed, Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                });
            }
        });
    }

    /**
     * Memperbarui visibilitas tampilan kosong berdasarkan daftar markah.
     */
    private void updateEmptyState() {
        if (bookmarks == null || bookmarks.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.recyclerBookmarks.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.recyclerBookmarks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Mencegah kebocoran memori (memory leak)
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
