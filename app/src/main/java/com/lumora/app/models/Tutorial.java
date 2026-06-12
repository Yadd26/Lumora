package com.lumora.app.models;

import java.io.Serializable;

public class Tutorial implements Serializable {
    private int id;
    private String title;
    private String category;
    private String difficulty;
    private String timeEstimation;
    private String description;
    private String concepts;
    private String steps;
    private String implementation;
    private String codeExample;
    private String outputExample;
    private String academicTips;
    private String relatedBookQuery;
    private String status; // Belum Dimulai, Sedang Dipelajari, Selesai
    private int progress; // Percentage (0 - 100)

    public Tutorial() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getTimeEstimation() {
        return timeEstimation;
    }

    public void setTimeEstimation(String timeEstimation) {
        this.timeEstimation = timeEstimation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConcepts() {
        return concepts;
    }

    public void setConcepts(String concepts) {
        this.concepts = concepts;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public String getCodeExample() {
        return codeExample;
    }

    public void setCodeExample(String codeExample) {
        this.codeExample = codeExample;
    }

    public String getOutputExample() {
        return outputExample;
    }

    public void setOutputExample(String outputExample) {
        this.outputExample = outputExample;
    }

    public String getAcademicTips() {
        return academicTips;
    }

    public void setAcademicTips(String academicTips) {
        this.academicTips = academicTips;
    }

    public String getRelatedBookQuery() {
        return relatedBookQuery;
    }

    public void setRelatedBookQuery(String relatedBookQuery) {
        this.relatedBookQuery = relatedBookQuery;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
