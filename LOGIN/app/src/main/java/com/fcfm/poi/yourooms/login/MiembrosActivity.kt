package com.fcfm.poi.yourooms.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.MemberListAdapter
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import com.fcfm.poi.yourooms.login.data.models.dao.RoomDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MiembrosActivity : AppCompatActivity(){
    private lateinit var membersRecyclerView: RecyclerView
    private var groupId : String? = null
    private var type : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_miembros)

        membersRecyclerView = findViewById(R.id.MiembrosRecyclerView)
        membersRecyclerView.layoutManager = LinearLayoutManager(this)

        groupId = intent.getStringExtra("groupId")
        type = intent.getStringExtra("type")

        if (groupId != null && type != null) {
            when(type) {
                "channel" -> loadChannelMembers()
                "room" -> loadRoomMembers()
                "chat" -> loadChatMembers()
            }

            findViewById<FloatingActionButton>(R.id.add_member).setOnClickListener {
                val i = Intent(this, AddMembersActivity::class.java)
                i.putExtra("groupId", groupId)
                i.putExtra("type", type)

                if (type == "channel") {
                    val roomId = intent.getStringExtra("roomId")
                    i.putExtra("roomId", roomId)
                }

                startActivity(i)
            }
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