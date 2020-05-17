package com.zafaris.whatsappclone.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Chat

class ChatsAdapter(private val chatsList: List<Chat>) : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {
    private var listener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Int) -> Unit)) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.nameTextView.text = chatsList[position].name
        holder.lastMessageTextView.text = chatsList[position].lastMessage
    }

    override fun getItemCount(): Int = chatsList.size

    inner class ChatsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textview_chat_name)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.textview_chat_last_message)

        init {
            itemView.setOnClickListener { listener?.invoke(adapterPosition) }
        }
    }

}