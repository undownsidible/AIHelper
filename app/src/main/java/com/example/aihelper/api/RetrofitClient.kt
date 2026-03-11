package com.example.aihelper.api

import android.content.Context
import com.example.aihelper.App
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()

        // 超时配置
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)

        .addInterceptor { chain ->

            val sp = App.context.getSharedPreferences("user", Context.MODE_PRIVATE)
            val token = sp.getString("token", "")

            android.util.Log.d("tokenInterceptor", token ?: "")

            val builder = chain.request().newBuilder()

            // 改为 token 请求头
            if (!token.isNullOrEmpty()) {
                builder.addHeader("token", token)
            }

            chain.proceed(builder.build())
        }

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