package com.fcfm.poi.yourooms.login.authentication

import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthentication(
    private val idToken: String
): Authentication() {
    override fun signIn(): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return  auth.signInWithCredential(credential).isSuccessful
    }
}