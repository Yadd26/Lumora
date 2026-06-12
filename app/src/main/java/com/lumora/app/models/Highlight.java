package com.lumora.app.models;

import java.io.Serializable;

public class Highlight implements Serializable {
    private int id;
    private int userId;
    private String bookKey;
    private String selectedText;
    private String color;
    private String createdAt;

    public Highlight() {}

    public Highlight(int id, int userId, String bookKey, String selectedText, String color, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.bookKey = bookKey;
        this.selectedText = selectedText;
        this.color = color;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getBookKey() { return bookKey; }
    public void setBookKey(String bookKey) { this.bookKey = bookKey; }

    public String getSelectedText() { return selectedText; }
    public void setSelectedText(String selectedText) { this.selectedText = selectedText; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
