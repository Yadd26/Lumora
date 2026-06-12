# Lumora - Digital Academic Archive 🏛️📚

**Where Ancient Wisdom Meets Modern Learning**

Lumora adalah aplikasi Perpustakaan Digital Akademik Premium yang dirancang khusus untuk mahasiswa, akademisi, dan pembelajar seumur hidup. Mengusung estetika *Dark Academia* dan *Ancient Library*, Lumora menawarkan pengalaman membaca dan belajar yang mendalam, terstruktur, dan terintegrasi secara offline.

## ✨ Fitur Utama (Features)

*   **Archive (Perpustakaan Digital Terbuka)**: Akses ke ribuan literatur melalui integrasi langsung dengan **Open Library API**.
*   **Learning Center 2.0**: Ekosistem pembelajaran terstruktur yang mencakup:
    *   **Courses**: Modul pembelajaran berjenjang.
    *   **Tutorials**: Panduan langkah demi langkah untuk setiap subjek.
    *   **Exercises & Quizzes**: Latihan soal dan evaluasi bergradasi untuk mengukur pemahaman.
    *   **Certificates**: Penerbitan sertifikat digital otomatis saat mencapai progres 100%.
*   **Digital Reader 3.0 & Academic Notes**: Pengalaman membaca layaknya Kindle/Google Books, dilengkapi fitur anotasi (catatan akademis), kustomisasi tema (Sepia/Dark Mode), dan penyimpanan offline.
*   **Offline Library**: Mengunduh buku dan materi untuk diakses tanpa koneksi internet.
*   **Scholar Profile & Analytics**: Melacak waktu belajar, pencapaian (*streak*), dan histori pembelajaran secara komprehensif.

## 🛠️ Implementasi Teknis (Tech Stack)

Aplikasi ini dibangun menggunakan arsitektur monolitik Android tradisional dengan komponen yang solid dan teruji:

*   **Bahasa Pemrograman**: Java
*   **Arsitektur UI**: Android View System (XML) & Material Design 3
*   **Navigasi**: Android Jetpack Navigation Component (Single Activity, Multiple Fragments)
*   **Database & Penyimpanan**: SQLite OpenHelper (Skema Relasional) & SharedPreferences (Session Manager)
*   **Networking**: Retrofit2 (untuk Open Library API)
*   **Konkurensi**: `ExecutorService` untuk proses *background* dan pemuatan data asinkron.
*   **Tema Visual**: Desain kustom *Dark Academia* dengan sistem *Day/Night Mode* dinamis.

## 🚀 Cara Penggunaan (Getting Started)

### Prasyarat
*   Android Studio (versi terbaru yang mendukung Gradle 8+)
*   Android SDK (Target API 34, Min API 24)
*   Koneksi internet untuk *build* pertama (mengunduh *dependencies* dan memanggil *Open Library API*).

### Instalasi & Menjalankan Aplikasi
1.  **Kloning Repositori**:
    ```bash
    git clone https://github.com/username/lumora.git
    cd lumora
    ```
2.  **Buka di Android Studio**:
    *   Pilih `File` -> `Open` dan arahkan ke direktori `lumora`.
    *   Tunggu hingga proses sinkronisasi Gradle selesai.
3.  **Jalankan Aplikasi**:
    *   Pilih emulator (AVD) atau perangkat Android fisik yang sudah terhubung (Pastikan USB Debugging aktif).
    *   Klik tombol **Run** (`Shift + F10`) di Android Studio.

### Alur Penggunaan Dasar
1.  **Registrasi & Login**: Buat akun *Scholar* lokal (data disimpan di SQLite).
2.  **Jelajahi Arsip**: Gunakan tab **Archive** untuk mencari dan mengunduh buku.
3.  **Mulai Belajar**: Buka tab **Learning Center**, pilih *Course*, lalu selesaikan modul, latihan, hingga mendapatkan sertifikat.
4.  **Baca & Catat**: Buka koleksi pribadi Anda, masuk ke *Reader Mode*, dan tambahkan catatan akademis.

## 🤝 Kontribusi (Contributing)

Kami menggunakan **Semantic Commit Messages** untuk setiap kontribusi:
*   `feat`: Menambahkan fitur baru.
*   `fix`: Memperbaiki *bug*.
*   `docs`: Perubahan pada dokumentasi (termasuk `README.md`).
*   `style`: Perbaikan format kode (spasi, indentasi, dll).
*   `refactor`: Perubahan struktur kode tanpa mengubah fungsi.
*   `perf`: Peningkatan performa aplikasi.

---
*Dikembangkan dengan penuh dedikasi untuk mendukung pembelajaran global.*
