package com.fcfm.poi.yourooms.login.authentication

class EmailPasswordAuthentication(
    private val email: String,
    private val password: String
): Authentication() {
    override fun signIn(): Boolean {
        return auth.signInWithEmailAndPassword(email, password).isSuccessful
    }

    fun registerUser(): Boolean {
        return auth.createUserWithEmailAndPassword(email, password).isSuccessful
    }
}