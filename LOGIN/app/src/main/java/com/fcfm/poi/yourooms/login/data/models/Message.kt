package com.fcfm.poi.yourooms.login.data.models

import java.util.*

data  class Message(
    val id: String? = null,
    val date: Date? = null,
    val body: String? = null,
    val sender: User? = null,
    val hasMultimedia: Boolean? = false
)