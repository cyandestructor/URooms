package com.fcfm.poi.yourooms.login.data.models.dao

import com.fcfm.poi.yourooms.login.data.models.Assignment
import com.fcfm.poi.yourooms.login.data.models.Group
import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AssignmentDao {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addAssignment(assignment: Assignment): String? {
        val data = hashMapOf(
            "name" to assignment.name,
            "description" to assignment.description,
            "dueDate" to assignment.dueDate,
            "postDate" to assignment.postDate,
            "hasMultimedia" to assignment.hasMultimedia,
            "poster" to hashMapOf(
                "id" to assignment.poster?.id,
                "name" to assignment.poster?.name,
                "lastname" to assignment.poster?.lastname
            ),
            "group" to hashMapOf(
                "id" to assignment.group?.id,
                "name" to assignment.group?.name,
                "image" to assignment.group?.image
            )
        )

        return try {
            val result = db.collection("assignments")
                .add(data)
                .await()

            result.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getUserPostedAssignments(userId: String): List<Assignment> {
        val assignments = mutableListOf<Assignment>()

        try {
            val result = db.collection("assignments")
                .whereEqualTo("poster.id", userId)
                .get()
                .await()

            for (document in result.documents) {
                val assignment = Assignment(
                    document.id,
                    document.getString("name"),
                    document.getString("description"),
                    document.getDate("dueDate"),
                    document.getDate("postDate"),
                    User(
                        document.getString("poster.id"),
                        document.getString("poster.name"),
                        document.getString("poster.lastname")
                    ),
                    Group(
                        document.getString("group.id"),
                        document.getString("group.name"),
                        document.getString("group.image")
                    ),
                    document.getBoolean("hasMultimedia")
                )

                assignments += assignment
            }
        }
        catch (e: Exception) {
            // TODO: Print error message
        }

        return  assignments
    }

    suspend fun getAssignment(assignmentId: String): Assignment? {
        return try {
            val result = db.collection("assignments")
                .document(assignmentId)
                .get()
                .await()

            Assignment(
                result.id,
                result.getString("name"),
                result.getString("description"),
                result.getDate("dueDate"),
                result.getDate("postDate"),
                User(
                    result.getString("poster.id"),
                    result.getString("poster.name"),
                    result.getString("poster.lastname")
                ),
                Group(
                    result.getString("group.id"),
                    result.getString("group.name"),
                    result.getString("group.image")
                ),
                result.getBoolean("hasMultimedia")
            )
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun deleteAssignment(assignmentId: String): Boolean {
        return try {
            db.collection("assignments")
                .document(assignmentId)
                .delete()
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun updateAssignment(assignment: Assignment): Boolean {
        val data = hashMapOf<String, Any?>(
            "name" to assignment.name,
            "description" to assignment.description,
            "dueDate" to assignment.dueDate
        )

        return try {
            db.collection("assignments")
                .document(assignment.id!!)
                .update(data)
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }
}