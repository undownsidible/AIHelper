package com.example.aihelper.model

data class Schedule(
    val id: Long,
    val name: String,
    val startTime: String,
    val endTime: String?,
    val remark: String?
)