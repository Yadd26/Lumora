package com.lumora.app.models;

import java.io.Serializable;
import java.util.List;

/**
 * QuizCategory - Model data untuk merepresentasikan kategori kuis beserta metadata pembelajarannya.
 */
public class QuizCategory implements Serializable {

    private final int id;
    private final String name;
    private final String description;
    private final int questionCount;
    private final String difficulty;
    private final String durationEstimate;
    private final List<String> topics;

    public QuizCategory(int id, String name, String description, int questionCount, String difficulty, String durationEstimate, List<String> topics) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.questionCount = questionCount;
        this.difficulty = difficulty;
        this.durationEstimate = durationEstimate;
        this.topics = topics;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getDurationEstimate() {
        return durationEstimate;
    }

    public List<String> getTopics() {
        return topics;
    }
}
