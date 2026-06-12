package com.lumora.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.databinding.ItemMaterialBinding;
import com.lumora.app.models.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * BookAdapter - Adapter RecyclerView untuk menampilkan daftar Buku dari Open Library API.
 * Menggunakan Glide untuk memuat gambar cover secara asinkron.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;
    private final OnBookClickListener listener;

    /**
     * Interface untuk menangani klik item buku.
     */
    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(List<Book> books, OnBookClickListener listener) {
        this.books = books != null ? books : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMaterialBinding binding = ItemMaterialBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    /**
     * Memperbarui data buku di adapter dan menyegarkan RecyclerView.
     */
    public void updateData(List<Book> newBooks) {
        this.books = newBooks != null ? newBooks : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder untuk item buku menggunakan ViewBinding.
     */
    class BookViewHolder extends RecyclerView.ViewHolder {

        private final ItemMaterialBinding binding;

        BookViewHolder(@NonNull ItemMaterialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Book book) {
            binding.textBookTitle.setText(book.getTitle());
            binding.textBookAuthor.setText(book.getAuthor());
            binding.textBookYear.setText("Tahun Terbit: " + book.getFirstPublishYear());

            // Memuat gambar cover buku dengan Glide
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(book.getCoverUrl())
                        .placeholder(R.drawable.ic_bookmark_outline)
                        .error(R.drawable.ic_bookmark_outline)
                        .into(binding.imageBookCover);
            } else {
                binding.imageBookCover.setImageResource(R.drawable.ic_bookmark_outline);
            }

            // Listener klik item untuk diarahkan ke halaman Detail
            binding.cardBook.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(book);
                }
            });
        }
    }
}
