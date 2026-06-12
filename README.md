# Lumora - Digital Academic Archive 🏛️📚

**Where Ancient Wisdom Meets Modern Learning**

Lumora adalah aplikasi Perpustakaan Digital Akademik Premium dengan estetika *Dark Academia* yang mengintegrasikan repositori literatur terbuka dengan platform pembelajaran digital. Aplikasi ini dirancang untuk memenuhi spesifikasi teknis pengembangan aplikasi Android modern.

---

## 📖 Deskripsi Aplikasi

Lumora menggabungkan fitur perpustakaan kuno (*Ancient Library*) dengan platform pembelajaran (*Learning Center*). Pengguna dapat menelusuri literatur global melalui *Open Library API*, mendaftarkan diri dalam *Course* pembelajaran terstruktur, membaca buku secara *offline*, dan mengelola catatan akademis. Aplikasi ini ditujukan bagi mahasiswa dan akademisi untuk menciptakan ekosistem pembelajaran (*Digital Academic Archive*) yang komprehensif.

## 🚀 Cara Penggunaan

1. **Kloning & Build**: Kloning repositori ini dan buka menggunakan Android Studio (Gradle 8+). Sinkronisasikan *project* dan jalankan pada emulator atau perangkat fisik Android.
2. **Login/Register**: Saat aplikasi pertama kali dibuka (*Splash Activity* berlanjut ke *Login/Register Activity*), buat akun baru (tersimpan secara lokal) untuk masuk ke menu utama.
3. **Jelajahi Arsip**: Pada halaman utama (*Archive*), Anda dapat melihat buku populer atau mencari buku spesifik. Jika internet terputus, Anda dapat menggunakan fitur **Refresh** saat koneksi kembali.
4. **Learning Center**: Navigasi ke tab *Learning Center* melalui *Bottom Navigation* untuk melihat modul pembelajaran, tutorial, latihan, dan kuis.
5. **Koleksi & Baca**: Simpan buku ke koleksi lokal (*Bookmark*), dan buka *ReaderActivity* untuk membaca sekaligus membuat catatan (*Academic Notes*) yang dapat diakses secara *offline*.
6. **Tema Visual**: Aplikasi mendukung mode Gelap (*Dark Theme*) dan Terang (*Light Theme*) yang dapat menyesuaikan dengan sistem perangkat Anda (Estetika *Dark Academia* dioptimalkan untuk mode gelap).

---

## 🛠️ Implementasi Spesifikasi Teknis

Lumora dibangun secara spesifik untuk mematuhi seluruh kriteria penilaian dan spesifikasi teknis pengembangan Android, sebagai berikut:

### 1. Activity (Minimal 2 Activity)
Aplikasi ini memiliki lebih dari dua *Activity* utama:
*   `SplashActivity` (Sebagai **Launcher** aplikasi).
*   `MainActivity` (Sebagai penampung Navigation Component / Bottom Nav).
*   `LoginActivity` & `RegisterActivity` (Untuk autentikasi).
*   `ReaderActivity` & `DetailActivity` (Untuk konten detail dan pengalaman membaca).

### 2. Intent
*Intent* digunakan secara ekstensif untuk berkomunikasi dan berpindah antar *Activity*. Contoh:
*   Berpindah dari `SplashActivity` ke `LoginActivity`.
*   Berpindah dari `LoginActivity` ke `MainActivity` (dengan membawa data *session* user).
*   Membuka `DetailActivity` dan `ReaderActivity` dengan menyisipkan `putExtra` (ID Buku, Judul, Author) untuk dirender pada *Activity* tujuan.

### 3. RecyclerView
`RecyclerView` adalah komponen utama untuk menampilkan seluruh daftar data dinamis di Lumora:
*   Menampilkan daftar Buku Populer, Referensi Akademik, dan Hasil Pencarian di halaman Archive.
*   Menampilkan daftar *Course* Pembelajaran di *Learning Center*.
*   Menampilkan daftar koleksi buku tersimpan (*Bookmark*).

### 4. Fragment & Navigation Component
Aplikasi ini mengadopsi arsitektur *Single-Activity Multiple-Fragments* pada bagian utamanya:
*   Memiliki lebih dari dua Fragment: `HomeFragment` (Archive), `LearningCenterFragment`, `BookmarkFragment`, dan `ProfileFragment`.
*   Menggunakan **Android Jetpack Navigation Component** (`nav_graph.xml`) yang terhubung langsung dengan `BottomNavigationView` untuk mengelola transisi antarmuka secara *seamless*.

### 5. Background Thread (Executor)
Operasi berat di latar belakang tidak memblokir *Main UI Thread*. Lumora menggunakan **`ExecutorService`**:
*   Pengambilan dan penyimpanan data dari/ke SQLite (seperti `DatabaseHelper.insertNote`, `getCurrentStreak`).
*   Kalkulasi progres harian dan rendering data statistik pada *Dashboard Learning Center* dilakukan di dalam `executorService.execute(() -> { ... })` sebelum di-post kembali ke UI melalui `runOnUiThread`.

### 6. Networking (Retrofit & API)
*   Mengambil pustaka data secara *real-time* dari **Open Library API** menggunakan pustaka **Retrofit2**.
*   Data buku (`OpenLibraryResponse`) di-*parsing* menggunakan *converter* Gson dan ditampilkan secara visual ke dalam `RecyclerView` beserta *Cover Image*-nya (menggunakan Glide).
*   Terdapat fungsionalitas `SwipeRefreshLayout` yang bertindak sebagai **Tombol Refresh** manakala data gagal dimuat (misalnya saat kondisi tidak ada jaringan).

### 7. Local Data Persistent & Tema
*   **SQLite**: Digunakan secara ekstensif untuk menyimpan data terstruktur (Tabel `users`, `bookmarks`, `courses`, `notes`, `module_progress`). Data seperti *Bookmark* dan *Academic Notes* sepenuhnya tersedia dan dapat ditampilkan kembali ketika aplikasi **tidak terhubung ke jaringan** (Mode *Offline*).
*   **SharedPreferences**: Digunakan untuk mengelola *Session Login* (menyimpan `user_id` aktif) dan pengaturan target belajar harian.
*   **Tema (Dark/Light Theme)**: Aplikasi menerapkan pewarnaan dinamis `values/colors.xml` (Light) dan `values-night/colors.xml` (Dark). Palet warna disesuaikan agar estetika *Dark Academia* tetap elegan di kedua mode, mengikuti konfigurasi perangkat pengguna (Material Design 3).

---
*Dikembangkan menggunakan pendekatan Semantic Commit pada GitHub untuk keteraturan dokumentasi dan riwayat versi.*
