package com.example.aihelper.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.aihelper.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //左上角会话选择按钮
        val btnSessionSelect = findViewById<ImageButton>(R.id.btnSessionSelect)
        btnSessionSelect.setOnClickListener {
            val intent = Intent(this@ChatActivity, SessionActivity::class.java)
            startActivity(intent)
        }
    }
}