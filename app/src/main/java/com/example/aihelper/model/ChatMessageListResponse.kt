package com.example.aihelper.model

data class ChatMessageListResponse(
    val code: Int,
    val message: String,
    val data: List<ChatMessage>
)