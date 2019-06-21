package com.alfanthariq.skeleton.features.main.chat

import alfanthariq.com.signatureapp.util.PreferencesHelper
import com.alfanthariq.skeleton.data.local.AppDatabase
import com.alfanthariq.skeleton.data.model.Messages
import com.alfanthariq.skeleton.features.base.BasePresenterImpl
import com.alfanthariq.skeleton.utils.DateOperationUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ChatPresenter (var view: ChatContract.View) :
    BasePresenterImpl<ChatContract.View>(), ChatContract.Presenter {

    private val context = view.getContext()
    private val pref_profile = PreferencesHelper.getProfilePref(context)
    private val db = AppDatabase.getInstance(context)!!

    override fun getSenderName(senderId : Int, callback: (String) -> Unit) {
        doAsync {
            val user = db.UserDAO().one(senderId)

            uiThread {
                callback(user.user_name)
            }
        }
    }

    override fun sendMessage(message: String, sender : Int, conversation_id: Int, callback: (Boolean) -> Unit) {
        doAsync {
            val msg = Messages()
            msg.message = message
            msg.sender_id = sender
            msg.conversation_id = conversation_id
            msg.user_id = pref_profile.getInt("user_id", -1)
            msg.message_time = DateOperationUtil.getCurrentTimeStr("yyyy-MM-dd HH:mm:ss")

            db.MessageDAO().insert(msg)

            uiThread {
                callback(true)
            }
        }
    }

    override fun insertNewMessage(message: String, time : String, sender: Int, conversation_id: Int) {
        doAsync {
            val msg = Messages()
            msg.message = message
            msg.sender_id = sender
            msg.conversation_id = conversation_id
            msg.user_id = pref_profile.getInt("user_id", -1)
            msg.message_time = time

            db.MessageDAO().insert(msg)

            loadMessage(sender)
        }
    }

    override fun loadMessage(conversation_id: Int) {
        doAsync {
            val data = db.MessageDAO().ByConversation(conversation_id)

            uiThread {
                view.onLoadMessage(data)
            }
        }
    }
}