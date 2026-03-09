package com.example.aihelper.api

import android.content.Context
import com.example.aihelper.App
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()

        // 自动添加 Token
        .addInterceptor { chain ->

            val sp = App.context.getSharedPreferences("user", Context.MODE_PRIVATE)
            val token = sp.getString("token", "")

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            chain.proceed(request)
        }

        // 日志
        .addInterceptor(logging)

        .build()


    val apiService: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}