package com.lumora.app.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiClient - Konfigurasi klien Retrofit untuk berbagai API eksternal.
 * Menyediakan instance terkonfigurasi untuk Open Library dan Open Trivia Database.
 */
public class ApiClient {

    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/";
    private static final String OPENTDB_URL = "https://opentdb.com/";

    private static final int TIMEOUT_SECONDS = 20;

    private static Retrofit openLibraryRetrofit = null;
    private static Retrofit quizRetrofit = null;

    private static OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Mendapatkan Retrofit instance untuk Open Library API.
     */
    private static Retrofit getOpenLibraryClient() {
        if (openLibraryRetrofit == null) {
            openLibraryRetrofit = new Retrofit.Builder()
                    .baseUrl(OPEN_LIBRARY_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return openLibraryRetrofit;
    }

    /**
     * Mendapatkan Retrofit instance untuk Open Trivia DB API.
     */
    private static Retrofit getQuizClient() {
        if (quizRetrofit == null) {
            quizRetrofit = new Retrofit.Builder()
                    .baseUrl(OPENTDB_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return quizRetrofit;
    }

    /**
     * Mendapatkan Api Service untuk pencarian Buku.
     */
    public static OpenLibraryApiService getBookApiService() {
        return getOpenLibraryClient().create(OpenLibraryApiService.class);
    }

    /**
     * Mendapatkan Api Service untuk data Kuis.
     */
    public static QuizApiService getQuizApiService() {
        return getQuizClient().create(QuizApiService.class);
    }
}
