package com.lumora.app.models;

/**
 * ResourceItem - Model data untuk mempresentasikan item tutorial, referensi akademik,
 * dan materi pembelajaran digital lainnya di dalam tab Learning Resources.
 */
public class ResourceItem {

    private final String title;
    private final String type; // "Buku", "Tutorial", "Referensi Akademik"
    private final String description;
    private final String authorOrProvider;
    private final String durationOrPages;

    public ResourceItem(String title, String type, String description, String authorOrProvider, String durationOrPages) {
        this.title = title;
        this.type = type;
        this.description = description;
        this.authorOrProvider = authorOrProvider;
        this.durationOrPages = durationOrPages;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthorOrProvider() {
        return authorOrProvider;
    }

    public String getDurationOrPages() {
        return durationOrPages;
    }
}
