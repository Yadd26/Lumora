package com.lumora.app.utils;

import com.lumora.app.models.QuizCategory;
import com.lumora.app.models.QuizQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * QuizQuestionProvider - Menyediakan bank data kategori kuis dan pertanyaan kuis lokal (offline)
 * untuk menjamin kestabilan materi evaluasi belajar yang disesuaikan dengan kurikulum Lumora.
 */
public class QuizQuestionProvider {

    // Daftar Kategori
    private static final List<QuizCategory> categories = new ArrayList<>();

    static {
        // 1. Pemrograman
        categories.add(new QuizCategory(
                1,
                "Pemrograman",
                "Uji dasar logika, sintaksis Java, OOP, tipe data, dan alur kontrol program.",
                10,
                "Menengah",
                "15 Menit",
                Arrays.asList("Logika dasar & Operator", "Konsep Pemrograman Berorientasi Objek (OOP)", "Tipe Data Primitif & Reference", "Struktur Kontrol percabangan & perulangan", "Manajemen Memory & Garbace Collector")
        ));

        // 2. Basis Data
        categories.add(new QuizCategory(
                2,
                "Basis Data",
                "Evaluasi perintah SQL, normalisasi data, relasi tabel, transaksi, dan perancangan ERD.",
                10,
                "Menengah",
                "15 Menit",
                Arrays.asList("Konsep Relasional & Schema", "Perintah DDL & DML SQL", "Normalisasi Data (1NF, 2NF, 3NF)", "Primary Key, Foreign Key & Indexing", "Transaksi ACID & Join Query")
        ));

        // 3. Jaringan Komputer
        categories.add(new QuizCategory(
                3,
                "Jaringan Komputer",
                "Uji pemahaman Model OSI, alamat IP, protokol internet (HTTP/TCP), routing, dan subnetting.",
                10,
                "Menengah",
                "15 Menit",
                Arrays.asList("Model OSI & TCP/IP Stack", "Subnetting & Alamat IPv4/IPv6", "Protokol Transport (TCP & UDP)", "DNS, HTTP, SMTP & DHCP", "Perangkat Jaringan (Router, Switch, Hub)")
        ));

        // 4. Mobile Development
        categories.add(new QuizCategory(
                4,
                "Mobile Development",
                "Pertanyaan seputar siklus hidup Android Activity, Kotlin, UI modern, dan arsitektur aplikasi.",
                10,
                "Menengah",
                "15 Menit",
                Arrays.asList("Siklus Hidup Android Activity/Fragment", "Bahasa Pemrograman Kotlin dasar", "Jetpack Compose & Deklaratif UI", "Penyimpanan data lokal & API Networking", "Arsitektur MVVM & Android Jetpack")
        ));

        // 5. Artificial Intelligence
        categories.add(new QuizCategory(
                5,
                "Artificial Intelligence",
                "Menguji konsep dasar Machine Learning, algoritma pencarian, neural networks, dan NLP.",
                10,
                "Sulit",
                "20 Menit",
                Arrays.asList("Prinsip kecerdasan buatan", "Supervised vs Unsupervised Learning", "Konsep dasar Jaringan Saraf Tiruan (Neural Networks)", "Pengenalan NLP & Computer Vision", "Model AI Generatif & LLM")
        ));

        // 6. Cyber Security
        categories.add(new QuizCategory(
                6,
                "Cyber Security",
                "Uji taktik pertahanan siber, jenis serangan (DDoS/Phishing), enkripsi, dan keamanan sistem.",
                10,
                "Sulit",
                "20 Menit",
                Arrays.asList("Konsep CIA Triad (Confidentiality, Integrity, Availability)", "Kriptografi & Enkripsi data (Simetris & Asimetris)", "Jenis Malware (Trojan, Ransomware, Worm)", "Ancaman Social Engineering (Phishing)", "Keamanan Jaringan & Firewall")
        ));

        // 7. Data Science
        categories.add(new QuizCategory(
                7,
                "Data Science",
                "Evaluasi metode analisis data, statistika dasar, manipulasi data (Pandas), dan visualisasi data.",
                10,
                "Menengah",
                "15 Menit",
                Arrays.asList("Metodologi analisis data", "Statistika Deskriptif (Mean, Median, Modus)", "Data Cleaning & Preprocessing", "Library manipulasi data (Pandas & NumPy)", "Visualisasi data (Matplotlib & Seaborn)")
        ));
    }

    /**
     * Mendapatkan daftar semua kategori kuis yang tersedia.
     */
    public static List<QuizCategory> getCategories() {
        return categories;
    }

    /**
     * Mendapatkan kategori kuis berdasarkan ID.
     */
    public static QuizCategory getCategoryById(int id) {
        for (QuizCategory cat : categories) {
            if (cat.getId() == id) {
                return cat;
            }
        }
        return null;
    }

    /**
     * Menyediakan daftar acak 10 soal untuk kategori yang dipilih.
     */
    public static List<QuizQuestion> getQuestionsForCategory(String categoryName) {
        List<QuizQuestion> allQuestions = new ArrayList<>();

        if ("Pemrograman".equalsIgnoreCase(categoryName)) {
            allQuestions.add(new QuizQuestion("Apa output dari System.out.println(5 + 2 + \"3\"); di Java?", "73", Arrays.asList("10", "523", "Error"), "Menengah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Bahasa pemrograman manakah yang menggunakan maskot ular?", "Python", Arrays.asList("Java", "C++", "Ruby"), "Mudah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Manakah di bawah ini yang BUKAN merupakan tipe data primitif di Java?", "String", Arrays.asList("int", "double", "boolean"), "Mudah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Apa fungsi utama dari kata kunci 'static' pada Java?", "Menandakan variabel/method milik kelas, bukan instance", Arrays.asList("Mencegah variabel diubah nilainya", "Membuat method berjalan secara asinkron", "Menghapus objek secara otomatis"), "Menengah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Struktur data manakah yang menggunakan prinsip LIFO (Last In First Out)?", "Stack", Arrays.asList("Queue", "Array", "LinkedList"), "Mudah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Bahasa pemrograman mana yang sering digunakan untuk pengembangan aplikasi web interaktif di sisi klien (client-side)?", "JavaScript", Arrays.asList("Python", "PHP", "C#"), "Mudah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Apa fungsi dari operator '%' (modulus) dalam pemrograman?", "Menghitung sisa hasil bagi", Arrays.asList("Menghitung persentase nilai", "Membagi dua bilangan riil", "Melakukan pemangkatan"), "Mudah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Konsep OOP yang membungkus data dan method dalam satu unit kelas disebut?", "Enkapsulasi", Arrays.asList("Polimorfisme", "Inheritance", "Abstraksi"), "Menengah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Mana di antara berikut yang merupakan representasi dari NullPointerException di Java?", "Mencoba mengakses method dari referensi objek yang bernilai null", Arrays.asList("Kesalahan pembagian dengan angka nol", "Indeks array melebihi ukuran array", "Tipe data casting yang tidak valid"), "Menengah", "Pemrograman"));
            allQuestions.add(new QuizQuestion("Dalam pemrograman, apa fungsi dari konstruktor?", "Inisialisasi nilai awal sebuah objek baru", Arrays.asList("Menghapus objek dari memory", "Mencegah duplikasi inheritance", "Mempercepat eksekusi looping"), "Menengah", "Pemrograman"));

        } else if ("Basis Data".equalsIgnoreCase(categoryName)) {
            allQuestions.add(new QuizQuestion("Apa kepanjangan dari SQL?", "Structured Query Language", Arrays.asList("Simple Question Language", "System Query Locator", "Statement Query Logic"), "Mudah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Perintah SQL mana yang digunakan untuk mengambil data dari database?", "SELECT", Arrays.asList("GET", "FETCH", "EXTRACT"), "Mudah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Kolom unik yang digunakan sebagai pengidentifikasi utama sebuah baris tabel disebut?", "Primary Key", Arrays.asList("Foreign Key", "Unique Key", "Super Key"), "Mudah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Proses pengorganisasian data untuk menghindari redundansi dan anomali disebut?", "Normalisasi", Arrays.asList("Denormalisasi", "Indexing", "Querying"), "Menengah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Jenis join mana yang mengembalikan baris jika ada kecocokan di kedua tabel?", "INNER JOIN", Arrays.asList("LEFT JOIN", "RIGHT JOIN", "FULL JOIN"), "Menengah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Perintah DDL (Data Definition Language) yang digunakan untuk menghapus tabel beserta strukturnya adalah?", "DROP TABLE", Arrays.asList("DELETE TABLE", "TRUNCATE TABLE", "REMOVE TABLE"), "Menengah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Apa kegunaan utama dari 'Index' pada database?", "Mempercepat proses pencarian dan eksekusi query", Arrays.asList("Mengamankan data dari serangan SQL Injection", "Menghapus data duplikat secara otomatis", "Menghemat penyimpanan harddisk"), "Menengah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Properti 'Atomicity' dalam transaksi ACID menjamin bahwa...", "Transaksi harus berhasil sepenuhnya atau dibatalkan seluruhnya", Arrays.asList("Transaksi tidak boleh terganggu transaksi lain", "Data harus konsisten sebelum dan setelah transaksi", "Hasil transaksi disimpan secara permanen"), "Sulit", "Basis Data"));
            allQuestions.add(new QuizQuestion("Manakah dari berikut yang merupakan sistem manajemen database NoSQL?", "MongoDB", Arrays.asList("MySQL", "PostgreSQL", "Oracle DB"), "Menengah", "Basis Data"));
            allQuestions.add(new QuizQuestion("Perintah SQL mana yang digunakan untuk memperbarui data yang sudah ada di dalam tabel?", "UPDATE", Arrays.asList("MODIFY", "INSERT", "CHANGE"), "Mudah", "Basis Data"));

        } else if ("Jaringan Komputer".equalsIgnoreCase(categoryName)) {
            allQuestions.add(new QuizQuestion("Protokol mana yang digunakan untuk mengirim pesan email antara server?", "SMTP", Arrays.asList("HTTP", "FTP", "POP3"), "Menengah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Apa kepanjangan dari IP dalam jaringan komputer?", "Internet Protocol", Arrays.asList("Intranet Port", "Internet Path", "Information Provider"), "Mudah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Perangkat jaringan yang berfungsi menghubungkan dua jaringan berbeda segmen adalah?", "Router", Arrays.asList("Switch", "Hub", "Repeater"), "Mudah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Berapa panjang alamat IPv4 dalam bit?", "32 bit", Arrays.asList("64 bit", "128 bit", "256 bit"), "Mudah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Lapisan paling bawah pada model referensi OSI (Open Systems Interconnection) adalah?", "Physical Layer", Arrays.asList("Application Layer", "Network Layer", "Transport Layer"), "Mudah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Protokol apa yang digunakan untuk menerjemahkan nama domain (seperti google.com) menjadi alamat IP?", "DNS", Arrays.asList("DHCP", "FTP", "HTTP"), "Mudah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Protokol transport mana yang bersifat connectionless dan tidak menjamin pengiriman paket data?", "UDP", Arrays.asList("TCP", "HTTP", "FTP"), "Menengah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Berapa bit panjang alamat IPv6?", "128 bit", Arrays.asList("32 bit", "64 bit", "256 bit"), "Mudah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Apa fungsi utama dari protokol DHCP di suatu jaringan?", "Memberikan alamat IP secara dinamis kepada perangkat klien", Arrays.asList("Mengamankan koneksi dengan enkripsi SSL", "Memfilter konten website berbahaya", "Mentransmisikan file media berukuran besar"), "Menengah", "Jaringan Komputer"));
            allQuestions.add(new QuizQuestion("Manakah dari berikut yang merupakan alamat IP kelas privat (Local IP)?", "192.168.1.1", Arrays.asList("8.8.8.8", "202.158.4.5", "100.100.100.100"), "Menengah", "Jaringan Komputer"));

        } else if ("Mobile Development".equalsIgnoreCase(categoryName)) {
            allQuestions.add(new QuizQuestion("Bahasa pemrograman utama untuk pengembangan Android modern yang direkomendasikan Google adalah?", "Kotlin", Arrays.asList("Java", "Swift", "Dart"), "Mudah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Framework buatan Google untuk membangun aplikasi lintas platform (Android & iOS) dengan sekali tulis kode adalah?", "Flutter", Arrays.asList("React Native", "Xamarin", "Cordova"), "Mudah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Komponen Android utama yang menangani visualisasi antarmuka pengguna (UI) adalah?", "Activity", Arrays.asList("Service", "Broadcast Receiver", "Content Provider"), "Mudah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Bahasa pemrograman utama untuk pengembangan aplikasi iOS secara native saat ini adalah?", "Swift", Arrays.asList("Objective-C", "Kotlin", "Java"), "Mudah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Apa nama IDE resmi yang digunakan untuk mengembangkan aplikasi Android?", "Android Studio", Arrays.asList("Xcode", "Visual Studio Code", "Eclipse"), "Mudah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Metode siklus hidup (lifecycle) Activity yang dipanggil ketika Activity pertama kali dibuat adalah?", "onCreate()", Arrays.asList("onStart()", "onResume()", "onPause()"), "Mudah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Dalam Android Studio, file konfigurasi apa yang digunakan untuk mendaftarkan komponen aplikasi, permission, dan launcher activity?", "AndroidManifest.xml", Arrays.asList("build.gradle.kts", "strings.xml", "settings.gradle"), "Menengah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Toolkit modern buatan Google untuk membuat UI deklaratif native di Android adalah?", "Jetpack Compose", Arrays.asList("Android XML Layout", "Flutter UI", "SwiftUI"), "Menengah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Komponen Android yang berjalan di latar belakang (background) untuk memproses tugas berat tanpa UI disebut?", "Service", Arrays.asList("Activity", "Broadcast Receiver", "Intent"), "Menengah", "Mobile Development"));
            allQuestions.add(new QuizQuestion("Library populer untuk memuat gambar secara asinkron dari internet ke ImageView di Android adalah?", "Glide", Arrays.asList("Retrofit", "Room", "Navigation UI"), "Mudah", "Mobile Development"));

        } else if ("Artificial Intelligence".equalsIgnoreCase(categoryName)) {
            allQuestions.add(new QuizQuestion("Bidang AI yang berfokus pada kemampuan komputer untuk belajar dari data secara mandiri disebut?", "Machine Learning", Arrays.asList("Expert System", "Deep Fake", "Data Mining"), "Mudah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Algoritma ML yang meniru cara kerja struktur biologis otak manusia disebut?", "Neural Network", Arrays.asList("Decision Tree", "Linear Regression", "K-Means"), "Menengah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Apa kepanjangan dari NLP dalam konteks kecerdasan buatan?", "Natural Language Processing", Arrays.asList("Network Logic Protocol", "Neural Linear Programming", "Native Language Processor"), "Mudah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Algoritma pembelajaran mesin yang dilatih menggunakan data yang sudah berlabel disebut?", "Supervised Learning", Arrays.asList("Unsupervised Learning", "Reinforcement Learning", "Semi-supervised Learning"), "Menengah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Manakah dari berikut yang merupakan contoh model AI generatif berbasis Large Language Model (LLM)?", "ChatGPT", Arrays.asList("Google Search", "Tesla Autopilot", "IBM Deep Blue"), "Mudah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Tipe pembelajaran mesin di mana sistem belajar melalui sistem reward (hadiah) dan punishment (hukuman) adalah?", "Reinforcement Learning", Arrays.asList("Supervised Learning", "Unsupervised Learning", "Clustering"), "Menengah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Teknik deep learning mana yang sangat optimal digunakan untuk memproses data gambar (Computer Vision)?", "Convolutional Neural Network (CNN)", Arrays.asList("Recurrent Neural Network (RNN)", "Linear Regression", "Naive Bayes"), "Sulit", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Siapakah ilmuwan komputer yang mengusulkan 'Turing Test' untuk menguji kecerdasan mesin?", "Alan Turing", Arrays.asList("John McCarthy", "Ada Lovelace", "Geoffrey Hinton"), "Menengah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Dalam Machine Learning, apa yang dimaksud dengan 'Overfitting'?", "Model bekerja sangat baik pada data pelatihan tetapi buruk pada data baru", Arrays.asList("Model terlalu sederhana sehingga gagal belajar", "Ukuran model terlalu besar untuk disimpan", "Dataset pelatihan terlalu sedikit"), "Menengah", "Artificial Intelligence"));
            allQuestions.add(new QuizQuestion("Fungsi matematika yang digunakan untuk memperkenalkan sifat non-linear ke dalam node Jaringan Saraf Tiruan disebut?", "Activation Function", Arrays.asList("Cost Function", "Gradient Descent", "Loss Function"), "Sulit", "Artificial Intelligence"));

        } else if ("Cyber Security".equalsIgnoreCase(categoryName)) {
            allQuestions.add(new QuizQuestion("Praktik penipuan dengan mengirim email palsu yang menyerupai institusi resmi untuk mencuri data pribadi disebut?", "Phishing", Arrays.asList("Malware", "DDoS", "SQL Injection"), "Mudah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Jenis malware yang mengunci file korban dan meminta uang tebusan sebagai syarat pembukanya disebut?", "Ransomware", Arrays.asList("Spyware", "Adware", "Trojan"), "Mudah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Apa saja tiga pilar utama keamanan informasi yang menyusun konsep CIA Triad?", "Confidentiality, Integrity, Availability", Arrays.asList("Control, Identification, Authentication", "Cyber, Internet, Application", "Crypt, Key, Cipher"), "Menengah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Proses konversi teks biasa menjadi teks acak rahasia yang tidak dapat dibaca tanpa kunci dekripsi disebut?", "Enkripsi", Arrays.asList("Dekripsi", "Hashing", "Obfuscation"), "Mudah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Jenis serangan siber yang membanjiri server dengan lalu lintas palsu agar tidak bisa diakses pengguna asli disebut?", "DDoS Attack", Arrays.asList("Brute Force", "Man-in-the-Middle", "SQL Injection"), "Menengah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Tindakan menebak kata sandi secara berulang-ulang menggunakan kombinasi karakter hingga berhasil disebut?", "Brute Force Attack", Arrays.asList("Phishing", "Buffer Overflow", "Zero Day"), "Mudah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Dalam kriptografi, apa perbedaan utama enkripsi Simetris dengan Asimetris?", "Simetris menggunakan satu kunci, Asimetris menggunakan sepasang kunci", Arrays.asList("Simetris lebih lambat dibanding Asimetris", "Asimetris hanya bisa untuk data kecil", "Simetris tidak aman dikirim ke jaringan"), "Menengah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Perangkat lunak atau sistem yang memantau dan memfilter lalu lintas jaringan masuk dan keluar berdasarkan aturan keamanan disebut?", "Firewall", Arrays.asList("Antivirus", "Proxy Server", "VPN"), "Mudah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Jenis serangan siber di mana penyerang menyusup di antara komunikasi dua pihak untuk menguping atau mengubah data disebut?", "Man-in-the-Middle (MitM)", Arrays.asList("Phishing", "DDoS", "Cross-Site Scripting"), "Menengah", "Cyber Security"));
            allQuestions.add(new QuizQuestion("Serangan yang menyisipkan perintah query SQL berbahaya ke dalam form input web untuk mengakses database secara ilegal disebut?", "SQL Injection", Arrays.asList("Cross-Site Scripting", "Buffer Overflow", "Ransomware"), "Menengah", "Cyber Security"));

        } else if ("Data Science".equalsIgnoreCase(categoryName)) {
            allQuestions.add(new QuizQuestion("Bahasa pemrograman yang paling populer dan banyak digunakan oleh para Data Scientist saat ini adalah?", "Python", Arrays.asList("HTML", "PHP", "C#"), "Mudah", "Data Science"));
            allQuestions.add(new QuizQuestion("Library Python yang sangat populer digunakan untuk analisis dan manipulasi data berstruktur tabel adalah?", "Pandas", Arrays.asList("Matplotlib", "Scikit-Learn", "Flask"), "Mudah", "Data Science"));
            allQuestions.add(new QuizQuestion("Nilai tengah dari sekumpulan data yang telah diurutkan dari terkecil ke terbesar disebut?", "Median", Arrays.asList("Mean", "Modus", "Varian"), "Mudah", "Data Science"));
            allQuestions.add(new QuizQuestion("Library Python utama yang digunakan untuk membuat visualisasi grafik data dasar adalah?", "Matplotlib", Arrays.asList("Pandas", "NumPy", "Django"), "Mudah", "Data Science"));
            allQuestions.add(new QuizQuestion("Tahap membersihkan data dari nilai yang kosong (missing values), duplikasi, atau format yang salah disebut?", "Data Cleaning", Arrays.asList("Data Collection", "Data Modeling", "Data Presentation"), "Mudah", "Data Science"));
            allQuestions.add(new QuizQuestion("Algoritma ML sederhana yang digunakan untuk memprediksi nilai numerik kontinu (misal: harga rumah) adalah?", "Linear Regression", Arrays.asList("Logistic Regression", "K-Means Clustering", "Decision Tree"), "Menengah", "Data Science"));
            allQuestions.add(new QuizQuestion("Format penyimpanan data terstruktur berbasis baris dan kolom yang dipisahkan koma adalah?", "CSV", Arrays.asList("JSON", "XML", "PDF"), "Mudah", "Data Science"));
            allQuestions.add(new QuizQuestion("Library Python yang menyediakan struktur data array N-dimensi yang cepat dan efisien untuk operasi numerik adalah?", "NumPy", Arrays.asList("Pandas", "BeautifulSoup", "Seaborn"), "Menengah", "Data Science"));
            allQuestions.add(new QuizQuestion("Dalam pengolahan data, nilai ekstrim yang sangat jauh berbeda dari pola umum mayoritas data lainnya disebut?", "Outlier", Arrays.asList("Mean", "Varian", "Deviasi"), "Menengah", "Data Science"));
            allQuestions.add(new QuizQuestion("Metode klasterisasi tidak terawasi (unsupervised clustering) yang membagi data menjadi sejumlah K kelompok adalah?", "K-Means", Arrays.asList("Linear Regression", "Naive Bayes", "Random Forest"), "Menengah", "Data Science"));
        }

        // Pastikan selalu acak 10 pertanyaan dan acak juga urutannya
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(10, allQuestions.size()));
    }
}
