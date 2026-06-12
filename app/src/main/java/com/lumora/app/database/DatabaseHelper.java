package com.lumora.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lumora.app.models.Book;
import com.lumora.app.models.Bookmark;
import com.lumora.app.models.User;
import com.lumora.app.models.Tutorial;
import com.lumora.app.models.LearningHistoryItem;
import com.lumora.app.models.Note;
import com.lumora.app.models.Highlight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DatabaseHelper - Pengelola database SQLite untuk aplikasi Lumora.
 * Mengelola tabel: bookmarks, discussions, users, quiz_history,
 * tutorials, tutorial_progress, learning_path_progress, book_progress, dan learning_history.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lumora.db";
    private static final int DATABASE_VERSION = 10; // Dinaikkan ke versi 10 untuk penambahan fitur Course System 3.0 dan Quiz 3.0

    // Tabel: bookmarks
    private static final String TABLE_BOOKMARKS = "bookmarks";
    private static final String COL_BOOKMARK_ID = "id";
    private static final String COL_BOOKMARK_TITLE = "title";
    private static final String COL_BOOKMARK_AUTHOR = "author";
    private static final String COL_BOOKMARK_YEAR = "year";
    private static final String COL_BOOKMARK_COVER_URL = "coverUrl";
    private static final String COL_BOOKMARK_BOOK_KEY = "bookKey";
    private static final String COL_BOOKMARK_SUBJECT = "subject";
    private static final String COL_BOOKMARK_EDITION_COUNT = "editionCount";

    // Tabel: discussions
    private static final String TABLE_DISCUSSIONS = "discussions";
    private static final String COL_DISCUSSION_ID = "id";
    private static final String COL_DISCUSSION_TITLE = "title";
    private static final String COL_DISCUSSION_CONTENT = "content";

    // Tabel: users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_CREATED_AT = "created_at";

    // Tabel: quiz_history
    private static final String TABLE_QUIZ_HISTORY = "quiz_history";
    private static final String COL_QUIZ_ID = "id";
    private static final String COL_QUIZ_CATEGORY = "category";
    private static final String COL_QUIZ_SCORE = "score";
    private static final String COL_QUIZ_TOTAL = "total_question";
    private static final String COL_QUIZ_DATE = "date";

    // Tabel Baru: tutorials (Konten lokal)
    private static final String TABLE_TUTORIALS = "tutorials";
    private static final String COL_TUTORIAL_ID = "id";
    private static final String COL_TUTORIAL_TITLE = "title";
    private static final String COL_TUTORIAL_CATEGORY = "category";
    private static final String COL_TUTORIAL_DIFFICULTY = "difficulty";
    private static final String COL_TUTORIAL_TIME = "time_estimation";
    private static final String COL_TUTORIAL_DESC = "description";
    private static final String COL_TUTORIAL_CONCEPTS = "concepts";
    private static final String COL_TUTORIAL_STEPS = "steps";
    private static final String COL_TUTORIAL_IMPLEMENTATION = "implementation";
    private static final String COL_TUTORIAL_CODE = "code_example";
    private static final String COL_TUTORIAL_OUTPUT = "output_example";
    private static final String COL_TUTORIAL_TIPS = "academic_tips";
    private static final String COL_TUTORIAL_BOOK_QUERY = "related_book_query";

    // Tabel Baru: tutorial_progress
    private static final String TABLE_TUTORIAL_PROGRESS = "tutorial_progress";
    private static final String COL_TP_ID = "id";
    private static final String COL_TP_USER_ID = "user_id";
    private static final String COL_TP_TUTORIAL_ID = "tutorial_id";
    private static final String COL_TP_STATUS = "status";
    private static final String COL_TP_COMPLETION = "completion";
    private static final String COL_TP_LAST_OPENED = "last_opened";

    // Tabel Baru: learning_path_progress
    private static final String TABLE_PATH_PROGRESS = "learning_path_progress";
    private static final String COL_LPP_ID = "id";
    private static final String COL_LPP_USER_ID = "user_id";
    private static final String COL_LPP_PATH_NAME = "path_name";
    private static final String COL_LPP_MODULE_NAME = "module_name";
    private static final String COL_LPP_STATUS = "status";
    private static final String COL_LPP_PROGRESS = "progress";
    private static final String COL_LPP_LAST_ACCESS = "last_access";

    // Tabel Baru: book_progress
    private static final String TABLE_BOOK_PROGRESS = "book_progress";
    private static final String COL_BP_ID = "id";
    private static final String COL_BP_USER_ID = "user_id";
    private static final String COL_BP_BOOK_KEY = "book_key";
    private static final String COL_BP_TITLE = "book_title";
    private static final String COL_BP_AUTHOR = "book_author";
    private static final String COL_BP_COVER = "book_cover";
    private static final String COL_BP_CHAPTER = "current_chapter";
    private static final String COL_BP_PROGRESS = "progress";
    private static final String COL_BP_LAST_READ = "last_read";

    // Tabel Baru: learning_history (Riwayat Pembelajaran Terpadu)
    private static final String TABLE_LEARNING_HISTORY = "learning_history";
    private static final String COL_LH_ID = "id";
    private static final String COL_LH_USER_ID = "user_id";
    private static final String COL_LH_TITLE = "title";
    private static final String COL_LH_CATEGORY = "category";
    private static final String COL_LH_TYPE = "type"; // BOOK, TUTORIAL, QUIZ
    private static final String COL_LH_ACCESS_DATE = "access_date";

    // Tabel Baru: achievements
    private static final String TABLE_ACHIEVEMENTS = "achievements";
    private static final String COL_ACH_ID = "id";
    private static final String COL_ACH_USER_ID = "user_id";
    private static final String COL_ACH_TITLE = "title";
    private static final String COL_ACH_DESC = "description";
    private static final String COL_ACH_EARNED_AT = "earned_at";

    // Tabel Baru: notes
    private static final String TABLE_NOTES = "notes";
    private static final String COL_NOTE_ID = "id";
    private static final String COL_NOTE_USER_ID = "user_id";
    private static final String COL_NOTE_BOOK_KEY = "book_key";
    private static final String COL_NOTE_TITLE = "title";
    private static final String COL_NOTE_CONTENT = "content";
    private static final String COL_NOTE_CREATED_AT = "created_at";
    private static final String COL_NOTE_UPDATED_AT = "updated_at";

    // Tabel Baru: highlights
    private static final String TABLE_HIGHLIGHTS = "highlights";
    private static final String COL_HL_ID = "id";
    private static final String COL_HL_USER_ID = "user_id";
    private static final String COL_HL_BOOK_KEY = "book_key";
    private static final String COL_HL_SELECTED_TEXT = "selected_text";
    private static final String COL_HL_COLOR = "color";
    private static final String COL_HL_CREATED_AT = "created_at";

    
    // Tabel Baru: learning_streak
    private static final String TABLE_STREAK = "learning_streak";
    private static final String COL_STR_ID = "id";
    private static final String COL_STR_USER_ID = "user_id";
    private static final String COL_STR_CURRENT = "current_streak";
    private static final String COL_STR_BEST = "best_streak";
    private static final String COL_STR_LAST_ACTIVITY = "last_activity";

    // Tabel Baru: daily_study_time
    private static final String TABLE_STUDY_TIME = "daily_study_time";
    private static final String COL_DST_ID = "id";
    private static final String COL_DST_USER_ID = "user_id";
    private static final String COL_DST_DATE = "study_date";
    private static final String COL_DST_DURATION = "duration_minutes";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Bookmarks Table
        db.execSQL("CREATE TABLE " + TABLE_BOOKMARKS + " ("
                + COL_BOOKMARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BOOKMARK_TITLE + " TEXT, "
                + COL_BOOKMARK_AUTHOR + " TEXT, "
                + COL_BOOKMARK_YEAR + " TEXT, "
                + COL_BOOKMARK_COVER_URL + " TEXT, "
                + COL_BOOKMARK_BOOK_KEY + " TEXT UNIQUE, "
                + COL_BOOKMARK_SUBJECT + " TEXT, "
                + COL_BOOKMARK_EDITION_COUNT + " INTEGER)");

        // 2. Discussions Table (Forum 2.0 Thread)
        db.execSQL("CREATE TABLE " + TABLE_DISCUSSIONS + " ("
                + COL_DISCUSSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DISCUSSION_TITLE + " TEXT, "
                + "category TEXT, "
                + COL_DISCUSSION_CONTENT + " TEXT, "
                + "author TEXT, "
                + "date TEXT, "
                + "replies_count INTEGER DEFAULT 0, "
                + "is_solved INTEGER DEFAULT 0, "
                + "best_answer TEXT, "
                + "likes_count INTEGER DEFAULT 0)");

        // 3. Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT, "
                + COL_USER_EMAIL + " TEXT UNIQUE, "
                + COL_USER_PASSWORD + " TEXT, "
                + COL_USER_CREATED_AT + " TEXT)");

        // 4. Quiz History Table
        db.execSQL("CREATE TABLE " + TABLE_QUIZ_HISTORY + " ("
                + COL_QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_QUIZ_CATEGORY + " TEXT, "
                + COL_QUIZ_SCORE + " INTEGER, "
                + COL_QUIZ_TOTAL + " INTEGER, "
                + COL_QUIZ_DATE + " TEXT)");

        // 5. Tutorials Table
        db.execSQL("CREATE TABLE " + TABLE_TUTORIALS + " ("
                + COL_TUTORIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TUTORIAL_TITLE + " TEXT, "
                + COL_TUTORIAL_CATEGORY + " TEXT, "
                + COL_TUTORIAL_DIFFICULTY + " TEXT, "
                + COL_TUTORIAL_TIME + " TEXT, "
                + COL_TUTORIAL_DESC + " TEXT, "
                + COL_TUTORIAL_CONCEPTS + " TEXT, "
                + COL_TUTORIAL_STEPS + " TEXT, "
                + COL_TUTORIAL_IMPLEMENTATION + " TEXT, "
                + COL_TUTORIAL_CODE + " TEXT, "
                + COL_TUTORIAL_OUTPUT + " TEXT, "
                + COL_TUTORIAL_TIPS + " TEXT, "
                + COL_TUTORIAL_BOOK_QUERY + " TEXT)");

        // 6. Tutorial Progress Table
        db.execSQL("CREATE TABLE " + TABLE_TUTORIAL_PROGRESS + " ("
                + COL_TP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TP_USER_ID + " INTEGER, "
                + COL_TP_TUTORIAL_ID + " INTEGER, "
                + COL_TP_STATUS + " TEXT, "
                + COL_TP_COMPLETION + " INTEGER, "
                + COL_TP_LAST_OPENED + " TEXT)");

        // 7. Learning Path Progress Table
        db.execSQL("CREATE TABLE " + TABLE_PATH_PROGRESS + " ("
                + COL_LPP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LPP_USER_ID + " INTEGER, "
                + COL_LPP_PATH_NAME + " TEXT, "
                + COL_LPP_MODULE_NAME + " TEXT, "
                + COL_LPP_STATUS + " TEXT, "
                + COL_LPP_PROGRESS + " INTEGER, "
                + COL_LPP_LAST_ACCESS + " TEXT)");

        // 8. Book Progress Table
        db.execSQL("CREATE TABLE " + TABLE_BOOK_PROGRESS + " ("
                + COL_BP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BP_USER_ID + " INTEGER, "
                + COL_BP_BOOK_KEY + " TEXT UNIQUE, "
                + COL_BP_TITLE + " TEXT, "
                + COL_BP_AUTHOR + " TEXT, "
                + COL_BP_COVER + " TEXT, "
                + COL_BP_CHAPTER + " INTEGER, "
                + COL_BP_PROGRESS + " INTEGER, "
                + COL_BP_LAST_READ + " TEXT)");

        // 9. Learning History Table (Terpadu)
        db.execSQL("CREATE TABLE " + TABLE_LEARNING_HISTORY + " ("
                + COL_LH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LH_USER_ID + " INTEGER, "
                + COL_LH_TITLE + " TEXT, "
                + COL_LH_CATEGORY + " TEXT, "
                + COL_LH_TYPE + " TEXT, "
                + COL_LH_ACCESS_DATE + " TEXT)");

        // 10. Achievements Table
        db.execSQL("CREATE TABLE " + TABLE_ACHIEVEMENTS + " ("
                + COL_ACH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ACH_USER_ID + " INTEGER, "
                + COL_ACH_TITLE + " TEXT, "
                + COL_ACH_DESC + " TEXT, "
                + COL_ACH_EARNED_AT + " TEXT)");

        // 11. Notes Table
        db.execSQL("CREATE TABLE " + TABLE_NOTES + " ("
                + COL_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOTE_USER_ID + " INTEGER, "
                + COL_NOTE_BOOK_KEY + " TEXT, "
                + COL_NOTE_TITLE + " TEXT, "
                + COL_NOTE_CONTENT + " TEXT, "
                + COL_NOTE_CREATED_AT + " TEXT, "
                + COL_NOTE_UPDATED_AT + " TEXT)");

        // 12. Highlights Table
        db.execSQL("CREATE TABLE " + TABLE_HIGHLIGHTS + " ("
                + COL_HL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_HL_USER_ID + " INTEGER, "
                + COL_HL_BOOK_KEY + " TEXT, "
                + COL_HL_SELECTED_TEXT + " TEXT, "
                + COL_HL_COLOR + " TEXT, "
                + COL_HL_CREATED_AT + " TEXT)");

        // 13. Journals Table

        // 14. Learning Streak Table
        db.execSQL("CREATE TABLE " + TABLE_STREAK + " ("
                + COL_STR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_STR_USER_ID + " INTEGER, "
                + COL_STR_CURRENT + " INTEGER, "
                + COL_STR_BEST + " INTEGER, "
                + COL_STR_LAST_ACTIVITY + " TEXT)");

        // 15. Daily Study Time Table
        db.execSQL("CREATE TABLE " + TABLE_STUDY_TIME + " ("
                + COL_DST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DST_USER_ID + " INTEGER, "
                + COL_DST_DATE + " TEXT, "
                + COL_DST_DURATION + " INTEGER)");

        // 16. courses table
        db.execSQL("CREATE TABLE courses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT UNIQUE, "
                + "description TEXT, "
                + "duration TEXT, "
                + "modules_count INTEGER DEFAULT 0, "
                + "level TEXT, "
                + "progress INTEGER DEFAULT 0)");

        // 17. course_modules table
        db.execSQL("CREATE TABLE course_modules ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "course_id INTEGER, "
                + "name TEXT, "
                + "description TEXT, "
                + "goals TEXT, "
                + "content TEXT, "
                + "example TEXT, "
                + "tutorial_id INTEGER, "
                + "exercise TEXT, "
                + "quiz_id INTEGER, "
                + "status TEXT DEFAULT 'Belum Dimulai', "
                + "completion_percentage INTEGER DEFAULT 0)");

        // 18. module_progress table
        db.execSQL("CREATE TABLE module_progress ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "module_id INTEGER, "
                + "materi_completed INTEGER DEFAULT 0, "
                + "tutorial_completed INTEGER DEFAULT 0, "
                + "latihan_completed INTEGER DEFAULT 0, "
                + "quiz_completed INTEGER DEFAULT 0, "
                + "status TEXT DEFAULT 'Belum Dimulai', "
                + "completion_percentage INTEGER DEFAULT 0, "
                + "last_access TEXT)");

        // 19. forum_reputation table
        db.execSQL("CREATE TABLE forum_reputation ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER UNIQUE, "
                + "points INTEGER DEFAULT 0)");

        // 20. forum_badges table
        db.execSQL("CREATE TABLE forum_badges ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "badge_name TEXT)");

        // 21. forum_references table
        db.execSQL("CREATE TABLE forum_references ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "thread_id INTEGER, "
                + "reference_url TEXT)");

        // 22. exercise_progress table
        db.execSQL("CREATE TABLE exercise_progress ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "module_id INTEGER, "
                + "status TEXT DEFAULT 'Belum Dikerjakan')");

        // 23. quiz_result table
        db.execSQL("CREATE TABLE quiz_result ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "course_id INTEGER, "
                + "module_id INTEGER, "
                + "score INTEGER, "
                + "passed INTEGER, "
                + "date TEXT)");

        // Pre-populate data paths and modules
        populateDefaultPathsAndModules(db);

        // Pre-populate forum threads
        

        // Pre-populate data tutorial
        populateDefaultTutorials(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUTORIALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUTORIAL_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATH_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHLIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STREAK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDY_TIME);
        db.execSQL("DROP TABLE IF EXISTS learning_paths");
        db.execSQL("DROP TABLE IF EXISTS learning_modules");
        db.execSQL("DROP TABLE IF EXISTS courses");
        db.execSQL("DROP TABLE IF EXISTS course_modules");
        db.execSQL("DROP TABLE IF EXISTS module_progress");
        
        
        
        db.execSQL("DROP TABLE IF EXISTS exercise_progress");
        db.execSQL("DROP TABLE IF EXISTS quiz_result");
        onCreate(db);
    }

    // ==========================================
    // SEED DATA: TUTORIAL AKADEMIK BAWAAN
    // ==========================================

    private void populateDefaultTutorials(SQLiteDatabase db) {
        // 1. Pemrograman
        insertTutorialRaw(db,
                "Pengenalan OOP Java", "Pemrograman", "Pemula", "15 Menit",
                "Tutorial ini membahas dasar-dasar Pemrograman Berorientasi Objek (OOP) menggunakan bahasa Java.",
                "OOP adalah paradigma pemrograman yang berorientasi pada kelas (class) dan objek (object). Empat pilar utama OOP meliputi: Inheritance (Pewarisan), Polymorphism (Polimorfisme), Encapsulation (Enkapsulasi), dan Abstraction (Abstraksi).",
                "1. Pahami Class & Object.\n2. Buat Class dengan variabel instance.\n3. Definisikan Constructor.\n4. Tambahkan Method.\n5. Instansiasi Class di Main Method.",
                "Kita akan menulis kelas sederhana bernama 'Book' untuk mewakili entitas pustaka kuno.",
                "public class Book {\n    private String title;\n    public Book(String title) {\n        this.title = title;\n    }\n    public void read() {\n        System.out.println(\"Membaca: \" + title);\n    }\n    public static void main(String[] args) {\n        Book myBook = new Book(\"Codex Gigas\");\n        myBook.read();\n    }\n}",
                "Membaca: Codex Gigas",
                "Selalu gunakan enkapsulasi (private modifier) untuk menjaga integritas data model Anda.",
                "Java programming");

        // 2. Basis Data
        insertTutorialRaw(db,
                "Normalisasi Database", "Basis Data", "Menengah", "20 Menit",
                "Materi ini menjelaskan cara merancang database relasional yang efisien dan bebas redundansi.",
                "Normalisasi adalah proses pengorganisasian data dalam database untuk menghindari anomali data (insert, update, delete) dan redundansi. Tahapan umum meliputi 1NF, 2NF, dan 3NF.",
                "1. Hilangkan grup berulang untuk 1NF.\n2. Pastikan ketergantungan fungsional penuh pada primary key untuk 2NF.\n3. Hilangkan ketergantungan transitif untuk 3NF.",
                "Contoh memisahkan tabel transaksi buku ke dalam skema normal.",
                "CREATE TABLE Users (\n    id INTEGER PRIMARY KEY,\n    name TEXT\n);\n\nCREATE TABLE BorrowedBooks (\n    id INTEGER PRIMARY KEY,\n    user_id INTEGER,\n    book_key TEXT,\n    FOREIGN KEY(user_id) REFERENCES Users(id)\n);",
                "Tabel relasional ternormalisasi 3NF berhasil didefinisikan.",
                "Desain relasi database Anda di atas kertas terlebih dahulu sebelum mengimplementasikannya dalam kode SQLite.",
                "Database design");

        // 3. Mobile Development
        insertTutorialRaw(db,
                "Lifecycle dan Navigasi Fragment", "Mobile Development", "Menengah", "25 Menit",
                "Panduan praktis mengelola daur hidup fragment dan navigasi antar layar di Android.",
                "Fragment adalah bagian modular dari UI suatu Activity. Memahami transisi state daur hidup fragment (onCreateView, onViewCreated, onDestroyView) sangat krusial untuk mencegah memory leaks.",
                "1. Buat class turunan Fragment.\n2. Gelembungkan layout di onCreateView.\n3. Lakukan inisialisasi view di onViewCreated.\n4. Gunakan Navigation Component untuk transaksi layar.",
                "Contoh penggunaan Fragment binding secara aman.",
                "public class HomeFragment extends Fragment {\n    private FragmentHomeBinding binding;\n    @Override\n    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle b) {\n        binding = FragmentHomeBinding.inflate(inf, vg, false);\n        return binding.getRoot();\n    }\n    @Override\n    public void onDestroyView() {\n        super.onDestroyView();\n        binding = null; // Prevent memory leak!\n    }\n}",
                "Fragment terikat secara dinamis dan aman dari kebocoran memori.",
                "Biasakan selalu menyetel variabel binding ke null di onDestroyView() fragment Anda.",
                "Android programming");

        // 4. Artificial Intelligence
        insertTutorialRaw(db,
                "Pengenalan Regresi Linear", "Artificial Intelligence", "Menengah", "30 Menit",
                "Memahami dasar-dasar Machine Learning dengan model matematika Regresi Linear.",
                "Regresi Linear adalah algoritma statistik yang memetakan hubungan linear antara variabel dependen (Y) dan satu atau lebih variabel independen (X) dengan garis lurus terbaik (line of best fit).",
                "1. Kumpulkan dataset.\n2. Tentukan variabel prediktor (X) dan target (Y).\n3. Hitung slope (m) dan intercept (c).\n4. Evaluasi model dengan Mean Squared Error (MSE).",
                "Implementasi dasar persamaan y = mx + c dalam Java.",
                "public class LinearRegression {\n    public static void main(String[] args) {\n        double x = 5.0; // Input\n        double m = 2.5; // Slope\n        double c = 1.2; // Intercept\n        double y = m * x + c;\n        System.out.println(\"Prediksi Y: \" + y);\n    }\n}",
                "Prediksi Y: 13.7",
                "Regresi linear sangat baik digunakan jika hubungan antar data bersifat linear sederhana.",
                "Machine Learning math");

        // 5. Cyber Security
        insertTutorialRaw(db,
                "Konsep Dasar Kriptografi Kunci Publik", "Cyber Security", "Mahir", "20 Menit",
                "Mempelajari bagaimana data dilindungi melalui enkripsi asimetris.",
                "Kriptografi Kunci Publik (Asimetris) menggunakan pasangan kunci: Kunci Publik (untuk enkripsi, disebarluaskan) dan Kunci Privat (untuk dekripsi, dijaga rahasia). RSA adalah contoh algoritma terpopuler.",
                "1. Bangun pasangan kunci (Public & Private).\n2. Enkripsi pesan menggunakan Kunci Publik penerima.\n3. Kirim pesan terenkripsi.\n4. Penerima mendekripsi pesan menggunakan Kunci Privat mereka sendiri.",
                "Contoh enkripsi string sederhana menggunakan algoritma asimetris virtual.",
                "public class CryptoKeyPair {\n    public static void main(String[] args) {\n        String msg = \"Koleksi Kerajaan\";\n        String enc = encrypt(msg, \"PUBLIC_KEY\");\n        System.out.println(\"Ciphertext: \" + enc);\n    }\n    private static String encrypt(String s, String key) {\n        return java.util.Base64.getEncoder().encodeToString(s.getBytes());\n    }\n}",
                "Ciphertext: S29sZWtzaSBLZXJhamFhbg==",
                "Jangan pernah menyimpan kunci privat langsung di dalam repositori source code aplikasi Anda.",
                "Cryptography security");

        // 6. Data Science
        insertTutorialRaw(db,
                "Eksplorasi Data dengan Python Pandas", "Data Science", "Pemula", "15 Menit",
                "Pengenalan pustaka Python Pandas untuk melakukan analisis data statistik.",
                "Pandas adalah pustaka Python yang menyediakan struktur data siap pakai (DataFrame) untuk manipulasi data tabular dan analisis deret waktu secara efisien.",
                "1. Impor pustaka pandas.\n2. Muat dataset CSV ke DataFrame.\n3. Tampilkan rangkuman statistik deskriptif.\n4. Lakukan pemfilteran data bersyarat.",
                "Script Python sederhana untuk analisis DataFrame.",
                "import pandas as pd\n\ndata = {\n    'Buku': ['Codex', 'Manuscript', 'Archival'],\n    'Dipinjam': [25, 40, 15]\n}\ndf = pd.DataFrame(data)\nprint(df.describe())",
                "Rangkuman statistik tabular (mean, std, min, max) dari data peminjaman.",
                "Selalu bersihkan data kosong (null values) menggunakan metode dropna() sebelum melakukan analisis data statistik.",
                "Data analysis Python");
    }

    private void insertTutorialRaw(SQLiteDatabase db, String title, String category, String diff, String time,
                                   String desc, String concepts, String steps, String impl, String code,
                                   String output, String tips, String query) {
        ContentValues values = new ContentValues();
        values.put(COL_TUTORIAL_TITLE, title);
        values.put(COL_TUTORIAL_CATEGORY, category);
        values.put(COL_TUTORIAL_DIFFICULTY, diff);
        values.put(COL_TUTORIAL_TIME, time);
        values.put(COL_TUTORIAL_DESC, desc);
        values.put(COL_TUTORIAL_CONCEPTS, concepts);
        values.put(COL_TUTORIAL_STEPS, steps);
        values.put(COL_TUTORIAL_IMPLEMENTATION, impl);
        values.put(COL_TUTORIAL_CODE, code);
        values.put(COL_TUTORIAL_OUTPUT, output);
        values.put(COL_TUTORIAL_TIPS, tips);
        values.put(COL_TUTORIAL_BOOK_QUERY, query);
        db.insert(TABLE_TUTORIALS, null, values);
    }

    // ==========================================
    // OPERASI TABEL USERS & AUTH
    // ==========================================

    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PASSWORD, user.getPassword());
        values.put(COL_USER_CREATED_AT, user.getCreatedAt());
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID},
                    COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?",
                    new String[]{email, password},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID},
                    COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null,
                    COL_USER_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_CREATED_AT)));
                return user;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PASSWORD, user.getPassword());
        return db.update(TABLE_USERS, values,
                COL_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    public int countUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    // ==========================================
    // OPERASI TABEL BOOKMARK (KOLEKSI PRIBADI)
    // ==========================================

    public long insertBookmark(Bookmark bookmark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKMARK_TITLE, bookmark.getTitle());
        values.put(COL_BOOKMARK_AUTHOR, bookmark.getAuthor());
        values.put(COL_BOOKMARK_YEAR, bookmark.getYear());
        values.put(COL_BOOKMARK_COVER_URL, bookmark.getCoverUrl());
        values.put(COL_BOOKMARK_BOOK_KEY, bookmark.getBookKey());
        values.put(COL_BOOKMARK_SUBJECT, bookmark.getSubject());
        values.put(COL_BOOKMARK_EDITION_COUNT, bookmark.getEditionCount());
        return db.insertWithOnConflict(TABLE_BOOKMARKS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int deleteBookmark(String bookKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKMARKS,
                COL_BOOKMARK_BOOK_KEY + " = ?",
                new String[]{bookKey});
    }

    public List<Bookmark> getBookmarks() {
        List<Bookmark> bookmarks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BOOKMARKS, null, null, null, null, null,
                    COL_BOOKMARK_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_ID);
                int titleIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_TITLE);
                int authorIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_AUTHOR);
                int yearIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_YEAR);
                int coverIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_COVER_URL);
                int keyIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_BOOK_KEY);
                int subjectIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_SUBJECT);
                int editionIndex = cursor.getColumnIndexOrThrow(COL_BOOKMARK_EDITION_COUNT);

                do {
                    Bookmark bookmark = new Bookmark();
                    bookmark.setId(cursor.getInt(idIndex));
                    bookmark.setTitle(cursor.getString(titleIndex));
                    bookmark.setAuthor(cursor.getString(authorIndex));
                    bookmark.setYear(cursor.getString(yearIndex));
                    bookmark.setCoverUrl(cursor.getString(coverIndex));
                    bookmark.setBookKey(cursor.getString(keyIndex));
                    bookmark.setSubject(cursor.getString(subjectIndex));
                    bookmark.setEditionCount(cursor.getInt(editionIndex));
                    bookmarks.add(bookmark);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bookmarks;
    }

    public boolean isBookmarked(String bookKey) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BOOKMARKS,
                    new String[]{COL_BOOKMARK_ID},
                    COL_BOOKMARK_BOOK_KEY + " = ?",
                    new String[]{bookKey},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int countBookmarks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKMARKS, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    // ==========================================
    // OPERASI TABEL DISCUSSION (FORUM AKADEMIK)
    // ==========================================

    
    
    
    
    
    // ==========================================
    // OPERASI TABEL QUIZ HISTORY
    // ==========================================

    public long insertQuizHistory(String category, int score, int totalQuestion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUIZ_CATEGORY, category);
        values.put(COL_QUIZ_SCORE, score);
        values.put(COL_QUIZ_TOTAL, totalQuestion);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        values.put(COL_QUIZ_DATE, currentDate);

        return db.insert(TABLE_QUIZ_HISTORY, null, values);
    }

    public int countQuizHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_QUIZ_HISTORY, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public int getMaxQuizScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT MAX(" + COL_QUIZ_SCORE + ") FROM " + TABLE_QUIZ_HISTORY, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public String getFavoriteQuizCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + COL_QUIZ_CATEGORY + ", COUNT(" + COL_QUIZ_CATEGORY + ") as c FROM " 
                    + TABLE_QUIZ_HISTORY + " GROUP BY " + COL_QUIZ_CATEGORY + " ORDER BY c DESC LIMIT 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "-";
    }

    // ==========================================
    // OPERASI TABEL BOOK PROGRESS (READER)
    // ==========================================

    public long insertOrUpdateBookProgress(int userId, String bookKey, String title, String author, String cover, int chapter, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BP_USER_ID, userId);
        values.put(COL_BP_BOOK_KEY, bookKey);
        values.put(COL_BP_TITLE, title);
        values.put(COL_BP_AUTHOR, author);
        values.put(COL_BP_COVER, cover);
        values.put(COL_BP_CHAPTER, chapter);
        values.put(COL_BP_PROGRESS, progress);
        values.put(COL_BP_LAST_READ, new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));

        return db.insertWithOnConflict(TABLE_BOOK_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor getBookProgress(int userId, String bookKey) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_BOOK_PROGRESS, null,
                COL_BP_USER_ID + " = ? AND " + COL_BP_BOOK_KEY + " = ?",
                new String[]{String.valueOf(userId), bookKey},
                null, null, null);
    }

    public Cursor getRecentBookProgress(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_BOOK_PROGRESS, null,
                COL_BP_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, COL_BP_LAST_READ + " DESC LIMIT 5");
    }

    public int countBooksRead(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOK_PROGRESS + 
                    " WHERE " + COL_BP_USER_ID + " = ? AND " + COL_BP_PROGRESS + " > 0", new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    // ==========================================
    // OPERASI TABEL LEARNING PATH PROGRESS
    // ==========================================

    public long insertOrUpdatePathProgress(int userId, String pathName, String moduleName, String status, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LPP_USER_ID, userId);
        values.put(COL_LPP_PATH_NAME, pathName);
        values.put(COL_LPP_MODULE_NAME, moduleName);
        values.put(COL_LPP_STATUS, status);
        values.put(COL_LPP_PROGRESS, progress);
        values.put(COL_LPP_LAST_ACCESS, new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));

        // We use UNIQUE(user_id, path_name, module_name) to replace progress values if already present
        // Let's first delete if already exists to replicate unique constraint manually
        db.delete(TABLE_PATH_PROGRESS,
                COL_LPP_USER_ID + " = ? AND " + COL_LPP_PATH_NAME + " = ? AND " + COL_LPP_MODULE_NAME + " = ?",
                new String[]{String.valueOf(userId), pathName, moduleName});

        return db.insert(TABLE_PATH_PROGRESS, null, values);
    }

    public List<String> getCompletedModules(int userId, String pathName) {
        List<String> completed = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PATH_PROGRESS, new String[]{COL_LPP_MODULE_NAME},
                    COL_LPP_USER_ID + " = ? AND " + COL_LPP_PATH_NAME + " = ? AND " + COL_LPP_STATUS + " = 'Selesai'",
                    new String[]{String.valueOf(userId), pathName},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    completed.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return completed;
    }

    public int getPathProgressPercentage(int userId, String pathName, int totalModules) {
        if (totalModules == 0) return 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PATH_PROGRESS + 
                    " WHERE " + COL_LPP_USER_ID + " = ? AND " + COL_LPP_PATH_NAME + " = ? AND " + COL_LPP_STATUS + " = 'Selesai'",
                    new String[]{String.valueOf(userId), pathName});
            if (cursor != null && cursor.moveToFirst()) {
                int completed = cursor.getInt(0);
                return (completed * 100) / totalModules;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public String getModuleStatus(int userId, String pathName, String moduleName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PATH_PROGRESS, new String[]{COL_LPP_STATUS},
                    COL_LPP_USER_ID + " = ? AND " + COL_LPP_PATH_NAME + " = ? AND " + COL_LPP_MODULE_NAME + " = ?",
                    new String[]{String.valueOf(userId), pathName, moduleName},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "Belum Dimulai";
    }

    // ==========================================
    // OPERASI TABEL TUTORIALS & TUTORIAL PROGRESS
    // ==========================================

    public List<Tutorial> getTutorials(int userId) {
        List<Tutorial> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // LEFT JOIN progress
        String query = "SELECT t.*, p.status, p.completion " +
                "FROM " + TABLE_TUTORIALS + " t " +
                "LEFT JOIN " + TABLE_TUTORIAL_PROGRESS + " p " +
                "ON t.id = p.tutorial_id AND p.user_id = ?";
                
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Tutorial tut = new Tutorial();
                        tut.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TUTORIAL_ID)));
                        tut.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_TITLE)));
                        tut.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_CATEGORY)));
                        tut.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_DIFFICULTY)));
                        tut.setTimeEstimation(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_TIME)));
                        tut.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_DESC)));
                        tut.setConcepts(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_CONCEPTS)));
                        tut.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_STEPS)));
                        tut.setImplementation(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_IMPLEMENTATION)));
                        tut.setCodeExample(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_CODE)));
                        tut.setOutputExample(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_OUTPUT)));
                        tut.setAcademicTips(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_TIPS)));
                        tut.setRelatedBookQuery(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_BOOK_QUERY)));
                        
                        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                        tut.setStatus(status != null ? status : "Belum Dimulai");
                        
                        tut.setProgress(cursor.isNull(cursor.getColumnIndexOrThrow("completion")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("completion")));
                        
                        list.add(tut);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    public Tutorial getTutorialById(int userId, int tutorialId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.*, p.status, p.completion " +
                "FROM " + TABLE_TUTORIALS + " t " +
                "LEFT JOIN " + TABLE_TUTORIAL_PROGRESS + " p " +
                "ON t.id = p.tutorial_id AND p.user_id = ? " +
                "WHERE t.id = ?";
                
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(tutorialId)});
        if (cursor != null && cursor.moveToFirst()) {
            try {
                Tutorial tut = new Tutorial();
                tut.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TUTORIAL_ID)));
                tut.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_TITLE)));
                tut.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_CATEGORY)));
                tut.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_DIFFICULTY)));
                tut.setTimeEstimation(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_TIME)));
                tut.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_DESC)));
                tut.setConcepts(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_CONCEPTS)));
                tut.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_STEPS)));
                tut.setImplementation(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_IMPLEMENTATION)));
                tut.setCodeExample(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_CODE)));
                tut.setOutputExample(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_OUTPUT)));
                tut.setAcademicTips(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_TIPS)));
                tut.setRelatedBookQuery(cursor.getString(cursor.getColumnIndexOrThrow(COL_TUTORIAL_BOOK_QUERY)));
                
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                tut.setStatus(status != null ? status : "Belum Dimulai");
                
                tut.setProgress(cursor.isNull(cursor.getColumnIndexOrThrow("completion")) ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow("completion")));
                return tut;
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public long insertOrUpdateTutorialProgress(int userId, int tutorialId, String status, int completion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TP_USER_ID, userId);
        values.put(COL_TP_TUTORIAL_ID, tutorialId);
        values.put(COL_TP_STATUS, status);
        values.put(COL_TP_COMPLETION, completion);
        values.put(COL_TP_LAST_OPENED, new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));

        db.delete(TABLE_TUTORIAL_PROGRESS,
                COL_TP_USER_ID + " = ? AND " + COL_TP_TUTORIAL_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(tutorialId)});

        return db.insert(TABLE_TUTORIAL_PROGRESS, null, values);
    }

    public int countTutorialsCompleted(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TUTORIAL_PROGRESS + 
                    " WHERE " + COL_TP_USER_ID + " = ? AND " + COL_TP_STATUS + " = 'Selesai'", new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    // ==========================================
    // OPERASI TABEL RIWAYAT PEMBELAJARAN TERPADU
    // ==========================================

    public long insertLearningHistory(int userId, String title, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LH_USER_ID, userId);
        values.put(COL_LH_TITLE, title);
        values.put(COL_LH_CATEGORY, category);
        values.put(COL_LH_TYPE, type);
        values.put(COL_LH_ACCESS_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        // Avoid exact duplicate insertions of the same activity within a short period
        db.delete(TABLE_LEARNING_HISTORY,
                COL_LH_USER_ID + " = ? AND " + COL_LH_TITLE + " = ? AND " + COL_LH_TYPE + " = ?",
                new String[]{String.valueOf(userId), title, type});

        return db.insert(TABLE_LEARNING_HISTORY, null, values);
    }

    public List<LearningHistoryItem> getLearningHistoryList(int userId, int limit) {
        List<LearningHistoryItem> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_LEARNING_HISTORY, null,
                    COL_LH_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, COL_LH_ACCESS_DATE + " DESC LIMIT " + limit);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COL_LH_ID);
                int titleIndex = cursor.getColumnIndexOrThrow(COL_LH_TITLE);
                int catIndex = cursor.getColumnIndexOrThrow(COL_LH_CATEGORY);
                int typeIndex = cursor.getColumnIndexOrThrow(COL_LH_TYPE);
                int dateIndex = cursor.getColumnIndexOrThrow(COL_LH_ACCESS_DATE);

                do {
                    LearningHistoryItem item = new LearningHistoryItem();
                    item.setId(cursor.getInt(idIndex));
                    item.setTitle(cursor.getString(titleIndex));
                    item.setCategory(cursor.getString(catIndex));
                    item.setType(cursor.getString(typeIndex));
                    item.setDate(cursor.getString(dateIndex));
                    historyList.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return historyList;
    }

    /**
     * Backward-compatible helper method to count the total history entries.
     */
    public int countHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LEARNING_HISTORY + " WHERE " + COL_LH_TYPE + " = 'BOOK'", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * Wrapper for backward compatibility.
     */
    public long insertOrUpdateHistory(Book book) {
        return insertLearningHistory(1, book.getTitle(), book.getSubject() != null ? book.getSubject() : "Umum", "BOOK");
    }

    public int getStudyStreak(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        List<String> activeDates = new ArrayList<>();
        
        // Query distinct dates of activity
        String query = "SELECT DISTINCT substr(" + COL_LH_ACCESS_DATE + ", 1, 10) as d FROM " 
                + TABLE_LEARNING_HISTORY + " WHERE " + COL_LH_USER_ID + " = ? ORDER BY d DESC";
                
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    activeDates.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (activeDates.isEmpty()) return 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        String yesterday = sdf.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

        // If user has no activity today nor yesterday, streak is reset to 0
        if (!activeDates.contains(today) && !activeDates.contains(yesterday)) {
            return 0;
        }

        int streak = 0;
        String checkDate = activeDates.contains(today) ? today : yesterday;
        
        while (true) {
            if (activeDates.contains(checkDate)) {
                streak++;
                // Check previous day
                try {
                    Date parsed = sdf.parse(checkDate);
                    Date prev = new Date(parsed.getTime() - 24 * 60 * 60 * 1000);
                    checkDate = sdf.format(prev);
                } catch (Exception e) {
                    break;
                }
            } else {
                break;
            }
        }
        return streak;
    }

    public int getLearningPathsCompleted(int userId) {
        // Simple logic: we have 7 paths. Calculate progress for each.
        // A path is completed if progress percentage is 100.
        int completedCount = 0;
        String[] paths = {
            "Pemrograman", "Basis Data", "Mobile Development", 
            "Artificial Intelligence", "Cyber Security", "Data Science", "Software Engineering"
        };
        int[] totalModules = {5, 5, 9, 6, 6, 6, 6}; // Custom sizes for roadmaps

        for (int i = 0; i < paths.length; i++) {
            int progress = getPathProgressPercentage(userId, paths[i], totalModules[i]);
            if (progress >= 100) {
                completedCount++;
            }
        }
        return completedCount;
    }

    // ==========================================
    // PRESTASI / MINAT UTAMA
    // ==========================================

    public String getFavoriteCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + COL_LH_CATEGORY + ", COUNT(" + COL_LH_CATEGORY + ") as c FROM " 
                    + TABLE_LEARNING_HISTORY + " GROUP BY " + COL_LH_CATEGORY + " ORDER BY c DESC LIMIT 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                String fav = cursor.getString(0);
                if (fav != null && !fav.isEmpty()) {
                    return fav;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "Umum";
    }

    // ==========================================
    // OPERASI TABEL ACHIEVEMENTS
    // ==========================================

    public long insertAchievement(int userId, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACH_USER_ID, userId);
        values.put(COL_ACH_TITLE, title);
        values.put(COL_ACH_DESC, description);
        values.put(COL_ACH_EARNED_AT, new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));

        // Pastikan tidak ada duplikasi pencapaian yang sama
        db.delete(TABLE_ACHIEVEMENTS,
                COL_ACH_USER_ID + " = ? AND " + COL_ACH_TITLE + " = ?",
                new String[]{String.valueOf(userId), title});

        return db.insert(TABLE_ACHIEVEMENTS, null, values);
    }

    public boolean hasAchievement(int userId, String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ACHIEVEMENTS,
                    new String[]{COL_ACH_ID},
                    COL_ACH_USER_ID + " = ? AND " + COL_ACH_TITLE + " = ?",
                    new String[]{String.valueOf(userId), title},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<String> getEarnedAchievements(int userId) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ACHIEVEMENTS,
                    new String[]{COL_ACH_TITLE},
                    COL_ACH_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    // ==========================================
    // OPERASI TABEL NOTES (CATATAN AKADEMIK)
    // ==========================================

    public long insertNote(int userId, String bookKey, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTE_USER_ID, userId);
        values.put(COL_NOTE_BOOK_KEY, bookKey);
        values.put(COL_NOTE_TITLE, title);
        values.put(COL_NOTE_CONTENT, content);
        String dateStr = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        values.put(COL_NOTE_CREATED_AT, dateStr);
        values.put(COL_NOTE_UPDATED_AT, dateStr);
        return db.insert(TABLE_NOTES, null, values);
    }

    public int updateNote(int noteId, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTE_TITLE, title);
        values.put(COL_NOTE_CONTENT, content);
        values.put(COL_NOTE_UPDATED_AT, new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));
        return db.update(TABLE_NOTES, values, COL_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
    }

    public int deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTES, COL_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
    }

    public List<Note> getNotesByBook(int userId, String bookKey) {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null,
                COL_NOTE_USER_ID + " = ? AND " + COL_NOTE_BOOK_KEY + " = ?",
                new String[]{String.valueOf(userId), bookKey},
                null, null, COL_NOTE_UPDATED_AT + " DESC");
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int idCol = cursor.getColumnIndexOrThrow(COL_NOTE_ID);
                    int uCol = cursor.getColumnIndexOrThrow(COL_NOTE_USER_ID);
                    int bkCol = cursor.getColumnIndexOrThrow(COL_NOTE_BOOK_KEY);
                    int tCol = cursor.getColumnIndexOrThrow(COL_NOTE_TITLE);
                    int cCol = cursor.getColumnIndexOrThrow(COL_NOTE_CONTENT);
                    int crCol = cursor.getColumnIndexOrThrow(COL_NOTE_CREATED_AT);
                    int upCol = cursor.getColumnIndexOrThrow(COL_NOTE_UPDATED_AT);
                    do {
                        list.add(new Note(
                            cursor.getInt(idCol),
                            cursor.getInt(uCol),
                            cursor.getString(bkCol),
                            cursor.getString(tCol),
                            cursor.getString(cCol),
                            cursor.getString(crCol),
                            cursor.getString(upCol)
                        ));
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    // ==========================================
    // OPERASI TABEL HIGHLIGHTS (SOROTAN TEKS)
    // ==========================================

    public long insertHighlight(int userId, String bookKey, String selectedText, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HL_USER_ID, userId);
        values.put(COL_HL_BOOK_KEY, bookKey);
        values.put(COL_HL_SELECTED_TEXT, selectedText);
        values.put(COL_HL_COLOR, color);
        values.put(COL_HL_CREATED_AT, new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));
        return db.insert(TABLE_HIGHLIGHTS, null, values);
    }

    public int deleteHighlight(int highlightId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HIGHLIGHTS, COL_HL_ID + " = ?", new String[]{String.valueOf(highlightId)});
    }

    public List<Highlight> getHighlightsByBook(int userId, String bookKey) {
        List<Highlight> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HIGHLIGHTS, null,
                COL_HL_USER_ID + " = ? AND " + COL_HL_BOOK_KEY + " = ?",
                new String[]{String.valueOf(userId), bookKey},
                null, null, COL_HL_ID + " DESC");
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int idCol = cursor.getColumnIndexOrThrow(COL_HL_ID);
                    int uCol = cursor.getColumnIndexOrThrow(COL_HL_USER_ID);
                    int bkCol = cursor.getColumnIndexOrThrow(COL_HL_BOOK_KEY);
                    int tCol = cursor.getColumnIndexOrThrow(COL_HL_SELECTED_TEXT);
                    int cCol = cursor.getColumnIndexOrThrow(COL_HL_COLOR);
                    int crCol = cursor.getColumnIndexOrThrow(COL_HL_CREATED_AT);
                    do {
                        list.add(new Highlight(
                            cursor.getInt(idCol),
                            cursor.getInt(uCol),
                            cursor.getString(bkCol),
                            cursor.getString(tCol),
                            cursor.getString(cCol),
                            cursor.getString(crCol)
                        ));
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    // ==========================================
    // OPERASI TABEL JOURNALS (JURNAL AKADEMIK)
    // ==========================================

    
    
    
    
    // ==========================================
    // OPERASI TABEL DAILY STUDY TIME & GOAL
    // ==========================================

    public void addStudyDuration(int userId, int minutes) {
        if (minutes <= 0) return;
        SQLiteDatabase db = this.getWritableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Cursor cursor = db.query(TABLE_STUDY_TIME, new String[]{COL_DST_DURATION},
                COL_DST_USER_ID + " = ? AND " + COL_DST_DATE + " = ?",
                new String[]{String.valueOf(userId), today},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int current = cursor.getInt(0);
            ContentValues cv = new ContentValues();
            cv.put(COL_DST_DURATION, current + minutes);
            db.update(TABLE_STUDY_TIME, cv, COL_DST_USER_ID + " = ? AND " + COL_DST_DATE + " = ?", new String[]{String.valueOf(userId), today});
            cursor.close();
        } else {
            if (cursor != null) cursor.close();
            ContentValues cv = new ContentValues();
            cv.put(COL_DST_USER_ID, userId);
            cv.put(COL_DST_DATE, today);
            cv.put(COL_DST_DURATION, minutes);
            db.insert(TABLE_STUDY_TIME, null, cv);
        }
        
        // Trigger update streak data simultaneously
        updateStreakData(userId);
    }

    public int getTodayStudyDuration(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.query(TABLE_STUDY_TIME, new String[]{COL_DST_DURATION},
                COL_DST_USER_ID + " = ? AND " + COL_DST_DATE + " = ?",
                new String[]{String.valueOf(userId), today},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int duration = cursor.getInt(0);
            cursor.close();
            return duration;
        }
        if (cursor != null) cursor.close();
        return 0;
    }

    public int getTotalStudyDuration(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_DST_DURATION + ") FROM " + TABLE_STUDY_TIME + " WHERE " + COL_DST_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            int total = cursor.getInt(0);
            cursor.close();
            return total;
        }
        if (cursor != null) cursor.close();
        return 0;
    }

    // ==========================================
    // OPERASI TABEL LEARNING STREAK
    // ==========================================

    public void updateStreakData(int userId) {
        int calculatedStreak = getStudyStreak(userId);
        SQLiteDatabase db = this.getWritableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Cursor cursor = db.query(TABLE_STREAK, null,
                COL_STR_USER_ID + " = ?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int best = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STR_BEST));
            if (calculatedStreak > best) {
                best = calculatedStreak;
            }
            ContentValues cv = new ContentValues();
            cv.put(COL_STR_CURRENT, calculatedStreak);
            cv.put(COL_STR_BEST, best);
            cv.put(COL_STR_LAST_ACTIVITY, today);
            db.update(TABLE_STREAK, cv, COL_STR_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            cursor.close();
        } else {
            if (cursor != null) cursor.close();
            ContentValues cv = new ContentValues();
            cv.put(COL_STR_USER_ID, userId);
            cv.put(COL_STR_CURRENT, calculatedStreak);
            cv.put(COL_STR_BEST, calculatedStreak);
            cv.put(COL_STR_LAST_ACTIVITY, today);
            db.insert(TABLE_STREAK, null, cv);
        }
    }

    public int getCurrentStreak(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STREAK, new String[]{COL_STR_CURRENT},
                COL_STR_USER_ID + " = ?", new String[]{String.valueOf(userId)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int current = cursor.getInt(0);
            cursor.close();
            return current;
        }
        if (cursor != null) cursor.close();
        return getStudyStreak(userId); // fallback to dynamic calculation
    }

    // ==========================================
    // SEED DATA: COURSES & MODULES & FORUM THREADS
    // ==========================================

    private void populateDefaultPathsAndModules(SQLiteDatabase db) {
        // 1. Pemrograman (5 Modul)
        long path1 = insertCourse(db, "Pemrograman", "Kembangkan fondasi pemrograman kuat menggunakan Java, logika algoritma, pilar OOP, dan struktur data.", "4 Minggu", "Pemula", 5);
        insertCourseModule(db, path1, "Java Dasar & Algoritma", "Dasar sintaksis, variabel, percabangan, perulangan, dan pemecahan masalah algoritma dasar.", 1, 1);
        insertCourseModule(db, path1, "OOP Java & Pewarisan", "Kelas, objek, pewarisan (inheritance), enkapsulasi, polimorfisme, dan interface.", 1, 1);
        insertCourseModule(db, path1, "Exception & File Handling", "Pencegahan crash dengan try-catch dan penulisan file.", 0, 1);
        insertCourseModule(db, path1, "Collections Framework", "List, Map, Set untuk efisiensi penyimpanan data.", 0, 1);
        insertCourseModule(db, path1, "Concurrency & Threading", "Proses asynchronous dan pengelolaan thread di Java.", 0, 1);

        // 2. Basis Data (5 Modul)
        long path2 = insertCourse(db, "Basis Data", "Pelajari perancangan database relasional, SQL Query, normalisasi skema 1NF/2NF/3NF, indeks, dan ACID.", "3 Minggu", "Menengah", 5);
        insertCourseModule(db, path2, "Skema Relasional & ERD", "Perancangan diagram database logis.", 2, 2);
        insertCourseModule(db, path2, "Perintah SQL Dasar (DDL/DML)", "Query SELECT, INSERT, UPDATE, DELETE.", 2, 2);
        insertCourseModule(db, path2, "Normalisasi Database", "Menghilangkan redundansi data.", 2, 2);
        insertCourseModule(db, path2, "Indexing & Optimasi Query", "Meningkatkan kecepatan query.", 0, 2);
        insertCourseModule(db, path2, "Transaksi ACID", "Menjamin integritas data transaksi.", 0, 2);

        // 3. Software Engineering (6 Modul)
        long path3 = insertCourse(db, "Software Engineering", "Pelajari siklus pengembangan perangkat lunak (SDLC), metodologi Agile/Scrum, perancangan arsitektur, clean code, dan testing.", "3 Minggu", "Menengah", 6);
        insertCourseModule(db, path3, "Pengenalan SDLC & Agile", "Siklus pengembangan dan cara kerja Scrum.", 0, 3);
        insertCourseModule(db, path3, "Analisis Kebutuhan", "Mendokumentasikan use case dan requirement.", 0, 3);
        insertCourseModule(db, path3, "Perancangan UML & Arsitektur", "Membuat diagram kelas dan use case.", 0, 3);
        insertCourseModule(db, path3, "Clean Code & Refactoring", "Menulis kode yang mudah dibaca.", 0, 3);
        insertCourseModule(db, path3, "Software Testing & QA", "Unit testing dan integrasi test.", 0, 3);
        insertCourseModule(db, path3, "CI/CD & Deployment", "Otomatisasi build dan deploy.", 0, 3);

        // 4. Mobile Development (9 Modul)
        long path4 = insertCourse(db, "Mobile Development", "Kuasai Android SDK secara mendalam menggunakan Kotlin, UI, navigation, API Retrofit, local Room, MVVM, dan Compose.", "5 Minggu", "Mahir", 9);
        insertCourseModule(db, path4, "Kotlin Dasar", "Variabel, fungsi, null safety di Kotlin.", 3, 4);
        insertCourseModule(db, path4, "Android Lifecycle", "Siklus hidup activity dan fragment.", 3, 4);
        insertCourseModule(db, path4, "ViewBinding & XML Layout", "UI Android klasik dengan XML.", 3, 4);
        insertCourseModule(db, path4, "Navigation Component", "Navigasi antar fragment.", 3, 4);
        insertCourseModule(db, path4, "Retrofit API Integration", "Koneksi internet dengan Retrofit.", 3, 4);
        insertCourseModule(db, path4, "Local SQLite & Room", "Penyimpanan data lokal di Android.", 3, 4);
        insertCourseModule(db, path4, "Jetpack Compose", "UI modern Android.", 0, 4);
        insertCourseModule(db, path4, "Arsitektur MVVM", "Clean architecture untuk Android.", 0, 4);
        insertCourseModule(db, path4, "Testing Aplikasi Mobile", "Instrumented dan unit testing Android.", 0, 4);

        // 5. Artificial Intelligence (6 Modul)
        long path5 = insertCourse(db, "Artificial Intelligence", "Bangun pemahaman dasar Machine Learning, regresi linear, neural networks, NLP, CV, dan LLM.", "6 Minggu", "Mahir", 6);
        insertCourseModule(db, path5, "Matematika untuk AI", "Aljabar linear, kalkulus, statistika.", 4, 5);
        insertCourseModule(db, path5, "Regresi & Klasifikasi", "Model regresi linear dan logistik.", 4, 5);
        insertCourseModule(db, path5, "Jaringan Saraf Tiruan", "Deep learning basics.", 4, 5);
        insertCourseModule(db, path5, "Natural Language Processing", "Analisis teks dan sentiment.", 0, 5);
        insertCourseModule(db, path5, "Computer Vision", "Pemrosesan gambar dengan CNN.", 0, 5);
        insertCourseModule(db, path5, "Generative AI & LLM", "Prompt engineering dan transformer.", 0, 5);

        // 6. Cyber Security (6 Modul)
        long path6 = insertCourse(db, "Cyber Security", "Lindungi sistem menggunakan CIA Triad, enkripsi asimetris RSA, malware analysis, firewall, dan pentest.", "4 Minggu", "Mahir", 6);
        insertCourseModule(db, path6, "Konsep CIA Triad", "Confidentiality, Integrity, Availability.", 5, 6);
        insertCourseModule(db, path6, "Kriptografi & Enkripsi", "Enkripsi simetris dan asimetris.", 5, 6);
        insertCourseModule(db, path6, "Malware & Trojan Analysis", "Menganalisis software berbahaya.", 5, 6);
        insertCourseModule(db, path6, "Social Engineering", "Serangan non-teknis.", 0, 6);
        insertCourseModule(db, path6, "Network Security & Firewall", "Keamanan jaringan dan port filtering.", 0, 6);
        insertCourseModule(db, path6, "Penetration Testing", "Langkah-langkah audit keamanan.", 0, 6);

        // 7. Data Science (6 Modul)
        long path7 = insertCourse(db, "Data Science", "Eksplorasi statistika deskriptif, analisis data tabular Pandas, manipulasi array NumPy, visualisasi Seaborn.", "4 Minggu", "Menengah", 6);
        insertCourseModule(db, path7, "Statistika Deskriptif", "Mean, median, modus, sebaran data.", 6, 7);
        insertCourseModule(db, path7, "Python Data Stack", "Pandas, NumPy, Matplotlib.", 6, 7);
        insertCourseModule(db, path7, "Data Cleaning Pandas", "Membersihkan data kosong dan duplikat.", 6, 7);
        insertCourseModule(db, path7, "Manipulasi Array NumPy", "Operasi matriks di Python.", 0, 7);
        insertCourseModule(db, path7, "Visualisasi Data", "Seaborn and Matplotlib.", 0, 7);
        insertCourseModule(db, path7, "Model Prediksi Sederhana", "Machine learning sederhana dengan Scikit-Learn.", 0, 7);
    }

    private long insertCourse(SQLiteDatabase db, String name, String desc, String duration, String level, int modulesCount) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("duration", duration);
        cv.put("level", level);
        cv.put("modules_count", modulesCount);
        cv.put("progress", 0);
        return db.insert("courses", null, cv);
    }

    private long insertCourseModule(SQLiteDatabase db, long courseId, String name, String desc, int tutorialId, int quizId) {
        ContentValues cv = new ContentValues();
        cv.put("course_id", courseId);
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("goals", "Menguasai topik " + name + " secara teori dan praktis.");
        cv.put("content", "Materi pembelajaran akademik mengenai " + name + ".");
        cv.put("example", "Contoh implementasi dan analisis untuk " + name + ".");
        cv.put("tutorial_id", tutorialId);
        cv.put("exercise", "Latihan menulis kode program atau menjawab studi kasus untuk " + name + ".");
        cv.put("quiz_id", quizId);
        return db.insert("course_modules", null, cv);
    }

    
    // ==========================================
    // FORUM REPUTATION & BADGES & REFERENCES
    // ==========================================

    public int getUserReputation(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("forum_reputation", new String[]{"points"},
                    "user_id = ?", new String[]{String.valueOf(userId)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    public void addUserReputationPoints(int userId, int points) {
        SQLiteDatabase db = this.getWritableDatabase();
        int current = getUserReputation(userId);
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("points", current + points);
        
        db.delete("forum_reputation", "user_id = ?", new String[]{String.valueOf(userId)});
        db.insert("forum_reputation", null, cv);

        // Check for new badges
        
    }

    
    public boolean hasBadge(int userId, String badgeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("forum_badges", new String[]{"id"},
                    "user_id = ? AND badge_name = ?", new String[]{String.valueOf(userId), badgeName},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    
    
    public void updateSolvedStatus(int threadId, int isSolved, String bestAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_solved", isSolved);
        if (bestAnswer != null) {
            cv.put("best_answer", bestAnswer);
        }
        db.update(TABLE_DISCUSSIONS, cv, COL_DISCUSSION_ID + " = ?", new String[]{String.valueOf(threadId)});
    }

    
    
    // ==========================================
    // COURSE & MODULE 3.0 METHODS
    // ==========================================

    public List<com.lumora.app.models.Module> getModulesForPath(int userId, String pathName) {
        List<com.lumora.app.models.Module> modules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT m.*, p.materi_completed, p.tutorial_completed, p.latihan_completed, p.quiz_completed, p.status as progress_status, p.completion_percentage " +
                "FROM course_modules m " +
                "JOIN courses c ON m.course_id = c.id " +
                "LEFT JOIN module_progress p ON m.id = p.module_id AND p.user_id = ? " +
                "WHERE c.name = ? " +
                "ORDER BY m.id ASC";
                
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), pathName});
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int idCol = cursor.getColumnIndexOrThrow("id");
                    int nameCol = cursor.getColumnIndexOrThrow("name");
                    int descCol = cursor.getColumnIndexOrThrow("description");
                    int contentCol = cursor.getColumnIndexOrThrow("content");
                    int exampleCol = cursor.getColumnIndexOrThrow("example");
                    int tutCol = cursor.getColumnIndexOrThrow("tutorial_id");
                    int exCol = cursor.getColumnIndexOrThrow("exercise");
                    int qCol = cursor.getColumnIndexOrThrow("quiz_id");
                    
                    int matCompCol = cursor.getColumnIndexOrThrow("materi_completed");
                    int tutCompCol = cursor.getColumnIndexOrThrow("tutorial_completed");
                    int latCompCol = cursor.getColumnIndexOrThrow("latihan_completed");
                    int quizCompCol = cursor.getColumnIndexOrThrow("quiz_completed");
                    int statusCol = cursor.getColumnIndexOrThrow("progress_status");
                    int pctCol = cursor.getColumnIndexOrThrow("completion_percentage");
                    
                    do {
                        com.lumora.app.models.Module module = new com.lumora.app.models.Module();
                        module.setId(cursor.getInt(idCol));
                        module.setPathName(pathName);
                        module.setName(cursor.getString(nameCol));
                        module.setDescription(cursor.getString(descCol));
                        module.setContent(cursor.getString(contentCol));
                        module.setExample(cursor.getString(exampleCol));
                        module.setTutorialId(cursor.getInt(tutCol));
                        module.setExercise(cursor.getString(exCol));
                        module.setQuizId(cursor.getInt(qCol));
                        
                        module.setMateriCompleted(cursor.isNull(matCompCol) ? 0 : cursor.getInt(matCompCol));
                        module.setTutorialCompleted(cursor.isNull(tutCompCol) ? 0 : cursor.getInt(tutCompCol));
                        module.setLatihanCompleted(cursor.isNull(latCompCol) ? 0 : cursor.getInt(latCompCol));
                        module.setQuizCompleted(cursor.isNull(quizCompCol) ? 0 : cursor.getInt(quizCompCol));
                        
                        String status = cursor.getString(statusCol);
                        module.setStatus(status != null ? status : "Belum Dimulai");
                        
                        module.setCompletionPercentage(cursor.isNull(pctCol) ? 0 : cursor.getInt(pctCol));
                        
                        modules.add(module);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return modules;
    }

    public void updateModuleSubProgress(int userId, int moduleId, int materi, int tutorial, int latihan, int quiz) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        int count = 0;
        if (materi == 1) count++;
        if (tutorial == 1) count++;
        if (latihan == 1) count++;
        if (quiz == 1) count++;
        int pct = count * 25;
        
        String status = "Belum Dimulai";
        if (pct == 100) {
            status = "Selesai";
        } else if (pct > 0) {
            status = "Sedang Dipelajari";
        }
        
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("module_id", moduleId);
        cv.put("materi_completed", materi);
        cv.put("tutorial_completed", tutorial);
        cv.put("latihan_completed", latihan);
        cv.put("quiz_completed", quiz);
        cv.put("status", status);
        cv.put("completion_percentage", pct);
        cv.put("last_access", new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));
        
        db.delete("module_progress", "user_id = ? AND module_id = ?", new String[]{String.valueOf(userId), String.valueOf(moduleId)});
        db.insert("module_progress", null, cv);
        
        Cursor moduleCursor = db.rawQuery("SELECT c.name, m.name FROM course_modules m JOIN courses c ON m.course_id = c.id WHERE m.id = ?", new String[]{String.valueOf(moduleId)});
        if (moduleCursor != null) {
            try {
                if (moduleCursor.moveToFirst()) {
                    String courseName = moduleCursor.getString(0);
                    String moduleName = moduleCursor.getString(1);
                    insertOrUpdatePathProgress(userId, courseName, moduleName, status, pct);
                }
            } finally {
                moduleCursor.close();
            }
        }
    }

    public boolean isModuleLocked(int userId, String pathName, int moduleId) {
        List<com.lumora.app.models.Module> modules = getModulesForPath(userId, pathName);
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getId() == moduleId) {
                if (i == 0) return false;
                return !modules.get(i - 1).getStatus().equals("Selesai");
            }
        }
        return false;
    }

    // ==========================================
    // EXERCISE PROGRESS OPERATIONS
    // ==========================================

    public String getExerciseStatus(int userId, int moduleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("exercise_progress", new String[]{"status"},
                    "user_id = ? AND module_id = ?", new String[]{String.valueOf(userId), String.valueOf(moduleId)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return "Belum Dikerjakan";
    }

    public void updateExerciseStatus(int userId, int moduleId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("module_id", moduleId);
        cv.put("status", status);

        db.delete("exercise_progress", "user_id = ? AND module_id = ?", new String[]{String.valueOf(userId), String.valueOf(moduleId)});
        db.insert("exercise_progress", null, cv);

        // Update module progress latihan_completed
        int completed = "Selesai".equals(status) ? 1 : 0;
        updateModuleLatihanCompleted(userId, moduleId, completed);
    }

    private void updateModuleLatihanCompleted(int userId, int moduleId, int completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        int mat = 0, tut = 0, lat = completed, qz = 0;
        Cursor c = db.query("module_progress", null, "user_id = ? AND module_id = ?", new String[]{String.valueOf(userId), String.valueOf(moduleId)}, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    mat = c.getInt(c.getColumnIndexOrThrow("materi_completed"));
                    tut = c.getInt(c.getColumnIndexOrThrow("tutorial_completed"));
                    qz = c.getInt(c.getColumnIndexOrThrow("quiz_completed"));
                }
            } finally {
                c.close();
            }
        }
        updateModuleSubProgress(userId, moduleId, mat, tut, lat, qz);
    }

    // ==========================================
    // QUIZ RESULT OPERATIONS
    // ==========================================

    public long insertQuizResult(int userId, int courseId, int moduleId, int score, int passed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("course_id", courseId);
        cv.put("module_id", moduleId);
        cv.put("score", score);
        cv.put("passed", passed);
        cv.put("date", new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));

        long result = db.insert("quiz_result", null, cv);

        // Update module progress quiz_completed
        updateModuleQuizCompleted(userId, moduleId, passed);

        return result;
    }

    private void updateModuleQuizCompleted(int userId, int moduleId, int passed) {
        SQLiteDatabase db = this.getWritableDatabase();
        int mat = 0, tut = 0, lat = 0, qz = passed;
        Cursor c = db.query("module_progress", null, "user_id = ? AND module_id = ?", new String[]{String.valueOf(userId), String.valueOf(moduleId)}, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    mat = c.getInt(c.getColumnIndexOrThrow("materi_completed"));
                    tut = c.getInt(c.getColumnIndexOrThrow("tutorial_completed"));
                    lat = c.getInt(c.getColumnIndexOrThrow("latihan_completed"));
                }
            } finally {
                c.close();
            }
        }
        updateModuleSubProgress(userId, moduleId, mat, tut, lat, qz);
    }

    // ==========================================
    // LEARNING CENTER 2.0 METHODS
    // ==========================================
    
    public List<com.lumora.app.models.Course> getAllCourses(int userId) {
        List<com.lumora.app.models.Course> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("courses", null, null, null, null, null, "id ASC");
        if (cursor != null) {
            try {
                int idCol = cursor.getColumnIndexOrThrow("id");
                int nameCol = cursor.getColumnIndexOrThrow("name");
                int descCol = cursor.getColumnIndexOrThrow("description");
                int durCol = cursor.getColumnIndexOrThrow("duration");
                int modCountCol = cursor.getColumnIndexOrThrow("modules_count");
                int lvlCol = cursor.getColumnIndexOrThrow("level");
                
                while (cursor.moveToNext()) {
                    com.lumora.app.models.Course course = new com.lumora.app.models.Course(
                            cursor.getInt(idCol),
                            cursor.getString(nameCol),
                            cursor.getString(descCol),
                            cursor.getString(durCol),
                            cursor.getInt(modCountCol),
                            cursor.getString(lvlCol),
                            0 // progress calculated later
                    );
                    course.setProgress(calculateCourseProgress(course.getId(), userId));
                    courses.add(course);
                }
            } finally {
                cursor.close();
            }
        }
        return courses;
    }
    
    public int calculateCourseProgress(int courseId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT AVG(completion_percentage) FROM module_progress p " +
                       "JOIN course_modules m ON p.module_id = m.id " +
                       "WHERE m.course_id = ? AND p.user_id = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(courseId), String.valueOf(userId)});
        int progress = 0;
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    progress = (int) Math.round(c.getDouble(0));
                }
            } finally {
                c.close();
            }
        }
        return progress;
    }
    
    public List<com.lumora.app.models.Module> getCourseModules(int courseId, int userId) {
        List<com.lumora.app.models.Module> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.*, " +
                       "IFNULL(p.materi_completed, 0) as materi_completed, " +
                       "IFNULL(p.tutorial_completed, 0) as tutorial_completed, " +
                       "IFNULL(p.latihan_completed, 0) as latihan_completed, " +
                       "IFNULL(p.quiz_completed, 0) as quiz_completed, " +
                       "IFNULL(p.status, 'Belum Dimulai') as status, " +
                       "IFNULL(p.completion_percentage, 0) as completion_percentage " +
                       "FROM course_modules m " +
                       "LEFT JOIN module_progress p ON m.id = p.module_id AND p.user_id = ? " +
                       "WHERE m.course_id = ? " +
                       "ORDER BY m.id ASC";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(courseId)});
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    com.lumora.app.models.Module mod = new com.lumora.app.models.Module();
                    mod.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                    mod.setName(c.getString(c.getColumnIndexOrThrow("name")));
                    mod.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
                    mod.setContent(c.getString(c.getColumnIndexOrThrow("content")));
                    mod.setExample(c.getString(c.getColumnIndexOrThrow("example")));
                    mod.setTutorialId(c.getInt(c.getColumnIndexOrThrow("tutorial_id")));
                    mod.setExercise(c.getString(c.getColumnIndexOrThrow("exercise")));
                    mod.setQuizId(c.getInt(c.getColumnIndexOrThrow("quiz_id")));
                    
                    mod.setMateriCompleted(c.getInt(c.getColumnIndexOrThrow("materi_completed")));
                    mod.setTutorialCompleted(c.getInt(c.getColumnIndexOrThrow("tutorial_completed")));
                    mod.setLatihanCompleted(c.getInt(c.getColumnIndexOrThrow("latihan_completed")));
                    mod.setQuizCompleted(c.getInt(c.getColumnIndexOrThrow("quiz_completed")));
                    mod.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
                    mod.setCompletionPercentage(c.getInt(c.getColumnIndexOrThrow("completion_percentage")));
                    
                    list.add(mod);
                }
            } finally {
                c.close();
            }
        }
        return list;
    }


    
}
