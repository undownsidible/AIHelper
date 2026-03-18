package com.example.aihelper.api

import com.example.aihelper.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /** ================= 用户 ================= */
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    /** ================= 会话 ================= */
    @GET("session/list")
    suspend fun getSessionList(): Response<SessionListResponse>

    @POST("session/create")
    suspend fun createSession(): Response<Session>

    @DELETE("session/delete/{id}")
    suspend fun deleteSession(
        @Path("id") id: Long
    ): Response<Unit>


    /** ================= 聊天 ================= */
    @POST("chat/send")
    suspend fun chat(
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @GET("chat/list/{sessionId}")
    suspend fun getMessageList(
        @Path("sessionId") sessionId: Long
    ): Response<ChatMessageListResponse>

    /** ================= 日程 ================= */

    /** 查询全部日程 */
    @GET("schedule/list")
    suspend fun getScheduleList(
        @Query("userId") userId: Long?
    ): Response<Result<List<Schedule>>>

    /** 查询今日日程 */
    @GET("schedule/today")
    suspend fun getScheduleToday(
        @Query("userId") userId: Long?
    ): Response<Result<List<Schedule>>>

    /** 新增日程 */
    @POST("schedule")
    suspend fun createSchedule(
        @Body request: ScheduleCreateRequest
    ): Response<Result<Any>>

    /** 更新日程 */
    @PUT("schedule")
    suspend fun updateSchedule(
        @Body request: ScheduleUpdateRequest
    ): Response<Result<Any>>

    /** 删除日程 */
    @DELETE("schedule/{id}")
    suspend fun deleteSchedule(
        @Path("id") id: Long
    ): Response<Result<Any>>
}