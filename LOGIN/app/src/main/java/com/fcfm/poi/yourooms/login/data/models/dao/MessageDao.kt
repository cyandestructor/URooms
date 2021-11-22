package com.fcfm.poi.yourooms.login.data.models.dao

import android.content.ContentValues
import android.util.Log
import com.fcfm.poi.yourooms.login.data.models.Message
import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

class MessageDao {
    private val db: FirebaseFirestore = Firebase.firestore

    private val chatListenerRegistrations = mutableMapOf<String, ListenerRegistration>()

    private var lastMessage: Message? = null

    private var canPaginate: Boolean = true

    suspend fun addMessage(message: Message, chatId: String): String? {
        val data = hashMapOf(
            "body" to message.body,
            "date" to message.date,
            "hasMultimedia" to message.hasMultimedia,
            "encrypted" to message.encrypted,
            "sender" to hashMapOf(
                "id" to message.sender?.id,
                "name" to message.sender?.name,
                "lastname" to message.sender?.lastname,
                "image" to message.sender?.image
            )
        )

        val chat = db.collection("chats").document(chatId)
        val messageDocument = chat.collection("messages").document()

        val batch = db.batch()

        return try {
            batch.set(messageDocument, data)

            val lastMessage = hashMapOf(
                "body" to message.body,
                "encrypted" to message.encrypted,
                "sender" to hashMapOf(
                    "name" to message.sender?.name,
                    "lastname" to message.sender?.lastname,
                )
            )

            batch.update(chat, "lastMessage", lastMessage)

            batch.commit().await()

            messageDocument.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getChatMessages(
        chatId: String,
        limit: Long? = null,
        pagination: Boolean = false): List<Message> {

        val messages = mutableListOf<Message>()

        // Return no messages if the user request pagination but it is no longer possible
        if (pagination && !canPaginate) {
            return messages
        }

        var query = db.collection("chats/${chatId}/messages")
            .orderBy("date", Query.Direction.DESCENDING)

        if (pagination && canPaginate && lastMessage != null) {
            query = query.startAfter(lastMessage!!.date)
        }

        if (limit != null) {
            query = query.limit(limit)
        }

        try {
            val result = query.get().await()

            // If the total documents are less than the specified limit it means
            // that those are the last results, so we cannot not paginate next
            if (pagination && result.documents.size < (limit ?: 1)) {
                canPaginate = false
            }

            for (document in result.documents) {
                val message = Message(
                    document.id,
                    document.getTimestamp("date"),
                    document.getString("body"),
                    User(
                        document.getString("sender.id"),
                        document.getString("sender.name"),
                        document.getString("sender.lastname"),
                        document.getString("sender.image")
                    ),
                    document.getBoolean("hasMultimedia"),
                    document.getBoolean("encrypted")
                )

                messages += message
            }
        }
        catch (e: Exception) {
            // TODO: Print error message
            Log.e("MessageDao", e.message ?: "No message")
        }

        if (pagination && messages.isNotEmpty()) {
            lastMessage = messages.last()
        }

        return  messages
    }

    fun resetChatMessagesPagination() {
        lastMessage = null
        canPaginate = true
    }

    fun listenChatLatestMessages(chatId: String, onUpdate: (List<Message>) -> Unit) {
        // Remove previous listener if exists
        chatListenerRegistrations[chatId]?.remove()

        db.collection("chats/${chatId}/messages")
            .whereGreaterThanOrEqualTo("date", Date())
            .orderBy("date")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Message>()

                for (document in value!!) {
                    val message = Message(
                        document.id,
                        document.getTimestamp("date"),
                        document.getString("body"),
                        User(
                            document.getString("sender.id"),
                            document.getString("sender.name"),
                            document.getString("sender.lastname"),
                            document.getString("sender.image")
                        ),
                        document.getBoolean("hasMultimedia"),
                        document.getBoolean("encrypted")
                    )

                    messages += message
                }

                onUpdate(messages)
            }.also { chatListenerRegistrations[chatId] = it }
    }

    fun stopListeningChatLatestMessages(chatId: String) {
        chatListenerRegistrations[chatId]?.remove()
    }
}