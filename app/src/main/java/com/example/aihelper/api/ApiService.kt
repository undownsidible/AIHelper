package com.example.aihelper.api

import com.example.aihelper.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /** ================= 用户 ================= */
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<Result<UserData>>

    @POST("user/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Result<Any>>

    /** ================= 会话 ================= */
    // 查询会话列表
    @GET("sessions")
    suspend fun getSessionList(): Response<Result<List<Session>>>

    // 创建会话
    @POST("sessions")
    suspend fun createSession(): Response<Result<Long>>

    // 修改会话名称（新增）
    @PUT("sessions")
    suspend fun updateSessionName(
        @Body dto: SessionUpdateRequest
    ): Response<Result<Any>>

    // 删除会话
    @DELETE("sessions/{id}")
    suspend fun deleteSession(
        @Path("id") id: Long
    ): Response<Result<Any>>


    /** ================= 聊天 ================= */
    @POST("chat/send")
    suspend fun chat(
        @Body request: ChatRequest
    ): Response<Result<ChatMessage>>

    @GET("chat/list/{sessionId}")
    suspend fun getMessageList(
        @Path("sessionId") sessionId: Long
    ): Response<Result<List<ChatMessage>>>

    /** ================= 日程 ================= */

    /** 查询全部日程 */
    @GET("schedule/list")
    suspend fun getScheduleList(): Response<Result<List<Schedule>>>

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