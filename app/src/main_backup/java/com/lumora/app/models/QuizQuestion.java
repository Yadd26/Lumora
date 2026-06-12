package com.lumora.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * QuizQuestion - Merepresentasikan satu pertanyaan kuis dari Open Trivia Database.
 */
public class QuizQuestion {

    @SerializedName("question")
    private String question;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("incorrect_answers")
    private List<String> incorrectAnswers;

    @SerializedName("difficulty")
    private String difficulty;

    @SerializedName("category")
    private String category;

    // Konstruktor default
    public QuizQuestion() {
    }

    // Konstruktor berparameter lengkap
    public QuizQuestion(String question, String correctAnswer, List<String> incorrectAnswers, String difficulty, String category) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.difficulty = difficulty;
        this.category = category;
    }

    // Getter dan Setter
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
