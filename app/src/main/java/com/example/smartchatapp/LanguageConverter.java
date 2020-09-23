package com.example.smartchatapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageConverter {

    public static String translate(String fromLang, String toLang, String text) {
        // TODO: Should have used a 3rd party library to make a JSON string from an object
        try{
            String CLIENT_ID = "FREE_TRIAL_ACCOUNT";
            String CLIENT_SECRET = "PUBLIC_SECRET";
            String ENDPOINT = "http://api.whatsmate.net/v1/translation/translate";

            String jsonPayload = new StringBuilder()
                    .append("{")
                    .append("\"fromLang\":\"")
                    .append(fromLang)
                    .append("\",")
                    .append("\"toLang\":\"")
                    .append(toLang)
                    .append("\",")
                    .append("\"text\":\"")
                    .append(text)
                    .append("\"")
                    .append("}")
                    .toString();

            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-WM-CLIENT-ID", CLIENT_ID);
            conn.setRequestProperty("X-WM-CLIENT-SECRET", CLIENT_SECRET);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(40000);
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(jsonPayload.getBytes());
            os.flush();
            os.close();

            int statusCode = conn.getResponseCode();
            Log.d("", "translate: code="+statusCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()
            ));
            String output;
            String str="";
            while ((output = br.readLine()) != null) {
                Log.d("", "translate: output="+output);
                str=output;
            }
            Log.d("", "translate: str="+str);
            conn.disconnect();
            return str;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return text;
        }
    }

}
