package com.lumora.app.models;

import java.io.Serializable;

public class Module implements Serializable {
    private int id;
    private String pathName;
    private String name;
    private String description;
    private String content;
    private String example;
    private int tutorialId;
    private String exercise;
    private int quizId;

    // Progress fields
    private int materiCompleted;
    private int tutorialCompleted;
    private int latihanCompleted;
    private int quizCompleted;
    private String status; // Belum Dimulai, Sedang Dipelajari, Selesai
    private int completionPercentage;

    public Module() {
        this.status = "Belum Dimulai";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(int tutorialId) {
        this.tutorialId = tutorialId;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getMateriCompleted() {
        return materiCompleted;
    }

    public void setMateriCompleted(int materiCompleted) {
        this.materiCompleted = materiCompleted;
    }

    public int getTutorialCompleted() {
        return tutorialCompleted;
    }

    public void setTutorialCompleted(int tutorialCompleted) {
        this.tutorialCompleted = tutorialCompleted;
    }

    public int getLatihanCompleted() {
        return latihanCompleted;
    }

    public void setLatihanCompleted(int latihanCompleted) {
        this.latihanCompleted = latihanCompleted;
    }

    public int getQuizCompleted() {
        return quizCompleted;
    }

    public void setQuizCompleted(int quizCompleted) {
        this.quizCompleted = quizCompleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
