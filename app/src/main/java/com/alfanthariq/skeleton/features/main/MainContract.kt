package com.alfanthariq.skeleton.features.main

import com.alfanthariq.skeleton.data.model.Conversation
import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.features.base.BaseMvpView
import com.alfanthariq.skeleton.features.base.BasePresenter

object MainContract {
    interface View : BaseMvpView {
        fun onRefreshData(list : List<Conversation>)
    }

    interface Presenter : BasePresenter<View>{
        fun refreshData()
    }
}