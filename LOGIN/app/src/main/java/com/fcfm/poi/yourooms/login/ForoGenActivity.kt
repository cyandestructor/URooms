package com.fcfm.poi.yourooms.login

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ForoGenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.foro_generalpantalla)

        val channelName = intent.getStringExtra("channelName")
        findViewById<TextView>(R.id.creargpo_text).text = channelName

        val roomName = intent.getStringExtra("roomName")
        findViewById<TextView>(R.id.channel_room_name).text = roomName
    }
}