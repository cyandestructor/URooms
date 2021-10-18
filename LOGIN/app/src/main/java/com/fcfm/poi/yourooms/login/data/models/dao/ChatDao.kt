package com.fcfm.poi.yourooms.login.data.models.dao

import android.util.Log
import com.fcfm.poi.yourooms.login.data.models.Chat
import com.fcfm.poi.yourooms.login.data.models.Message
import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ChatDao {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addChat(chat: Chat): String? {
        val chatDocument = db.collection("chats").document()
        val chatMembers = chatDocument.collection("members")

        val batch = db.batch()

        val membersIds = mutableListOf<String>()

        return try {
            if(chat.members != null) {
                for (member in chat.members) {
                    if(member.id != null) {
                        membersIds += member.id

                        val memberData = hashMapOf(
                            "name" to member.name,
                            "lastname" to member.lastname,
                            "image" to member.image
                        )

                        val memberDocument = chatMembers.document(member.id)
                        batch.set(memberDocument, memberData)
                    }
                }
            }

            val data = hashMapOf(
                "name" to chat.name,
                "image" to chat.image,
                "members" to membersIds,
                "totalMembers" to membersIds.size
            )

            batch.set(chatDocument, data)

            batch.commit().await()

            chatDocument.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getUserChats(userId: String): List<Chat> {
        val chats = mutableListOf<Chat>()

        try {
            val result = db.collection("chats")
                .whereArrayContains("members", userId)
                .get()
                .await()

            for (document in result.documents) {
                val chat = Chat(
                    document.id,
                    document.getString("name"),
                    document.getString("image"),
                    null,
                    Message(
                        null,
                        null,
                        document.getString("lastMessage.body"),
                        User(
                            null,
                            document.getString("lastMessage.sender.name"),
                            document.getString("lastMessage.sender.lastname")
                        )
                    )
                )

                chats += chat
            }
        }
        catch (e: Exception) {
            // TODO: Print error message
        }

        return chats
    }

    suspend fun getChatMembers(chatId: String): List<User> {
        val members = mutableListOf<User>()

        try {
            val result = db.collection("chats/${chatId}/members")
                .get()
                .await()

            for (document in result.documents) {
                val member = User(
                    document.id,
                    document.getString("name"),
                    document.getString("lastname"),
                    document.getString("image")
                )

                members += member
            }
        }
        catch (e: Exception) {
            // TODO: Print error message
        }

        return members
    }

    suspend fun addMembersToChat(chatId: String, members: List<User>): Boolean {
        val chat = db.collection("chats").document(chatId)
        val chatMembers = chat.collection("members")

        val batch = db.batch()

        return try {
            // Create a member in the Chat members subcollection
            for (member in members) {
                if (member.id != null) {
                    val data = hashMapOf(
                        "name" to member.name,
                        "lastname" to member.lastname,
                        "image" to member.image
                    )

                    val memberDocument = chatMembers.document(member.id)
                    batch.set(memberDocument, data)

                    batch.update(chat,
                        "members", FieldValue.arrayUnion(member.id),
                        "totalMembers", FieldValue.increment(1)
                    )
                }
            }

            batch.commit().await()

            true
        }
        catch (e: Exception){
            Log.e("ChatDao", e.message ?: "No message")
            return false
        }
    }

    suspend fun updateChat(chat: Chat): Boolean {
        return try {
            db.collection("chats")
                .document(chat.id!!)
                .update(
                    "name", chat.name,
                    "image", chat.image
                )
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun removeChatMembers(chatId: String, memberIds: List<String>): Boolean {
        val chat = db.collection("chats").document(chatId)
        val batch = db.batch()

        return try {
            for (memberId in memberIds) {
                val member = chat.collection("members").document(memberId)
                // TODO: Write a Cloud Function to update also the TotalMembers field
                batch.update(chat, "members", FieldValue.arrayRemove(memberId))
                batch.delete(member)
            }

            batch.commit().await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }
}