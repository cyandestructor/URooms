package com.fcfm.poi.yourooms.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.ChatListAdapter
import com.fcfm.poi.yourooms.login.adapters.EquiposListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Chat
import com.fcfm.poi.yourooms.login.data.models.Room
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PrincipalActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_principal)

        setSupportActionBar(findViewById(R.id.toolbar2))
        setupNavigation()

        findViewById<FloatingActionButton>(R.id.boton_flotante).setOnClickListener {
            val toolView: Toolbar = findViewById(R.id.toolbar2)
            val title = toolView.title

            when (title) {
                "Equipo" -> loadCreateRoomScreen()
            }
        }

    }

    private fun loadCreateRoomScreen() {
        val i = Intent(this, CrearEquipoActivity::class.java)
        startActivity(i)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            commit()
        }
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigati)

        val toolView: Toolbar = findViewById(R.id.toolbar2)

        val equipofragment = Fragmento_equipos();
        val chatfragment = FragmentoChats();
        val tareasfragment = Fragmento_Tareas();
        val activfragment = Fragmento_actividades();



        navView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.page_1 -> {
                    toolView.title = "Equipo"
                    setCurrentFragment(equipofragment)
                    true
                }
                R.id.page_2 -> {
                    toolView.title = "Chats"
                    setCurrentFragment(chatfragment)
                    true
                }
                R.id.page_3 -> {
                    toolView.title = "Actividades"
                    setCurrentFragment(activfragment)
                    true
                }
                R.id.page_4 -> {
                    toolView.title = "Tareas"
                    setCurrentFragment(tareasfragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_buscador, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.icon_1->{
                startActivity(Intent(this, PerfilActivity::class.java))
            }
            R.id.icon_2->{
                startActivity(Intent(this, PerfilActivity::class.java))
            }
            R.id.icon_3->{
                startActivity(Intent(this, PerfilActivity::class.java))
            }
            R.id.icon_4->{
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
