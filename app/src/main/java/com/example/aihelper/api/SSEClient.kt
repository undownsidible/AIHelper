package com.example.aihelper.api

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SSEClient {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    fun connect(
        sessionId: Long,
        content: String,
        token: String,
        onMessage: (String) -> Unit,
        onComplete: () -> Unit
    ) {

        val json = JSONObject()
        json.put("sessionId", sessionId)
        json.put("content", content)

        val request = Request.Builder()
            .url("http://10.0.2.2:8080/chat/stream")
            .post(
                RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )
            )
            // 改为 token 请求头
            .addHeader("token", token)
            .build()

        EventSources.createFactory(client)
            .newEventSource(request, object : EventSourceListener() {

                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    onMessage(data)
                }

                override fun onClosed(eventSource: EventSource) {
                    onComplete()
                }
            })
    }
}