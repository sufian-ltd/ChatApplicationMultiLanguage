package com.example.smartchatapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("translate.php")
    Call<ServerResponse> translate(@Query("fromLang") String fromLang,@Query("toLang") String toLang,
                                    @Query("text") String text);

}
