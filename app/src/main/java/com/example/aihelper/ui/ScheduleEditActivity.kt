package com.example.aihelper.ui

import android.Manifest
import android.app.*
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.aihelper.R
import com.example.aihelper.api.RetrofitClient
import com.example.aihelper.model.ScheduleCreateRequest
import com.example.aihelper.model.ScheduleUpdateRequest
import com.example.aihelper.util.request
import com.example.aihelper.utils.ReminderWorker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ScheduleEditActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var etRemark: EditText
    private lateinit var btnSave: Button

    private var scheduleId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)

        createNotificationChannel()
        requestNotificationPermission()

        etName = findViewById(R.id.etName)
        tvStart = findViewById(R.id.tvStartTime)
        tvEnd = findViewById(R.id.tvEndTime)
        etRemark = findViewById(R.id.etRemark)
        btnSave = findViewById(R.id.btnSave)

        scheduleId = intent.getLongExtra("id", -1)

        val name = intent.getStringExtra("name")
        val startTime = intent.getStringExtra("startTime")
        val endTime = intent.getStringExtra("endTime")
        val remark = intent.getStringExtra("remark")

        if (scheduleId != -1L) {
            etName.setText(name)
            tvStart.text = startTime
            tvEnd.text = endTime
            etRemark.setText(remark)
        }

        btnSave.setOnClickListener { saveSchedule() }

        tvStart.setOnClickListener {
            pickDateTime { tvStart.text = it }
        }

        tvEnd.setOnClickListener {
            pickDateTime { tvEnd.text = it }
        }
    }

    // ===== 时间选择 =====
    private fun pickDateTime(onResult: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this,
            { _, year, month, day ->
                TimePickerDialog(this,
                    { _, hour, minute ->
                        val result = String.format(
                            "%04d-%02d-%02d %02d:%02d:00",
                            year, month + 1, day, hour, minute
                        )
                        onResult(result)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveSchedule() {
        val name = etName.text.toString()
        val startTime = tvStart.text.toString()
        val endTime = tvEnd.text.toString()
        val remark = etRemark.text.toString()

        if (name.isBlank() || startTime.isBlank()) {
            Toast.makeText(this, "名称和开始时间不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        if (scheduleId == -1L) {
            val request = ScheduleCreateRequest(
                name, startTime,
                if (endTime.isBlank()) null else endTime,
                if (remark.isBlank()) null else remark
            )
            createSchedule(request, name, startTime)
        } else {
            val request = ScheduleUpdateRequest(
                scheduleId, name, startTime,
                if (endTime.isBlank()) null else endTime,
                if (remark.isBlank()) null else remark
            )
            updateSchedule(request, name, startTime)
        }
    }

    private fun createSchedule(
        requestBody: ScheduleCreateRequest,
        name: String,
        startTime: String
    ) {
        lifecycleScope.launch {
            request(
                apiCall = { RetrofitClient.apiService.createSchedule(requestBody) },
                onSuccess = {
                    scheduleReminder(name, startTime)
                    Toast.makeText(this@ScheduleEditActivity, "新增成功", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = {
                    Toast.makeText(this@ScheduleEditActivity, it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun updateSchedule(
        requestBody: ScheduleUpdateRequest,
        name: String,
        startTime: String
    ) {
        lifecycleScope.launch {
            request(
                apiCall = { RetrofitClient.apiService.updateSchedule(requestBody) },
                onSuccess = {
                    scheduleReminder(name, startTime)
                    Toast.makeText(this@ScheduleEditActivity, "更新成功", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = {
                    Toast.makeText(this@ScheduleEditActivity, it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // ===== 核心提醒 =====
    private fun scheduleReminder(title: String, startTime: String) {

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetTime = sdf.parse(startTime)?.time ?: return

        val triggerTime = targetTime
        val delay = triggerTime - System.currentTimeMillis()

        if (delay <= 0) return

        val data = Data.Builder()
            .putString("title", title)
            .build()

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("schedule_$scheduleId")
            .build()

        WorkManager.getInstance(this).cancelAllWorkByTag("schedule_$scheduleId")
        WorkManager.getInstance(this).enqueue(work)
    }

    // ===== 通知权限 =====
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    // ===== 通知渠道 =====
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "remind",
                "日程提醒",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}