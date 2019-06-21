package com.alfanthariq.skeleton.features.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.data.local.AppDatabase
import com.alfanthariq.skeleton.data.model.Conversation
import com.alfanthariq.skeleton.utils.GlideApp
import com.livinglifetechway.k4kotlin.onClick
import kotlinx.android.synthetic.main.item_user.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ConversationAdapter (private var detail: ArrayList<Conversation>,
                           private val clickListener: (Conversation) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var db : AppDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh : RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        db = AppDatabase.getInstance(v.context)!!
        vh = MainHolder(v, db)
        return vh
    }

    override fun getItemCount(): Int = detail.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainHolder){
            holder.bind(detail[position], clickListener)
        }
    }

    class MainHolder(private val view: View, private val db : AppDatabase) : RecyclerView.ViewHolder(view) {
        fun bind(data: Conversation, listener: (Conversation) -> Unit) {
            doAsync {
                val user = db.UserDAO().one(data.sender_id)
                view.txt_username.text = user.user_name
                view.container.onClick {
                    listener(data)
                }

                val lastMsg = db.MessageDAO().lastByConversation(data.id)
                uiThread {
                    if (lastMsg != null) {
                        view.txt_last_msg.text = lastMsg.message
                    } else {
                        view.txt_last_msg.text = ""
                    }

                    GlideApp.with(view.context)
                        .load(user.user_photo)
                        .error(R.mipmap.ic_user)
                        .into(view.img_profpic)
                }
            }
        }
    }
}