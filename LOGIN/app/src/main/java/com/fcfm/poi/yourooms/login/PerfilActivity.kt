package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.view.inputmethod.InputBinding
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import com.fcfm.poi.yourooms.login.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_perfil)

        loadUserData()

        val spinner = findViewById<Spinner>(R.id.spinner_estado)
        val lista = resources.getStringArray(R.array.estados_usuario)

        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, lista)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener = object:
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(this@PerfilActivity, lista[p2],Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        findViewById<Button>(R.id.button_insignias).setOnClickListener{
            val pantallainsig = Intent(this, InsigniasActivity::class.java)
            startActivity(pantallainsig)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)

            if (userModel != null) {
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textView_Nombre_Perfil).text = "${userModel.name} ${userModel.lastname}"
                    findViewById<TextView>(R.id.textView_Correo).text = userModel.email
                }
            }
        }
    }
}