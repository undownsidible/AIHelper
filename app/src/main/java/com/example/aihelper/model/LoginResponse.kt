package com.example.aihelper.model

data class LoginResponse(
    val code: Int,
    val message: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val token: String
)