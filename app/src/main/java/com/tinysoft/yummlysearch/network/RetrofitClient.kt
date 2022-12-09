package com.tinysoft.yummlysearch.network

import com.google.gson.GsonBuilder
import com.tinysoft.yummlysearch.Constants
import com.tinysoft.yummlysearch.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun logInterceptor(): Interceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    } else {
        // disable retrofit log on release
        loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
    }
    return loggingInterceptor
}

fun provideOkHttp(): OkHttpClient {
    return OkHttpClient.Builder()
        .addNetworkInterceptor(logInterceptor())
        .addInterceptor(headerInterceptor())
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
}

private fun headerInterceptor(): Interceptor {
    return Interceptor {
        val original = it.request()
        val request = original.newBuilder()
            .header("Content-Type", "application/json; charset=utf-8")
            .addHeader("X-Yummly-App-ID", Constants.APP_ID)
            .addHeader("X-Yummly-App-Key", Constants.APP_KEY)
            .method(original.method, original.body)
            .build()
        it.proceed(request)
    }
}

fun provideSearchApiService(client: OkHttpClient): RestApiService {
    val gson = GsonBuilder().setLenient().create()
    return Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .callFactory { request -> client.newCall(request) }
        .build()
        .create(RestApiService::class.java)
}