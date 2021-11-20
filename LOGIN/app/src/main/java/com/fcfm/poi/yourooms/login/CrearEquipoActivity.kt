package com.fcfm.poi.yourooms.login

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Channel
import com.fcfm.poi.yourooms.login.data.models.Room
import com.fcfm.poi.yourooms.login.data.models.User
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import com.fcfm.poi.yourooms.login.data.models.dao.RoomDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearEquipoActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_crearequipo)

        findViewById<Button>(R.id.button_creargpo).setOnClickListener {
            createRoom(it)
        }
    }

    private fun createRoom(it: View) {
        val user = AuthenticationManager().getCurrentUser() ?: return
        val userId = user.uid

        it.isEnabled = false
        val roomNameTextView = findViewById<EditText>(R.id.add_nombre)
        val roomName = roomNameTextView.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)

            val members = listOf<User>(userModel!!)

            val room = Room(
                null,
                roomName,
                null,
                members
            )

            var channelId : String? = null
            val roomId = RoomDao().addRoom(room)

            if (roomId != null) {
                // Create General Channel
                val channel = Channel(
                    null,
                    "General",
                    members
                )

                channelId = ChannelDao().addChannel(channel, roomId)
            }

            withContext(Dispatchers.Main) {
                if (roomId != null && channelId != null) {
                    Toast.makeText(applicationContext, "Equipo creado con éxito", Toast.LENGTH_SHORT).show()
                    roomNameTextView.text.clear()
                }
                else {
                    Toast.makeText(applicationContext, "No se ha podido crear el equipo", Toast.LENGTH_SHORT).show()
                }

                it.isEnabled = true
            }
        }
    }
}