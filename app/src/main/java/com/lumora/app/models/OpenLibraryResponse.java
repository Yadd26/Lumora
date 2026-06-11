package com.lumora.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * OpenLibraryResponse - Model pembungkus respons pencarian buku dari Open Library API.
 */
public class OpenLibraryResponse {

    @SerializedName("docs")
    private List<BookDoc> docs;

    public List<BookDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<BookDoc> docs) {
        this.docs = docs;
    }

    /**
     * BookDoc - Representasi data buku mentah di dalam array docs respons JSON.
     */
    public static class BookDoc {
        @SerializedName("title")
        private String title;

        @SerializedName("author_name")
        private List<String> authorName;

        @SerializedName("cover_i")
        private Long coverI;

        @SerializedName("first_publish_year")
        private Integer firstPublishYear;

        @SerializedName("key")
        private String key;

        @SerializedName("edition_count")
        private Integer editionCount;

        @SerializedName("subject")
        private List<String> subject;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getAuthorName() {
            return authorName;
        }

        public void setAuthorName(List<String> authorName) {
            this.authorName = authorName;
        }

        public Long getCoverI() {
            return coverI;
        }

        public void setCoverI(Long coverI) {
            this.coverI = coverI;
        }

        public Integer getFirstPublishYear() {
            return firstPublishYear;
        }

        public void setFirstPublishYear(Integer firstPublishYear) {
            this.firstPublishYear = firstPublishYear;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getEditionCount() {
            return editionCount;
        }

        public void setEditionCount(Integer editionCount) {
            this.editionCount = editionCount;
        }

        public List<String> getSubject() {
            return subject;
        }

        public void setSubject(List<String> subject) {
            this.subject = subject;
        }

        /**
         * Mengonversi objek mentah BookDoc ke model domain Book.
         */
        public Book toBook() {
            String authorStr = (authorName != null && !authorName.isEmpty()) 
                    ? authorName.get(0) : "Penulis Tidak Diketahui";
            String yearStr = firstPublishYear != null 
                    ? String.valueOf(firstPublishYear) : "Tidak Diketahui";
            String coverUrlStr = coverI != null 
                    ? "https://covers.openlibrary.org/b/id/" + coverI + "-L.jpg" : "";
            
            // Menggabungkan subjek-subjek menjadi string yang dipisahkan koma
            String subjectStr = "";
            if (subject != null && !subject.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Math.min(5, subject.size()); i++) {
                    sb.append(subject.get(i));
                    if (i < Math.min(5, subject.size()) - 1) {
                        sb.append(", ");
                    }
                }
                subjectStr = sb.toString();
            } else {
                subjectStr = "Umum";
            }

            int editions = editionCount != null ? editionCount : 1;

            return new Book(title, authorStr, coverUrlStr, yearStr, subjectStr, editions, key);
        }
    }
}
