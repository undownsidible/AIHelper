package com.example.aihelper.model

data class ScheduleCreateRequest(
    val name: String,
    val startTime: String,
    val endTime: String?,
    val remark: String?
)