package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityQuizBinding;
import com.lumora.app.models.QuizQuestion;
import com.lumora.app.utils.QuizQuestionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * QuizActivity - Mengelola siklus pengerjaan kuis akademik untuk kategori yang dipilih.
 * Menyediakan navigasi bolak-balik (Sebelumnya/Selanjutnya), mempertahankan jawaban,
 * dan menyimpan riwayat kuis secara permanen ke SQLite.
 */
public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";

    private ActivityQuizBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    private String categoryName;
    private List<QuizQuestion> questions = new ArrayList<>();
    
    // Menyimpan opsi jawaban yang telah diacak agar tidak berubah saat navigasi
    private final List<List<String>> shuffledOptionsList = new ArrayList<>();
    
    // Menyimpan jawaban terpilih oleh pengguna untuk tiap indeks pertanyaan
    private String[] userSelectedAnswers;
    
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Ambil nama kategori
        if (getIntent() != null) {
            categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        }

        if (categoryName == null || categoryName.isEmpty()) {
            categoryName = "Umum";
        }

        binding.toolbar.setTitle("Kuis " + categoryName);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadQuestions();
        setupListeners();
    }

    /**
     * Memuat daftar pertanyaan dari penyedia lokal.
     */
    private void loadQuestions() {
        questions = QuizQuestionProvider.getQuestionsForCategory(categoryName);
        int totalQuestions = questions.size();
        userSelectedAnswers = new String[totalQuestions];

        // Acak dan simpan pilihan jawaban untuk masing-masing soal sekali saja
        for (QuizQuestion q : questions) {
            List<String> options = new ArrayList<>();
            options.add(q.getCorrectAnswer());
            options.addAll(q.getIncorrectAnswers());
            Collections.shuffle(options);
            shuffledOptionsList.add(options);
        }

        if (totalQuestions > 0) {
            displayQuestion(0);
        } else {
            Toast.makeText(this, "Tidak ada pertanyaan untuk kategori ini.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Menampilkan pertanyaan ke layar berdasarkan indeks.
     */
    private void displayQuestion(int index) {
        currentQuestionIndex = index;
        QuizQuestion question = questions.get(index);
        List<String> options = shuffledOptionsList.get(index);

        // Render nomor soal & progress bar
        int questionNumber = index + 1;
        binding.textQuestionNumber.setText("Soal " + questionNumber + " dari " + questions.size());
        binding.progressQuiz.setProgress(questionNumber);

        // Dekode entitas HTML untuk teks pertanyaan
        String decodedQuestion = Html.fromHtml(question.getQuestion(), Html.FROM_HTML_MODE_LEGACY).toString();
        binding.textQuestion.setText(decodedQuestion);

        // Reset & Tampilkan Opsi RadioButton
        binding.radioGroupAnswers.clearCheck();
        
        setOptionText(binding.radioOption1, options.size() > 0 ? options.get(0) : "");
        setOptionText(binding.radioOption2, options.size() > 1 ? options.get(1) : "");
        setOptionText(binding.radioOption3, options.size() > 2 ? options.get(2) : "");
        setOptionText(binding.radioOption4, options.size() > 3 ? options.get(3) : "");

        // Pulihkan jawaban jika sebelumnya pernah diisi
        String previouslySelected = userSelectedAnswers[index];
        if (previouslySelected != null) {
            if (previouslySelected.equals(binding.radioOption1.getText().toString())) {
                binding.radioOption1.setChecked(true);
            } else if (previouslySelected.equals(binding.radioOption2.getText().toString())) {
                binding.radioOption2.setChecked(true);
            } else if (previouslySelected.equals(binding.radioOption3.getText().toString())) {
                binding.radioOption3.setChecked(true);
            } else if (previouslySelected.equals(binding.radioOption4.getText().toString())) {
                binding.radioOption4.setChecked(true);
            }
            binding.btnNextQuestion.setEnabled(true);
        } else {
            binding.btnNextQuestion.setEnabled(false);
        }

        // Atur status tombol navigasi bawah
        if (index == 0) {
            binding.btnPrevQuestion.setVisibility(View.INVISIBLE);
        } else {
            binding.btnPrevQuestion.setVisibility(View.VISIBLE);
        }

        if (index == questions.size() - 1) {
            binding.btnNextQuestion.setText("Selesai");
        } else {
            binding.btnNextQuestion.setText("Selanjutnya");
        }
    }

    private void setOptionText(RadioButton radioButton, String text) {
        if (!text.isEmpty()) {
            radioButton.setVisibility(View.VISIBLE);
            String decoded = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
            radioButton.setText(decoded);
        } else {
            radioButton.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        // Hanya aktifkan tombol lanjut jika salah satu jawaban terpilih
        binding.radioGroupAnswers.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                binding.btnNextQuestion.setEnabled(true);
                RadioButton checkedButton = findViewById(checkedId);
                userSelectedAnswers[currentQuestionIndex] = checkedButton.getText().toString();
            }
        });

        // Tombol Sebelumnya
        binding.btnPrevQuestion.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                displayQuestion(currentQuestionIndex - 1);
            }
        });

        // Tombol Selanjutnya / Selesai
        binding.btnNextQuestion.setOnClickListener(v -> {
            if (currentQuestionIndex == questions.size() - 1) {
                evaluateAndFinishQuiz();
            } else {
                displayQuestion(currentQuestionIndex + 1);
            }
        });
    }

    /**
     * Mengevaluasi hasil pengerjaan kuis, menyimpan ke database SQLite, dan membuka hasil.
     */
    private void evaluateAndFinishQuiz() {
        int correct = 0;
        int incorrect = 0;

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);
            String userAns = userSelectedAnswers[i];
            String correctAnsDecoded = Html.fromHtml(q.getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY).toString();

            if (userAns != null && userAns.equals(correctAnsDecoded)) {
                correct++;
            } else {
                incorrect++;
            }
        }

        int score = correct * 10;
        final int finalCorrect = correct;
        final int finalIncorrect = incorrect;

        int totalQuestions = questions.size();
        int pct = totalQuestions > 0 ? (correct * 100) / totalQuestions : 0;
        final boolean passed = pct >= 70;

        // Simpan hasil kuis ke database SQLite di thread latar belakang
        executorService.execute(() -> {
            com.lumora.app.preferences.SessionManager session = com.lumora.app.preferences.SessionManager.getInstance(QuizActivity.this);
            int userId = session.getUserId();
            if (userId <= 0) userId = 1;

            int courseId = getIntent().getIntExtra("extra_course_id", -1);
            int moduleId = getIntent().getIntExtra("extra_module_id", -1);

            databaseHelper.insertQuizResult(userId, courseId, moduleId, score, passed ? 1 : 0);
            databaseHelper.insertQuizHistory(categoryName, score, totalQuestions);
            databaseHelper.insertLearningHistory(userId, "Menyelesaikan Kuis " + categoryName, categoryName, "QUIZ");

            runOnUiThread(() -> {
                // Buka Halaman Hasil Kuis
                Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
                intent.putExtra(QuizResultActivity.EXTRA_CORRECT, finalCorrect);
                intent.putExtra(QuizResultActivity.EXTRA_INCORRECT, finalIncorrect);
                intent.putExtra(QuizResultActivity.EXTRA_SCORE, score);
                intent.putExtra(QuizResultActivity.EXTRA_CATEGORY, categoryName);
                startActivity(intent);
                finish(); // Tutup QuizActivity
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
