package com.fcfm.poi.yourooms.login.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.CifradoUtils
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.Message
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class MessageListAdapter(val messageList: MutableList<Message>) : RecyclerView.Adapter<MessageListAdapter.MessageHolder>() {
    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat")
        fun bind(message: Message) {
            val userName = "${message.sender?.name} ${message.sender?.lastname}"

            itemView.findViewById<TextView>(R.id.Propietario_mensaje).text = userName

            var messageBody = message.body

            if (message.encrypted != null && message.encrypted) {
                messageBody = CifradoUtils.descifrar(messageBody!!, "mypassword")
            }

            itemView.findViewById<TextView>(R.id.msj_cont).text = messageBody

            val imageView = itemView.findViewById<ImageView>(R.id.icono_pic_grupo)
            Picasso.get()
                .load(message.sender?.image)
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(imageView)

            val date = message.date?.toDate()
            if (date != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd h:mm a")
                itemView.findViewById<TextView>(R.id.fecha_mensaje).text = sdf.format(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MessageHolder(inflater.inflate(R.layout.estilo_mensajes, parent, false))
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size
}