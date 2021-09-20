package com.fcfm.poi.yourooms.login.data.models

import java.util.*

data class AssignmentResponse(
    val id: String? = null,
    val date: Date? = null,
    val user: User? = null,
    val assignmentId: String? = null,
    val hasMultimedia: Boolean? = false
)