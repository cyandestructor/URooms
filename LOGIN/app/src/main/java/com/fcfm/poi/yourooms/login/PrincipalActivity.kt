package com.fcfm.poi.yourooms.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.ChatListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Chat
import com.fcfm.poi.yourooms.login.data.models.dao.ChatDao
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PrincipalActivity:AppCompatActivity() {
    //private lateinit var chatRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_principal)


        setupNavigation()
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

   /* private fun loadChatList() {
        chatRecyclerView = findViewById(R.id.Lista_chats)
        chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = AuthenticationManager().getCurrentUser()
            if (currentUser != null) {
                val userId = currentUser.uid
                val userChats = ChatDao().getUserChats(userId)

                val chatListAdapter = ChatListAdapter(userChats)
                chatListAdapter.setOnClickListener {
                    val chat = it.tag as Chat
                    val i = Intent(this@PrincipalActivity, ChatActivity::class.java)
                    i.putExtra("chatId", chat.id)
                    i.putExtra("chatName", chat.name)
                    startActivity(i)
                }

                withContext(Dispatchers.Main) {
                    chatRecyclerView.adapter = chatListAdapter
                }
            }
        }
    }*/
}
