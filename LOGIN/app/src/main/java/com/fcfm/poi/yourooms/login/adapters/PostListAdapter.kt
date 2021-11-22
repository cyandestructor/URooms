package com.fcfm.poi.yourooms.login.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.R
import com.fcfm.poi.yourooms.login.data.models.Post
import java.text.SimpleDateFormat

class PostListAdapter(val posts: MutableList<Post>) : RecyclerView.Adapter<PostListAdapter.PostHolder>(), View.OnClickListener {
    private var listener: View.OnClickListener? = null

    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(post: Post) {

            if (post.hasMultimedia != null && post.hasMultimedia) {
                itemView.findViewById<TextView>(R.id.msj_cont).text = "Esta publicación tiene contenido multimedia. Presiona para verlo"
            }
            else {
                itemView.findViewById<TextView>(R.id.msj_cont).text = post.body
            }

            itemView.findViewById<TextView>(R.id.Propietario_mensaje).text = post.poster?.name
            val date = post.date?.toDate()
            if (date != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd h:mm a")
                itemView.findViewById<TextView>(R.id.fecha_mensaje).text = sdf.format(date)
            }

            itemView.tag = post
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.estilo_publicacionforo, parent, false)

        view.setOnClickListener(this)

        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onClick(p0: View?) {
        if (listener != null) {
            listener!!.onClick(p0)
        }
    }
}