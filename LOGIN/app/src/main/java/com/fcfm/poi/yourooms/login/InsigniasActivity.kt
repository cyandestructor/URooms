package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.BadgeListAdapter

class InsigniasActivity : AppCompatActivity() {
    private lateinit var badgesRecyclerView: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_insignias)

        badgesRecyclerView = findViewById(R.id.InsigniaRecyclerView)
        badgesRecyclerView.layoutManager = LinearLayoutManager(this)

        val score = intent.getIntExtra("score", 0)

        findViewById<TextView>(R.id.totalScore).text = "Puntos: $score"

        val badges = intent.getStringArrayExtra("badges")
        if (badges != null) {
            badgesRecyclerView.adapter = BadgeListAdapter(badges)
        }
    }
}