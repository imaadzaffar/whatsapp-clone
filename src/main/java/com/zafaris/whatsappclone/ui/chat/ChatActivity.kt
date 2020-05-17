package com.zafaris.whatsappclone.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Message
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var currentUserId = ""
    private lateinit var chatsReference: DatabaseReference
    private lateinit var messagesReference: DatabaseReference

    private lateinit var prefs: SharedPreferences
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList: MutableList<Message> = ArrayList()
    private val messageIdsList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val chatName = intent.getStringExtra("chatName")
        val chatId = intent.getStringExtra("chatId")
        title = chatName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prefs = getSharedPreferences("com.zafaris.whatsappclone", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "")!!

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.uid!!
        chatsReference = Firebase.database.getReference("chats/$chatId")
        messagesReference = Firebase.database.getReference("messages/$chatId")

        setupRv()

        messagesReference.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val messageId = dataSnapshot.key!!
                messageIdsList.add(messageId)
                val message = dataSnapshot.getValue<Message>()!!
                messagesList.add(message)
                messagesAdapter.notifyDataSetChanged()
                recyclerview_messages.scrollToPosition(messagesAdapter.itemCount - 1)  //Scrolls to bottom of recyclerView
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val messageId = dataSnapshot.key!!
                val index = messageIdsList.indexOf(messageId)
                //Checks if message is already in list
                if (index > -1) {
                    messageIdsList.removeAt(index)
                    messagesList.removeAt(index)
                    messagesAdapter.notifyItemRemoved(index)
                    recyclerview_messages.scrollToPosition(messagesAdapter.itemCount - 1)  //Scrolls to bottom of recyclerView
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("Feature not available in WhatsApp")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("Feature not available in WhatsApp")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ChatActivity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })

        button_send.setOnClickListener {
            val messageText = edittext_message.text.toString()
            if (messageText.isNotEmpty()) {
                val message = Message(messageText, name, currentUserId)
                messagesReference.push().setValue(message)  //Adds new message
                chatsReference.child("lastMessage")
                    .setValue("${name}: ${message.message}")  //Adds lastMessage
                edittext_message.text.clear()  //Clears editText
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRv() {
        Log.d("currentUserId", currentUserId)
        messagesAdapter = MessagesAdapter(currentUserId, messagesList)
        recyclerview_messages.adapter = messagesAdapter
        val layoutManager = LinearLayoutManager(this)
        recyclerview_messages.layoutManager = layoutManager
    }

}
