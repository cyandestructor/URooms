package com.fcfm.poi.yourooms.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.ChatListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Chat
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentoChats: Fragment(R.layout.fragmento_chats) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadChatList(view)
    }

    private lateinit var chatRecyclerView: RecyclerView

    private fun loadChatList(view: View) {

        chatRecyclerView = view.findViewById(R.id.Lista_chats)
        chatRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = AuthenticationManager().getCurrentUser()
            if (currentUser != null) {
                val userId = currentUser.uid
                val userChats = ChatDao().getUserChats(userId)

                val chatListAdapter = ChatListAdapter(userChats)
                chatListAdapter.setOnClickListener {
                    val chat = it.tag as Chat
                    val i = Intent(activity, ChatActivity::class.java)
                    i.putExtra("chatId", chat.id)
                    i.putExtra("chatName", chat.name)
                    startActivity(i)
                }

                withContext(Dispatchers.Main) {
                    chatRecyclerView.adapter = chatListAdapter
                }
            }
        }
    }



}