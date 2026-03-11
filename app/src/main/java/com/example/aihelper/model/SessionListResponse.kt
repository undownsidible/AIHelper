package com.example.aihelper.model

data class SessionListResponse(
    val code: Int,
    val message: String,
    val data: List<Session>
)