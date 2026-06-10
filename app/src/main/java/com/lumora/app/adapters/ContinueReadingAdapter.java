package com.lumora.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.activities.ReaderActivity;
import com.lumora.app.models.BookProgress;

import java.util.List;

public class ContinueReadingAdapter extends RecyclerView.Adapter<ContinueReadingAdapter.ViewHolder> {

    private final Context context;
    private final List<BookProgress> progressList;

    public ContinueReadingAdapter(Context context, List<BookProgress> progressList) {
        this.context = context;
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_continue_reading, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookProgress bp = progressList.get(position);
        holder.textTitle.setText(bp.getTitle());
        holder.textProgress.setText("Bab " + bp.getCurrentChapter() + " • " + bp.getProgress() + "%");
        holder.progressBar.setProgress(bp.getProgress());

        if (bp.getCoverUrl() != null && !bp.getCoverUrl().isEmpty()) {
            Glide.with(context)
                    .load(bp.getCoverUrl())
                    .placeholder(R.drawable.ic_bookmark_outline)
                    .error(R.drawable.ic_bookmark_outline)
                    .into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(R.drawable.ic_bookmark_outline);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReaderActivity.class);
            intent.putExtra("extra_book_key", bp.getBookKey());
            intent.putExtra("extra_book_title", bp.getTitle());
            intent.putExtra("extra_book_author", bp.getAuthor());
            intent.putExtra("extra_book_cover", bp.getCoverUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView textTitle, textProgress;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_continue_cover);
            textTitle = itemView.findViewById(R.id.text_continue_title);
            textProgress = itemView.findViewById(R.id.text_continue_progress);
            progressBar = itemView.findViewById(R.id.progress_continue);
        }
    }
}
