package com.alfanthariq.skeleton.features.main

import alfanthariq.com.signatureapp.util.PreferencesHelper
import com.alfanthariq.skeleton.data.local.AppDatabase
import com.alfanthariq.skeleton.data.remote.ApiService
import com.alfanthariq.skeleton.features.base.BasePresenterImpl

class MainPresenter (var view: MainContract.View) :
    BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    val context = view.getContext()
    val pref_setting = PreferencesHelper.getSettingPref(context)
    val pref_profile = PreferencesHelper.getProfilePref(context)
    var apiService: ApiService? = null
    val db = AppDatabase.getInstance(context)!!


}