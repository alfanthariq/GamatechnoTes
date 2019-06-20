package com.alfanthariq.skeleton.features.base

import android.content.Context

interface BasePresenter<in V : BaseMvpView> {

    fun attachView(view: V)

    fun detachView()

    fun getContext(context:Context)
}