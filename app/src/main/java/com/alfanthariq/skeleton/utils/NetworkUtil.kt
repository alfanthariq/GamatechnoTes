package com.alfanthariq.skeleton.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import retrofit2.HttpException
import java.net.URL


object NetworkUtil {

    /**
     * Returns true if the Throwable is an instance of RetrofitError with an
     * http status code equals to the given one.
     */
    val trial = "http://167.99.66.123:2727/"
    val socket = "http://167.99.66.123:2727/" //"https://socket-io-chat.now.sh"
    var useAPI = trial

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}