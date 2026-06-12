package com.lumora.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lumora.app.R;
import com.lumora.app.adapters.DiscussionAdapter;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.DialogAddDiscussionBinding;
import com.lumora.app.databinding.FragmentDiscussionBinding;
import com.lumora.app.models.Discussion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DiscussionFragment - Mengelola diskusi akademis yang dibuat oleh pengguna.
 *
 * Fitur:
 * - RecyclerView yang menampilkan daftar diskusi dari SQLite
 * - FloatingActionButton untuk menambahkan diskusi baru
 * - Dialog untuk membuat diskusi baru dengan validasi input
 * - Konfirmasi dialog sebelum menghapus diskusi
 * - Semua operasi database berjalan di background thread
 */
public class DiscussionFragment extends Fragment
        implements DiscussionAdapter.OnDiscussionActionListener {

    private FragmentDiscussionBinding binding;
    private DiscussionAdapter discussionAdapter;
    private List<Discussion> discussions = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscussionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi database dan executor
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerView();
        setupFab();

        // Memuat diskusi dari database
        loadDiscussions();
    }

    /**
     * Inisialisasi RecyclerView dengan LinearLayoutManager dan adapter.
     */
    private void setupRecyclerView() {
        discussionAdapter = new DiscussionAdapter(discussions, this);
        binding.recyclerDiscussions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerDiscussions.setAdapter(discussionAdapter);
    }

    /**
     * Menyiapkan FAB untuk menampilkan dialog tambah diskusi.
     */
    private void setupFab() {
        binding.fabAddDiscussion.setOnClickListener(v -> showAddDiscussionDialog());
    }

    /**
     * Memuat semua diskusi dari SQLite di background thread.
     */
    private void loadDiscussions() {
        executorService.execute(() -> {
            List<Discussion> loadedDiscussions = databaseHelper.getDiscussions();

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    discussions = loadedDiscussions;
                    discussionAdapter.updateData(discussions);
                    updateEmptyState();
                });
            }
        });
    }

    /**
     * Menampilkan dialog Material Design untuk membuat diskusi baru.
     * Termasuk validasi input untuk kolom yang wajib diisi.
     */
    private void showAddDiscussionDialog() {
        DialogAddDiscussionBinding dialogBinding = DialogAddDiscussionBinding.inflate(
                LayoutInflater.from(requireContext()));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();

        // Klik tombol Kirim (Post)
        dialogBinding.btnPost.setOnClickListener(v -> {
            String title = dialogBinding.editDiscussionTitle.getText() != null
                    ? dialogBinding.editDiscussionTitle.getText().toString().trim() : "";
            String content = dialogBinding.editDiscussionContent.getText() != null
                    ? dialogBinding.editDiscussionContent.getText().toString().trim() : "";

            // Validasi input
            boolean isValid = true;

            if (title.isEmpty()) {
                dialogBinding.inputLayoutTitle.setError(getString(R.string.field_required));
                isValid = false;
            } else {
                dialogBinding.inputLayoutTitle.setError(null);
            }

            if (content.isEmpty()) {
                dialogBinding.inputLayoutContent.setError(getString(R.string.field_required));
                isValid = false;
            } else {
                dialogBinding.inputLayoutContent.setError(null);
            }

            if (isValid) {
                Discussion newDiscussion = new Discussion(title, content);
                insertDiscussion(newDiscussion);
                dialog.dismiss();
            }
        });

        // Klik tombol Batal (Cancel)
        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Memasukkan diskusi baru ke dalam database di background thread.
     *
     * @param discussion Diskusi yang akan dimasukkan
     */
    private void insertDiscussion(Discussion discussion) {
        executorService.execute(() -> {
            databaseHelper.insertDiscussion(discussion);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            R.string.discussion_posted, Toast.LENGTH_SHORT).show();
                    loadDiscussions(); // Muat ulang untuk mendapatkan ID yang dibuat otomatis
                });
            }
        });
    }

    // ==========================================
    // PENANGAN AKSI DISKUSI
    // ==========================================

    @Override
    public void onDeleteDiscussion(Discussion discussion, int position) {
        // Tampilkan dialog konfirmasi sebelum menghapus
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_discussion_title)
                .setMessage(R.string.delete_discussion_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteDiscussion(discussion, position);
                })
                .show();
    }

    /**
     * Menghapus diskusi dari database di background thread.
     *
     * @param discussion Diskusi yang akan dihapus
     * @param position   Posisi adapter untuk animasi
     */
    private void deleteDiscussion(Discussion discussion, int position) {
        executorService.execute(() -> {
            databaseHelper.deleteDiscussion(discussion.getId());

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    discussionAdapter.removeItem(position);
                    Toast.makeText(requireContext(),
                            R.string.discussion_deleted, Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                });
            }
        });
    }

    /**
     * Memperbarui visibilitas tampilan kosong (empty state) berdasarkan daftar diskusi.
     */
    private void updateEmptyState() {
        if (discussions == null || discussions.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.recyclerDiscussions.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.recyclerDiscussions.setVisibility(View.VISIBLE);
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
