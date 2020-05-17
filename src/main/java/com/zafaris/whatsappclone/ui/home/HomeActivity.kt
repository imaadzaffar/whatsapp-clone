package com.zafaris.whatsappclone.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.zafaris.whatsappclone.ui.chat.ChatActivity
import com.zafaris.whatsappclone.ui.login.LoginActivity
import com.zafaris.whatsappclone.ui.newchat.NewChatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userChatsReference: DatabaseReference
    private lateinit var chatsReference: DatabaseReference

    private lateinit var prefs: SharedPreferences
    private var name = ""

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

            //Intent to LoginActivity
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

        prefs = getSharedPreferences("com.zafaris.whatsappclone", Context.MODE_PRIVATE)
        name = prefs.getString("name", "")!!

        getChats()

        //FAB onClick
        fab.setOnClickListener {
            //Intent to NewChatActivity
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
        //Retrieves all chatId strings in the user's chats
        userChatsReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Checks if the user has any chats
                if (dataSnapshot.value != null) {
                    chatIdsList.clear()
                    chatsList.clear()

                    //Retrieves chat object for each chatId in the user's chats
                    for (chatIdSnapshot in dataSnapshot.children) {
                        val chatId = chatIdSnapshot.key!!

                        chatsReference.orderByKey().equalTo(chatId)
                            .addChildEventListener(object : ChildEventListener {
                                override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                                    TODO("Not yet implemented")
                                }

                                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                                    val addedChatId = dataSnapshot.key!!
                                    chatIdsList.add(addedChatId)

                                    val chat = dataSnapshot.getValue<Chat>()!!

                                    //Checks if chat doesn't have a value for name (not a group chat)
                                    if (dataSnapshot.child("name").value == null) {
                                        val usersRef = dataSnapshot.child("userNames")
                                        //For all of the userNames for the chat
                                        for (userName in usersRef.children) {
                                            //Checks if the userName is not equal to the current user's userName
                                            if (userName.key != name) {
                                                val chatName =
                                                    userName.key!!  //Gets other user's userName
                                                chat.name =
                                                    chatName  //Sets chatName to the other user's userName
                                            }
                                        }
                                    }
                                    chatsList.add(chat)
                                    chatsAdapter.notifyDataSetChanged()
                                }

                                override fun onChildChanged(
                                    dataSnapshot: DataSnapshot,
                                    p1: String?
                                ) {
                                    val changedChatId = dataSnapshot.key!!
                                    val updatedChat = dataSnapshot.getValue<Chat>()!!

                                    //Checks if chatId is already in chatIdsList
                                    val index = chatIdsList.indexOf(changedChatId)
                                    if (index > -1) {
                                        //Checks if chat doesn't have a value for name (not a group chat)
                                        if (dataSnapshot.child("name").value == null) {
                                            val usersRef = dataSnapshot.child("userNames")
                                            //For all of the userNames for the chat
                                            for (userName in usersRef.children) {
                                                //Checks if the userName is not equal to the current user's userName
                                                if (userName.key != name) {
                                                    val chatName =
                                                        userName.key!!  //Gets other user's userName
                                                    updatedChat.name =
                                                        chatName  //Sets chatName to the other user's userName
                                                }
                                            }
                                        }
                                        chatsList[index] = updatedChat
                                        chatsAdapter.notifyItemChanged(index)
                                    }
                                }

                                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                                    val removedChatId = dataSnapshot.key!!

                                    //Checks if chatId is already in chatIdsList
                                    val index = chatIdsList.indexOf(removedChatId)
                                    if (index > -1) {
                                        chatIdsList.removeAt(index)
                                        chatsList.removeAt(index)
                                        chatsAdapter.notifyItemRemoved(index)
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        databaseError.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "No chats yet!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }

}
