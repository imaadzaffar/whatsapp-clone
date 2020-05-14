package com.zafaris.whatsappclone.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.parse.ParseException
import com.parse.ParseUser
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Chat
import kotlinx.android.synthetic.main.activity_chats.*

class ChatsActivity : AppCompatActivity() {

    private lateinit var chatsAdapter: ChatsAdapter
    private val chatsList: MutableList<Chat> = ArrayList()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            ParseUser.logOutInBackground { e: ParseException? ->
                if (e == null) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        setupRv()

        val chatNames: List<String> = ParseUser.getCurrentUser().getList("chatsList")!!
        if (chatNames.isNotEmpty()) {
            for (chatName in chatNames) {
                chatsList.add(Chat(chatName))
            }
            chatsAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "No chats found", Toast.LENGTH_SHORT).show()
        }

        fab.setOnClickListener {
            //TODO: Create new chat feature
            Toast.makeText(this, "FAB Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRv() {
        chatsAdapter = ChatsAdapter(chatsList)
        chatsAdapter.setOnItemClickListener { chat -> chatOnClick(chat) }
        recyclerview_chats.adapter = chatsAdapter

        val divider = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
        recyclerview_chats.addItemDecoration(divider)
        recyclerview_chats.layoutManager = LinearLayoutManager(this)
    }

    private fun chatOnClick(chat: Chat) {
        //TODO: Intent to chat activity and pass chat name
        Toast.makeText(this, chat.name, Toast.LENGTH_SHORT).show()
    }
}
