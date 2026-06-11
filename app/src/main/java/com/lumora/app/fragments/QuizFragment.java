package com.lumora.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lumora.app.R;
import com.lumora.app.activities.QuizResultActivity;
import com.lumora.app.databinding.FragmentQuizBinding;
import com.lumora.app.models.QuizQuestion;
import com.lumora.app.models.QuizResponse;
import com.lumora.app.network.ApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * QuizFragment - Menampilkan kuis interaktif dengan 10 soal acak dari Open Trivia Database.
 * Menyediakan alur pengisian multi-choice (pilihan ganda) satu per satu.
 */
public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;
    private List<QuizQuestion> quizQuestions = new ArrayList<>();
    
    private int currentQuestionIndex = 0;
    private int correctCount = 0;
    private int incorrectCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
        loadQuizQuestions();
    }

    /**
     * Menyiapkan listener interaktif pada tombol "Coba Lagi" dan tombol "Selanjutnya".
     */
    private void setupListeners() {
        binding.btnRetry.setOnClickListener(v -> loadQuizQuestions());

        // Dengarkan pilihan jawaban untuk mengaktifkan tombol selanjutnya
        binding.radioGroupAnswers.setOnCheckedChangeListener((group, checkedId) -> {
            binding.btnNextQuestion.setEnabled(checkedId != -1);
        });

        binding.btnNextQuestion.setOnClickListener(v -> handleNextClick());
    }

    /**
     * Memanggil API Trivia DB secara asinkron menggunakan Retrofit.
     */
    private void loadQuizQuestions() {
        showLoadingState();

        ApiClient.getQuizApiService().getQuestions(10).enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    quizQuestions = response.body().getResults();
                    if (!quizQuestions.isEmpty()) {
                        currentQuestionIndex = 0;
                        correctCount = 0;
                        incorrectCount = 0;
                        showContentState();
                        displayQuestion(currentQuestionIndex);
                    } else {
                        showErrorState("Tidak ada soal yang tersedia dari server.");
                    }
                } else {
                    showErrorState("Gagal memuat soal. Kesalahan server.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                showErrorState("Koneksi gagal. Periksa jaringan Anda.");
            }
        });
    }

    /**
     * Menampilkan pertanyaan kuis ke antarmuka berdasarkan indeks soal saat ini.
     *
     * @param index Indeks soal (0 sampai 9)
     */
    private void displayQuestion(int index) {
        QuizQuestion question = quizQuestions.get(index);

        // Dekode entitas HTML untuk visualisasi teks yang rapi
        String decodedQuestion = Html.fromHtml(question.getQuestion(), Html.FROM_HTML_MODE_LEGACY).toString();
        binding.textQuestion.setText(decodedQuestion);

        // Atur metadata kuis
        String metaText = "Kategori: " + question.getCategory() + " | Kesulitan: " + question.getDifficulty().toUpperCase();
        binding.textQuizMeta.setText(metaText);

        // Atur nomor soal dan progress bar
        int questionNumber = index + 1;
        binding.textQuestionNumber.setText("Soal " + questionNumber + " dari 10");
        binding.progressQuiz.setProgress(questionNumber);

        // Gabungkan dan acak pilihan jawaban
        List<String> options = new ArrayList<>();
        options.add(question.getCorrectAnswer());
        for (String inc : question.getIncorrectAnswers()) {
            options.add(inc);
        }
        Collections.shuffle(options);

        // Reset RadioGroup
        binding.radioGroupAnswers.clearCheck();
        binding.btnNextQuestion.setEnabled(false);

        // Render opsi jawaban ke RadioButton (dekode entitas HTML terlebih dahulu)
        setOptionText(binding.radioOption1, options.size() > 0 ? options.get(0) : "");
        setOptionText(binding.radioOption2, options.size() > 1 ? options.get(1) : "");
        setOptionText(binding.radioOption3, options.size() > 2 ? options.get(2) : "");
        setOptionText(binding.radioOption4, options.size() > 3 ? options.get(3) : "");

        // Atur label tombol aksi
        if (index == quizQuestions.size() - 1) {
            binding.btnNextQuestion.setText("Selesai");
        } else {
            binding.btnNextQuestion.setText("Selanjutnya");
        }
    }

    /**
     * Membantu mendekode string HTML pilihan jawaban sebelum diisi ke RadioButton.
     */
    private void setOptionText(RadioButton radioButton, String text) {
        if (!text.isEmpty()) {
            radioButton.setVisibility(View.VISIBLE);
            String decoded = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
            radioButton.setText(decoded);
        } else {
            radioButton.setVisibility(View.GONE);
        }
    }

    /**
     * Memproses evaluasi jawaban terpilih dan mengendalikan perpindahan soal.
     */
    private void handleNextClick() {
        int selectedId = binding.radioGroupAnswers.getCheckedRadioButtonId();
        if (selectedId == -1) return;

        RadioButton selectedRadioButton = binding.radioGroupAnswers.findViewById(selectedId);
        String selectedAnswer = selectedRadioButton.getText().toString();

        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        String correctAnswerDecoded = Html.fromHtml(currentQuestion.getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY).toString();

        // Bandingkan teks pilihan dengan jawaban yang benar
        if (selectedAnswer.equals(correctAnswerDecoded)) {
            correctCount++;
        } else {
            incorrectCount++;
        }

        // Cek apakah ini soal terakhir
        if (currentQuestionIndex == quizQuestions.size() - 1) {
            // Arahkan ke QuizResultActivity
            Intent intent = new Intent(requireActivity(), QuizResultActivity.class);
            intent.putExtra(QuizResultActivity.EXTRA_CORRECT, correctCount);
            intent.putExtra(QuizResultActivity.EXTRA_INCORRECT, incorrectCount);
            intent.putExtra(QuizResultActivity.EXTRA_SCORE, correctCount * 10);
            startActivity(intent);

            // Reset status kuis di fragment agar bersih jika pengguna kembali
            currentQuestionIndex = 0;
            correctCount = 0;
            incorrectCount = 0;
            binding.radioGroupAnswers.clearCheck();
            binding.layoutQuizContent.setVisibility(View.GONE);
            binding.layoutLoading.setVisibility(View.VISIBLE);
            loadQuizQuestions();
        } else {
            // Tampilkan soal berikutnya
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        }
    }

    // ==========================================
    // MANAJEMEN STATE TAMPILAN
    // ==========================================

    private void showLoadingState() {
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutQuizContent.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showContentState() {
        binding.layoutQuizContent.setVisibility(View.VISIBLE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
    }

    private void showErrorState(String message) {
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setText(message);
        binding.layoutQuizContent.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Cegah memory leak
    }
}
