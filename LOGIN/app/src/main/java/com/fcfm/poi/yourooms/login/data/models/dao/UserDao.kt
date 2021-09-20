package com.fcfm.poi.yourooms.login.data.models.dao

import com.fcfm.poi.yourooms.login.data.models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserDao {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addUser(user: User): String? {
        val data = hashMapOf(
            "name" to user.name,
            "lastname" to user.lastname,
            "email" to user.email,
            "image" to user.image,
            "score" to user.score
        )

        return try {
            db.collection("users")
                .document(user.id!!)
                .set(data)
                .await()

            user.id
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun getUser(id: String): User? {
        return try {
            val result = db.collection("users")
                .document(id)
                .get()
                .await()

            User(
                result.id,
                result.getString("name"),
                result.getString("lastname"),
                result.getString("image"),
                (result.get("badges") as? List<*>)?.filterIsInstance<String>(),
                result.getString("email"),
                result.getDouble("score")?.toInt()
            )
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun deleteUser(id: String): Boolean {
        return try {
            db.collection("users")
                .document(id)
                .delete()
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun updateUser(user: User): Boolean {
        val data = hashMapOf<String, Any?>(
            "name" to user.name,
            "lastname" to user.lastname,
            "email" to user.email,
            "image" to user.image,
        )

        return try {
            db.collection("users")
                .document(user.id!!)
                .update(data)
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun addScoreToUser(userId: String, score: Int): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .update("score", FieldValue.increment(score.toLong()))
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }

    suspend fun addBadgesToUser(userId: String, badges: List<String>): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .update("badges", FieldValue.arrayUnion(badges))
                .await()

            true
        }
        catch (e: Exception) {
            return false
        }
    }
}