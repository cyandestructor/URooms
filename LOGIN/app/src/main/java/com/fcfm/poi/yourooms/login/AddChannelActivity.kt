package com.fcfm.poi.yourooms.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Channel
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddChannelActivity : AppCompatActivity() {
    private var roomId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_channel)

        roomId = intent.getStringExtra("roomId")
        if (roomId != null) {
            findViewById<Button>(R.id.btnAddChannel).setOnClickListener {
                addChannel(it)
            }
        }
    }

    private fun addChannel(it: View) {
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        it.isEnabled = false

        val channelNameText = findViewById<EditText>(R.id.channelName)
        val channelName = channelNameText.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)

            if (userModel != null) {
                val channel = Channel(
                    null,
                    channelName,
                    listOf(userModel)
                )

                val channelId = ChannelDao().addChannel(channel, roomId!!)

                withContext(Dispatchers.Main) {
                    if (channelId != null) {
                        channelNameText.text.clear()
                        Toast.makeText(applicationContext, "Canal creado con éxito", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(applicationContext, "No se pudo crear el canal", Toast.LENGTH_SHORT).show()
                    }
                    it.isEnabled = true
                }
            }
        }
    }
}