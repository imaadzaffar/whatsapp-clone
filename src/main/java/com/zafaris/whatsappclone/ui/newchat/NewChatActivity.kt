package com.zafaris.whatsappclone.ui.newchat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Chat
import com.zafaris.whatsappclone.model.User
import com.zafaris.whatsappclone.ui.chat.ChatActivity
import kotlinx.android.synthetic.main.activity_new_chat.*

class NewChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var currentUserId = ""
    private lateinit var databaseReference: DatabaseReference

    private lateinit var prefs: SharedPreferences
    private var name = ""

    private lateinit var selectUsersAdapter: SelectUsersAdapter
    private val userIdsList: MutableList<String> = ArrayList()
    private val usersList: MutableList<User> = ArrayList()
    private val usersSelectedList: MutableList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)
        title = "Create new chat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.uid!!
        databaseReference = Firebase.database.reference

        prefs = getSharedPreferences("com.zafaris.whatsappclone", Context.MODE_PRIVATE)
        name = prefs.getString("name", "")!!

        setupRv()
        getUsers()

        button_new_chat.setOnClickListener { newChatButtonOnClick() }
    }

    private fun setupRv() {
        selectUsersAdapter = SelectUsersAdapter(usersList, usersSelectedList)
        selectUsersAdapter.setOnCheckedChangedListener { position, isChecked ->
            selectCheckBoxOnCheck(position, isChecked)
        }
        recyclerview_select_users.adapter = selectUsersAdapter

        val divider = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
        recyclerview_select_users.addItemDecoration(divider)
        recyclerview_select_users.layoutManager = LinearLayoutManager(this)
    }

    private fun selectCheckBoxOnCheck(position: Int, isChecked: Boolean) {
        if (isChecked) {
            usersSelectedList.add(position)
        } else {
            usersSelectedList.removeAt(usersSelectedList.indexOf(position))
        }
        //Shows chat name editText if more than 1 user is selected
        if (usersSelectedList.size > 1) {
            edittext_new_chat_name.visibility = View.VISIBLE
        } else {
            edittext_new_chat_name.visibility = View.GONE
        }
    }

    private fun getUsers() {
        val userReference = databaseReference.child("users")
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val userId = userSnapshot.key!!
                    //Adds all of the users apart from the currentUser
                    if (userId != currentUserId) {
                        userIdsList.add(userId)
                        val user = userSnapshot.getValue<User>()!!
                        usersList.add(user)
                    }
                }
                selectUsersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@NewChatActivity, databaseError.message, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun newChatButtonOnClick() {
        //Checks how many users are selected
        when {
            usersSelectedList.size == 1 -> {
                userIdsList.add(currentUserId)
                val chat = Chat()
                createNewChat(chat)
            }
            usersSelectedList.size > 1 -> {
                if (edittext_new_chat_name.text.isNotEmpty()) {
                    userIdsList.add(currentUserId)
                    val chatName = edittext_new_chat_name.text.toString()
                    val chat = Chat(name = chatName)
                    createNewChat(chat)
                } else {
                    Toast.makeText(this, "Please enter a chat name", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "Select at least one user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNewChat(chat: Chat) {
        val chatId = databaseReference.push().key  //Generates new chatId with push().key

        val userNames: HashMap<String, Boolean> = HashMap()
        val updatesMap: HashMap<String, Any> = HashMap()

        //Adds values for the current user (as it is not in usersSelectedList)
        userNames[name] = true  //Adds name of the current user
        updatesMap["users/${currentUserId}/chats/$chatId"] = true  //dds chatId to the current user's chats

        //Adds values for each user in the usersSelectedList
        for (index in usersSelectedList) {
            userNames[usersList[index].name] = true  //Adds name of the user
            val userId = userIdsList[index]  //userId of the user
            updatesMap["users/$userId/chats/$chatId"] = true  //Adds chatId to the user's chats
        }
        chat.userNames = userNames //Sets the new chat's userNames to the userNames in usersSelectedList

        updatesMap["chats/$chatId"] = chat  //Adds new chat object to the new chatId node
        databaseReference.updateChildren(updatesMap)

        //Intent to ChatActivity
        val intent = Intent(this, ChatActivity::class.java)
        //Checks if only one user is selected
        if (usersSelectedList.size == 1) {
            val chatName = usersList[usersSelectedList[0]].name  //Sets chatName to other user's userName
            intent.putExtra("chatName", chatName)
        } else {
            intent.putExtra("chatName", chat.name)  //Sets chatName to value entered in editText
        }
        intent.putExtra("chatId", chatId)
        startActivity(intent)
    }
}
