package com.example.aihelper.api

import com.example.aihelper.model.ChatMessageListResponse
import com.example.aihelper.model.ChatRequest
import com.example.aihelper.model.ChatResponse
import com.example.aihelper.model.LoginRequest
import com.example.aihelper.model.LoginResponse
import com.example.aihelper.model.Session
import com.example.aihelper.model.SessionListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("/session/list")
    suspend fun getSessionList(): Response<SessionListResponse>

    @POST("/session/create")
    suspend fun createSession(): Response<Session>

    @DELETE("/session/delete/{id}")
    suspend fun deleteSession(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("chat/send")
    suspend fun chat(
        @Body request: ChatRequest
    ): Response<ChatResponse>
    @GET("chat/list/{sessionId}")
    suspend fun getMessageList(
        @Path("sessionId") sessionId: Long
    ): Response<ChatMessageListResponse>
}