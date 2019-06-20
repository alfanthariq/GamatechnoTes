package com.alfanthariq.skeleton.features.main.chat

import android.os.Bundle
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.features.base.BaseActivity
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : BaseActivity<ChatContract.View,
        ChatPresenter>(),
    ChatContract.View {

    private var id = -1

    override var mPresenter: ChatPresenter
        get() = ChatPresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_chat

    override fun getTagClass(): String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        id = intent.getIntExtra("user_id", -1)

        init()
    }

    fun init(){
        setToolbar(toolbar, "", true)
        toolbar.setNavigationIcon(R.mipmap.ic_back)


    }
}
