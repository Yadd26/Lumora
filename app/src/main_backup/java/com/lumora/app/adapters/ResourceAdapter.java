package com.lumora.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lumora.app.databinding.ItemResourceBinding;
import com.lumora.app.models.ResourceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ResourceAdapter - RecyclerView adapter untuk menampilkan Sumber Belajar (Buku, Tutorial, Referensi Akademik).
 */
public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {

    private List<ResourceItem> items;
    private final OnResourceClickListener listener;

    public interface OnResourceClickListener {
        void onResourceClick(ResourceItem item);
    }

    public ResourceAdapter(List<ResourceItem> items, OnResourceClickListener listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResourceBinding binding = ItemResourceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ResourceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<ResourceItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ResourceViewHolder extends RecyclerView.ViewHolder {
        private final ItemResourceBinding binding;

        ResourceViewHolder(@NonNull ItemResourceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ResourceItem item) {
            binding.textResourceTitle.setText(item.getTitle());
            binding.textResourceDescription.setText(item.getDescription());
            binding.textResourceType.setText(item.getType());
            binding.textResourceDuration.setText(item.getDurationOrPages());
            binding.textResourceAuthor.setText("Sumber: " + item.getAuthorOrProvider());

            binding.cardResource.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onResourceClick(item);
                }
            });
        }
    }
}
