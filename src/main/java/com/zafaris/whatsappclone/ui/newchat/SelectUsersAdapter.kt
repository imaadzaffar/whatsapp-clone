package com.zafaris.whatsappclone.ui.newchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.User

class SelectUsersAdapter(private val usersList: List<User>, private val usersSelectedList: List<Int>) : RecyclerView.Adapter<SelectUsersAdapter.SelectUserViewHolder>() {
    var listener: ((Int, Boolean) -> Unit)? = null

    fun setOnCheckedChangedListener(listener: ((Int, Boolean) -> Unit)?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_select_user, parent, false)
        return SelectUserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SelectUserViewHolder, position: Int) {
        holder.nameTextView.text = usersList[position].name
    }

    override fun getItemCount(): Int = usersList.size

    inner class SelectUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textview_user_name)
        private val selectCheckBox: CheckBox = itemView.findViewById(R.id.checkbox_user_select)

        init {
            selectCheckBox.setOnCheckedChangeListener { _, isChecked -> listener?.invoke(adapterPosition, isChecked) }
        }
    }
}