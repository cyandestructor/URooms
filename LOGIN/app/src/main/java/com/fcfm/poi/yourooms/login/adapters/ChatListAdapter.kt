package com.fcfm.poi.yourooms.login.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.Chat

class ChatListAdapter(private val chatList: List<Chat>) : RecyclerView.Adapter<ChatListAdapter.ChatHolder>(), View.OnClickListener {

    private var listener: View.OnClickListener? = null

    class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chat: Chat) {
            itemView.findViewById<TextView>(R.id.titulo_nombre).text = chat.name
            itemView.findViewById<TextView>(R.id.lastMessageBody).text = chat.lastMessage?.body
            itemView.tag = chat

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.elemento_chats, parent, false)

        view.setOnClickListener(this)

        return ChatHolder(view)
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }
}