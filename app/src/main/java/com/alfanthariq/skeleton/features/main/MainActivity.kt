package com.alfanthariq.skeleton.features.main

import alfanthariq.com.signatureapp.util.PreferencesHelper
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.data.model.Conversation
import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.features.base.BaseActivity
import com.alfanthariq.skeleton.features.common.ErrorView
import com.alfanthariq.skeleton.features.main.chat.ChatActivity
import com.alfanthariq.skeleton.features.main.contact.ContactActivity
import com.alfanthariq.skeleton.features.socketioservice.AppSocketListener
import com.alfanthariq.skeleton.features.socketioservice.SocketEventConstants
import com.alfanthariq.skeleton.features.socketioservice.SocketListener
import com.alfanthariq.skeleton.utils.*
import com.livinglifetechway.k4kotlin.onClick
import com.livinglifetechway.k4kotlin.toast
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.Transport
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import kotlin.collections.ArrayList

class MainActivity : BaseActivity<MainContract.View,
        MainPresenter>(),
    MainContract.View, ErrorView.ErrorListener {

    private var conversationAdapter : ConversationAdapter? = null
    private var details = ArrayList<Conversation>()
    private var lastPage = 1
    private lateinit var mSocket: Socket

    override var mPresenter: MainPresenter
        get() = MainPresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_main

    override fun getTagClass(): String = javaClass.simpleName

    override fun onReloadData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        initializeSocket()
//        addSocketHandlers()
        init()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onRefreshData(list: List<Conversation>) {
        swipe.isRefreshing = false
        hideLoadingDialog()
        details.clear()
        details.addAll(list)
        conversationAdapter?.notifyDataSetChanged()

        if (details.size > 0) {
            lyt_empty.gone()
            recycler.visible()
        } else {
            lyt_empty.visible()
            recycler.gone()
        }
    }

    fun init(){
        setupRecycler()

        if (NetworkUtil.isNetworkConnected(this)) {
            showLoadingDialog("Loading ...")
            mPresenter.getData(lastPage){ list, status, message, next ->
                hideLoadingDialog()
                if (!status) {
                    toast(message)
                }
            }
        }

        mPresenter.refreshData()

        fab_contact.onClick {
            AppRoute.open(this@MainActivity, ContactActivity::class.java)
            //attemptSend()
        }

        swipe.setOnRefreshListener {
            mPresenter.refreshData()
        }

        AppSocketListener.getInstance().setActiveSocketListener(object : SocketListener {
            override fun onSocketConnected() {

            }

            override fun onSocketDisconnected() {

            }

            override fun onNewMessageReceived(jsonData: String) {
                mPresenter.refreshData()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mPresenter.refreshData()
    }

    fun setupRecycler(){
        conversationAdapter = ConversationAdapter(details){
            val param = HashMap<String, String>()
            param["sender_id"] = it.sender_id.toString()
            param["conversation_id"] = it.id.toString()
            AppRoute.open(this, ChatActivity::class.java, param)
        }

        recycler.apply {
            val linLayout = LinearLayoutManager(this@MainActivity,  RecyclerView.VERTICAL, false)
            layoutManager = linLayout
            adapter = conversationAdapter
        }
    }

    private fun initializeSocket() {
        try {
            val options = IO.Options()
            options.forceNew = true
            options.reconnection = true
            options.timeout = 20000
            options.reconnectionAttempts = 100
            val pref_profile = PreferencesHelper.getProfilePref(this)
            val token = pref_profile.getString("token", "")
            mSocket = IO.socket(NetworkUtil.socket, options)

            mSocket.io()?.on(Manager.EVENT_TRANSPORT) { args ->
                val transport = args[0] as Transport

                transport.on(Transport.EVENT_REQUEST_HEADERS) { argsa ->
                    val headers =  argsa[0] as MutableMap<String, List<String>>
                    // modify request headers
                    headers["token"] = listOf(token)
                }
            }
        } catch (e: Exception) {
            Log.e("Error", "Exception in socket creation")
            throw RuntimeException(e)
        }

    }

    private fun addSocketHandlers() {
        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, Emitter.Listener {
            val intent = Intent(SocketEventConstants.socketConnection)
            intent.putExtra("connectionStatus", true)
            Log.d("SocketIOMain", "Socket connected")
        })

        mSocket.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            val intent = Intent(SocketEventConstants.socketConnection)
            intent.putExtra("connectionStatus", false)
            Log.d("SocketIOMain", "Socket disconnected")
        })

        mSocket.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener {
            val intent = Intent(SocketEventConstants.connectionFailure)
            Log.d("SocketIOMain", "Socket error")
        })

        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, Emitter.Listener {
            val intent = Intent(SocketEventConstants.connectionFailure)
            Log.d("SocketIOMain", "Socket timeout")
        })
        addNewMessageHandler()
    }

    fun addNewMessageHandler() {
        mSocket.off(SocketEventConstants.getMessage)
        mSocket.on(SocketEventConstants.getMessage) { args ->
            println("SocketIOMain received : " + args[0].toString())
            Toast.makeText(this@MainActivity, args[0].toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
