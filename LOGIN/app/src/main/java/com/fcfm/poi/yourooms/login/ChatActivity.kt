package com.fcfm.poi.yourooms.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.MessageListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.File
import com.fcfm.poi.yourooms.login.data.models.Message
import com.fcfm.poi.yourooms.login.data.models.User
import com.fcfm.poi.yourooms.login.data.models.dao.FileDao
import com.fcfm.poi.yourooms.login.data.models.dao.MessageDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class ChatActivity : AppCompatActivity (){
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var messageRecyclerView: RecyclerView
    private var chatId: String? = null
    private val messageDao = MessageDao()
    private var messageListAdapter: MessageListAdapter? = null
    private var encryptionMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_pantalla)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<ImageButton>(R.id.imageButton_sendmess).setOnClickListener {
            sendMessage(it)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        chatId = intent.getStringExtra("chatId")
        val chatName = intent.getStringExtra("chatName")
        supportActionBar?.title = chatName

        findViewById<ImageButton>(R.id.imgBtn_position).setOnClickListener {
            sendLocation(it)
        }

        findViewById<ImageButton>(R.id.imageButton_adjfile).setOnClickListener {
            launchFileActivity.launch("*/*")
        }

        findViewById<ImageButton>(R.id.imgBtn_encrypt).setOnClickListener {
            encryptionMode = !encryptionMode

            if (encryptionMode) {
                Toast.makeText(applicationContext, "Cifrado de mensajes activado", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(applicationContext, "Cifrado de mensajes desactivado", Toast.LENGTH_SHORT).show()
            }
        }
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

                messageListAdapter?.setOnClickListener {
                    val message = it.tag as Message
                    if (message.hasMultimedia != null && message.hasMultimedia) {
                        val messageId = message.id
                        val i = Intent(this@ChatActivity, ContainerFilesActivity::class.java)
                        i.putExtra("containerId", messageId)
                        startActivity(i)
                    }
                }

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
        var body = messageEditText.text.toString()
        CoroutineScope(Dispatchers.IO).launch {

            val userModel = UserDao().getUser(userId)

            if (encryptionMode) {
                body = CifradoUtils.cifrar(body, "mypassword")
            }

            val message = Message(
                null,
                Timestamp.now(),
                body,
                userModel,
                false,
                encryptionMode
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(applicationContext, "Permiso aceptado. Int??ntelo de nuevo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Debe permitir compartir su ubicaci??n", Toast.LENGTH_SHORT).show()
            }
        }

    private fun sendLocationMessage(it: View, location : String) {
        val user = AuthenticationManager().getCurrentUser() ?: return
        val userId = user.uid

        var body = location
        CoroutineScope(Dispatchers.IO).launch {

            val userModel = UserDao().getUser(userId)

            if (encryptionMode) {
                body = CifradoUtils.cifrar(body, "mypassword")
            }

            val message = Message(
                null,
                Timestamp.now(),
                body,
                userModel,
                false,
                encryptionMode
            )

            val messageId = messageDao.addMessage(message, chatId!!)
            withContext(Dispatchers.Main) {
                if (messageId == null) {
                    Toast.makeText(applicationContext, "Message could not be sent", Toast.LENGTH_SHORT).show()
                }
                it.isEnabled = true
            }
        }
    }

    private fun sendLocation(it: View) {
        it.isEnabled = false
        CoroutineScope(Dispatchers.IO).launch {
            val location = getLocation()
            withContext(Dispatchers.Main) {
                if (location != null) {
                    sendLocationMessage(it, location)
                }
                else {
                    Toast.makeText(applicationContext, "No se puede enviar la ubicaci??n", Toast.LENGTH_SHORT).show()
                    it.isEnabled = true
                }
            }
        }
    }

    private suspend fun getLocation() : String? {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location = fusedLocationClient.lastLocation.await()

                return translateCoordinates(location?.latitude!!, location.longitude)
            }
            else {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }

        }else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        return null
    }

    private fun translateCoordinates(latitude : Double, longitude : Double) : String? {
        val geocoder = Geocoder(this, Locale.getDefault())

        val directions = geocoder.getFromLocation(latitude, longitude, 1)

        if (directions.size > 0) {
            return directions[0].getAddressLine(0)
        }

        return null
    }

    private var launchFileActivity = registerForActivityResult(ActivityResultContracts.GetContent()) {
        sendFileMessage(it)
    }

    private fun sendFileMessage(fileUri : Uri) {
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)
            var result = false

            if (userModel != null) {
                val uploadedFile = FileDao().uploadFile(fileUri, "files")
                val url = uploadedFile?.url
                if (url != null) {

                    val messageId = MessageDao().getNewMessageId(chatId!!)

                    val file = File(
                        null,
                        messageId,
                        chatId!!,
                        url,
                        uploadedFile.name,
                        uploadedFile.contentType,
                        Date()
                    )

                    val fileId = FileDao().addFile(file)
                    if (fileId != null) {
                        val message = Message(
                            messageId,
                            Timestamp.now(),
                            uploadedFile.name,
                            userModel,
                            true
                        )

                        val newMessageId = messageDao.addMessageWithId(message, chatId!!)
                        if (newMessageId != null && messageId == newMessageId) {
                            result = true
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                if (!result) {
                    Toast.makeText(applicationContext, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}