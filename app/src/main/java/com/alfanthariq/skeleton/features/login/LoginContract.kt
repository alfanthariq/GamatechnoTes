package com.alfanthariq.skeleton.features.login

import com.alfanthariq.skeleton.features.base.BaseMvpView
import com.alfanthariq.skeleton.features.base.BasePresenter

object LoginContract {
    interface View : BaseMvpView {

    }

    interface Presenter : BasePresenter<View> {
        fun doLogin(username : String, password : String, token : String,
                    callback : (Boolean, String) -> Unit)
    }
}