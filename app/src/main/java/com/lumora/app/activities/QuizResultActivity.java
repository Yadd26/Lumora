package com.lumora.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.databinding.ActivityQuizResultBinding;
import com.lumora.app.preferences.SessionManager;

/**
 * QuizResultActivity - Menampilkan hasil evaluasi pengerjaan kuis.
 * Menghitung persentase akurasi jawaban, memperbarui statistik kuis di SharedPreferences.
 */
public class QuizResultActivity extends AppCompatActivity {

    public static final String EXTRA_CORRECT = "extra_correct";
    public static final String EXTRA_INCORRECT = "extra_incorrect";
    public static final String EXTRA_SCORE = "extra_score";

    private ActivityQuizResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ekstrak data hasil kuis dari intent
        int correct = getIntent().getIntExtra(EXTRA_CORRECT, 0);
        int incorrect = getIntent().getIntExtra(EXTRA_INCORRECT, 0);
        int score = getIntent().getIntExtra(EXTRA_SCORE, 0);

        // Hitung persentase akurasi
        int totalQuestions = correct + incorrect;
        int accuracy = totalQuestions > 0 ? (correct * 100) / totalQuestions : 0;

        // Render nilai hasil ke UI
        binding.textResultScore.setText(String.valueOf(score));
        binding.textResultCorrect.setText(String.valueOf(correct));
        binding.textResultIncorrect.setText(String.valueOf(incorrect));
        binding.textResultPercentage.setText(accuracy + "%");

        // Simpan hasil ke SharedPreferences secara lokal
        saveQuizStatistics(score);

        // Tombol kembali ke menu utama (MainActivity)
        binding.btnBackHome.setOnClickListener(v -> finish());
    }

    /**
     * Menyimpan statistik kuis (skor tertinggi dan kuis selesai) melalui SessionManager.
     */
    private void saveQuizStatistics(int score) {
        SessionManager sessionManager = SessionManager.getInstance(this);
        sessionManager.incrementQuizCompleted();
        sessionManager.updateHighScore(score);
    }
}
