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
    val local = "http://192.168.1.37:8080/"
    val office = "http://10.8.12.188:8080/"
    val trial = "http://167.99.66.123:2727/"
    val socket = "https://socket-io-chat.now.sh" //"http://167.99.66.123:2727/"
    var useAPI = trial
    val PROFILE_IMG_BASE_URL = "https://api.e-fikaliber42.id/cdn/abs/profile_img/"

    fun getPhotoURL(context: Context) : String {
        val pref_setting: SharedPreferences by lazy {
            context.getSharedPreferences("setting.conf", Context.MODE_PRIVATE)
        }

        val url = URL(pref_setting.getString("url_api", "http://appdk-trial.duakelinci.co.id:13510/dkapi/api/smmdk/"))
        val url_port = if (url.port != -1) {
            url.port.toString()
        } else {
            ""
        }
        val baseUrl = url.protocol + "://" + url.host + ":" + url_port
        val photoUrl = "$baseUrl/dkapi/web/"

        return photoUrl
    }

    fun getUrlAPI(context: Context) {
        val pref_setting: SharedPreferences by lazy {
            context.getSharedPreferences("setting.conf", Context.MODE_PRIVATE)
        }

        useAPI = pref_setting.getString("url_api", "http://appdk-trial.duakelinci.co.id:13510/dkapi/api/smmdk/")
    }

    fun isHttpStatusCode(throwable: Throwable, statusCode: Int): Boolean {
        return throwable is HttpException && throwable.code() == statusCode
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}