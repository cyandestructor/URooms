package com.fcfm.poi.yourooms.login.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.Channel

class ChannelListAdapter(private val channels: List<Channel>) : RecyclerView.Adapter<ChannelListAdapter.ChannelHolder>(), View.OnClickListener {
    private var listener : View.OnClickListener? = null

    class ChannelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind (channel: Channel) {
            itemView.findViewById<TextView>(R.id.NombreEquipo).text = channel.name
            itemView.tag = channel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelListAdapter.ChannelHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.elemento_equipos, parent, false)

        view.setOnClickListener(this)

        return ChannelHolder(view)
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }
}