package com.example.aihelper.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aihelper.R
import com.example.aihelper.api.RetrofitClient
import com.example.aihelper.model.RegisterRequest
import com.example.aihelper.util.request
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)

        // 返回
        btnBack.setOnClickListener {
            finish()
        }

        // 注册
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(username, password)
        }
    }

    private fun register(username: String, password: String) {

        lifecycleScope.launch {

            request(
                apiCall = {
                    RetrofitClient.apiService.register(
                        RegisterRequest(username, password)
                    )
                },

                onSuccess = {
                    Toast.makeText(
                        this@RegisterActivity,
                        "注册成功",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 注册成功返回登录页
                    finish()
                },

                onError = { msg ->
                    Toast.makeText(
                        this@RegisterActivity,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}