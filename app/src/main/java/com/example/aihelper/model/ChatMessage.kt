package com.example.aihelper.model

data class ChatMessage(
    val role: String,   // user 或 assistant
    var content: String
)