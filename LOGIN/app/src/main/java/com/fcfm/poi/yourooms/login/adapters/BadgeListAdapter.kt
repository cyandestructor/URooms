package com.fcfm.poi.yourooms.login.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.R

class BadgeListAdapter(private val badges : Array<String>) : RecyclerView.Adapter<BadgeListAdapter.BadgeHolder>() {
    class BadgeHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(badge : String) {
            itemView.findViewById<TextView>(R.id.titulo_nombre).text = badge
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.elemento_insignias, parent, false)

        return BadgeHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeHolder, position: Int) {
        holder.bind(badges[position])
    }

    override fun getItemCount(): Int = badges.size


}