package com.fcfm.poi.yourooms.login.data.models

import java.util.*

data class Assignment(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val dueDate: Date? = null,
    val postDate: Date? = null,
    val poster: User? = null,
    val group: Group? = null,
    val hasMultimedia: Boolean? = false,
    val score: Int? = 0
)