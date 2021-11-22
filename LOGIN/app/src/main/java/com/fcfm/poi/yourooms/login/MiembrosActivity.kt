package com.fcfm.poi.yourooms.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.MemberListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Chat
import com.fcfm.poi.yourooms.login.data.models.User
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import com.fcfm.poi.yourooms.login.data.models.dao.RoomDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
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

                memberListAdapter.setOnClickListener {
                    val user = it.tag as User
                    createChatWithUser(user)
                }

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

            memberListAdapter.setOnClickListener {
                val user = it.tag as User
                createChatWithUser(user)
            }

            withContext(Dispatchers.Main) {
                membersRecyclerView.adapter = memberListAdapter
            }
        }
    }

    private fun loadChatMembers() {
        CoroutineScope(Dispatchers.IO).launch {
            val members = ChatDao().getChatMembers(groupId!!)

            val memberListAdapter = MemberListAdapter(members)

            memberListAdapter.setOnClickListener {
                val user = it.tag as User
                createChatWithUser(user)
            }

            withContext(Dispatchers.Main) {
                membersRecyclerView.adapter = memberListAdapter
            }
        }
    }

    private fun createChatWithUser(user: User) {
        val currentUserId = AuthenticationManager().getCurrentUser()?.uid

        if (currentUserId != null) {
            CoroutineScope(Dispatchers.IO).launch {

                var existingChatId = ChatDao().getPrivateChatBetween(
                    currentUserId,
                    user.id!!
                )

                if (existingChatId == null) {

                    val userModel = UserDao().getUser(currentUserId)

                    val chat = Chat(
                        null,
                        "${user.name} ${user.lastname}",
                        user.image,
                        listOf(userModel!!, user),
                        null,
                        true
                    )

                    existingChatId = ChatDao().addChat(chat)
                }

                withContext(Dispatchers.Main) {
                    if (existingChatId != null) {
                        redirectToChatActivity(
                            existingChatId,
                            "${user.name} ${user.lastname}"
                        )
                    }
                    else {
                        Toast.makeText(applicationContext, "Lo sentimos, algo ha fallado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun redirectToChatActivity(chatId: String, chatName: String) {
        val i = Intent(this, ChatActivity::class.java)
        i.putExtra("chatId", chatId)
        i.putExtra("chatName", chatName)
        startActivity(i)
    }
}