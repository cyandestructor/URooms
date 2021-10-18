package com.fcfm.poi.yourooms.login.authentication

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class AuthenticationManager {
    protected val auth = Firebase.auth

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserSignIn(): Boolean {
        return auth.currentUser != null
    }
}