package com.fcfm.poi.yourooms.login.data.models.dao

import com.fcfm.poi.yourooms.login.data.models.AssignmentResponse
import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AssignmentResponseDao {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addAssignmentResponse(assignmentResponse: AssignmentResponse): String? {
        val data = hashMapOf(
            "date" to assignmentResponse.date,
            "hasMultimedia" to assignmentResponse.hasMultimedia,
            "user" to hashMapOf(
                "id" to assignmentResponse.user?.id,
                "name" to assignmentResponse.user?.name,
                "lastname" to assignmentResponse.user?.lastname
            )
        )

        return try {
            val userAssignment = db.collection("users/${assignmentResponse.user?.id}/userAssignments")
                .document(assignmentResponse.assignmentId!!)
            val response = db.collection("assignments/${assignmentResponse.assignmentId}/assignmentResponses")
                .document()

            val batch = db.batch()

            batch.set(response, data)
            batch.update(userAssignment,
                "delivered", true,
                "responseId", response.id
            )

            batch.commit().await()

            response.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getAssignmentResponses(assignmentId: String): List<AssignmentResponse> {
        val responses = mutableListOf<AssignmentResponse>()

        try {
            val result = db.collection("assignments/${assignmentId}/assignmentsResponses")
                .get()
                .await()

            for (document in result.documents) {
                val response = AssignmentResponse(
                    document.id,
                    document.getDate("date"),
                    User(
                        document.getString("user.id"),
                        document.getString("user.name"),
                        document.getString("user.lastname")
                    ),
                    document.reference.parent.id,
                    document.getBoolean("hasMultimedia")
                )

                responses += response
            }
        }
        catch (e: Exception) {
            // TODO: Print Error Message
        }

        return responses
    }
}