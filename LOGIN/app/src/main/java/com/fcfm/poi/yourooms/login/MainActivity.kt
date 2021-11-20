package com.fcfm.poi.yourooms.login

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.authentication.EmailPasswordAuthentication
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.entrar_login).setOnClickListener {
            login(it)
        }

        findViewById<Button>(R.id.boton_registrarse).setOnClickListener {
            val i = Intent(this@MainActivity, CrearActivity::class.java)
            startActivity(i)
        }

    }

    private fun login(it: View) {
        it.isEnabled = false
        val email = findViewById<EditText>(R.id.Usuario_login).text.toString()
        val password = findViewById<EditText>(R.id.usuario_contrasena).text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            val authentication = EmailPasswordAuthentication(email, password)

            val success = authentication.signIn()

            withContext(Dispatchers.Main) {
                if (success) {
                    it.isEnabled = true
                    val i = Intent(this@MainActivity, PrincipalActivity::class.java)
                    startActivity(i)
                }
                else {
                    Toast.makeText(applicationContext, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                }
                it.isEnabled = true
            }
        }
    }
}

/*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.equipos_pantalla,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.nav_opciones ->{
                Toast.makeText(this, "Selecciono opciones", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.nav_cuenta ->{
                Toast.makeText(this, "Selecciono cuenta", Toast.LENGTH_SHORT).show()
                true
            }

            else -> false
        }
    }
    */
