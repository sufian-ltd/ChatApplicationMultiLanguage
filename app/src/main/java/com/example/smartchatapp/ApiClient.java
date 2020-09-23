package com.example.smartchatapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL="http://sufianmax5.000webhostapp.com/chatapp/";
    //public static final String BASE_URL="http://192.168.43.71/MNGroupApp/";
    private static Retrofit retrofit=null;

    public static Retrofit getRetrofit(){
        if (retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();

        }
        return retrofit;

    }
}
