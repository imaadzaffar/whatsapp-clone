package com.zafaris.whatsappclone.model

data class Chat (
    var name: String? = null,
    val lastMessage: String? = null,
    var userNames: HashMap<String, Boolean> = HashMap()
)