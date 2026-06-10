package com.lumora.app.models;

/**
 * Discussion - Merepresentasikan topik diskusi yang dibuat oleh pengguna.
 * Disimpan secara lokal di database SQLite.
 */
public class Discussion {

    private int id;
    private String title;
    private String content;

    // Konstruktor default
    public Discussion() {
    }

    // Konstruktor berparameter
    public Discussion(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // Konstruktor tanpa ID (untuk diskusi baru sebelum dimasukkan ke database)
    public Discussion(String title, String content) {
        this.title = title;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
