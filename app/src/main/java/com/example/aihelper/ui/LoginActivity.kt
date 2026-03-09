package com.example.aihelper.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aihelper.R
import com.example.aihelper.api.RetrofitClient
import com.example.aihelper.model.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            login(username, password)
        }
    }

    private fun login(username: String, password: String) {

        lifecycleScope.launch {

            try {

                val response = RetrofitClient.apiService.login(
                    LoginRequest(username, password)
                )

                if (response.isSuccessful) {
                    //返回成功
                    val body = response.body()
                    if (body?.code == 200) {
                        //登录成功
                        val token = body.data.token
                        val sp = getSharedPreferences("user", MODE_PRIVATE)
                        sp.edit().putString("token", token).apply()
                        Toast.makeText(
                            this@LoginActivity,
                            "登录成功",
                            Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, ChatActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        //登录失败
                        Toast.makeText(this@LoginActivity,
                            body?.message,
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //返回失败
                    Toast.makeText(
                        this@LoginActivity,
                        "登录失败",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@LoginActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}