package com.fcfm.poi.yourooms.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.EquiposListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Room
import com.fcfm.poi.yourooms.login.data.models.dao.RoomDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Fragmento_equipos: Fragment(R.layout.fragmento_equipos) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadEquipoList(view)
    }

    private lateinit var equipoRecyclerView: RecyclerView

    private fun loadEquipoList(view: View) {

        equipoRecyclerView = view.findViewById(R.id.Lista_equipos)
        equipoRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = AuthenticationManager().getCurrentUser()
            if (currentUser != null) {
                val userId = currentUser.uid
                val rooms = RoomDao().getUserRooms(userId)

                val equipoListAdapter = EquiposListAdapter(rooms)
                equipoListAdapter.setOnClickListener{
                    val equipo = it.tag as Room
                    val i = Intent(activity, ChannelsActivity::class.java)
                    i.putExtra("roomId", equipo.id)
                    i.putExtra("roomName", equipo.name)
                    startActivity(i)
                }

                withContext(Dispatchers.Main) {
                    equipoRecyclerView.adapter = equipoListAdapter
                }
            }
        }
    }

}