package com.fcfm.poi.yourooms.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.ChannelListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Channel
import com.fcfm.poi.yourooms.login.data.models.dao.ChannelDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChannelsActivity : AppCompatActivity() {
    private lateinit var channelsRecyclerView: RecyclerView
    private var roomName: String? = null
    private var roomId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        roomId = intent.getStringExtra("roomId")
        roomName = intent.getStringExtra("roomName")
        toolbar.title = roomName

        channelsRecyclerView = findViewById(R.id.channels_list)
        channelsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        loadChannels()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.opt_members -> {
                val i = Intent(this, MiembrosActivity::class.java)
                i.putExtra("groupId", roomId!!)
                i.putExtra("type", "room")
                startActivity(i)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadChannels() {
        val roomId = intent.getStringExtra("roomId") ?: return
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val channels = ChannelDao().getRoomUserChannels(roomId, userId)
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