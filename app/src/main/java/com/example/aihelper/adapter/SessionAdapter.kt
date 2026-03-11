package com.example.aihelper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aihelper.R
import com.example.aihelper.model.Session

class SessionAdapter(
    private val list: MutableList<Session>,
    private val onClick: (Session) -> Unit,
    private val onDelete: (Session) -> Unit
) : RecyclerView.Adapter<SessionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.sessionTitle)
        val deleteBtn: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val session = list[position]

        holder.title.text = session.title

        holder.itemView.setOnClickListener {
            onClick(session)
        }

        holder.deleteBtn.setOnClickListener {
            onDelete(session)
        }
    }

    override fun getItemCount(): Int = list.size
}