package com.zafaris.whatsappclone.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Chat
import com.zafaris.whatsappclone.model.Message
import com.zafaris.whatsappclone.ui.chat.ChatActivity
import com.zafaris.whatsappclone.ui.login.LoginActivity
import com.zafaris.whatsappclone.ui.newchat.NewChatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userChatsReference: DatabaseReference
    private lateinit var chatsReference: DatabaseReference

    private lateinit var chatsAdapter: ChatsAdapter
    private val chatIdsList: MutableList<String> = ArrayList()
    private val chatsList: MutableList<Chat> = ArrayList()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupRv()

        auth = FirebaseAuth.getInstance()

        userChatsReference = Firebase.database.getReference("users/${auth.uid!!}/chats")
        chatsReference = Firebase.database.getReference("chats")

        getChats()

        fab.setOnClickListener {
            val intent = Intent(this, NewChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRv() {
        chatsAdapter = ChatsAdapter(chatsList)
        chatsAdapter.setOnItemClickListener { position -> chatOnClick(position) }
        recyclerview_chats.adapter = chatsAdapter

        val divider = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
        recyclerview_chats.addItemDecoration(divider)
        recyclerview_chats.layoutManager = LinearLayoutManager(this)
    }

    private fun chatOnClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chatIdsList[position])
        intent.putExtra("chatName", chatsList[position].name)
        startActivity(intent)
    }

    private fun getChats() {

        //Retrieves all chatId strings in the user's chatList
        userChatsReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    chatIdsList.clear()
                    chatsList.clear()

                    //Retrieves chat object for each chatId in the user's chatList
                    for (chatIdSnapshot in dataSnapshot.children) {
                        val chatId = chatIdSnapshot.key!!

                        chatsReference.orderByKey().equalTo(chatId)
                            .addChildEventListener(object : ChildEventListener {
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(this@HomeActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                                }

                                override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                                    TODO("Not yet implemented")
                                }

                                override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                                    val chatId = dataSnapshot.key!!
                                    val updatedChat = dataSnapshot.getValue<Chat>()!!
                                    val index = chatIdsList.indexOf(chatId)
                                    if (index > -1) {
                                        chatsList[index] = updatedChat
                                        chatsAdapter.notifyItemChanged(index)
                                    }
                                }

                                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                                    val chatId = dataSnapshot.key!!
                                    chatIdsList.add(chatId)
                                    val chat = dataSnapshot.getValue<Chat>()!!
                                    chatsList.add(chat)
                                    chatsAdapter.notifyDataSetChanged()
                                }

                                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                                    val chatId = dataSnapshot.key!!
                                    val index = chatIdsList.indexOf(chatId)
                                    if (index > -1) {
                                        chatIdsList.removeAt(index)
                                        chatsList.removeAt(index)
                                        chatsAdapter.notifyItemRemoved(index)
                                    }
                                }

                            })

                    }
                } else {
                    Toast.makeText(this@HomeActivity, "No chats yet!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("chatIds", databaseError.message)
                Toast.makeText(this@HomeActivity, "Error getting user chatList...", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
