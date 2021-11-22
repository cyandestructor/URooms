package com.fcfm.poi.yourooms.login.data.models.dao

import com.fcfm.poi.yourooms.login.data.models.Room
import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RoomDao {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addRoom(room: Room): String? {
        val roomDocument = db.collection("rooms").document()
        val roomMembers = roomDocument.collection("members")

        val batch = db.batch()

        val membersIds = mutableListOf<String>()

        return try {
            if (room.members != null) {
                for (member in room.members) {
                    if (member.id != null) {
                        membersIds += member.id

                        val memberData = hashMapOf(
                            "name" to member.name,
                            "lastname" to member.lastname,
                            "image" to member.image
                        )

                        val memberDocument = roomMembers.document(member.id)
                        batch.set(memberDocument, memberData)
                    }
                }
            }

            val data = hashMapOf(
                "name" to room.name,
                "image" to room.image,
                "members" to membersIds
            )

            batch.set(roomDocument, data)

            batch.commit().await()

            roomDocument.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getUserRooms(userId: String): List<Room> {
        val rooms = mutableListOf<Room>()

        try {
            val result = db.collection("rooms")
                .whereArrayContains("members", userId)
                .get()
                .await()

            for (document in result.documents) {
                val room = Room(
                    document.id,
                    document.getString("name"),
                    document.getString("image")
                )

                rooms += room
            }
        }
        catch (e: Exception) {
            // TODO: Print error message
        }

        return rooms
    }

    suspend fun getRoomMembers(roomId: String): List<User> {
        val members = mutableListOf<User>()

        try {
            val result = db.collection("rooms/${roomId}/members")
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

    suspend fun deleteRoom(roomId: String): Boolean {
        return try {
            db.collection("rooms")
                .document(roomId)
                .delete()
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun removeRoomMembers(roomId: String, memberIds: List<String>): Boolean {
        val room = db.collection("rooms").document(roomId)
        val batch = db.batch()

        return try {
            for (memberId in memberIds) {
                val member = room.collection("members").document(memberId)
                batch.update(room, "members", FieldValue.arrayRemove(memberId))
                batch.delete(member)
            }

            batch.commit().await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun addMembersToRoom(roomId: String, members: List<User>): Boolean {
        val room = db.collection("rooms").document(roomId)
        val roomMembers = room.collection("members")

        val batch = db.batch()

        return try {
            // Create a member in the Room members subcollection
            for (member in members) {
                if(member.id != null) {
                    val data = hashMapOf(
                        "name" to member.name,
                        "lastname" to member.lastname,
                        "image" to member.image
                    )

                    val memberDocument = roomMembers.document(member.id)
                    batch.set(memberDocument, data)
                    batch.update(room, "members", FieldValue.arrayUnion(member.id))
                }
            }

            batch.commit().await()

            true
        }
        catch (e: Exception){
            return false
        }
    }

    suspend fun updateRoom(room: Room): Boolean {
        return try {
            db.collection("rooms")
                .document(room.id!!)
                .update(
                    "name", room.name,
                    "image", room.image
                )
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }
}