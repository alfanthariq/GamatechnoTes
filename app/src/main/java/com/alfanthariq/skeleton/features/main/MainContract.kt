package com.alfanthariq.skeleton.features.main

import com.alfanthariq.skeleton.features.base.BaseMvpView
import com.alfanthariq.skeleton.features.base.BasePresenter

object MainContract {
    interface View : BaseMvpView {

    }

    interface Presenter : BasePresenter<View>{

    }
}