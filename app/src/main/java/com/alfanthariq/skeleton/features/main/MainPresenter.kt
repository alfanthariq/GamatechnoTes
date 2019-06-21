package com.alfanthariq.skeleton.features.main

import alfanthariq.com.signatureapp.util.PreferencesHelper
import com.alfanthariq.skeleton.data.local.AppDatabase
import com.alfanthariq.skeleton.data.model.Conversation
import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.data.remote.ApiClient
import com.alfanthariq.skeleton.data.remote.ApiService
import com.alfanthariq.skeleton.features.base.BasePresenterImpl
import com.alfanthariq.skeleton.utils.NetworkUtil
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainPresenter (var view: MainContract.View) :
    BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    private val context = view.getContext()
    private val pref_profile = PreferencesHelper.getProfilePref(context)
    private var apiService: ApiService? = null
    private val db = AppDatabase.getInstance(context)!!

    override fun refreshData() {
        doAsync {
            val data = db.conversationDAO().all()

            uiThread {
                view.onRefreshData(data)
            }
        }
    }
}