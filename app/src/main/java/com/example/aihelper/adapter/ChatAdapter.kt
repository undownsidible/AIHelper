package com.example.aihelper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aihelper.R
import com.example.aihelper.model.ChatMessage

class ChatAdapter(private val list: MutableList<ChatMessage>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (list[position].role == "user") 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = if (viewType == 0) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_ai, parent, false)
        }

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = list[position]
        val tv = holder.itemView.findViewById<TextView>(R.id.tvMessage)

        tv.text = message.content
    }

    override fun getItemCount() = list.size

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view)
}