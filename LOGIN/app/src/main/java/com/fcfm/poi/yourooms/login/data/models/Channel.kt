package com.fcfm.poi.yourooms.login.data.models

class Channel(
    id: String? = null,
    name: String? = null,
    val members: List<User>? = null
) : Group(id, name, "") {}