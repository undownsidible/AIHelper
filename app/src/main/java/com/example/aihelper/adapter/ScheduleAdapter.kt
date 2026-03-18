package com.example.aihelper.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aihelper.R
import com.example.aihelper.model.Schedule

class ScheduleAdapter(
    private val list: List<Schedule>,
    private val onClick: (Schedule) -> Unit,
    private val onLongClick: (Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val time: TextView = view.findViewById(R.id.tvTime)
        val remark: TextView = view.findViewById(R.id.tvRemark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val schedule = list[position]

        holder.name.text = schedule.name
        holder.time.text = "${schedule.startTime} - ${schedule.endTime ?: ""}"
        holder.remark.text = schedule.remark ?: ""

        holder.itemView.setOnClickListener {
            onClick(schedule)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(schedule)
            true
        }
    }

    override fun getItemCount(): Int = list.size
}