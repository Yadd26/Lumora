package com.lumora.app.network;

import com.lumora.app.models.QuizResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * QuizApiService - Mendefinisikan endpoint Retrofit untuk mengambil data soal kuis dari Open Trivia Database.
 */
public interface QuizApiService {

    /**
     * Mengambil daftar soal kuis acak.
     * Endpoint: GET https://opentdb.com/api.php?amount={amount}
     *
     * @param amount Jumlah soal yang diminta (default: 10)
     * @return Call pembungkus QuizResponse
     */
    @GET("api.php")
    Call<QuizResponse> getQuestions(
            @Query("amount") int amount
    );
}
