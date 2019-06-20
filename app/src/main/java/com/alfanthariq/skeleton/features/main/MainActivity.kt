package com.alfanthariq.skeleton.features.main

import android.content.Context
import android.os.Bundle
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.features.base.BaseActivity
import com.alfanthariq.skeleton.features.common.ErrorView
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity : BaseActivity<MainContract.View,
        MainPresenter>(),
    MainContract.View, ErrorView.ErrorListener {

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

    fun init(){

    }
}
