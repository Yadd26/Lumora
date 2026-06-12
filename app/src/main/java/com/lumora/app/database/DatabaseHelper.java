package com.lumora.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lumora.app.models.Book;
import com.lumora.app.models.Bookmark;
import com.lumora.app.models.Discussion;
import com.lumora.app.models.User;
import com.lumora.app.models.Tutorial;
import com.lumora.app.models.LearningHistoryItem;

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
    private static final int DATABASE_VERSION = 7; // Dinaikkan ke versi 7 untuk penambahan tabel achievements

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

        // 2. Discussions Table
        db.execSQL("CREATE TABLE " + TABLE_DISCUSSIONS + " ("
                + COL_DISCUSSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DISCUSSION_TITLE + " TEXT, "
                + COL_DISCUSSION_CONTENT + " TEXT)");

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

        // Pre-populate data tutorial
        populateDefaultTutorials(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCUSSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUTORIALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUTORIAL_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATH_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
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

    public long insertDiscussion(Discussion discussion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DISCUSSION_TITLE, discussion.getTitle());
        values.put(COL_DISCUSSION_CONTENT, discussion.getContent());
        return db.insert(TABLE_DISCUSSIONS, null, values);
    }

    public int deleteDiscussion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DISCUSSIONS,
                COL_DISCUSSION_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Discussion> getDiscussions() {
        List<Discussion> discussions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_DISCUSSIONS, null, null, null, null, null,
                    COL_DISCUSSION_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COL_DISCUSSION_ID);
                int titleIndex = cursor.getColumnIndexOrThrow(COL_DISCUSSION_TITLE);
                int contentIndex = cursor.getColumnIndexOrThrow(COL_DISCUSSION_CONTENT);

                do {
                    Discussion discussion = new Discussion();
                    discussion.setId(cursor.getInt(idIndex));
                    discussion.setTitle(cursor.getString(titleIndex));
                    discussion.setContent(cursor.getString(contentIndex));
                    discussions.add(discussion);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return discussions;
    }

    public int countDiscussions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DISCUSSIONS, null);
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
}
