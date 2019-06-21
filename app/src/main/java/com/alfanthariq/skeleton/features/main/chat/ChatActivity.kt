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
import com.alfanthariq.skeleton.features.socketioservice.SocketEventConstants
import com.alfanthariq.skeleton.features.socketioservice.SocketListener
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.livinglifetechway.k4kotlin.onClick
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONException

class ChatActivity : BaseActivity<ChatContract.View,
        ChatPresenter>(),
    ChatContract.View {

    private var sender_id = -1
    private var conversation_id = -1
    private var user_id = -1
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
        conversation_id = intent.getStringExtra("conversation_id").toInt()
        user_id = pref_profile.getInt("user_id", -1)

        AppSocketListener.getInstance().setActiveSocketListener(object : SocketListener {
            override fun onSocketConnected() {

            }

            override fun onSocketDisconnected() {

            }

            override fun onNewMessageReceived(jsonData: String) {
                val json = JsonParser().parse(jsonData).asJsonObject
                val obj = json.get("gtfwResult").asJsonObject
                val data = obj.get("data").asJsonObject
                val from = data.get("from_user_id").asInt
                if (from == sender_id) {
                    val msg = data.get("from_message").asString
                    val time = data.get("timestamp").asString
                    mPresenter.insertNewMessage(msg, time, sender_id, conversation_id)
                }
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

        mPresenter.getSenderName(sender_id){
            toolbar_title.text = it
        }

        mPresenter.loadMessage(conversation_id)

        btn_send.onClick {
            mPresenter.sendMessage(edit_pesan.text.toString(), user_id, conversation_id){
                if (it) {
                    attemptSend(edit_pesan.text.toString())
                    edit_pesan.text.clear()
                    mPresenter.loadMessage(conversation_id)
                }
            }
        }

        setupRecycler()
    }

    private fun attemptSend(msg : String) {
        val message = "{" +
                "                \"gtfwRequest\": {\n" +
                "                    \"data\": {\n" +
                "                        \"user_id\": $sender_id,\n" +
                "                        \"message\": \"$msg\"\n" +
                "                    }\n" +
                "} }\n"
        try {
            System.out.println("Emit : $message")
            AppSocketListener.getInstance().emit(SocketEventConstants.newMessage, message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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
