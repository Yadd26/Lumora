package com.lumora.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lumora.app.databinding.ItemDiscussionBinding;
import com.lumora.app.models.Discussion;

import java.util.ArrayList;
import java.util.List;

/**
 * DiscussionAdapter - Adapter RecyclerView untuk menampilkan daftar item diskusi.
 * Mendukung fungsi penghapusan diskusi melalui listener callback.
 */
public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder> {

    private List<Discussion> discussions;
    private final OnDiscussionActionListener listener;

    /**
     * Interface untuk menangani aksi pada diskusi (seperti penghapusan).
     */
    public interface OnDiscussionActionListener {
        void onDeleteDiscussion(Discussion discussion, int position);
    }

    public DiscussionAdapter(List<Discussion> discussions, OnDiscussionActionListener listener) {
        this.discussions = discussions != null ? discussions : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscussionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiscussionBinding binding = ItemDiscussionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DiscussionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionViewHolder holder, int position) {
        Discussion discussion = discussions.get(position);
        holder.bind(discussion);
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    /**
     * Memperbarui data adapter dan menyegarkan tampilan RecyclerView.
     *
     * @param newDiscussions Daftar diskusi baru yang akan ditampilkan
     */
    public void updateData(List<Discussion> newDiscussions) {
        this.discussions = newDiscussions != null ? newDiscussions : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Menghapus item pada posisi tertentu dengan efek animasi.
     *
     * @param position Posisi item yang akan dihapus
     */
    public void removeItem(int position) {
        if (position >= 0 && position < discussions.size()) {
            discussions.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, discussions.size());
        }
    }

    /**
     * ViewHolder untuk item diskusi menggunakan ViewBinding.
     */
    class DiscussionViewHolder extends RecyclerView.ViewHolder {

        private final ItemDiscussionBinding binding;

        DiscussionViewHolder(@NonNull ItemDiscussionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Discussion discussion) {
            binding.textDiscussionTitle.setText(discussion.getTitle());
            binding.textDiscussionContent.setText(discussion.getContent());

            // Klik tombol hapus diskusi
            binding.btnDeleteDiscussion.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onDeleteDiscussion(discussion, pos);
                    }
                }
            });
        }
    }
}
