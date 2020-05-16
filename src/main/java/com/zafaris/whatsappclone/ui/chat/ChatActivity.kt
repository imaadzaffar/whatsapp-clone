package com.zafaris.whatsappclone.ui.chat

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
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
    private lateinit var chatsReference: DatabaseReference
    private lateinit var messagesReference: DatabaseReference

    private lateinit var prefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList: MutableList<Message> = ArrayList()
    private val messageIdsList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatId = intent.getStringExtra("chatId")

        prefs = getSharedPreferences("com.zafaris.whatsappclone", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "")!!

        auth = FirebaseAuth.getInstance()
        userId = auth.uid!!

        chatsReference = Firebase.database.getReference("chats/$chatId")
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
                val messageId = dataSnapshot.key!!
                val updatedMessage = dataSnapshot.getValue<Message>()!!
                val index = messageIdsList.indexOf(messageId)
                if (index > -1) {
                    messagesList[index] = updatedMessage
                    messagesAdapter.notifyItemChanged(index)
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val messageId = dataSnapshot.key!!
                messageIdsList.add(messageId)
                val message = dataSnapshot.getValue<Message>()!!
                messagesList.add(message)
                messagesAdapter.notifyDataSetChanged()
                recyclerview_messages.scrollToPosition(messagesAdapter.itemCount - 1)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val messageId = dataSnapshot.key!!
                val index = messageIdsList.indexOf(messageId)
                if (index > -1) {
                    messageIdsList.removeAt(index)
                    messagesList.removeAt(index)
                    messagesAdapter.notifyItemRemoved(index)
                    recyclerview_messages.scrollToPosition(messagesAdapter.itemCount - 1)
                }
            }
        })

        button_send.setOnClickListener {
            val messageText = edittext_message.text.toString()
            if (messageText.isNotEmpty()) {
                val message = Message(messageText, name, userId)
                messagesReference.push().setValue(message)
                chatsReference.child("lastMessage").setValue("${name}: ${message.message}")
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRv() {
        messagesAdapter =
            MessagesAdapter(
                userId,
                messagesList
            )
        recyclerview_messages.adapter = messagesAdapter
        val layoutManager = LinearLayoutManager(this)
        recyclerview_messages.layoutManager = layoutManager
    }

}
