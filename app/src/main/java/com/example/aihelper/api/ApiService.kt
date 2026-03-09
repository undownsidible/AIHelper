package com.example.aihelper.api

import com.example.aihelper.model.LoginRequest
import com.example.aihelper.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

}