package com.fcfm.poi.yourooms.login.data.models

import java.util.*

data class Post(
    val id: String? = null,
    val body: String? = null,
    val date: Date? = null,
    val poster: User? = null,
    val hasMultimedia: Boolean? = false,
    val roomId: String? = null,
    val channelId: String? = null,
)