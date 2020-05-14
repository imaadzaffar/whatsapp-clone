package com.zafaris.whatsappclone.model

import java.util.*

data class Message (
    val chatName: String,
    val message: String,
    val username: String,
    val sentByMe: Boolean
    //TODO: add time - val timeSent: Date
)