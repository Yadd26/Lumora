# Lumora: Where Ancient Wisdom Meets Modern Learning

Lumora adalah Perpustakaan Digital Akademik Premium yang menggabungkan nuansa *Ancient Library* dan *Dark Academia* dengan fitur pembelajaran modern. Aplikasi ini dirancang sebagai ekosistem pembelajaran akademik lengkap di mana pengguna dapat membaca, belajar, berlatih, dan berdiskusi.

Proyek ini dibuat untuk memenuhi tugas final mata kuliah Pemrograman Mobile.

## 🌟 Fitur Utama (Cara Penggunaan)

1. **Digital Reader 2.0 & Perpustakaan Offline**: Baca buku digital dengan dukungan mode Fokus, penyesuaian ukuran font, tema Sepia/Dark/Light, serta fitur penanda (bookmark) dan catatan (highlight/note).
2. **Academic Course System**: Modul pembelajaran interaktif terstruktur mulai dari materi, tutorial, latihan, hingga kuis. Modul baru akan terbuka (unlock) jika sub-modul sebelumnya diselesaikan.
3. **Forum Akademik (Scholar Community)**: Diskusikan materi pembelajaran. Forum dilengkapi dengan sistem reputasi poin, penanda jawaban terbaik (*Solved/Best Answer*), pemberian *like*, balasan, dan *badges* tingkat kemahiran (*Knowledge Seeker*, *Helpful Scholar*, dll).
4. **Universal Search 2.0**: Cari buku, tutorial, kursus, maupun *thread* forum melalui satu fitur pencarian universal di bagian atas layar.
5. **Leveling & Analytics**: Analitik lengkap riwayat bacaan, streak harian, kursus yang diselesaikan, hingga sertifikat kelulusan dalam bentuk file PDF.

## 🛠 Penjelasan Implementasi Teknis

Aplikasi Lumora dikembangkan dengan memenuhi spesifikasi teknis berikut:

### 1. Activity & Intent
- **Activity**: Terdapat lebih dari 2 Activity, di antaranya `SplashActivity` sebagai launcher, `MainActivity`, `ReaderActivity`, `QuizActivity`, dan berbagai activity detail lainnya.
- **Intent**: Intent eksplisit digunakan secara luas untuk navigasi dari daftar (`RecyclerView`) menuju halaman detail (contoh: membuka materi atau kuis dari *Learning Path*).

### 2. Fragment & Navigation Component
- **Fragment**: Aplikasi menggunakan lebih dari 2 *Fragment*, antara lain `HomeFragment`, `TutorialFragment`, `QuizFragment`, `DiscussionFragment`, dan `ProfileFragment`.
- **Navigation**: Seluruh perpindahan antar-menu utama pada *Bottom Navigation Bar* dikelola menggunakan *Jetpack Navigation Component* (`nav_graph.xml`).

### 3. RecyclerView & Adapter
Menampilkan berbagai daftar dengan performa yang lancar menggunakan `RecyclerView`, seperti daftar buku, daftar modul pembelajaran (*Learning Path*), daftar *thread* forum, hingga slide *Onboarding* menggunakan `ViewPager2`.

### 4. Background Thread (Executor)
Operasi berat seperti pengambilan data dari internet, pengisian basis data (*seed database*), pembuatan file PDF sertifikat, serta manipulasi data lokal (Insert/Update SQLite) dieksekusi secara asinkron di latar belakang (Background Thread) menggunakan `ExecutorService` untuk menjaga kelancaran antarmuka (UI Thread).

### 5. Networking (Retrofit & API)
- **API**: Mengambil data buku dari API publik **Open Library API**.
- **Retrofit**: Implementasi jaringan dikelola secara efisien menggunakan Retrofit2 + Gson Converter.
- **Refresh Control**: Tersedia penanganan ketika gagal memuat data/kondisi jaringan buruk dengan menggunakan tombol aksi atau mekanisme *Pull-to-Refresh* (SwipeRefreshLayout) untuk mencoba ulang *request*.

### 6. Local Data Persistent (SQLite & SharedPreferences)
- **SQLite**: Menyimpan seluruh kemajuan belajar, riwayat forum (diskusi, reputasi), *bookmark* buku, dan catatan (*notes/highlights*) ke dalam basis data SQLite lokal secara persisten. Data ini dapat ditampilkan sepenuhnya bahkan tanpa koneksi internet (Fitur Offline).
- **SharedPreferences**: Mengelola dan menyimpan sesi login pengguna (Session Manager), pengaturan tema preferensi, serta *flag* penyelesaian Onboarding (*Welcome Tour*).

### 7. Dual Theme (Dark Theme & Light Theme)
Mendukung perpindahan tema dari *Light Mode* ke *Dark Mode* secara sistem maupun manual. Antarmuka UI (komponen warna, kontras teks, palet warna *Dark Academia*) telah memenuhi standar kelayakan aksesibilitas warna WCAG AA.

---

## 🎨 User Interface (UI/UX)
Dibangun menggunakan *Material Design 3* dengan pendekatan desain *Dark Academia*, sehingga memberikan kesan perpustakaan kuno, manuskrip bersejarah, dan elit akademik. Termasuk *custom font* (Cormorant Garamond, Cinzel), animasi elemen, interaksi responsif (minimal area tekan 48dp), hingga *Premium Splash Screen*.

## 👨‍💻 Dokumentasi
Seluruh *source code* didokumentasikan di repository GitHub ini dengan mengadopsi standar *semantic commit*.

---
*Dibuat untuk Tugas Final Mobile Programming (Batas Pengerjaan: 12 Juni 2026).*
