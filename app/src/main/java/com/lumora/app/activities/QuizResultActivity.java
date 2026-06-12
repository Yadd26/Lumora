package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.databinding.ActivityQuizResultBinding;

/**
 * QuizResultActivity - Menampilkan hasil evaluasi pengerjaan kuis,
 * termasuk skor, akurasi, dan nama kategori kuis.
 * Menyediakan aksi untuk mengulangi kuis atau kembali ke beranda.
 */
public class QuizResultActivity extends AppCompatActivity {

    public static final String EXTRA_CORRECT = "extra_correct";
    public static final String EXTRA_INCORRECT = "extra_incorrect";
    public static final String EXTRA_SCORE = "extra_score";
    public static final String EXTRA_CATEGORY = "extra_category";

    private ActivityQuizResultBinding binding;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ekstrak data hasil kuis dari intent
        int correct = getIntent().getIntExtra(EXTRA_CORRECT, 0);
        int incorrect = getIntent().getIntExtra(EXTRA_INCORRECT, 0);
        int score = getIntent().getIntExtra(EXTRA_SCORE, 0);
        categoryName = getIntent().getStringExtra(EXTRA_CATEGORY);

        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = "Umum";
        }

        // Hitung persentase akurasi
        int totalQuestions = correct + incorrect;
        int accuracy = totalQuestions > 0 ? (correct * 100) / totalQuestions : 0;

        // Render nilai hasil ke UI
        if (accuracy >= 70) {
            binding.textResultStatus.setText("Kuis LULUS");
            binding.textResultStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            binding.textResultStatus.setText("Kuis GAGAL");
            binding.textResultStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }

        binding.textResultCategory.setText("Kategori: " + categoryName);
        binding.textResultScore.setText(String.valueOf(score));
        binding.textResultCorrect.setText(String.valueOf(correct));
        binding.textResultIncorrect.setText(String.valueOf(incorrect));
        binding.textResultPercentage.setText(accuracy + "%");

        // Tombol Ulangi Kuis
        binding.btnRetryQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, QuizActivity.class);
            intent.putExtra(QuizActivity.EXTRA_CATEGORY_NAME, categoryName);
            startActivity(intent);
            finish();
        });

        // Tombol kembali ke menu utama (MainActivity)
        binding.btnBackHome.setOnClickListener(v -> finish());
    }
}
