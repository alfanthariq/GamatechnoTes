package com.alfanthariq.skeleton.features.main.chat

import alfanthariq.com.signatureapp.util.PreferencesHelper
import android.content.SharedPreferences
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.data.model.Messages
import com.alfanthariq.skeleton.features.base.BaseActivity
import com.alfanthariq.skeleton.features.socketioservice.AppSocketListener
import com.alfanthariq.skeleton.features.socketioservice.SocketListener
import com.livinglifetechway.k4kotlin.onClick
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : BaseActivity<ChatContract.View,
        ChatPresenter>(),
    ChatContract.View {

    private var sender_id = -1
    private var user_id = -1
    private var sender_name = ""
    private lateinit var msgAdapter : MessageAdapter
    private var messages = ArrayList<Messages>()
    private lateinit var pref_profile : SharedPreferences

    override var mPresenter: ChatPresenter
        get() = ChatPresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_chat

    override fun getTagClass(): String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref_profile = PreferencesHelper.getProfilePref(this)

        sender_id = intent.getStringExtra("sender_id").toInt()
        sender_name = intent.getStringExtra("sender_name")
        user_id = pref_profile.getInt("user_id", -1)

        AppSocketListener.getInstance().setActiveSocketListener(object : SocketListener {
            override fun onSocketConnected() {

            }

            override fun onSocketDisconnected() {

            }

            override fun onNewMessageReceived(jsonData: String) {
                mPresenter.insertNewMessage("", sender_id)
            }
        })

        init()
    }

    override fun onLoadMessage(data: List<Messages>) {
        messages.clear()
        messages.addAll(data)
        msgAdapter.notifyDataSetChanged()
    }

    fun init(){
        setToolbar(toolbar, "", true)
        toolbar.setNavigationIcon(R.mipmap.ic_back)

        toolbar_title.text = sender_name

        mPresenter.loadMessage(sender_id)

        btn_send.onClick {
            mPresenter.sendMessage(edit_pesan.text.toString(), user_id){
                if (it) {
                    edit_pesan.text.clear()
                    mPresenter.loadMessage(sender_id)
                }
            }
        }

        setupRecycler()
    }

    fun setupRecycler(){
        msgAdapter = MessageAdapter(messages, this){

        }

        recycler_message.apply {
            val linLayout = LinearLayoutManager(this@ChatActivity,  RecyclerView.VERTICAL, false)
            linLayout.reverseLayout = true
            layoutManager = linLayout
            adapter = msgAdapter
        }
    }
}
