package com.lumora.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.databinding.ItemBookmarkBinding;
import com.lumora.app.models.Bookmark;

import java.util.ArrayList;
import java.util.List;

/**
 * BookmarkAdapter - Adapter RecyclerView untuk menampilkan daftar materi pembelajaran yang disimpan (markah).
 * Mendukung klik untuk membuka detail dan fungsi penghapusan markah.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Bookmark> bookmarks;
    private final OnBookmarkActionListener listener;

    /**
     * Interface untuk menangani aksi pada markah (klik dan hapus).
     */
    public interface OnBookmarkActionListener {
        void onBookmarkClick(Bookmark bookmark);
        void onDeleteBookmark(Bookmark bookmark, int position);
    }

    public BookmarkAdapter(List<Bookmark> bookmarks, OnBookmarkActionListener listener) {
        this.bookmarks = bookmarks != null ? bookmarks : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookmarkBinding binding = ItemBookmarkBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookmarkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        holder.bind(bookmark);
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    /**
     * Memperbarui data adapter dan menyegarkan tampilan RecyclerView.
     *
     * @param newBookmarks Daftar markah baru yang akan ditampilkan
     */
    public void updateData(List<Bookmark> newBookmarks) {
        this.bookmarks = newBookmarks != null ? newBookmarks : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Menghapus item pada posisi tertentu dengan efek animasi.
     *
     * @param position Posisi item yang akan dihapus
     */
    public void removeItem(int position) {
        if (position >= 0 && position < bookmarks.size()) {
            bookmarks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bookmarks.size());
        }
    }

    /**
     * ViewHolder untuk item markah menggunakan ViewBinding.
     */
    class BookmarkViewHolder extends RecyclerView.ViewHolder {

        private final ItemBookmarkBinding binding;

        BookmarkViewHolder(@NonNull ItemBookmarkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Bookmark bookmark) {
            // Mengatur judul dengan huruf pertama kapital
            String title = bookmark.getTitle();
            if (title != null && !title.isEmpty()) {
                title = title.substring(0, 1).toUpperCase() + title.substring(1);
            }
            binding.textBookmarkTitle.setText(title);
            binding.textBookmarkAuthor.setText(bookmark.getAuthor());
            binding.textBookmarkYear.setText("Tahun Terbit: " + bookmark.getYear());

            // Memuat gambar cover buku dengan Glide
            if (bookmark.getCoverUrl() != null && !bookmark.getCoverUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(bookmark.getCoverUrl())
                        .placeholder(R.drawable.ic_bookmark_outline)
                        .error(R.drawable.ic_bookmark_outline)
                        .into(binding.imageBookmarkCover);
            } else {
                binding.imageBookmarkCover.setImageResource(R.drawable.ic_bookmark_outline);
            }

            // Klik kartu - buka detail
            binding.cardBookmark.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookmarkClick(bookmark);
                }
            });

            // Klik tombol hapus
            binding.btnDeleteBookmark.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onDeleteBookmark(bookmark, pos);
                    }
                }
            });
        }
    }
}
