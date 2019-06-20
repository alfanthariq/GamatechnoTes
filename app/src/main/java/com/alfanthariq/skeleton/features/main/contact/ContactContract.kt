package com.alfanthariq.skeleton.features.main.contact

import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.features.base.BaseMvpView
import com.alfanthariq.skeleton.features.base.BasePresenter

object ContactContract {
    interface View : BaseMvpView {
        fun onRefreshData(list : List<Users>)
    }

    interface Presenter : BasePresenter<View> {
        fun getData(page : Int, callback : (List<Users>?, Boolean, String) -> Unit)
        fun refreshData()
        fun search(keyword : String)
    }
}