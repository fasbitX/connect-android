package com.chat.zipchat.Service;

import com.chat.zipchat.BuildConfig;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private static String API_BASE_URL = BuildConfig.BaseUrl + "api/v1/";
    private static String NOTIFICATION_URL = BuildConfig.NotificationUrl;
    private static String CONVERT_AMOUNT_URL = "https://min-api.cryptocompare.com/data/";
    private static String PAYMENT_URL = BuildConfig.PaymentUrl;

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("Request")
                    .response("Response").build())
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public static Retrofit getClient() {




        /*HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();*/


        retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        return retrofit;
    }

    public static Retrofit getClientNotification() {

        retrofit = new Retrofit.Builder()
                .baseUrl(NOTIFICATION_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        return retrofit;
    }

    public static Retrofit getClientPayment() {

        retrofit = new Retrofit.Builder()
                .baseUrl(PAYMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        return retrofit;
    }

    public static Retrofit getClientConvertion() {


        retrofit = new Retrofit.Builder()
                .baseUrl(CONVERT_AMOUNT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        return retrofit;
    }

    public static Retrofit getAuthClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor.Builder()
                        .loggable(BuildConfig.DEBUG)
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .addHeader("authid", "1")
                        .addHeader("authorization", "2")
                        .build())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();


        return retrofit;
    }

}
