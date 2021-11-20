package com.fcfm.poi.yourooms.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.poi.yourooms.login.adapters.PostListAdapter
import com.fcfm.poi.yourooms.login.authentication.AuthenticationManager
import com.fcfm.poi.yourooms.login.data.models.Post
import com.fcfm.poi.yourooms.login.data.models.dao.PostDao
import com.fcfm.poi.yourooms.login.data.models.dao.UserDao
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForoGenActivity : AppCompatActivity() {
    private lateinit var postsRecyclerView: RecyclerView
    private val postDao = PostDao()
    private var postListAdapter : PostListAdapter? = null
    private var roomId : String? = null
    private var channelId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.foro_generalpantalla)

        val channelName = intent.getStringExtra("channelName")
        findViewById<TextView>(R.id.creargpo_text).text = channelName

        val roomName = intent.getStringExtra("roomName")
        findViewById<TextView>(R.id.channel_room_name).text = roomName

        val btnSendPost = findViewById<ImageButton>(R.id.imageButton_sendmess)
        btnSendPost.setOnClickListener {
            sendPost(it)
        }
    }

    override fun onStart() {
        super.onStart()
        loadPosts()
    }

    override fun onStop() {
        super.onStop()
        if (roomId != null && channelId != null) {
            postDao.stopListeningPosts(roomId!!, channelId!!)
        }
    }

    fun loadPosts() {
        postsRecyclerView = findViewById(R.id.post_list)
        postsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        roomId = intent.getStringExtra("roomId")
        channelId = intent.getStringExtra("channelId")

        if (roomId != null && channelId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val posts = postDao.getChannelPosts(roomId!!, channelId!!)

                postListAdapter = PostListAdapter(posts.toMutableList())
                withContext(Dispatchers.Main) {
                    postsRecyclerView.adapter = postListAdapter
                    listenToPosts()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun listenToPosts() {
        postDao.listenPosts(roomId!!, channelId!!) {
            postListAdapter?.posts?.addAll(0, it)
            postListAdapter?.notifyDataSetChanged()
        }
    }

    private fun sendPost(it: View) {
        val user = AuthenticationManager().getCurrentUser() ?: return
        val userId = user.uid

        it.isEnabled = false
        val postBodyEditText = findViewById<EditText>(R.id.Edit_mensaje)
        val postBody = postBodyEditText.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val userModel = UserDao().getUser(userId)

            val post = Post(
                null,
                postBody,
                Timestamp.now(),
                userModel,
                false,
                roomId!!,
                channelId!!
            )

            val postId = PostDao().addPost(post)
            withContext(Dispatchers.Main) {
                if (postId != null) {
                    postBodyEditText.text.clear()
                }
                else {
                    Toast.makeText(applicationContext, "No se pudo crear el post", Toast.LENGTH_SHORT).show()
                }
                it.isEnabled = true
            }
        }
    }
}