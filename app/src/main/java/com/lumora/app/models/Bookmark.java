package com.lumora.app.models;

/**
 * Bookmark - Merepresentasikan buku pembelajaran yang disimpan oleh pengguna untuk akses offline.
 * Menyimpan data lengkap termasuk subjek dan edisi buku.
 */
public class Bookmark {

    private int id;
    private String title;
    private String author;
    private String year;
    private String coverUrl;
    private String bookKey;
    private String subject;
    private int editionCount;

    // Konstruktor default
    public Bookmark() {
    }

    // Konstruktor berparameter lengkap
    public Bookmark(int id, String title, String author, String year, String coverUrl, String bookKey, String subject, int editionCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.coverUrl = coverUrl;
        this.bookKey = bookKey;
        this.subject = subject;
        this.editionCount = editionCount;
    }

    // Konstruktor tanpa ID
    public Bookmark(String title, String author, String year, String coverUrl, String bookKey, String subject, int editionCount) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.coverUrl = coverUrl;
        this.bookKey = bookKey;
        this.subject = subject;
        this.editionCount = editionCount;
    }

    // Getter dan Setter
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getEditionCount() {
        return editionCount;
    }

    public void setEditionCount(int editionCount) {
        this.editionCount = editionCount;
    }
}
