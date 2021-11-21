package com.fcfm.poi.yourooms.login.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.UserAssignment

class UserAssignmentListAdapter(private val assignments : List<UserAssignment>) :
    RecyclerView.Adapter<UserAssignmentListAdapter.UserAssignmentHolder>(), View.OnClickListener {

    private var listener: View.OnClickListener? = null

    class UserAssignmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(userAssignment: UserAssignment) {
            itemView.findViewById<TextView>(R.id.Nombre_curso).text = userAssignment.group?.name
            itemView.findViewById<TextView>(R.id.texto_info).text = userAssignment.name

            val assignmentState = if (userAssignment.delivered != null && userAssignment.delivered) {
                "Entregado"
            } else {
                "Pendiente"
            }

            itemView.findViewById<TextView>(R.id.estado_tarea).text = assignmentState
            itemView.tag = userAssignment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAssignmentHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.elemento_tareas, parent, false)

        view.setOnClickListener(this)

        return UserAssignmentHolder(view)
    }

    override fun onBindViewHolder(holder: UserAssignmentHolder, position: Int) {
        holder.bind(assignments[position])
    }

    override fun getItemCount(): Int = assignments.size

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }
}