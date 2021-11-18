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

class EquiposListAdapter(private val equioList: List<Room>) : RecyclerView.Adapter<EquiposListAdapter.ViewHolder>(), View.OnClickListener {

    private var listener: View.OnClickListener? = null

    private var name = arrayOf("Materia1","Materia2","Materia3","Materia4","Materia5")
    private val image = intArrayOf(R.drawable.ic_materias,R.drawable.ic_materias,R.drawable.ic_materias,R.drawable.ic_materias,R.drawable.ic_materias)


    //private var listener: View.OnClickListener? = null

   /* class EquipoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(equipo: Room) {
            itemView.findViewById<TextView>(R.id.NombreEquipo).text = equipo.name
            //itemView.findViewById<TextView>(R.id.lastMessageBody).text = equipo.lastMessage?.body
            itemView.tag = equipo

        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.elemento_equipos, parent, false)
        //val view = inflater.inflate(R.layout.elemento_equipos, parent, false)

        //view.setOnClickListener(this)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
       // holder.bind(EquiposList[position])
        holder.itemTitle.text = name[position]
        holder.itemImage.setImageResource(image[position])


    }

    //override fun getItemCount():Int = EquiposList.size
/*

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }
*/

    /*override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }*/

    override fun getItemCount(): Int {
        return name.size
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener

    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var itemTitle: TextView
        var itemImage: ImageView

        init {
            itemImage = itemView.findViewById(R.id.icono_imagen)
            itemTitle = itemView.findViewById(R.id.NombreEquipo)
        }
    }

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }


}