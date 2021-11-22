package com.fcfm.poi.yourooms.login.data.models

class Chat(
    id: String? = null,
    name: String? = null,
    image: String? = null,
    val members: List<User>? = null,
    val lastMessage: Message? = null,
    val isPrivate: Boolean? = false
) : Group(id, name, image){}