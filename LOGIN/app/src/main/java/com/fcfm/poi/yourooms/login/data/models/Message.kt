package com.fcfm.poi.yourooms.login.data.models

import com.google.firebase.Timestamp

data  class Message(
    val id: String? = null,
    val date: Timestamp? = null,
    val body: String? = null,
    val sender: User? = null,
    val hasMultimedia: Boolean? = false,
    val encrypted : Boolean? = false
)