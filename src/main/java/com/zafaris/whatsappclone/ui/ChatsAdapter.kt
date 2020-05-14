package com.zafaris.whatsappclone.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Chat

class ChatsAdapter(private val chatsList: List<Chat>) : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {
    private var listener: ((Chat) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Chat) -> Unit)) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.nameTextView.text = chatsList[position].name
        //holder.pictureImageView.setImageDrawable(chatsList[position].image)
    }

    override fun getItemCount(): Int = chatsList.size

    inner class ChatsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textview_chat_name)
        //val pictureImageView: ImageView = itemView.findViewById(R.id.imageview_chat_picture)

        init {
            itemView.setOnClickListener { listener?.invoke(chatsList[adapterPosition]) }
        }
    }

}