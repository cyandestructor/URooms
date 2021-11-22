package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.view.inputmethod.InputBinding
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.User
import com.fcfm.poi.yourooms.login.data.models.dao.FileDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import com.fcfm.poi.yourooms.login.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {
    private var user : User? = null

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
                val connectionState = lista[p2]
                if (connectionState != "Estado de conexión") {
                    setUserConnectionState(connectionState)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        findViewById<Button>(R.id.button_insignias).setOnClickListener{
            val pantallainsig = Intent(this, InsigniasActivity::class.java)
            pantallainsig.putExtra("badges", user?.badges?.toTypedArray())
            pantallainsig.putExtra("score", user?.score)
            startActivity(pantallainsig)
        }

        findViewById<ImageButton>(R.id.imageButton_addpic).setOnClickListener {
            launchImageActivity.launch("image/*")
        }
    }

    private fun setUserConnectionState(connectionState : String) {
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val success = UserDao().setConnectionState(userId, connectionState)
            withContext(Dispatchers.Main) {
                if (success) {
                    findViewById<TextView>(R.id.textView_estado).text = connectionState
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)
            user = userModel

            if (userModel != null) {
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textView_Nombre_Perfil).text = "${userModel.name} ${userModel.lastname}"
                    findViewById<TextView>(R.id.textView_Correo).text = userModel.email
                    findViewById<TextView>(R.id.textView_estado).text = userModel.connectionState ?: "Desconocido"

                    val imageView = findViewById<ImageView>(R.id.icono_pic_grupo)
                    Picasso.get()
                        .load(userModel.image)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.avatar_placeholder)
                        .into(imageView)
                }
            }
        }
    }

    private var launchImageActivity = registerForActivityResult(ActivityResultContracts.GetContent()) {
        findViewById<ImageView>(R.id.icono_pic_grupo).setImageURI(it)
        editUserImage(it)
    }

    private fun editUserImage(imageUri: Uri) {
        val userId = AuthenticationManager().getCurrentUser()?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)
            var result = false

            if (userModel != null) {
                val url = FileDao().uploadFile(imageUri, "images")?.url
                if (url != null) {
                    val editedUser = User(
                        userId,
                        userModel.name,
                        userModel.lastname,
                        url,
                        null,
                        userModel.email
                    )

                    result = UserDao().updateUser(editedUser)
                }
            }

            withContext(Dispatchers.Main) {
                if (result) {
                    Toast.makeText(applicationContext, "La imagen se ha actualizado", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext, "No se pudo actualizar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}