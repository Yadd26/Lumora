package com.lumora.app.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LearningInfoProvider - Utility class untuk menyediakan informasi detail pembelajaran,
 * kompetensi, tingkat kesulitan, dan estimasi waktu berdasarkan subjek atau judul buku secara otomatis.
 */
public class LearningInfoProvider {

    public static class LearningData {
        private final List<String> whatWillBeLearned;
        private final List<String> competencies;
        private final String difficulty;
        private final String estimation;

        public LearningData(List<String> whatWillBeLearned, List<String> competencies, String difficulty, String estimation) {
            this.whatWillBeLearned = whatWillBeLearned;
            this.competencies = competencies;
            this.difficulty = difficulty;
            this.estimation = estimation;
        }

        public List<String> getWhatWillBeLearned() {
            return whatWillBeLearned;
        }

        public List<String> getCompetencies() {
            return competencies;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public String getEstimation() {
            return estimation;
        }
    }

    /**
     * Menghasilkan data materi belajar, kompetensi, tingkat kesulitan, dan waktu
     * belajar yang relevan secara dinamis berdasarkan judul dan subjek buku.
     */
    public static LearningData getLearningData(String title, String subject) {
        String combined = ((title != null ? title : "") + " " + (subject != null ? subject : "")).toLowerCase();
        
        List<String> learned = new ArrayList<>();
        List<String> competencies = new ArrayList<>();
        String difficulty = "Pemula";
        String estimation = "15 Jam";

        if (combined.contains("java")) {
            learned.addAll(Arrays.asList("Sintaksis Dasar Java", "Pemrograman Berorientasi Objek (OOP)", "Java Collections Framework", "Penanganan Eksepsi (Exceptions)", "Multithreading & Konkurensi"));
            competencies.addAll(Arrays.asList("Membangun aplikasi konsol & desktop Java", "Menerapkan prinsip OOP (Inheritance, Polymorphism)", "Mengelola database menggunakan JDBC"));
            difficulty = "Pemula - Menengah";
            estimation = "20 Jam";
        } else if (combined.contains("python")) {
            learned.addAll(Arrays.asList("Sintaksis & Tipe Data Python", "Struktur Data (List, Dict, Tuple)", "Fungsi & Modul", "Analisis Data Dasar", "Pemrograman Fungsional & OOP"));
            competencies.addAll(Arrays.asList("Menulis skrip otomatisasi dengan Python", "Melakukan manipulasi data menggunakan Pandas", "Membuat visualisasi data sederhana"));
            difficulty = "Pemula";
            estimation = "12 Jam";
        } else if (combined.contains("android") || combined.contains("mobile") || combined.contains("flutter") || combined.contains("ios") || combined.contains("swift")) {
            learned.addAll(Arrays.asList("Arsitektur Aplikasi Mobile", "Desain UI (Jetpack Compose / XML / Flutter)", "Pengolahan Background Task & Threads", "Penyimpanan Lokal (SQLite/Room)", "Integrasi REST API dengan Retrofit"));
            competencies.addAll(Arrays.asList("Membangun aplikasi mobile fungsional", "Mengintegrasikan database lokal & API publik", "Mendesain antarmuka responsif Material Design"));
            difficulty = "Menengah";
            estimation = "30 Jam";
        } else if (combined.contains("database") || combined.contains("sql") || combined.contains("mysql") || combined.contains("oracle") || combined.contains("basis data") || combined.contains("query")) {
            learned.addAll(Arrays.asList("Konsep Relasional & Skema Database", "Bahasa Kueri SQL (DDL, DML)", "Desain Normalisasi (1NF, 2NF, 3NF)", "Optimasi Kueri & Indeks", "Transaksi & Keamanan Data"));
            competencies.addAll(Arrays.asList("Mendesain skema database relasional", "Menulis kueri SQL kompleks (Join, Subquery)", "Mengelola transaksi database secara aman"));
            difficulty = "Pemula - Menengah";
            estimation = "18 Jam";
        } else if (combined.contains("network") || combined.contains("cisco") || combined.contains("internet") || combined.contains("jaringan") || combined.contains("routing")) {
            learned.addAll(Arrays.asList("Model Referensi OSI & TCP/IP", "Protokol Routing & Switching", "IP Subnetting (IPv4 & IPv6)", "Keamanan Jaringan & Firewall", "Analisis Trafik Jaringan"));
            competencies.addAll(Arrays.asList("Mengonfigurasi perangkat jaringan router/switch", "Melakukan subnetting IP secara efisien", "Mendeteksi & troubleshooting masalah jaringan"));
            difficulty = "Menengah";
            estimation = "25 Jam";
        } else if (combined.contains("artificial") || combined.contains("intelligence") || combined.contains("machine learning") || combined.contains("ai") || combined.contains("deep learning") || combined.contains("neural")) {
            learned.addAll(Arrays.asList("Konsep Dasar Kecerdasan Buatan", "Algoritma Supervised & Unsupervised Learning", "Neural Networks & Deep Learning", "Natural Language Processing (NLP)", "Evaluasi Model & Overfitting"));
            competencies.addAll(Arrays.asList("Mengimplementasikan algoritma Machine Learning", "Melatih dan mengevaluasi performa model", "Membangun sistem cerdas berbasis data"));
            difficulty = "Mahir";
            estimation = "40 Jam";
        } else if (combined.contains("security") || combined.contains("cyber") || combined.contains("hack") || combined.contains("kripto") || sContainsSecurity(combined)) {
            learned.addAll(Arrays.asList("Konsep CIA Triad", "Prinsip Kriptografi (Simetris & Asimetris)", "Analisis Kerentanan Sistem (Vulnerability)", "Keamanan Web & Jaringan", "Penetration Testing Dasar"));
            competencies.addAll(Arrays.asList("Menganalisis dan mendeteksi celah keamanan", "Menerapkan enkripsi data yang aman", "Melakukan audit keamanan sistem sederhana"));
            difficulty = "Mahir";
            estimation = "35 Jam";
        } else {
            // Default generic educational curriculum
            learned.addAll(Arrays.asList("Metodologi Studi Kasus", "Analisis Konseptual Sistem", "Pemikiran Kritis & Penyelesaian Masalah", "Implementasi Praktis Teori", "Evaluasi & Studi Lanjutan"));
            competencies.addAll(Arrays.asList("Memahami kerangka teoretis subjek", "Menganalisis studi kasus secara terstruktur", "Menerapkan konsep ke dalam praktik industri"));
            difficulty = "Pemula";
            estimation = "10 Jam";
        }

        return new LearningData(learned, competencies, difficulty, estimation);
    }

    private static boolean sContainsSecurity(String text) {
        return text.contains("security") || text.contains("keamanan") || text.contains("crypt") || text.contains("hacking");
    }
}
