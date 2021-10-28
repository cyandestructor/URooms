package com.fcfm.poi.yourooms.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.view.inputmethod.InputBinding
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.poi.yourooms.login.databinding.ActivityMainBinding

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_perfil)

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
                TODO("Not yet implemented")
            }
        }

        findViewById<ImageButton>(R.id.button_return).setOnClickListener{
            val pantallainic = Intent(this,PrincipalActivity::class.java)
            startActivity(pantallainic)
        }

        findViewById<Button>(R.id.button_insignias).setOnClickListener{
            val pantallainsig = Intent(this,InsigniasActivity::class.java)
            startActivity(pantallainsig)
        }
    }
}