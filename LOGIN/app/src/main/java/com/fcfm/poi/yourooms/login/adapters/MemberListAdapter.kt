package com.fcfm.poi.yourooms.login.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.User
import com.squareup.picasso.Picasso

class MemberListAdapter(private val members : List<User>) : RecyclerView.Adapter<MemberListAdapter.MemberHolder>(), View.OnClickListener {
    private var listener: View.OnClickListener? = null

    class MemberHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(member : User) {
            itemView.findViewById<TextView>(R.id.titulo_nombre).text = "${member.name} ${member.lastname}"
            val imageView = itemView.findViewById<ImageView>(R.id.icono_pic_grupo)
            Picasso.get()
                .load(member.image)
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(imageView)
            itemView.findViewById<TextView>(R.id.estadoConexion).text = member.connectionState ?: "Desconocido"
            itemView.tag = member
        }
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.elemento_miembros, parent, false)

        view.setOnClickListener(listener)

        return MemberHolder(view)
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }
}