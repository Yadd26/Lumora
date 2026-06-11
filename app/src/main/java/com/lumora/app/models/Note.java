package com.lumora.app.models;

import java.io.Serializable;

public class Note implements Serializable {
    private int id;
    private int userId;
    private String bookKey;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

    public Note() {}

    public Note(int id, int userId, String bookKey, String title, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.bookKey = bookKey;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getBookKey() { return bookKey; }
    public void setBookKey(String bookKey) { this.bookKey = bookKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
