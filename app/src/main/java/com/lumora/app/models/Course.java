package com.lumora.app.models;

import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {
    private int id;
    private String name;
    private String description;
    private String duration;
    private int modulesCount;
    private String level; // Pemula, Menengah, Lanjutan
    private int progress; // Percentage (0-100)
    
    // Non-DB runtime fields
    private List<Module> modules;

    public Course() {}

    public Course(int id, String name, String description, String duration, int modulesCount, String level, int progress) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.modulesCount = modulesCount;
        this.level = level;
        this.progress = progress;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getModulesCount() { return modulesCount; }
    public void setModulesCount(int modulesCount) { this.modulesCount = modulesCount; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) { this.modules = modules; }
}
