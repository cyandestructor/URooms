package com.fcfm.poi.yourooms.login.adapters

import android.icu.text.CaseMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.Room

class EquiposListAdapter(private val rooms: List<Room>) : RecyclerView.Adapter<EquiposListAdapter.ViewHolder>(), View.OnClickListener {

    private var listener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.elemento_equipos, parent, false)

        view.setOnClickListener(this)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(room: Room) {
            itemView.findViewById<TextView>(R.id.NombreEquipo).text = room.name
            itemView.tag = room
        }
    }

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }
}