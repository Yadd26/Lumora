package com.lumora.app.models;

/**
 * Book - Merepresentasikan materi pembelajaran berupa buku yang diambil dari Open Library API.
 * Mendukung data subject (kategori) dan jumlah edisi buku.
 */
public class Book {

    private String title;
    private String author;
    private String coverUrl;
    private String firstPublishYear; // Disimpan juga sebagai publishYear
    private String key;              // Disimpan juga sebagai bookKey
    private String subject;
    private int editionCount;

    // Konstruktor default
    public Book() {
    }

    // Konstruktor lengkap
    public Book(String title, String author, String coverUrl, String firstPublishYear, String subject, int editionCount, String key) {
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.firstPublishYear = firstPublishYear;
        this.subject = subject;
        this.editionCount = editionCount;
        this.key = key;
    }

    // Getter dan Setter
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

    public String getFirstPublishYear() {
        return firstPublishYear;
    }

    public void setFirstPublishYear(String firstPublishYear) {
        this.firstPublishYear = firstPublishYear;
    }

    // Kompatibilitas alias untuk publishYear
    public String getPublishYear() {
        return firstPublishYear;
    }

    public void setPublishYear(String publishYear) {
        this.firstPublishYear = publishYear;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Kompatibilitas alias untuk bookKey
    public String getBookKey() {
        return key;
    }

    public void setBookKey(String bookKey) {
        this.key = bookKey;
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
