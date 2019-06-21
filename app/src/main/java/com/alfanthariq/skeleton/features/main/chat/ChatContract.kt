package com.alfanthariq.skeleton.features.main.chat

import com.alfanthariq.skeleton.data.model.Messages
import com.alfanthariq.skeleton.features.base.BaseMvpView
import com.alfanthariq.skeleton.features.base.BasePresenter

object ChatContract {
    interface View : BaseMvpView {
        fun onLoadMessage(data : List<Messages>)
    }

    interface Presenter : BasePresenter<View> {
        fun sendMessage(message : String, sender : Int, conversation_id: Int, callback : (Boolean) -> Unit)
        fun loadMessage(conversation_id : Int)
        fun insertNewMessage(message : String, time : String, sender : Int, conversation_id: Int)
        fun getSenderName(senderId : Int, callback: (String) -> Unit)
    }
}