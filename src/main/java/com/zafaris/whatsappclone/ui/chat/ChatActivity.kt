package com.zafaris.whatsappclone.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Message
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var messagesReference: DatabaseReference

    private lateinit var userId: String
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList: MutableList<Message> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatId = intent.getStringExtra("chatId")

        auth = FirebaseAuth.getInstance()
        userId = auth.uid!!

        messagesReference = Firebase.database.getReference("messages/$chatId")

        setupRv()

        messagesReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("message", databaseError.message)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val message = dataSnapshot.getValue<Message>()!!
                messagesList.add(message)
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })

        button_send.setOnClickListener { TODO("Send new message with message entered and userId") }
    }

    private fun setupRv() {
        messagesAdapter =
            MessagesAdapter(
                userId,
                messagesList
            )
        recyclerview_messages.adapter = messagesAdapter
        recyclerview_messages.layoutManager = LinearLayoutManager(this)
    }

}
