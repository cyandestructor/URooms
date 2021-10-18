package com.fcfm.poi.yourooms.login.authentication

import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthentication(
    private val idToken: String
): Authentication() {
    override suspend fun signIn(): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential)
            true
        }
        catch (e: Exception) {
            false
        }
    }
}