package com.lumora.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lumora.app.R;
import com.lumora.app.models.QuizCategory;

import java.util.List;

/**
 * QuizCategoryAdapter - Menghubungkan daftar data kategori kuis dengan antarmuka kartu kategori.
 */
public class QuizCategoryAdapter extends RecyclerView.Adapter<QuizCategoryAdapter.ViewHolder> {

    private final List<QuizCategory> categories;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(QuizCategory category);
    }

    public QuizCategoryAdapter(List<QuizCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizCategory category = categories.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textBadge;
        private final TextView textDifficulty;
        private final TextView textTitle;
        private final TextView textDescription;
        private final TextView textInfo;
        private final View cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textBadge = itemView.findViewById(R.id.text_category_badge);
            textDifficulty = itemView.findViewById(R.id.text_category_difficulty);
            textTitle = itemView.findViewById(R.id.text_category_title);
            textDescription = itemView.findViewById(R.id.text_category_description);
            textInfo = itemView.findViewById(R.id.text_category_info);
            cardView = itemView.findViewById(R.id.card_quiz_category);
        }

        public void bind(QuizCategory category, OnCategoryClickListener listener) {
            textTitle.setText(category.getName());
            textDescription.setText(category.getDescription());
            textDifficulty.setText(category.getDifficulty());
            textInfo.setText(category.getQuestionCount() + " Soal • " + category.getDurationEstimate());
            
            // Atur warna atau teks badge berdasarkan kesulitan
            if ("Sulit".equalsIgnoreCase(category.getDifficulty())) {
                textDifficulty.setTextColor(itemView.getResources().getColor(android.R.color.holo_red_light));
            } else {
                textDifficulty.setTextColor(itemView.getResources().getColor(android.R.color.holo_green_light));
            }

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}
