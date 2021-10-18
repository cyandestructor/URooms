package com.fcfm.poi.yourooms.login.authentication

abstract class Authentication : AuthenticationManager() {
    abstract suspend fun signIn(): Boolean
}