package com.proton.easycooking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    public final static String UPLOAD_URL = "https://api.imgbb.com/";
    //    public final static String BASE_URL = "https://zyphost.000webhostapp.com/";
    public static String BASE_URL = "https://protonappdev.x10.mx/";
    private static Retrofit retrofit = null;
    private static Retrofit retrofit_up = null;

    public static Retrofit getService() {
        if (retrofit == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(60, TimeUnit.SECONDS)
//                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .hostnameVerifier((s, sslSession) -> true)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

        }
        return retrofit;


    }

    public static Retrofit getUploadService() {
        if (retrofit_up == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit_up = new Retrofit.Builder()
                    .baseUrl(UPLOAD_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

        }
        return retrofit_up;


    }

}
