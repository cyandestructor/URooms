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

class PostListAdapter(val posts: MutableList<Post>) : RecyclerView.Adapter<PostListAdapter.PostHolder>() {
    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat")
        fun bind(post: Post) {
            itemView.findViewById<TextView>(R.id.msj_cont).text = post.body
            itemView.findViewById<TextView>(R.id.Propietario_mensaje).text = post.poster?.name
            val date = post.date?.toDate()
            if (date != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd h:mm a")
                itemView.findViewById<TextView>(R.id.fecha_mensaje).text = sdf.format(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.estilo_publicacionforo, parent, false)

        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}