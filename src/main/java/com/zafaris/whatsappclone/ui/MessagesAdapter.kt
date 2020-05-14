package com.zafaris.whatsappclone.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.Message
import kotlinx.android.synthetic.main.item_message_me.view.*
import java.lang.IllegalArgumentException

class MessagesAdapter(private val messagesList: List<Message>): RecyclerView.Adapter<MessagesAdapter.BaseViewHolder<*>>() {

    override fun getItemViewType(position: Int): Int {
        return when (messagesList[position].sentByMe) {
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
            usernameTextView.text = item.username
        }

    }

    inner class OtherViewHolder(itemView: View): BaseViewHolder<Message>(itemView) {

        override fun bind(item: Message) {
            val messageTextView: TextView = itemView.findViewById(R.id.textview_other_message)
            val usernameTextView: TextView = itemView.findViewById(R.id.textview_other_username)
            messageTextView.text = item.message
            usernameTextView.text = item.username
        }
    }

    companion object {
        private const val TYPE_ME = 1
        private const val TYPE_OTHER = 2
    }
}