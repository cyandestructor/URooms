package com.fcfm.poi.yourooms.login.data.models

data class User(
    val id: String? = null,
    val name: String? = null,
    val lastname: String? = null,
    val image: String? = null,
    val badges: List<String>? = null,
    val email: String? = null,
    val score: Int? = 0,
    val connectionState : String? = null
)