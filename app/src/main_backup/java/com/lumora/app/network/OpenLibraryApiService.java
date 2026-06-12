package com.lumora.app.network;

import com.lumora.app.models.OpenLibraryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * OpenLibraryApiService - Mendefinisikan endpoint Retrofit untuk Open Library API.
 */
public interface OpenLibraryApiService {

    /**
     * Mencari buku berdasarkan query teks tertentu.
     * Endpoint: GET https://openlibrary.org/search.json?q={query}
     *
     * @param query Kata kunci pencarian (misal: "programming")
     * @return Call pembungkus OpenLibraryResponse
     */
    @GET("search.json")
    Call<OpenLibraryResponse> searchBooks(
            @Query("q") String query
    );
}
