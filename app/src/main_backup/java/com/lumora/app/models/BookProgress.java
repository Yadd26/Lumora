package com.lumora.app.models;

import java.io.Serializable;

public class BookProgress implements Serializable {
    private String bookKey;
    private String title;
    private String author;
    private String coverUrl;
    private int currentChapter;
    private int progress;

    public BookProgress(String bookKey, String title, String author, String coverUrl, int currentChapter, int progress) {
        this.bookKey = bookKey;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.currentChapter = currentChapter;
        this.progress = progress;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
