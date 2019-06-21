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

    override fun sendMessage(message: String, sender : Int, callback: (Boolean) -> Unit) {
        doAsync {
            val msg = Messages()
            msg.message = message
            msg.sender_id = sender
            msg.user_id = pref_profile.getInt("user_id", -1)
            msg.message_time = DateOperationUtil.getCurrentTimeStr("yyyy-MM-dd HH:mm:ss")

            db.MessageDAO().insert(msg)

            uiThread {
                callback(true)
            }
        }
    }

    override fun insertNewMessage(message: String, sender: Int) {
        doAsync {
            val msg = Messages()
            msg.message = message
            msg.sender_id = sender
            msg.user_id = pref_profile.getInt("user_id", -1)
            msg.message_time = DateOperationUtil.getCurrentTimeStr("yyyy-MM-dd HH:mm:ss")

            db.MessageDAO().insert(msg)

            loadMessage(sender)
        }
    }

    override fun loadMessage(sender_id: Int) {
        doAsync {
            val data = db.MessageDAO().ByUser(sender_id, pref_profile.getInt("user_id", -1))

            uiThread {
                view.onLoadMessage(data)
            }
        }
    }
}