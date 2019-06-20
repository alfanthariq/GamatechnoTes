package com.alfanthariq.skeleton.features.login

import alfanthariq.com.signatureapp.util.PreferencesHelper
import com.alfanthariq.skeleton.data.remote.ApiClient
import com.alfanthariq.skeleton.data.remote.ApiService
import com.alfanthariq.skeleton.features.base.BasePresenterImpl
import com.alfanthariq.skeleton.utils.NetworkUtil
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter (var view: LoginContract.View) :
    BasePresenterImpl<LoginContract.View>(), LoginContract.Presenter {

    var apiService: ApiService? = null
    val context = view.getContext()
    val pref_setting = PreferencesHelper.getSettingPref(context)
    val pref_profile = PreferencesHelper.getProfilePref(context)

    override fun doLogin(username: String, password: String, token: String, callback: (Boolean, String) -> Unit) {
        apiService = ApiClient.getClient(context,null, NetworkUtil.useAPI)
            .create(ApiService::class.java)
        val sendApi = apiService?.login(username, password)
        sendApi?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false, "Request Failed")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val resp = response.body()!!.string()
                    val json: JsonObject = JsonParser().parse(resp).asJsonObject
                    val obj = json.get("gtfwResult").asJsonObject
                    val status = obj.get("status").asInt
                    println("Status $status")

                    if (status == 200) {
                        try {
                            val data: JsonObject = obj.get("data").asJsonObject
                            val editorProfile = pref_profile.edit()
                            editorProfile.putInt("user_id", data.get("user_id").asInt)
                            editorProfile.putString("user_name", data.get("user_name").asString)
                            editorProfile.putString("user_username", data.get("user_username").asString)
                            editorProfile.putString("token", data.get("token").asString)
                            editorProfile.putString("user_email", data.get("user_email").asString)
                            editorProfile.apply()

                            val editor = pref_setting.edit()
                            editor.putBoolean("is_login", true)
                            editor.apply()

                            callback(true, "")
                        } catch (e : JsonIOException) {

                        }
                    } else {
                        callback(false, obj.get("message").asString)
                    }
                } else {
                    callback(false, "Internal Server Error")
                }
            }
        })
    }

}