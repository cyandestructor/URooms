package com.fcfm.poi.yourooms.login.data.models

data class UserAssignment(
    val id: String? = null,
    val name: String? = null,
    val group: Group? = null,
    val delivered: Boolean? = false,
    val responseId: String? = null
)