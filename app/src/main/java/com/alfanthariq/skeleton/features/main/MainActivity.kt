package com.alfanthariq.skeleton.features.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.features.base.BaseActivity
import com.alfanthariq.skeleton.features.common.ErrorView
import com.alfanthariq.skeleton.features.main.contact.ContactActivity
import com.alfanthariq.skeleton.utils.*
import com.livinglifetechway.k4kotlin.onClick
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity : BaseActivity<MainContract.View,
        MainPresenter>(),
    MainContract.View, ErrorView.ErrorListener {

    private var userAdapter : UserAdapter? = null
    private var details = ArrayList<Users>()
    private var lastPage = 1

    override var mPresenter: MainPresenter
        get() = MainPresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_main

    override fun getTagClass(): String = javaClass.simpleName

    override fun onReloadData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onRefreshData(list: List<Users>) {
        hideLoadingDialog()
        details.clear()
        details.addAll(list)
        userAdapter?.notifyDataSetChanged()

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

//        if (NetworkUtil.isNetworkConnected(this)) {
//            showLoadingDialog("Loading data ...")
//            mPresenter.getData(lastPage){ list, status, message ->
//                hideLoadingDialog()
//                if (status) {
//                    details.addAll(list!!)
//                    userAdapter?.notifyDataSetChanged()
//                } else {
//                    toast(message)
//                }
//            }
//        } else {
//            mPresenter.refreshData()
//        }

        mPresenter.refreshData()

        fab_contact.onClick {
            AppRoute.open(this@MainActivity, ContactActivity::class.java)
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
        userAdapter = UserAdapter(details){

        }

        recycler.apply {
            val linLayout = LinearLayoutManager(this@MainActivity,  RecyclerView.VERTICAL, false)
            layoutManager = linLayout
            adapter = userAdapter
        }
    }
}
