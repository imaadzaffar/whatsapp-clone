package com.zafaris.whatsappclone.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Message
import java.lang.IllegalArgumentException

class MessagesAdapter(private val userId: String, private val messagesList: List<Message>): RecyclerView.Adapter<MessagesAdapter.BaseViewHolder<*>>() {

    override fun getItemViewType(position: Int): Int {
        return when (messagesList[position].userId == userId) {
            true -> TYPE_ME
            false -> TYPE_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_ME -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message_me, parent, false)
                MeViewHolder(itemView)
            }
            TYPE_OTHER -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message_other, parent, false)
                OtherViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val message = messagesList[position]
        when (holder) {
            is MeViewHolder -> holder.bind(message)
            is OtherViewHolder -> holder.bind(message)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = messagesList.size

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class MeViewHolder(itemView: View): BaseViewHolder<Message>(itemView) {

        override fun bind(item: Message) {
            val messageTextView: TextView = itemView.findViewById(R.id.textview_me_message)
            val usernameTextView: TextView = itemView.findViewById(R.id.textview_me_username)
            messageTextView.text = item.message
            usernameTextView.text = item.name
        }

    }

    inner class OtherViewHolder(itemView: View): BaseViewHolder<Message>(itemView) {

        override fun bind(item: Message) {
            val messageTextView: TextView = itemView.findViewById(R.id.textview_other_message)
            val usernameTextView: TextView = itemView.findViewById(R.id.textview_other_username)
            messageTextView.text = item.message
            usernameTextView.text = item.name
        }
    }

    companion object {
        private const val TYPE_ME = 1
        private const val TYPE_OTHER = 2
    }
}