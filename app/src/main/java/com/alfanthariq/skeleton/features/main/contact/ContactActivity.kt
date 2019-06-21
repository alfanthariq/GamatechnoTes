package com.alfanthariq.skeleton.features.main.contact

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.features.base.BaseActivity
import com.alfanthariq.skeleton.features.main.chat.ChatActivity
import com.alfanthariq.skeleton.utils.*
import com.livinglifetechway.k4kotlin.toast
import kotlinx.android.synthetic.main.activity_contact.*
import org.jetbrains.anko.collections.forEachReversedWithIndex
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class ContactActivity : BaseActivity<ContactContract.View,
        ContactPresenter>(),
    ContactContract.View {

    private var contactAdapter : ContactAdapter? = null
    private var details = ArrayList<Users?>()
    private var lastPage = 1
    private var scrollListener : EndlessRecyclerViewScrollListener? = null
    private var isLoadMore = false

    override var mPresenter: ContactPresenter
        get() = ContactPresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_contact

    override fun getTagClass(): String = javaClass.simpleName

    fun removeLoading() {
        details.forEachReversedWithIndex { i, users ->
            if (users == null) {
                details.remove(users)
                return
            }
        }
    }

    override fun onRefreshData(list: List<Users>) {
        hideLoadingDialog()
        removeLoading()
        if (!isLoadMore) {
            details.clear()
        }
        details.addAll(list)
        contactAdapter?.notifyDataSetChanged()

        if (details.size > 0) {
            lyt_empty.gone()
            recycler.visible()
        } else {
            lyt_empty.visible()
            recycler.gone()
        }

        isLoadMore = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun init(){
        setToolbar(toolbar_contact, "", true)
        toolbar_contact.setNavigationIcon(R.mipmap.ic_back)

        setupRecycler()

        if (NetworkUtil.isNetworkConnected(this)) {
            showLoadingDialog("Loading data ...")
            mPresenter.getData(lastPage){ list, status, message, next ->
                hideLoadingDialog()
                if (status) {
                    if (next) {
                        lastPage += 1
                    }
                    details.addAll(list!!)
                    contactAdapter?.notifyDataSetChanged()
                } else {
                    toast(message)
                }
            }
        } else {
            mPresenter.refreshData()
        }

        val tw_cari = TextWatcherHelper()
        val tw_cari_listener : TextWatcherHelper.TextWatcherListener = object : TextWatcherHelper.TextWatcherListener {
            override fun onAfterChange(editable: Editable) {
                val txt = editable.toString()
                mPresenter.search("%$txt%")
            }
        }
        tw_cari.setListener(tw_cari_listener)
        edit_search.addTextChangedListener(tw_cari)
    }

    fun setupRecycler(){
        contactAdapter = ContactAdapter(details){
            val sender_id = it.user_id.toString()
            mPresenter.addConversation(it.user_id){status, id ->
                if (status) {
                    val param = HashMap<String, String>()
                    param["sender_id"] = sender_id
                    param["conversation_id"] = id.toString()
                    AppRoute.open(this, ChatActivity::class.java, param)
                }
            }
        }

        val linLayout = LinearLayoutManager(this@ContactActivity,  RecyclerView.VERTICAL, false)
        recycler.apply {
            layoutManager = linLayout
            adapter = contactAdapter
        }

        scrollListener = object : EndlessRecyclerViewScrollListener(linLayout) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (!isLoadMore) {
                    isLoadMore = true
                    contactAdapter?.addLoading()
                    mPresenter.getData(lastPage){ list, status, message, next ->
                        hideLoadingDialog()
                        if (next) {
                            lastPage += 1
                        }
                        if (status) {
                            details.addAll(list!!)
                            contactAdapter?.notifyDataSetChanged()
                        } else {
                            toast(message)
                        }
                    }
                }
            }
        }
        recycler.addOnScrollListener(scrollListener!!)
    }
}
