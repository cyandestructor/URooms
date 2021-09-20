package com.fcfm.poi.yourooms.login.data.models.dao

import com.fcfm.poi.yourooms.login.data.models.Group
import com.fcfm.poi.yourooms.login.data.models.UserAssignment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserAssignmentDao {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun getUserAssignments(userId: String, delivered: Boolean? = null): List<UserAssignment> {
        val assignments = mutableListOf<UserAssignment>()

        try {
            val collection = db.collection("users/${userId}/userAssignments")

            val result = if(delivered != null) {
                collection
                    .whereEqualTo("delivered", delivered)
                    .get()
                    .await()
            } else {
                collection
                    .get()
                    .await()
            }

            for (document in result.documents) {
                val assignment = UserAssignment(
                    document.id,
                    document.getString("name"),
                    Group(
                        document.getString("group.id"),
                        document.getString("group.name"),
                        document.getString("group.image")
                    ),
                    document.getBoolean("delivered"),
                    document.getString("responseId")
                )

                assignments += assignment
            }
        }
        catch (e: Exception) {
            // TODO: Print error message
        }

        return  assignments
    }

    suspend fun deliverUserAssignment(userId: String, assignmentId: String, delivered: Boolean): Boolean {
        return try {
            db.collection("users/${userId}/userAssignments")
                .document(assignmentId)
                .update("delivered", delivered)
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }
}