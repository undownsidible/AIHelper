package com.example.aihelper.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aihelper.R
import com.example.aihelper.adapter.ChatAdapter
import com.example.aihelper.adapter.SessionAdapter
import com.example.aihelper.api.RetrofitClient
import com.example.aihelper.api.SSEClient
import com.example.aihelper.model.ChatMessage
import com.example.aihelper.model.ChatRequest
import com.example.aihelper.model.Session
import com.example.aihelper.util.request
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    // 左侧会话抽屉
    lateinit var drawerLayout: DrawerLayout
    lateinit var sessionRecyclerView: RecyclerView

    private val sessionList = mutableListOf<Session>()
    private lateinit var sessionAdapter: SessionAdapter

    // 当前会话ID
    var currentSessionId: Long = -1

    // 聊天列表
    lateinit var chatRecyclerView: RecyclerView
    lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        drawerLayout = findViewById(R.id.drawerLayout)

        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnNewSession = findViewById<ImageButton>(R.id.btnNewSession)

        val btnSchedule = findViewById<ImageButton>(R.id.btnSchedule)

        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        val inputMessage = findViewById<EditText>(R.id.inputText)

        sessionRecyclerView = findViewById(R.id.sessionRecyclerView)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        btnSchedule.setOnClickListener {
            val intent = Intent(this@ChatActivity, ScheduleActivity::class.java)
            startActivity(intent)
        }
        // 打开左侧抽屉
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
        // 发送消息
        btnSend.setOnClickListener {

            val text = inputMessage.text.toString()

            if (text.isEmpty() || currentSessionId == -1L) return@setOnClickListener

            // 添加用户消息
            messageList.add(ChatMessage("user", text))
            chatAdapter.notifyItemInserted(messageList.size - 1)

            chatRecyclerView.scrollToPosition(messageList.size - 1)

            streamChat(text)

            inputMessage.setText("")
        }
        // 新建会话
        btnNewSession.setOnClickListener {
            createSession()
        }
        // 初始化聊天列表
        initChatList()
        // 初始化会话列表
        initSessionList()
        loadSessions()
    }

    // 初始化聊天列表
    private fun initChatList() {

        chatAdapter = ChatAdapter(messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter
    }

    // 初始化会话列表
    private fun initSessionList() {

        sessionAdapter = SessionAdapter(
            sessionList,
            onClick = { session ->

                currentSessionId = session.id

                drawerLayout.closeDrawer(Gravity.LEFT)

                // 清空聊天窗口
                messageList.clear()
                chatAdapter.notifyDataSetChanged()

                // 加载历史记录
                lifecycleScope.launch {

                    request<List<ChatMessage>>(
                        apiCall = { RetrofitClient.apiService.getMessageList(currentSessionId) },
                        onSuccess = { data ->
                            messageList.addAll(data ?: emptyList())
                            chatAdapter.notifyDataSetChanged()
                        },
                        onError = { msg ->
                            Toast.makeText(this@ChatActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onDelete = { session ->

                deleteSession(session.id)

            }
        )

        sessionRecyclerView.layoutManager = LinearLayoutManager(this)
        sessionRecyclerView.adapter = sessionAdapter
    }

    // 加载会话列表
    private fun loadSessions() {

        lifecycleScope.launch {

            request(
                apiCall = {
                    RetrofitClient.apiService.getSessionList()
                },

                onSuccess = { data ->
                    sessionList.clear()
                    sessionList.addAll(data ?: emptyList())
                    sessionAdapter.notifyDataSetChanged()
                },

                onError = { msg ->
                    Toast.makeText(this@ChatActivity, msg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // 创建会话
    private fun createSession() {

        lifecycleScope.launch {
            request(
                apiCall = {
                    RetrofitClient.apiService.createSession()
                },
                onSuccess = {
                    loadSessions()
                },
                onError = { msg ->
                    Toast.makeText(
                        this@ChatActivity,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    // 删除会话
    private fun deleteSession(id: Long) {

        lifecycleScope.launch {

            request(
                apiCall = {
                    RetrofitClient.apiService.deleteSession(id)
                },
                onSuccess = {
                    Toast.makeText(
                        this@ChatActivity,
                        "删除成功",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadSessions()
                },
                onError = { msg ->
                    Toast.makeText(
                        this@ChatActivity,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    // 发送聊天消息
    private fun sendMessage(text: String) {
        val request = ChatRequest(
            sessionId = currentSessionId,
            content = text
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.chat(request)

                if (response.isSuccessful && response.body()?.code == 200) {

                    val aiMessage = response.body()?.data
                    if (aiMessage != null) {
                        messageList.add(aiMessage)
                        chatAdapter.notifyItemInserted(messageList.size - 1)
                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 流式回复
    private fun streamChat(text: String){

        val sp = getSharedPreferences("user", MODE_PRIVATE)
        val token = sp.getString("token", "") ?: ""

        // 1 创建空的AI消息
        val aiMessage = ChatMessage("assistant", "")
        messageList.add(aiMessage)
        chatAdapter.notifyItemInserted(messageList.size - 1)

        // 2 建立SSE连接
        Log.d("SSE", "发送请求")
        SSEClient.connect(
            sessionId = currentSessionId,
            content = text,
            token = token,

            onMessage = { chunk ->

                runOnUiThread {

                    aiMessage.content += chunk

                    chatAdapter.notifyItemChanged(messageList.size - 1)

                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }
            },

            onComplete = {
                runOnUiThread {
                    android.util.Log.d("SSE", "AI回复完成")
                }
            }
        )
    }
}