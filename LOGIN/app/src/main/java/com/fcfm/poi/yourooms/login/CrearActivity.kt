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
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.authentication.EmailPasswordAuthentication
import com.fcfm.poi.yourooms.login.data.models.User
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CrearActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_usuario)

        findViewById<Button>(R.id.bn_iniciarsesion).setOnClickListener {
            val i = Intent(this@CrearActivity, MainActivity::class.java)
            startActivity(i)
        }

        findViewById<Button>(R.id.boton_crearcuenta).setOnClickListener {
            createAccount(it)
        }
    }

    private fun createAccount(it: View) {
        it.isEnabled = false

        val name = findViewById<EditText>(R.id.Nombre_crearcuenta).text.toString()
        val lastname = findViewById<EditText>(R.id.apellido_crearcuenta).text.toString()
        val email = findViewById<EditText>(R.id.correo_crearcuenta).text.toString()
        val password = findViewById<EditText>(R.id.contrasena_crearcuenta).text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            var registeredUserId : String? = null

            if (EmailPasswordAuthentication(email, password).registerUser()) {
                val userId = AuthenticationManager().getCurrentUser()?.uid

                if (userId != null) {
                    val user = User(
                        userId,
                        name,
                        lastname,
                        null,
                        null,
                        email
                    )

                    registeredUserId = UserDao().addUser(user)
                }
            }

            withContext(Dispatchers.Main) {
                if (registeredUserId != null) {
                    it.isEnabled = true
                    val i = Intent(this@CrearActivity, PrincipalActivity::class.java)
                    startActivity(i)
                }
                else{
                    Toast.makeText(applicationContext, "No se ha podido crear la cuenta", Toast.LENGTH_SHORT).show()
                }
                it.isEnabled = true
            }
        }
    }
}