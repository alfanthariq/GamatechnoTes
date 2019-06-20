package com.alfanthariq.skeleton.features.base

import android.content.Context

open class BasePresenterImpl<V : BaseMvpView> : BasePresenter<V> {

    protected var mView: V? = null

    protected var mContext: Context? = null

    override fun attachView(view: V) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun getContext(context: Context) {
        mContext = context
    }
}