package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.R;
import com.lumora.app.databinding.ActivityQuizDetailBinding;
import com.lumora.app.models.QuizCategory;

/**
 * QuizDetailActivity - Menampilkan rangkuman, kesulitan, durasi, dan daftar materi
 * yang akan diujikan pada kuis kategori terpilih sebelum kuis dimulai.
 */
public class QuizDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";

    private ActivityQuizDetailBinding binding;
    private QuizCategory category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi toolbar
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Ekstrak kategori dari intent
        if (getIntent() != null) {
            category = (QuizCategory) getIntent().getSerializableExtra(EXTRA_CATEGORY);
        }

        if (category != null) {
            populateUI();
        }
    }

    /**
     * Mempopulasikan seluruh metadata kuis ke tampilan antarmuka.
     */
    private void populateUI() {
        binding.textDetailCategoryTitle.setText(category.getName());
        binding.textDetailCategoryDesc.setText(category.getDescription());
        binding.textDetailDifficulty.setText(category.getDifficulty());
        binding.textDetailQuestions.setText(category.getQuestionCount() + " Soal");
        binding.textDetailDuration.setText(category.getDurationEstimate());

        // Atur warna kesulitan
        if ("Sulit".equalsIgnoreCase(category.getDifficulty())) {
            binding.textDetailDifficulty.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            binding.textDetailDifficulty.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        }

        // Populasikan materi uji (bullet list)
        binding.containerMateriUji.removeAllViews();
        if (category.getTopics() != null) {
            for (String topic : category.getTopics()) {
                TextView tv = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 6, 0, 6);
                tv.setLayoutParams(params);
                tv.setText("•  " + topic);
                tv.setTextSize(15);
                tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                binding.containerMateriUji.addView(tv);
            }
        }

        // Tombol Mulai Kuis
        binding.btnStartQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(QuizDetailActivity.this, QuizActivity.class);
            intent.putExtra(QuizActivity.EXTRA_CATEGORY_NAME, category.getName());
            startActivity(intent);
            finish(); // Tutup halaman detail agar saat kembali dari kuis tidak ke sini lagi
        });
    }
}
