package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.MemberListAdapter
import com.fcfm.poi.yourooms.login.data.models.User
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import com.fcfm.poi.yourooms.login.data.models.dao.RoomDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMembersActivity:AppCompatActivity() {
    private lateinit var usersRecyclerView: RecyclerView

    private var selectedUser : User? = null
    private var type : String? = null
    private var groupId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addmiembros)

        usersRecyclerView = findViewById(R.id.recyclerView_actualmembers)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)

        groupId = intent.getStringExtra("groupId")
        type = intent.getStringExtra("type")

        findViewById<EditText>(R.id.add_nombre).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loadUsers(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        findViewById<Button>(R.id.button_addmiembro).setOnClickListener {
            it.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                val result = addMember()

                withContext(Dispatchers.Main) {
                    if (result) {
                        Toast.makeText(applicationContext, "Se ha agregado a ${selectedUser?.name}", Toast.LENGTH_SHORT).show()
                        selectUser(null)
                    }
                    else {
                        Toast.makeText(applicationContext, "No se pudo agregar al usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            it.isEnabled = true
        }
    }

    private suspend fun addMember() : Boolean {
        var result = false

        if (groupId != null && type != null && selectedUser != null) {
            result = when(type) {
                "channel" -> addChannelMember()
                "room" -> addRoomMember()
                "chat" -> addChatMember()
                else -> false
            }
        }

        return result
    }

    private suspend fun addRoomMember() : Boolean {
        return RoomDao().addMembersToRoom(groupId!!, listOf(selectedUser!!))
    }

    private suspend fun addChannelMember() : Boolean {
        val roomId = intent.getStringExtra("roomId")

        return ChannelDao().addMembersToChannel(groupId!!, roomId!!, listOf(selectedUser!!))
    }

    private suspend fun addChatMember() : Boolean {
        return ChatDao().addMembersToChat(groupId!!, listOf(selectedUser!!))
    }

    @SuppressLint("SetTextI18n")
    private fun selectUser(user: User?) {
        selectedUser = user
        if (user != null) {
            findViewById<TextView>(R.id.selectedUser).text = "${user?.name} ${user.lastname}"
        }
        else {
            findViewById<TextView>(R.id.selectedUser).text = ""
        }
    }

    private fun loadUsers(input: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val users = UserDao().searchUsers(input)

            val adapter = MemberListAdapter(users)

            adapter.setOnClickListener {
                val user = it.tag as User
                selectUser(user)
            }

            withContext(Dispatchers.Main) {
                usersRecyclerView.adapter = adapter
            }
        }
    }
}