package com.fcfm.poi.yourooms.login.adapters

import android.annotation.SuppressLint
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.CifradoUtils
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.File
import com.fcfm.poi.yourooms.login.data.models.Message
import com.fcfm.poi.yourooms.login.data.models.dao.FileDao
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class MessageListAdapter(val messageList: MutableList<Message>) : RecyclerView.Adapter<MessageListAdapter.MessageHolder>(), View.OnClickListener {
    private var listener: View.OnClickListener? = null

    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat")
        fun bind(message: Message) {
            val userName = "${message.sender?.name} ${message.sender?.lastname}"

            itemView.findViewById<TextView>(R.id.Propietario_mensaje).text = userName

            var messageBody = message.body

            if (message.encrypted != null && message.encrypted) {
                messageBody = CifradoUtils.descifrar(messageBody!!, "mypassword")
            }

            if (message.hasMultimedia != null && message.hasMultimedia) {
                messageBody = "Este mensaje contiene archivos. Presiona el mensaje para verlos"
            }

            itemView.findViewById<TextView>(R.id.msj_cont).text = messageBody

            val date = message.date?.toDate()
            if (date != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd h:mm a")
                itemView.findViewById<TextView>(R.id.fecha_mensaje).text = sdf.format(date)
            }

            itemView.tag = message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.estilo_mensajes, parent, false)

        view.setOnClickListener(this)

        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }
}