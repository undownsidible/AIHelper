package com.example.aihelper.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aihelper.R
import com.example.aihelper.api.RetrofitClient
import com.example.aihelper.model.LoginRequest
import com.example.aihelper.util.request
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button   // 新增

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister) // 新增

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            login(username, password)
        }

        // 注册按钮点击事件
        btnRegister.setOnClickListener {
            startActivity(
                Intent(this@LoginActivity, RegisterActivity::class.java)
            )
        }
    }

    private fun login(username: String, password: String) {

        lifecycleScope.launch {

            request(
                apiCall = {
                    RetrofitClient.apiService.login(LoginRequest(username, password))
                },
                onSuccess = { data ->
                    val token = data?.token ?: ""

                    val sp = getSharedPreferences("user", MODE_PRIVATE)
                    sp.edit().putString("token", token).apply()

                    Log.d("token", token)

                    Toast.makeText(
                        this@LoginActivity,
                        "登录成功",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this@LoginActivity, ChatActivity::class.java)
                    )
                    finish()
                },

                onError = { msg ->
                    Toast.makeText(
                        this@LoginActivity,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}