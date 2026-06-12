package com.lumora.app.models;

import java.io.Serializable;
import java.util.List;

public class LearningPath implements Serializable {
    private String name;
    private String description;
    private String category;
    private String duration;
    private List<String> modules;
    private int progress; // Percentage (0-100)

    public LearningPath(String name, String description, String category, String duration, List<String> modules) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.duration = duration;
        this.modules = modules;
        this.progress = 0;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
