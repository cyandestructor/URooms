package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.MessageListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Message
import com.fcfm.poi.yourooms.login.data.models.dao.MessageDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity (){
    private lateinit var messageRecyclerView: RecyclerView
    private var chatId: String? = null
    private val messageDao = MessageDao()
    private var messageListAdapter: MessageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_pantalla)

        findViewById<ImageButton>(R.id.imageButton_sendmess).setOnClickListener {
            sendMessage(it)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        chatId = intent.getStringExtra("chatId")
        val chatName = intent.getStringExtra("chatName")
        supportActionBar?.title = chatName
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.opt_members -> {
                val i = Intent(this, MiembrosActivity::class.java)
                i.putExtra("groupId", chatId!!)
                i.putExtra("type", "chat")
                startActivity(i)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        loadMessages()
    }

    override fun onStop() {
        super.onStop()
        if (chatId != null) {
            messageDao.stopListeningChatLatestMessages(chatId!!)
        }
    }

    private fun loadMessages() {
        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        messageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        if (chatId != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val messages = messageDao.getChatMessages(chatId!!)

                messageListAdapter = MessageListAdapter(messages.toMutableList())
                withContext(Dispatchers.Main) {
                    messageRecyclerView.adapter = messageListAdapter
                    listenToMessages()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listenToMessages() {
        messageDao.listenChatLatestMessages(chatId!!) {
            messageListAdapter?.messageList?.addAll(0, it)
            messageListAdapter?.notifyDataSetChanged()
        }
    }

    private fun sendMessage(it: View) {
        val user = AuthenticationManager().getCurrentUser() ?: return
        val userId = user.uid

        it.isEnabled = false
        val messageEditText = findViewById<EditText>(R.id.Edit_mensaje)
        val body = messageEditText.text.toString()
        CoroutineScope(Dispatchers.IO).launch {

            val userModel = UserDao().getUser(userId)

            val message = Message(
                null,
                Timestamp.now(),
                body,
                userModel
            )

            val messageId = messageDao.addMessage(message, chatId!!)
            withContext(Dispatchers.Main) {
                if (messageId != null) {
                    messageEditText.text.clear()
                }
                else {
                    Toast.makeText(applicationContext, "Message could not be sent", Toast.LENGTH_SHORT).show()
                }
                it.isEnabled = true
            }
        }
    }
}