package com.example.aihelper.util

import com.example.aihelper.model.Result
import retrofit2.Response

/**
 * 通用网络请求封装
 */
suspend fun <T> request(
    apiCall: suspend () -> Response<Result<T>>,
    onSuccess: (T?) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val response = apiCall()

        if (response.isSuccessful) {
            val result = response.body()

            if (result?.code == 200) {
                onSuccess(result.data)
            } else {
                onError(result?.message ?: "业务错误")
            }

        } else {
            onError("网络错误: ${response.code()}")
        }

    } catch (e: Exception) {
        e.printStackTrace()
        onError("网络异常")
    }
}