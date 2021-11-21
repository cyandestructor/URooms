package com.fcfm.poi.yourooms.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.MemberListAdapter
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import com.fcfm.poi.yourooms.login.data.models.dao.RoomDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MiembrosActivity : AppCompatActivity(){
    private lateinit var membersRecyclerView: RecyclerView
    private var groupId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_miembros)

        membersRecyclerView = findViewById(R.id.MiembrosRecyclerView)
        membersRecyclerView.layoutManager = LinearLayoutManager(this)

        groupId = intent.getStringExtra("channelId")
        if (groupId != null) {
            loadChannelMembers()
            return
        }

        groupId = intent.getStringExtra("roomId")
        if (groupId != null) {
            loadRoomMembers()
            return
        }

        groupId = intent.getStringExtra("chatId")
        if (groupId != null) {
            loadChatMembers()
            return
        }
    }

    private fun loadChannelMembers() {
        CoroutineScope(Dispatchers.IO).launch {
            val roomId = intent.getStringExtra("roomId")
            if (roomId != null) {
                val members = ChannelDao().getChannelMembers(roomId, groupId!!)

                val memberListAdapter = MemberListAdapter(members)
                withContext(Dispatchers.Main) {
                    membersRecyclerView.adapter = memberListAdapter
                }
            }
        }
    }

    private fun loadRoomMembers() {
        CoroutineScope(Dispatchers.IO).launch {
            val members = RoomDao().getRoomMembers(groupId!!)

            val memberListAdapter = MemberListAdapter(members)
            withContext(Dispatchers.Main) {
                membersRecyclerView.adapter = memberListAdapter
            }
        }
    }

    private fun loadChatMembers() {
        CoroutineScope(Dispatchers.IO).launch {
            val members = ChatDao().getChatMembers(groupId!!)

            val memberListAdapter = MemberListAdapter(members)
            withContext(Dispatchers.Main) {
                membersRecyclerView.adapter = memberListAdapter
            }
        }
    }
}