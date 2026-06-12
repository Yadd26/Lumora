<div align="center">

# Lumora

**Perpustakaan Digital Akademik Premium bergaya *Dark Academia* yang dirancang untuk pembelajar sejati.**

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](#)
[![Android](https://img.shields.io/badge/Android-API_30%2B-green?style=flat-square&logo=android)](#)
[![SQLite](https://img.shields.io/badge/SQLite-Database-blue?style=flat-square)](#)
[![Material](https://img.shields.io/badge/Material_Design-3-purple?style=flat-square)](#)

</div>

---

## Tentang Aplikasi

**Lumora** adalah aplikasi perpustakaan digital dan ruang belajar interaktif berbasis Android native yang mengusung estetika **Dark Academia**. Dirancang khusus sebagai proyek akhir semester, aplikasi ini bertujuan menciptakan ekosistem pembelajaran bebas distraksi dengan menyatukan buku digital, *learning path* terstruktur, kuis interaktif, serta forum diskusi komunitas ke dalam satu platform yang elegan.

---

## Fitur Utama

Berikut adalah modul navigasi & fitur unggulan yang terintegrasi di dalam Lumora:

### 📖 `01` Digital Reader & Library
* **Perpustakaan Offline**: Baca ratusan materi teks dan buku secara lokal.
* **Mode Fokus & Kustomisasi**: Atur ukuran font dan tema membaca (Sepia/Dark/Light).
* **Anotasi Pintar**: Tambahkan *bookmark* dan simpan *notes* atau *highlight* penting saat membaca.

### 🗺 `02` Academic Course System (Learning Path)
* **Modul Terstruktur**: Belajar selangkah demi selangkah mulai dari materi dasar hingga lanjutan.
* **Sistem Unlock**: Modul tingkat lanjut hanya akan terbuka jika pengguna telah menyelesaikan kuis atau materi pada modul sebelumnya.

### 🧠 `03` Interactive Trivia & Quizzes
* **Uji Pengetahuan**: Kerjakan kuis pilihan ganda yang disesuaikan dengan topik bacaan.
* **Analitik Hasil**: Dapatkan skor dan *feedback* instan setelah menyelesaikan ujian.

### 💬 `04` Scholar Community (Forum)
* **Diskusi Akademik**: Ajukan pertanyaan dan berdiskusi dengan sesama pelajar.
* **Sistem Reputasi**: Dapatkan poin dari *like* dan tandai jawaban terbaik (*Best Answer*).

### 🌤 `05` Universal Search 2.0
* **Pencarian Terpadu**: Cari buku, kursus, maupun *thread* forum hanya melalui satu bilah pencarian universal di bagian atas aplikasi.

### 👤 `06` Profile & Learning Analytics
* **Statistik Belajar**: Pantau tingkat penyelesaian kursus, riwayat bacaan, dan *streak* harian Anda.
* **Sertifikat Digital**: Hasilkan dan unduh sertifikat kelulusan dalam format file PDF.

---



## ✅ Pemenuhan Spesifikasi Teknis

Tabel berikut merangkum pemenuhan seluruh syarat teknis yang diwajibkan dalam project ini:

### 1. Activity — ✅ TERPENUHI

Aplikasi memiliki **lebih dari 7 Activity** yang berbeda, jauh melebihi syarat minimal 2 Activity:

| Activity | Keterangan |
| :--- | :--- |
| `SplashActivity` | **Launcher Activity** — Layar pembuka dengan animasi, cek sesi login |
| `MainActivity` | Activity utama yang menjadi host navigasi Bottom Nav + Fragment |
| `LoginActivity` | Halaman login pengguna |
| `RegisterActivity` | Halaman registrasi pengguna baru |
| `ReaderActivity` | Layar khusus untuk membaca konten buku digital |
| `QuizActivity` | Halaman eksekusi kuis interaktif |
| `TutorialDetailActivity` | Layar penjelasan detail tutorial/materi |

> `SplashActivity` adalah **Launcher Activity** yang didefinisikan dengan `intent-filter ACTION_MAIN + CATEGORY_LAUNCHER` pada `AndroidManifest.xml`.

---

### 2. Intent — ✅ TERPENUHI

`Intent` (eksplisit) digunakan secara konsisten untuk berpindah antar Activity dan membawa data antar layar:

| Dari | Ke | Keterangan |
| :--- | :--- | :--- |
| `SplashActivity` | `MainActivity` | Jika sesi login aktif |
| `SplashActivity` | `LoginActivity` | Jika belum login |
| `LoginActivity` | `RegisterActivity` | Daftar akun baru |
| `HomeFragment` | `ReaderActivity` | Membuka buku (mengirim ID Buku via Extra) |
| `LearningPathFragment`| `QuizActivity` | Memulai kuis (mengirim ID Kuis via Extra) |

---

### 3. RecyclerView — ✅ TERPENUHI

`RecyclerView` digunakan di banyak tempat dengan adapter yang dinamis dan terpisah:

| Adapter | Digunakan di | Data yang Ditampilkan |
| :--- | :--- | :--- |
| `BookAdapter` | `HomeFragment` | Menampilkan koleksi buku terbaru/populer |
| `LearningPathAdapter` | `LearningPathFragment` | Daftar modul kursus akademik |
| `DiscussionAdapter` | `DiscussionFragment` | Menampilkan *thread* dan diskusi forum |
| `QuizCategoryAdapter`| `QuizFragment` | Kategori ujian dan kuis |

---

### 4. Fragment & Navigation Component — ✅ TERPENUHI

Aplikasi memiliki **banyak Fragment** yang dikelola sepenuhnya oleh Jetpack Navigation Component:

| Fragment | Fungsi |
| :--- | :--- |
| `HomeFragment` | Dashboard utama (buku terbaru, *resume reading*) |
| `LearningPathFragment`| Peta jalur belajar terstruktur |
| `DiscussionFragment` | Forum komunitas pengguna |
| `QuizFragment` | Portal kuis dan trivia |
| `ProfileFragment` | Profil pengguna dan analitik pembelajaran |

Navigasi antar Fragment diatur melalui `nav_graph.xml` menggunakan `NavController` yang terhubung ke `BottomNavigationView`.

---

### 5. Background Thread — ✅ TERPENUHI

Semua operasi berat dieksekusi di latar belakang menggunakan **`ExecutorService`**:

| Kelas / Modul | Implementasi | Operasi yang Dijalankan |
| :--- | :--- | :--- |
| `DatabaseHelper` | `ExecutorService` (Background Thread) | Insert, Update, Delete, Query ke SQLite lokal |
| `Network Call` | Asynchronous Thread via Retrofit | Pengambilan data buku dari API |
| `PDF Generator` | `ExecutorService` | Pembuatan sertifikat PDF kelulusan |

---

### 6. Networking (Retrofit) — ✅ TERPENUHI

Aplikasi mengintegrasikan **Retrofit 2** untuk mengambil data secara dinamis dari REST API:

**Library yang digunakan:**
- `Retrofit 2` — HTTP client untuk Android
- `Gson Converter` — Parsing response JSON dari API

**API yang diintegrasikan:**
- Mengambil data buku dan referensi literatur dari **Open Library API** (`ApiClient.java`).

**Tombol Refresh / Network Error Handling:** ✅
- Dilengkapi dengan *SwipeRefreshLayout* dan tombol aksi untuk mengambil ulang data (*Pull-to-Refresh*) ketika terjadi masalah koneksi atau kegagalan *request*.

---

### 7. Local Data Persistent — ✅ TERPENUHI

Aplikasi menggunakan **dua mekanisme** penyimpanan data lokal:

#### a) SQLite Database (`DatabaseHelper`)
Digunakan untuk menyimpan data secara terstruktur:
- Data pengguna, kemajuan belajar, *bookmark*, riwayat forum (diskusi), dan *notes*.
- Karena menggunakan SQLite secara komprehensif, fitur aplikasi dapat berfungsi maksimal dalam mode **Offline**.

#### b) SharedPreferences
`SharedPreferences` digunakan untuk menyimpan data sesi dan preferensi yang ringan:
- `SessionManager`: Menyimpan sesi login pengguna agar tidak perlu login berulang kali.
- `PreferenceManager`: Menyimpan preferensi tema (Dark Mode / Light Mode).

#### c) Dua Tema (Dark / Light) ✅
Aplikasi mendukung skema warna ganda dengan palet eksklusif *Dark Academia*:
- **Dark Mode** — `values-night/themes.xml` & `colors.xml`
- **Light Mode** — `values/themes.xml` & `colors.xml`
- Semua antarmuka telah memenuhi standar rasio kontras warna WCAG AA untuk kenyamanan membaca berjam-jam.

---

## Tech Stack & Arsitektur

| Komponen | Implementasi Teknologi |
| :--- | :--- |
| **Language** | <code>Java</code> |
| **User Interface** | <code>Material Design 3</code>, Custom Font (Cormorant, Cinzel) |
| **Database** | <code>SQLite OpenHelper</code>, <code>SharedPreferences</code> |
| **Networking** | <code>Retrofit 2</code>, <code>Gson</code> |
| **Architecture** | <code>MVC/MVP pattern</code> |

---

## Cara Menjalankan

**Prasyarat:** Android Studio + JDK 17 + device/emulator Android 11+ (API 30+)

```bash
# 1. Clone repo
git clone https://github.com/Yadd26/Lumora.git

# 2. Buka di Android Studio, lalu sync Gradle

# 3. Jalankan aplikasi
./gradlew assembleDebug
```

---

## Struktur Project

```
app/src/main/java/com/lumora/app/
├── activities/       # Login, Register, Splash, Main, Reader, Quiz
├── fragments/        # Home, LearningPath, Discussion, Profile, Quiz
├── adapters/         # RecyclerView adapters (Book, Discussion, dll)
├── models/           # Data Class (User, Book, Discussion)
├── network/          # Retrofit ApiClient & ApiService
├── database/         # DatabaseHelper SQLite
├── preferences/      # SessionManager, PreferenceManager
└── utils/            # Utilities (Constants, dll)
```

---

<sub>Dibuat untuk Tugas Final Mobile Programming · Juni 2026</sub>
