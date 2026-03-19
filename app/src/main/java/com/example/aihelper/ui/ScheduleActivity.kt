package com.example.aihelper.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aihelper.R
import com.example.aihelper.api.RetrofitClient
import com.example.aihelper.model.Schedule
import com.example.aihelper.util.request
import kotlinx.coroutines.launch

class ScheduleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScheduleAdapter

    private val scheduleList = mutableListOf<Schedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnAdd = findViewById<Button>(R.id.btnAddSchedule)
        recyclerView = findViewById(R.id.recyclerSchedule)

        btnBack.setOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ScheduleAdapter(
            scheduleList,
            onClick = { schedule -> editSchedule(schedule) },
            onLongClick = { schedule -> deleteSchedule(schedule) }
        )

        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            addSchedule()
        }

        loadSchedules()
    }

    /** 查询日程 */
    private fun loadSchedules() {

        lifecycleScope.launch {

            request(
                apiCall = { RetrofitClient.apiService.getScheduleList() },
                onSuccess = { data ->

                    scheduleList.clear()
                    data?.let { scheduleList.addAll(it) }
                    adapter.notifyDataSetChanged()
                },
                onError = { msg ->
                    Toast.makeText(this@ScheduleActivity,
                        msg,
                        Toast.LENGTH_SHORT)
                }
            )
        }
    }

    /** 新增日程 */
    private fun addSchedule() {

        val intent = Intent(this@ScheduleActivity, ScheduleEditActivity::class.java)
        startActivity(intent)
    }

    /** 编辑日程 */
    private fun editSchedule(schedule: Schedule) {

        val intent = Intent(this@ScheduleActivity, ScheduleEditActivity::class.java)

        intent.putExtra("id", schedule.id)
        intent.putExtra("name", schedule.name)
        intent.putExtra("startTime", schedule.startTime)
        intent.putExtra("endTime", schedule.endTime)
        intent.putExtra("remark", schedule.remark)

        startActivity(intent)
    }

    /** 删除日程 */
    private fun deleteSchedule(schedule: Schedule) {

        AlertDialog.Builder(this)
            .setTitle("删除日程")
            .setMessage("确定删除该日程？")
            .setPositiveButton("删除") { _, _ ->

                lifecycleScope.launch {

                    request(
                        apiCall = { RetrofitClient.apiService.deleteSchedule(schedule.id) },
                        onSuccess = {

                            scheduleList.remove(schedule)
                            adapter.notifyDataSetChanged()
                        },
                        onError = { msg ->
                            Toast.makeText(this@ScheduleActivity,
                                msg,
                                Toast.LENGTH_SHORT)
                        }
                    )
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadSchedules()
    }
}