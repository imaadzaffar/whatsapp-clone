package com.zafaris.whatsappclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Message
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val messagesList: MutableList<Message> = ArrayList()
        messagesList.add(Message("Test chat name",
            "Test message",
            "Test username",
            false))
        messagesList.add(Message("Test chat name",
            "Test message",
            "Test username",
            true))

        val adapter = MessagesAdapter(messagesList)
        recyclerview_messages.adapter = adapter
        recyclerview_messages.layoutManager = LinearLayoutManager(this)
    }
}
