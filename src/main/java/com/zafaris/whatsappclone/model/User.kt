package com.zafaris.whatsappclone.model

data class User (
    val name: String,
    val email: String,
    val password: String,
    val chatsList: List<Chat> = emptyList()
)