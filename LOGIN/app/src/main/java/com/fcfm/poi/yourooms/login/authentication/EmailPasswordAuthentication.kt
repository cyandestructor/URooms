package com.fcfm.poi.yourooms.login.authentication

import kotlinx.coroutines.tasks.await

class EmailPasswordAuthentication(
    private val email: String,
    private val password: String
): Authentication() {
    override suspend fun signIn(): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        }
        catch (e: Exception) {
            false
        }
    }

    suspend fun registerUser(): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            true
        }
        catch (e: Exception) {
            return false
        }
    }
}