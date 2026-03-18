package com.example.aihelper.util

import retrofit2.Response

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): T? {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}