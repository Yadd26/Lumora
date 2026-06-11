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

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper - Pengelola database SQLite untuk aplikasi Lumora.
 * Mengelola empat tabel: bookmarks, discussions, users, dan learning_history.
 * Semua metode publik bersifat thread-safe ketika dipanggil melalui ExecutorService.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Info Database
    private static final String DATABASE_NAME = "lumora.db";
    private static final int DATABASE_VERSION = 4; // Dinaikkan ke versi 4 untuk menambahkan tabel riwayat belajar

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

    // Tabel: learning_history
    private static final String TABLE_HISTORY = "learning_history";
    private static final String COL_HISTORY_ID = "id";
    private static final String COL_HISTORY_BOOK_KEY = "bookKey";
    private static final String COL_HISTORY_TITLE = "title";
    private static final String COL_HISTORY_AUTHOR = "author";
    private static final String COL_HISTORY_YEAR = "year";
    private static final String COL_HISTORY_COVER_URL = "coverUrl";
    private static final String COL_HISTORY_SUBJECT = "subject";
    private static final String COL_HISTORY_EDITION_COUNT = "editionCount";
    private static final String COL_HISTORY_LAST_ACCESSED = "lastAccessed";

    // Instance Singleton
    private static DatabaseHelper instance;

    /**
     * Mengembalikan instance singleton DatabaseHelper.
     * Menggunakan context aplikasi untuk mencegah kebocoran memori.
     */
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
        // Membuat tabel bookmarks (markah buku) dengan kolom baru
        String createBookmarksTable = "CREATE TABLE " + TABLE_BOOKMARKS + " ("
                + COL_BOOKMARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BOOKMARK_TITLE + " TEXT, "
                + COL_BOOKMARK_AUTHOR + " TEXT, "
                + COL_BOOKMARK_YEAR + " TEXT, "
                + COL_BOOKMARK_COVER_URL + " TEXT, "
                + COL_BOOKMARK_BOOK_KEY + " TEXT UNIQUE, "
                + COL_BOOKMARK_SUBJECT + " TEXT, "
                + COL_BOOKMARK_EDITION_COUNT + " INTEGER)";
        db.execSQL(createBookmarksTable);

        // Membuat tabel discussions (diskusi)
        String createDiscussionsTable = "CREATE TABLE " + TABLE_DISCUSSIONS + " ("
                + COL_DISCUSSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DISCUSSION_TITLE + " TEXT, "
                + COL_DISCUSSION_CONTENT + " TEXT)";
        db.execSQL(createDiscussionsTable);

        // Membuat tabel users (pengguna)
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT, "
                + COL_USER_EMAIL + " TEXT UNIQUE, "
                + COL_USER_PASSWORD + " TEXT, "
                + COL_USER_CREATED_AT + " TEXT)";
        db.execSQL(createUsersTable);

        // Membuat tabel learning_history (riwayat belajar)
        String createHistoryTable = "CREATE TABLE " + TABLE_HISTORY + " ("
                + COL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_HISTORY_BOOK_KEY + " TEXT UNIQUE, "
                + COL_HISTORY_TITLE + " TEXT, "
                + COL_HISTORY_AUTHOR + " TEXT, "
                + COL_HISTORY_YEAR + " TEXT, "
                + COL_HISTORY_COVER_URL + " TEXT, "
                + COL_HISTORY_SUBJECT + " TEXT, "
                + COL_HISTORY_EDITION_COUNT + " INTEGER, "
                + COL_HISTORY_LAST_ACCESSED + " INTEGER)";
        db.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCUSSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // ==========================================
    // OPERASI TABEL USERS (AUTENTIKASI)
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

    public int deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS,
                COL_USER_ID + " = ?",
                new String[]{String.valueOf(id)});
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
    // OPERASI TABEL BOOKMARK (MARKAH BUKU)
    // ==========================================

    /**
     * Memasukkan markah buku ke dalam database.
     * Menggunakan REPLACE jika terjadi duplikasi kunci buku.
     */
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

    /**
     * Menghapus markah buku berdasarkan bookKey-nya.
     */
    public int deleteBookmark(String bookKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKMARKS,
                COL_BOOKMARK_BOOK_KEY + " = ?",
                new String[]{bookKey});
    }

    /**
     * Mengambil semua data markah buku dari database.
     */
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

    /**
     * Memeriksa apakah suatu buku telah ditandai (bookmarked) berdasarkan bookKey.
     */
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

    /**
     * Mengembalikan jumlah total buku tersimpan.
     */
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
    // OPERASI TABEL DISCUSSION (DISKUSI)
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
    // OPERASI TABEL LEARNING HISTORY (RIWAYAT BELAJAR)
    // ==========================================

    /**
     * Memasukkan atau memperbarui riwayat belajar buku.
     */
    public long insertOrUpdateHistory(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HISTORY_BOOK_KEY, book.getKey());
        values.put(COL_HISTORY_TITLE, book.getTitle());
        values.put(COL_HISTORY_AUTHOR, book.getAuthor());
        values.put(COL_HISTORY_YEAR, book.getFirstPublishYear());
        values.put(COL_HISTORY_COVER_URL, book.getCoverUrl());
        values.put(COL_HISTORY_SUBJECT, book.getSubject());
        values.put(COL_HISTORY_EDITION_COUNT, book.getEditionCount());
        values.put(COL_HISTORY_LAST_ACCESSED, System.currentTimeMillis());
        return db.insertWithOnConflict(TABLE_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Mengambil riwayat belajar terbaru.
     */
    public List<Book> getHistoryList() {
        List<Book> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_HISTORY, null, null, null, null, null,
                    COL_HISTORY_LAST_ACCESSED + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                int keyIndex = cursor.getColumnIndexOrThrow(COL_HISTORY_BOOK_KEY);
                int titleIndex = cursor.getColumnIndexOrThrow(COL_HISTORY_TITLE);
                int authorIndex = cursor.getColumnIndexOrThrow(COL_HISTORY_AUTHOR);
                int yearIndex = cursor.getColumnIndexOrThrow(COL_HISTORY_YEAR);
                int coverIndex = cursor.getColumnIndexOrThrow(COL_HISTORY_COVER_URL);
                int subjectIndex = cursor.getColumnIndexOrThrow(COL_HISTORY_SUBJECT);
                int editionIndex = cursor.getColumnIndexOrThrow(COL_HISTORY_EDITION_COUNT);

                do {
                    Book book = new Book();
                    book.setKey(cursor.getString(keyIndex));
                    book.setTitle(cursor.getString(titleIndex));
                    book.setAuthor(cursor.getString(authorIndex));
                    book.setFirstPublishYear(cursor.getString(yearIndex));
                    book.setCoverUrl(cursor.getString(coverIndex));
                    book.setSubject(cursor.getString(subjectIndex));
                    book.setEditionCount(cursor.getInt(editionIndex));
                    history.add(book);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return history;
    }

    /**
     * Menghitung total jenis buku yang pernah dibuka.
     */
    public int countHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_HISTORY, null);
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
     * Menghitung kategori terfavorit berdasarkan subjek terbanyak dari riwayat belajar dan bookmark.
     */
    public String getFavoriteCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> subjects = new ArrayList<>();

        // Ambil subjek dari riwayat belajar
        Cursor cursor = db.rawQuery("SELECT " + COL_HISTORY_SUBJECT + " FROM " + TABLE_HISTORY, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String subj = cursor.getString(0);
                    if (subj != null) subjects.add(subj.toLowerCase());
                }
            } finally {
                cursor.close();
            }
        }

        // Ambil subjek dari bookmarks
        cursor = db.rawQuery("SELECT " + COL_BOOKMARK_SUBJECT + " FROM " + TABLE_BOOKMARKS, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String subj = cursor.getString(0);
                    if (subj != null) subjects.add(subj.toLowerCase());
                }
            } finally {
                cursor.close();
            }
        }

        if (subjects.isEmpty()) {
            return "-";
        }

        // Kalkulasi kecocokan kategori belajar
        int progCount = 0, dbCount = 0, netCount = 0, siCount = 0, aiCount = 0, dsCount = 0, secCount = 0, mobCount = 0;
        for (String s : subjects) {
            if (s.contains("program") || s.contains("java") || s.contains("python") || s.contains("c++") || s.contains("software") || s.contains("code") || s.contains("compiler")) progCount++;
            if (s.contains("databas") || s.contains("sql") || s.contains("mysql") || s.contains("oracle") || s.contains("query")) dbCount++;
            if (s.contains("network") || s.contains("internet") || s.contains("cisco") || s.contains("protocol") || s.contains("ip") || s.contains("routing")) netCount++;
            if (s.contains("information system") || s.contains("manajemen") || s.contains("business") || s.contains("sistem informasi")) siCount++;
            if (s.contains("artificial") || s.contains("intelligence") || s.contains("machine learning") || s.contains("ai") || s.contains("deep learning") || s.contains("neural")) aiCount++;
            if (s.contains("data science") || s.contains("analytic") || s.contains("statistic") || s.contains("big data")) dsCount++;
            if (s.contains("security") || s.contains("cyber") || s.contains("crypt") || s.contains("hack") || s.contains("keamanan")) secCount++;
            if (s.contains("mobile") || s.contains("android") || s.contains("ios") || s.contains("swift") || s.contains("flutter")) mobCount++;
        }

        int max = Math.max(progCount, Math.max(dbCount, Math.max(netCount, Math.max(siCount, Math.max(aiCount, Math.max(dsCount, Math.max(secCount, mobCount)))))));
        if (max == 0) return "Umum";
        if (max == progCount) return "Pemrograman";
        if (max == dbCount) return "Basis Data";
        if (max == netCount) return "Jaringan Komputer";
        if (max == siCount) return "Sistem Informasi";
        if (max == aiCount) return "Artificial Intelligence";
        if (max == dsCount) return "Data Science";
        if (max == secCount) return "Cyber Security";
        return "Mobile Development";
    }
}
