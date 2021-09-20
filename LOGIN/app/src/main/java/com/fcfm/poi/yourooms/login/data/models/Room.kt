package com.fcfm.poi.yourooms.login.data.models

class Room(
    id: String? = null,
    name: String? = null,
    image: String? = null,
    val members: List<User>? = null
) : Group(id, name, image) {}