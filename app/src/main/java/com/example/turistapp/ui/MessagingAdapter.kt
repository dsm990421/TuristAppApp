package com.example.turistapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.turistapp.R
import com.example.turistapp.utils.Constants.RECEIVE_ID
import com.example.turistapp.utils.Constants.SEND_ID
import com.example.turistapp.data.Message
import kotlinx.android.*

class MessagingAdapter: RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {

    var messagesList = mutableListOf<Message>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]
        when (currentMessage.id) {
            SEND_ID -> {
                holder.tv_message.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                holder.tv_bot_message.visibility = View.GONE
            }
            RECEIVE_ID -> {
                holder.tv_bot_message.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                holder.tv_message.visibility = View.GONE
            }
        }
    }

    fun insertMessage(message: Message) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_message:TextView
        var tv_bot_message:TextView
        init {

            tv_message=itemView.findViewById(R.id.tv_message)
            tv_bot_message=itemView.findViewById(R.id.tv_bot_message)

            itemView.setOnClickListener {
                //Remove message on the item clicked
                messagesList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }
}