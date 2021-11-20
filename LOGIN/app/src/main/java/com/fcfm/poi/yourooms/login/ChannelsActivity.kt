package com.fcfm.poi.yourooms.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.ChannelListAdapter
import com.fcfm.poi.yourooms.login.data.models.Channel
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChannelsActivity : AppCompatActivity() {
    private lateinit var channelsRecyclerView: RecyclerView
    private var roomName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels)

        channelsRecyclerView = findViewById(R.id.channels_list)
        channelsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        roomName = intent.getStringExtra("roomName")
        findViewById<TextView>(R.id.room_name).text = roomName

        loadChannels()
    }

    private fun loadChannels() {
        val roomId = intent.getStringExtra("roomId") ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val channels = ChannelDao().getRoomChannels(roomId)
            val channelsAdapter = ChannelListAdapter(channels)

            channelsAdapter.setOnClickListener {
                val channel = it.tag as Channel
                val i = Intent(this@ChannelsActivity, ForoGenActivity::class.java)
                i.putExtra("channelId", channel.id)
                i.putExtra("channelName", channel.name)
                i.putExtra("roomName", roomName)
                i.putExtra("roomId", roomId)
                startActivity(i)
            }

            withContext(Dispatchers.Main) {
                channelsRecyclerView.adapter = channelsAdapter
            }
        }
    }
}