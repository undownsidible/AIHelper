package com.example.aihelper.ui
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aihelper.R
import com.example.aihelper.api.RetrofitClient
import com.example.aihelper.model.ScheduleCreateRequest
import com.example.aihelper.model.ScheduleUpdateRequest
import com.example.aihelper.util.request
import kotlinx.coroutines.launch

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

        etName = findViewById(R.id.etName)
        tvStart = findViewById(R.id.tvStartTime)
        tvEnd = findViewById(R.id.tvEndTime)
        etRemark = findViewById(R.id.etRemark)
        btnSave = findViewById(R.id.btnSave)

        // 获取数据
        scheduleId = intent.getLongExtra("id", -1)

        val name = intent.getStringExtra("name")
        val startTime = intent.getStringExtra("startTime")
        val endTime = intent.getStringExtra("endTime")
        val remark = intent.getStringExtra("remark")

        // 如果是编辑 → 回填数据
        if (scheduleId != -1L) {
            etName.setText(name)
            tvStart.setText(startTime)
            tvEnd.setText(endTime)
            etRemark.setText(remark)
        }

        btnSave.setOnClickListener {
            saveSchedule()
        }
        tvStart.setOnClickListener {
            pickDateTime { time ->
                tvStart.text = time
            }
        }
        tvEnd.setOnClickListener {
            pickDateTime { time ->
                tvEnd.text = time
            }
        }
    }

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
            Toast.makeText(this@ScheduleEditActivity,
                "名称和开始时间不能为空",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (scheduleId == -1L) {
            // 新增
            val request = ScheduleCreateRequest(
                name = name,
                startTime = startTime,
                endTime = if (endTime.isBlank()) null else endTime,
                remark = if (remark.isBlank()) null else remark
            )

            createSchedule(request)

        } else {
            // 编辑
            val request = ScheduleUpdateRequest(
                id = scheduleId,
                name = name,
                startTime = startTime,
                endTime = if (endTime.isBlank()) null else endTime,
                remark = if (remark.isBlank()) null else remark
            )

            updateSchedule(request)
        }
    }

    private fun createSchedule(requestBody: ScheduleCreateRequest) {

        lifecycleScope.launch {

            request(
                apiCall = { RetrofitClient.apiService.createSchedule(requestBody) },
                onSuccess = {
                    Toast.makeText(this@ScheduleEditActivity,
                        "新增成功",
                        Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = {
                    Toast.makeText(this@ScheduleEditActivity,
                        it,
                        Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun updateSchedule(requestBody: ScheduleUpdateRequest) {

        lifecycleScope.launch {

            request(
                apiCall = { RetrofitClient.apiService.updateSchedule(requestBody) },
                onSuccess = {
                    Toast.makeText(this@ScheduleEditActivity,
                        "更新成功",
                        Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = {
                    Toast.makeText(this@ScheduleEditActivity,
                        it,
                        Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}