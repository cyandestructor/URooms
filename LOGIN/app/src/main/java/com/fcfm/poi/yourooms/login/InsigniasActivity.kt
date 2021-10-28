package com.fcfm.poi.yourooms.login

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class InsigniasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_insignias)


        findViewById<ImageButton>(R.id.imageButton_return).setOnClickListener{
            val pantallaperfil = Intent(this,PerfilActivity::class.java)
            startActivity(pantallaperfil)
        }
    }
}