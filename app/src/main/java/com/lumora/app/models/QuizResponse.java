package com.lumora.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * QuizResponse - Wrapper respons JSON untuk daftar pertanyaan kuis dari Open Trivia Database.
 */
public class QuizResponse {

    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("results")
    private List<QuizQuestion> results;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<QuizQuestion> getResults() {
        return results;
    }

    public void setResults(List<QuizQuestion> results) {
        this.results = results;
    }
}
