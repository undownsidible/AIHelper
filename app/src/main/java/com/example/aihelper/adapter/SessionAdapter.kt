package com.example.aihelper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aihelper.R
import com.example.aihelper.model.Session

class SessionAdapter(
    private val list: MutableList<Session>,
    private val onClick: (Session) -> Unit,
    private val onDelete: (Session) -> Unit,
    private val onRename: (Session) -> Unit
) : RecyclerView.Adapter<SessionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ✅ 改这里
        val title: TextView = view.findViewById(R.id.tv_session_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = list[position]

        holder.title.text = session.title

        // 点击
        holder.itemView.setOnClickListener {
            onClick(session)
        }

        // 长按菜单
        holder.itemView.setOnLongClickListener { view ->

            val popup = PopupMenu(view.context, view)
            popup.menu.add(0, 1, 0, "重命名")
            popup.menu.add(0, 2, 1, "删除")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        onRename(session)
                        true
                    }
                    2 -> {
                        onDelete(session)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
            true
        }
    }

    override fun getItemCount(): Int = list.size
}