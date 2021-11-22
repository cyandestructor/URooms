package com.fcfm.poi.yourooms.login.data.models.dao

import com.fcfm.poi.yourooms.login.data.models.Channel
import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ChannelDao {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addChannel(channel: Channel, roomId: String): String? {
        val channelDocument = db.collection("rooms/${roomId}/channels").document()

        val channelMembers = channelDocument.collection("members")

        val batch = db.batch()

        val membersIds = mutableListOf<String>()

        return try {
            if (channel.members != null) {
                for (member in channel.members) {
                    if (member.id != null) {
                        membersIds += member.id

                        val memberData = hashMapOf(
                            "name" to member.name,
                            "lastname" to member.lastname,
                            "image" to member.image
                        )

                        val memberDocument = channelMembers.document(member.id)
                        batch.set(memberDocument, memberData)
                    }
                }
            }

            val data = hashMapOf(
                "id" to channelDocument.id,
                "name" to channel.name,
                "members" to membersIds,
            )

            batch.set(channelDocument, data)

            batch.commit().await()

            channelDocument.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getRoomChannels(roomId: String): List<Channel> {
        val channels = mutableListOf<Channel>()

        try {
            val result = db.collection("rooms/${roomId}/channels")
                .get()
                .await()

            for (document in result.documents) {
                val channel = Channel(
                    document.id,
                    document.getString("name")
                )

                channels += channel
            }
        }
        catch (e: Exception) {
            // TODO: Print Error message
        }

        return channels
    }

    suspend fun getRoomUserChannels(roomId: String, userId: String): List<Channel> {
        val channels = mutableListOf<Channel>()

        try {
            val result = db.collection("rooms/${roomId}/channels")
                .whereArrayContains("members", userId)
                .get()
                .await()

            for (document in result.documents) {
                val channel = Channel(
                    document.id,
                    document.getString("name")
                )

                channels += channel
            }
        }
        catch (e: Exception) {
            // TODO: Print Error message
        }

        return channels
    }

    suspend fun getChannelMembers(roomId: String, channelId: String): List<User> {
        val members = mutableListOf<User>()

        try {
            val result = db.collection("rooms/${roomId}/channels/${channelId}/members")
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

    suspend fun deleteChannel(channelId: String, roomId: String): Boolean {
        return try {
            db.collection("rooms/${roomId}/channels")
                .document(channelId)
                .delete()
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun removeChannelMembers(channelId: String, roomId: String, memberIds: List<String>): Boolean {
        val channel = db.collection("rooms/${roomId}/channels").document(channelId)
        val batch = db.batch()

        return try {
            for (memberId in memberIds) {
                val member = channel.collection("members").document(memberId)
                batch.update(channel, "members", FieldValue.arrayRemove(memberId))
                batch.delete(member)
            }

            batch.commit().await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun addMembersToChannel(channelId: String, roomId: String, members: List<User>): Boolean {
        val channel = db.collection("rooms/${roomId}/channels").document(channelId)

        val channelMembers = channel.collection("members")

        val batch = db.batch()

        return try {
            // Create a member in the Channel members subcollection
            for (member in members) {
                if (member.id != null) {
                    val data = hashMapOf(
                        "name" to member.name,
                        "lastname" to member.lastname,
                        "image" to member.image
                    )

                    val memberDocument = channelMembers.document(member.id)
                    batch.set(memberDocument, data)

                    batch.update(channel, "members", FieldValue.arrayUnion(member.id))
                }
            }

            batch.commit().await()

            true
        }
        catch (e: Exception){
            return false
        }
    }

    suspend fun updateChannel(channel: Channel, roomId: String): Boolean {
        return try {
            db.collection("rooms/${roomId}/channels")
                .document(channel.id!!)
                .update("name", channel.name)
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }
}