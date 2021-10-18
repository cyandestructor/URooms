package com.fcfm.poi.yourooms.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.authentication.EmailPasswordAuthentication
import com.fcfm.poi.yourooms.login.data.models.Chat
import com.fcfm.poi.yourooms.login.data.models.Message
import com.fcfm.poi.yourooms.login.data.models.User
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import com.fcfm.poi.yourooms.login.data.models.dao.MessageDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class Test : AppCompatActivity() {
    private var chatId: String? = null
    private val messageDao: MessageDao = MessageDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        findViewById<Button>(R.id.registerBtn).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString()
            val name = findViewById<EditText>(R.id.nameInput).text.toString()
            val lastname = findViewById<EditText>(R.id.lastnameInput).text.toString()

            register(email, password, name, lastname)
        }

        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString()

            login(email, password)
        }

        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            AuthenticationManager().signOut()
            showUser(null)
        }

        findViewById<Button>(R.id.joinChatBtn).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val message = if (joinChat()) "You joined the chat" else "Could not join the chat"

                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.sendBtn).setOnClickListener {
            val messageText = findViewById<EditText>(R.id.messageInput).text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val showMessage = if (sendMessage(messageText)) "Message sent" else "Message could not be sent"
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, showMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.checkBtn).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                getUserChats()
            }
        }
    }

    private fun register(email: String, password: String, name: String, lastname: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val authentication = EmailPasswordAuthentication(email, password)

            if (authentication.registerUser()) {
                val id = authentication.getCurrentUser()!!.uid

                val userDao = UserDao()
                val userModel = User(id, name, lastname, null, null, email)
                userDao.addUser(userModel)

                withContext(Dispatchers.Main) {
                    showUser(userModel)
                }
            }
        }
    }

    private fun showUser(user: User?) {
        var fullname = "None"
        if (user != null) {
            fullname = "${user.name} ${user.lastname}"
        }

        findViewById<TextView>(R.id.currentUserLabel).text = fullname
    }

    private fun login(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val authentication = EmailPasswordAuthentication(email, password)

            if (authentication.signIn()) {
                val id = authentication.getCurrentUser()!!.uid

                val userDao = UserDao()
                val userModel = userDao.getUser(id)

                withContext(Dispatchers.Main) {
                    showUser(userModel)
                }
            }
        }
    }

    suspend fun joinChat(): Boolean {
        val user = AuthenticationManager().getCurrentUser()

        if (user != null) {
            val id = user.uid
            val userModel = UserDao().getUser(id)

            return if (chatId != null) {
                var success = false

                if (userModel != null) {
                    success = ChatDao().addMembersToChat(chatId!!, listOf(userModel))
                }
                success
            } else {
                if (userModel != null) {
                    val chat = Chat(
                        null,
                        "Test chat",
                        null,
                        listOf(userModel)
                    )

                    chatId = ChatDao().addChat(chat)
                }
                chatId != null
            }
        }

        return false
    }

    suspend fun sendMessage(messageText: String): Boolean {
        if (chatId == null)
            return false

        val user = AuthenticationManager().getCurrentUser() ?: return false
        val id = user.uid

        val userModel = UserDao().getUser(id) ?: return false

        val message = Message(
            null,
            Timestamp.now(),
            messageText,
            userModel
        )

        MessageDao().addMessage(message, chatId!!) ?: return false

        return true
    }

    suspend fun getUserChats() {
        val user = AuthenticationManager().getCurrentUser()

        if (user != null) {
            val id = user.uid

            val chats = ChatDao().getUserChats(id)

            Log.i("Chats", "User chats")
            for (chat in chats) {
                Log.i("Chats", "Id: ${chat.id} Name: ${chat.name}")
                getChatMessages(chat.id!!)
            }
        }
    }

    suspend fun getChatMessages(chatId: String) {
        val messages = messageDao.getChatMessages(chatId, 2, true)

        for (message in messages) {
            Log.i("Message", "Id: ${message.id}")
        }
    }
}