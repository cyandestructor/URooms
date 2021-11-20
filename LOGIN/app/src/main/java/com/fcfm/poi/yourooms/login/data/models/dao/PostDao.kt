package com.fcfm.poi.yourooms.login.data.models.dao

import android.content.ContentValues
import android.util.Log
import com.fcfm.poi.yourooms.login.data.models.Post
import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

class PostDao {
    private val db: FirebaseFirestore = Firebase.firestore

    private val postListenerRegistrations = mutableMapOf<String, ListenerRegistration>()

    suspend fun addPost(post: Post): String? {
        val data = hashMapOf(
            "body" to post.body,
            "date" to post.date,
            "hasMultimedia" to post.hasMultimedia,
            "poster" to hashMapOf(
                "id" to post.poster?.id,
                "name" to post.poster?.name,
                "lastname" to post.poster?.lastname,
                "image" to post.poster?.image
            )
        )

        return try {
            val result = db.collection("rooms/${post.roomId}/channels/${post.channelId}/posts")
                .add(data)
                .await()

            result.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getChannelPosts(roomId: String, channelId: String) : List<Post> {
        val posts = mutableListOf<Post>()

        try {
            val result = db.collection("rooms/${roomId}/channels/${channelId}/posts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            for (document in result.documents) {
                val post = Post(
                    document.id,
                    document.getString("body"),
                    document.getTimestamp("date"),
                    User(
                        document.getString("poster.id"),
                        document.getString("poster.name"),
                        document.getString("poster.lastname"),
                        document.getString("poster.image")
                    ),
                    document.getBoolean("hasMultimedia")
                )

                posts += post
            }
        }
        catch (e: Exception) {
            // TODO : Print error msg
        }

        return posts
    }

    fun listenPosts(roomId: String, channelId: String, onUpdate: (List<Post>) -> Unit) {
        // Remove previous listener if exists
        val key = "${roomId}/${channelId}";
        postListenerRegistrations[key]?.remove()

        db.collection("rooms/${roomId}/channels/${channelId}/posts")
            .whereGreaterThanOrEqualTo("date", Timestamp.now())
            .orderBy("date")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                val posts = mutableListOf<Post>()

                for (document in value!!) {
                    val post = Post(
                        document.id,
                        document.getString("body"),
                        document.getTimestamp("date"),
                        User(
                            document.getString("poster.id"),
                            document.getString("poster.name"),
                            document.getString("poster.lastname"),
                            document.getString("poster.image")
                        ),
                        document.getBoolean("hasMultimedia")
                    )

                    posts += post
                }

                onUpdate(posts)
            }.also { postListenerRegistrations[key] = it }
    }

    fun stopListeningPosts(roomId: String, channelId: String) {
        postListenerRegistrations["${roomId}/${channelId}"]?.remove()
    }

    suspend fun deletePost(post: Post): Boolean {
        return try {
            db.collection("rooms/${post.roomId}/channels/${post.channelId}/posts")
                .document(post.id!!)
                .delete()
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun updatePost(post: Post): Boolean {
        return try {
            db.collection("rooms/${post.roomId}/channels/${post.channelId}/posts")
                .document(post.id!!)
                .update("body", post.body)
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }
}